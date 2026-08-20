import { setWorldConstructor, IWorldOptions } from '@cucumber/cucumber';
import axios, { AxiosError, AxiosInstance, AxiosResponse } from 'axios';
import dotenv from 'dotenv';

dotenv.config();

export class World {
  attach!: IWorldOptions['attach'];
  client: AxiosInstance;
  response?: AxiosResponse;
  requestBody?: Record<string, unknown>;
  error?: AxiosError;
  currentScenarioName?: string;
  tags: string[] = [];

  constructor(options: IWorldOptions) {
    this.attach = options.attach;

    const headers: Record<string, string> = {
      "Content-Type": "application/json",
      Accept: "application/json",
    };

    const token = process.env.FSP_API_TOKEN;
    if (token) headers.Authorization = token;

    this.client = axios.create({
      baseURL: process.env.FSP_API_BASE_URL,
      timeout: 10000,
      headers,
      validateStatus: () => true,
    });
  }

  async get(requestPath: string) {
    this.error = undefined;
    this.response = await this.client.get(requestPath);
    return this.response;
  }

  async post(requestPath: string, body: Record<string, unknown> | undefined) {
    this.error = undefined;
    this.response = await this.client.post(requestPath, body);
    return this.response;
  }

  setPayload(payload: Record<string, unknown>) {
    this.requestBody = payload;
  }

  getByPath(obj: unknown, propertyPath: string): unknown {
    return propertyPath
      .split('.')
      .reduce((acc: any, key: string) => (acc == null ? acc : acc[key]), obj as any);
  }
}

setWorldConstructor(World);
export default World;
export type CustomWorld = World;
