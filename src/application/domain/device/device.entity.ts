import { UniqueEntityID } from '../../shared/domain/base/unique-entity-id';

export enum DeviceType {
  CAMERA = 'camera',
  SENSOR = 'sensor',
  OTHER = 'other',
}

export class DeviceStatus {
  private constructor(public readonly value: string) {}

  static ONLINE = new DeviceStatus('online');
  static OFFLINE = new DeviceStatus('offline');
  static MAINTENANCE = new DeviceStatus('maintenance');

  public static fromString(status: string): DeviceStatus {
    switch (status) {
      case DeviceStatus.ONLINE.value:
        return DeviceStatus.ONLINE;
      case DeviceStatus.OFFLINE.value:
        return DeviceStatus.OFFLINE;
      case DeviceStatus.MAINTENANCE.value:
        return DeviceStatus.MAINTENANCE;
      default:
        throw new Error(`Invalid device status: ${status}`);
    }
  }

  toString(): string {
    return this.value;
  }
}

export class DeviceConfig {
  constructor(public parameters: Record<string, any>) {}
}

export interface DeviceProps {
  id: string;
  name: string;
  type: DeviceType;
  location: string;
  ipAddress?: string;
  macAddress?: string;
  config: DeviceConfig;
  status?: DeviceStatus;
}

export class Device {
  private id: UniqueEntityID;
  private name: string;
  private type: DeviceType;
  private location: string;
  private ipAddress?: string;
  private macAddress?: string;
  private config: DeviceConfig;
  private status: DeviceStatus;

  private constructor(props: DeviceProps) {
    this.id = new UniqueEntityID(props.id);
    this.name = props.name;
    this.type = props.type;
    this.location = props.location;
    this.ipAddress = props.ipAddress;
    this.macAddress = props.macAddress;
    this.config = props.config;
    this.status = props.status ?? DeviceStatus.OFFLINE;
  }

  static create(props: DeviceProps): Device {
    return new Device(props);
  }

  getId(): string {
    return this.id.toString();
  }

  getName(): string {
    return this.name;
  }

  getType(): DeviceType {
    return this.type;
  }

  getLocation(): string {
    return this.location;
  }

  getConfig(): DeviceConfig {
    return this.config;
  }

  getStatus(): DeviceStatus {
    return this.status;
  }

  updateStatus(status: DeviceStatus, reason?: string): void {
    this.status = status;
    // Optionally handle reason (e.g., logging)
  }
}
