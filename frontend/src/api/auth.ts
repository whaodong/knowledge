import { request } from './client';
import { CurrentUser, LoginPayload } from '../types/auth';

export function login(payload: LoginPayload): Promise<CurrentUser> {
  return request<CurrentUser>('/api/auth/login', {
    method: 'POST',
    body: payload
  });
}

export function fetchCurrentUser(): Promise<CurrentUser> {
  return request<CurrentUser>('/api/auth/me', {
    method: 'GET'
  });
}

export function logout(): Promise<null> {
  return request<null>('/api/auth/logout', {
    method: 'POST',
    body: {}
  });
}
