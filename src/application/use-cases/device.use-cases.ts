import { Device, DeviceType, DeviceStatus, DeviceConfig } from '../domain/device/device.entity';
// Update the import path to the correct location of unique-entity-id
import { UniqueEntityID } from '../../shared/domain/unique-entity-id';

export interface DeviceRepository {
  save(device: Device): Promise<void>;
  findById(id: UniqueEntityID): Promise<Device | null>;
  findAll(): Promise<Device[]>;
  findByStatus(status: DeviceStatus): Promise<Device[]>;
  delete(id: UniqueEntityID): Promise<void>;
}

export interface CreateDeviceCommand {
  name: string;
  type: DeviceType;
  location: string;
  ipAddress?: string;
  macAddress?: string;
}

export interface UpdateDeviceStatusCommand {
  deviceId: string;
  status: string;
  reason?: string;
}

export class CreateDeviceUseCase {
  constructor(private deviceRepository: DeviceRepository) {}

  async execute(command: CreateDeviceCommand): Promise<Device> {
    const device = Device.create({
      id: new UniqueEntityID().toString(),
      name: command.name,
      type: command.type,
      location: command.location || '',
      ipAddress: command.ipAddress,
      macAddress: command.macAddress,
      config: new DeviceConfig({})
    });

    await this.deviceRepository.save(device);
    return device;
  }
}

export class UpdateDeviceStatusUseCase {
  constructor(private deviceRepository: DeviceRepository) {}

  async execute(command: UpdateDeviceStatusCommand): Promise<Device> {
    const device = await this.deviceRepository.findById(new UniqueEntityID(command.deviceId));
    if (!device) {
      throw new Error('Device not found');
    }

    const newStatus = DeviceStatus.fromString(command.status);
    device.updateStatus(newStatus, command.reason);

    await this.deviceRepository.save(device);
    return device;
  }
}

export class GetDeviceUseCase {
  constructor(private deviceRepository: DeviceRepository) {}

  async execute(deviceId: string): Promise<Device | null> {
    return await this.deviceRepository.findById(new UniqueEntityID(deviceId));
  }
}

export class ListDevicesUseCase {
  constructor(private deviceRepository: DeviceRepository) {}

  async execute(): Promise<Device[]> {
    return await this.deviceRepository.findAll();
  }
}

export class ListDevicesByStatusUseCase {
  constructor(private deviceRepository: DeviceRepository) {}

  async execute(status: DeviceStatus): Promise<Device[]> {
    return await this.deviceRepository.findByStatus(status);
  }
}

export class DeleteDeviceUseCase {
  constructor(private deviceRepository: DeviceRepository) {}

  async execute(deviceId: string): Promise<void> {
    const device = await this.deviceRepository.findById(new UniqueEntityID(deviceId));
    if (!device) {
      throw new Error('Device not found');
    }

    await this.deviceRepository.delete(new UniqueEntityID(deviceId));
  }
}
