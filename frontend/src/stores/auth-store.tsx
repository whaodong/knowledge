import React, { createContext, useCallback, useContext, useEffect, useMemo, useReducer, useRef } from 'react';
import { fetchCurrentUser, login, logout } from '../api/auth';
import { setUnauthorizedHandler } from '../api/client';
import { ApiError } from '../api/types';
import { CurrentUser, LoginPayload } from '../types/auth';

interface AuthState {
  user: CurrentUser | null;
  isAuthenticated: boolean;
  isInitialized: boolean;
  isInitializing: boolean;
  isLoggingIn: boolean;
  errorMessage: string | null;
}

type AuthAction =
  | { type: 'INITIALIZE_START' }
  | { type: 'INITIALIZE_SUCCESS'; payload: CurrentUser }
  | { type: 'INITIALIZE_ANONYMOUS' }
  | { type: 'LOGIN_START' }
  | { type: 'LOGIN_SUCCESS'; payload: CurrentUser }
  | { type: 'LOGIN_FAILURE'; payload: string }
  | { type: 'LOGOUT' }
  | { type: 'CLEAR_ERROR' };

interface AuthContextValue {
  state: AuthState;
  initializeAuth: () => Promise<void>;
  loginWithPassword: (payload: LoginPayload) => Promise<void>;
  logoutNow: () => Promise<void>;
  clearError: () => void;
}

const initialState: AuthState = {
  user: null,
  isAuthenticated: false,
  isInitialized: false,
  isInitializing: false,
  isLoggingIn: false,
  errorMessage: null
};

function authReducer(state: AuthState, action: AuthAction): AuthState {
  switch (action.type) {
    case 'INITIALIZE_START':
      return { ...state, isInitializing: true, errorMessage: null };
    case 'INITIALIZE_SUCCESS':
      return {
        ...state,
        user: action.payload,
        isAuthenticated: true,
        isInitialized: true,
        isInitializing: false,
        errorMessage: null
      };
    case 'INITIALIZE_ANONYMOUS':
      return {
        ...state,
        user: null,
        isAuthenticated: false,
        isInitialized: true,
        isInitializing: false,
        errorMessage: null
      };
    case 'LOGIN_START':
      return { ...state, isLoggingIn: true, errorMessage: null };
    case 'LOGIN_SUCCESS':
      return {
        ...state,
        user: action.payload,
        isAuthenticated: true,
        isInitialized: true,
        isInitializing: false,
        isLoggingIn: false,
        errorMessage: null
      };
    case 'LOGIN_FAILURE':
      return { ...state, isLoggingIn: false, errorMessage: action.payload };
    case 'LOGOUT':
      return {
        ...state,
        user: null,
        isAuthenticated: false,
        isInitialized: true,
        isInitializing: false,
        isLoggingIn: false,
        errorMessage: null
      };
    case 'CLEAR_ERROR':
      return { ...state, errorMessage: null };
    default:
      return state;
  }
}

const AuthContext = createContext<AuthContextValue | null>(null);

export function AuthProvider({ children }: { children: React.ReactNode }): JSX.Element {
  const [state, dispatch] = useReducer(authReducer, initialState);
  const initStartedRef = useRef(false);

  const initializeAuth = useCallback(async (): Promise<void> => {
    if (initStartedRef.current || state.isInitialized || state.isInitializing) {
      return;
    }
    initStartedRef.current = true;

    dispatch({ type: 'INITIALIZE_START' });

    try {
      const user = await fetchCurrentUser();
      dispatch({ type: 'INITIALIZE_SUCCESS', payload: user });
    } catch (error) {
      if (error instanceof ApiError && error.code === 401) {
        dispatch({ type: 'INITIALIZE_ANONYMOUS' });
        return;
      }
      dispatch({ type: 'INITIALIZE_ANONYMOUS' });
    }
  }, [state.isInitialized, state.isInitializing]);

  const loginWithPassword = useCallback(async (payload: LoginPayload): Promise<void> => {
    dispatch({ type: 'LOGIN_START' });

    try {
      await login(payload);
      const user = await fetchCurrentUser();
      dispatch({ type: 'LOGIN_SUCCESS', payload: user });
    } catch (error) {
      const message = error instanceof Error ? error.message : '登录失败，请稍后重试';
      dispatch({ type: 'LOGIN_FAILURE', payload: message });
      throw error;
    }
  }, []);

  const logoutNow = useCallback(async (): Promise<void> => {
    try {
      await logout();
    } finally {
      dispatch({ type: 'LOGOUT' });
    }
  }, []);

  const clearError = useCallback((): void => {
    dispatch({ type: 'CLEAR_ERROR' });
  }, []);

  const value = useMemo<AuthContextValue>(
    () => ({
      state,
      initializeAuth,
      loginWithPassword,
      logoutNow,
      clearError
    }),
    [state, initializeAuth, loginWithPassword, logoutNow, clearError]
  );

  useEffect(() => {
    setUnauthorizedHandler(() => {
      dispatch({ type: 'LOGOUT' });
    });
  }, []);

  return <AuthContext.Provider value={value}>{children}</AuthContext.Provider>;
}

export function useAuth(): AuthContextValue {
  const context = useContext(AuthContext);
  if (!context) {
    throw new Error('useAuth 必须在 AuthProvider 内使用');
  }
  return context;
}
