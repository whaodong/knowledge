export interface ApiResponse<T> {
  code: number;
  message: string;
  data: T;
  timestamp: string;
}

export interface ApiRequestOptions extends Omit<RequestInit, 'body'> {
  body?: unknown;
  skipAuthHandling?: boolean;
}

export class ApiError extends Error {
  public readonly code: number;
  public readonly status: number;
  public readonly timestamp?: string;

  constructor(message: string, code: number, status: number, timestamp?: string) {
    super(message);
    this.name = 'ApiError';
    this.code = code;
    this.status = status;
    this.timestamp = timestamp;
  }
}
