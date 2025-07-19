import { randomUUID } from 'crypto';

export class UniqueEntityID {
  private value: string;

  constructor(id?: string) {
    this.value = id ?? randomUUID();
  }

  toString(): string {
    return this.value;
  }
}
