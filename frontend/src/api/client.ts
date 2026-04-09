import { ApiError, ApiRequestOptions, ApiResponse } from './types';

const API_BASE_URL = import.meta.env.VITE_API_BASE_URL ?? '';
let unauthorizedHandler: (() => void) | null = null;

export function setUnauthorizedHandler(handler: () => void): void {
  unauthorizedHandler = handler;
}

function toApiError(message: string, code: number, status: number, timestamp?: string): ApiError {
  return new ApiError(message, code, status, timestamp);
}

export async function request<T>(path: string, options: ApiRequestOptions = {}): Promise<T> {
  const { body, headers, skipAuthHandling = false, ...restOptions } = options;

  const response = await fetch(`${API_BASE_URL}${path}`, {
    ...restOptions,
    credentials: 'include',
    headers: {
      'Content-Type': 'application/json',
      ...headers
    },
    body: body !== undefined ? JSON.stringify(body) : undefined
  });

  let payload: ApiResponse<T> | null = null;
  try {
    payload = (await response.json()) as ApiResponse<T>;
  } catch {
    if (!response.ok) {
      throw toApiError('网络响应解析失败', response.status, response.status);
    }
  }

  if (!payload) {
    throw toApiError('服务返回为空', 500, response.status || 500);
  }

  if (!response.ok || payload.code !== 200) {
    const errorCode = payload.code ?? response.status;
    const error = toApiError(payload.message || '请求失败', errorCode, response.status, payload.timestamp);

    if (!skipAuthHandling && (error.code === 401 || response.status === 401) && unauthorizedHandler) {
      unauthorizedHandler();
    }

    throw error;
  }

  return payload.data;
}
