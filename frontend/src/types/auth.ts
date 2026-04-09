export interface CurrentUser {
  id?: number;
  username: string;
  nickname?: string;
}

export interface LoginPayload {
  username: string;
  password: string;
}
