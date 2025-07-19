import fastify from 'fastify';
import cors from '@fastify/cors';
import jwt from '@fastify/jwt';
import websocket from '@fastify/websocket';
import multipart from '@fastify/multipart';
import swagger from '@fastify/swagger';
import swaggerUi from '@fastify/swagger-ui';
import { PrismaClient } from '@prisma/client';
import Redis from 'ioredis';

// Application Use Cases
import {
  CreateDeviceUseCase,
  UpdateDeviceStatusUseCase,
  GetDeviceUseCase,
  ListDevicesUseCase
} from './application/use-cases/device.use-cases.js';

import {
  CreateUserUseCase,
  UpdateUserUseCase,
  GetUserUseCase,
  ListUsersUseCase,
  AuthenticateUserUseCase,
  ChangePasswordUseCase,
  DeleteUserUseCase
} from './application/use-cases/user.use-cases.js';

// Infrastructure
import { PrismaDeviceRepository } from './infrastructure/repositories/device.repository.js';
import { PrismaUserRepository } from './infrastructure/repositories/user.repository.js';

// Controllers
import { DeviceController } from './interfaces/web/device.controller.js';
import { UserController } from './interfaces/web/user.controller.js';

export class VisionPlatformServer {
  private app = fastify({
    logger: {
      level: 'info',
      transport: {
        target: 'pino-pretty',
        options: {
          colorize: true
        }
      }
    }
  });

  private prisma = new PrismaClient();
  private redis = new Redis(process.env.REDIS_URL || 'redis://localhost:6379');

  // Repositories
  private deviceRepository = new PrismaDeviceRepository(this.prisma);
  private userRepository = new PrismaUserRepository(this.prisma);

  // Use Cases
  private createDeviceUseCase = new CreateDeviceUseCase(this.deviceRepository);
  private updateDeviceStatusUseCase = new UpdateDeviceStatusUseCase(this.deviceRepository);
  private getDeviceUseCase = new GetDeviceUseCase(this.deviceRepository);
  private listDevicesUseCase = new ListDevicesUseCase(this.deviceRepository);

  private createUserUseCase = new CreateUserUseCase(this.userRepository);
  private updateUserUseCase = new UpdateUserUseCase(this.userRepository);
  private getUserUseCase = new GetUserUseCase(this.userRepository);
  private listUsersUseCase = new ListUsersUseCase(this.userRepository);
  private authenticateUserUseCase = new AuthenticateUserUseCase(
    this.userRepository,
    { sign: (payload: any) => this.app.jwt.sign(payload) }
  );
  private changePasswordUseCase = new ChangePasswordUseCase(this.userRepository);
  private deleteUserUseCase = new DeleteUserUseCase(this.userRepository);

  // Controllers
  private deviceController = new DeviceController(
    this.createDeviceUseCase,
    this.updateDeviceStatusUseCase,
    this.getDeviceUseCase,
    this.listDevicesUseCase
  );

  private userController = new UserController(
    this.createUserUseCase,
    this.updateUserUseCase,
    this.getUserUseCase,
    this.listUsersUseCase,
    this.authenticateUserUseCase,
    this.changePasswordUseCase,
    this.deleteUserUseCase
  );

  async initialize(): Promise<void> {
    await this.setupMiddleware();
    await this.setupRoutes();
    await this.setupWebSocket();
    await this.setupShutdownHandlers();
  }

  private async setupMiddleware(): Promise<void> {
    // CORS
    await this.app.register(cors, {
      origin: process.env.FRONTEND_URL || 'http://localhost:3001',
      credentials: true
    });

    // JWT
    await this.app.register(jwt, {
      secret: process.env.JWT_SECRET || 'your-secret-key-here'
    });

    // Multipart support for file uploads
    await this.app.register(multipart);

    // Swagger documentation
    await this.app.register(swagger, {
      openapi: {
        info: {
          title: 'Vision Platform API',
          description: 'Machine Vision Quality Control Platform API',
          version: '1.0.0'
        },
        servers: [
          {
            url: `http://localhost:${process.env.PORT || 3000}`,
            description: 'Development server'
          }
        ]
      }
    });

    await this.app.register(swaggerUi, {
      routePrefix: '/docs',
      uiConfig: {
        docExpansion: 'list',
        deepLinking: false
      }
    });
  }

  private async setupRoutes(): Promise<void> {
    // Health check
    this.app.get('/health', async (request, reply) => {
      reply.send({
        status: 'ok',
        timestamp: new Date().toISOString(),
        uptime: process.uptime(),
        environment: process.env.NODE_ENV || 'development'
      });
    });

    // API routes
    this.app.register(async (apiRoutes) => {
      apiRoutes.addHook('preHandler', async (request, reply) => {
        // Add API versioning and common headers
        reply.header('API-Version', '1.0.0');
      });

      // Register controller routes
      await this.deviceController.registerRoutes(apiRoutes);
      await this.userController.registerRoutes(apiRoutes);
    }, { prefix: '/api/v1' });
  }

  private async setupWebSocket(): Promise<void> {
    await this.app.register(websocket);

    this.app.register(async (wsRoutes) => {
      wsRoutes.get('/ws/devices', { websocket: true }, (connection, req) => {
        interface DeviceWebSocketConnection {
          socket: {
            on(event: 'message' | 'close', listener: (message?: Buffer) => void): void;
            send(data: string): void;
          };
        }

        connection.socket.on('message', (message: Buffer) => {
          // Handle device real-time updates
          console.log('Device WebSocket message:', message.toString());
        });

        // Send initial device status
        this.sendDeviceStatus(connection);

        // Set up periodic updates
        const interval = setInterval(() => {
          this.sendDeviceStatus(connection);
        }, 5000);

        connection.socket.on('close', () => {
          clearInterval(interval);
        });
      });

      wsRoutes.get('/ws/detections', { websocket: true }, (connection, req) => {
        interface DetectionWebSocketConnection {
          socket: {
            on(event: 'message' | 'close', listener: (message?: Buffer) => void): void;
            send(data: string): void;
          };
        }

        (connection as DetectionWebSocketConnection).socket.on('message', (message?: Buffer) => {
          // Handle detection real-time updates
          if (message) {
            console.log('Detection WebSocket message:', message.toString());
          }
        });
      });
    });
  }

  private async sendDeviceStatus(connection: any): Promise<void> {
    try {
      const devices = await this.listDevicesUseCase.execute();
      connection.socket.send(JSON.stringify({
        type: 'device_status',
        data: devices.map(device => ({
          id: device.id,
          name: device.name,
          status: device.status,
          // Add other properties as needed
        }))
      }));
    } catch (error) {
      console.error('Error sending device status:', error);
    }
  }

  private async setupShutdownHandlers(): Promise<void> {
    const gracefulShutdown = async (signal: string) => {
      console.log(`Received ${signal}. Starting graceful shutdown...`);
      
      try {
        await this.app.close();
        await this.prisma.$disconnect();
        this.redis.disconnect();
        console.log('Graceful shutdown completed');
        process.exit(0);
      } catch (error) {
        console.error('Error during shutdown:', error);
        process.exit(1);
      }
    };

    process.on('SIGTERM', () => gracefulShutdown('SIGTERM'));
    process.on('SIGINT', () => gracefulShutdown('SIGINT'));
  }

  async start(port: number = 3000): Promise<void> {
    try {
      await this.initialize();
      await this.app.listen({ port, host: '0.0.0.0' });
      console.log(`🚀 Vision Platform Server is running on port ${port}`);
      console.log(`📖 API Documentation available at http://localhost:${port}/docs`);
    } catch (error) {
      console.error('Error starting server:', error);
      process.exit(1);
    }
  }
}

// Start the server
async function bootstrap() {
  const server = new VisionPlatformServer();
  const port = parseInt(process.env.PORT || '3000', 10);
  await server.start(port);
}

if (import.meta.url === `file://${process.argv[1]}`) {
  bootstrap();
}
