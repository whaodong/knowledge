import { useEffect } from 'react';
import { useAuth } from '../../stores/auth-store';

export function AuthInitializer({ children }: { children: React.ReactNode }): JSX.Element {
  const { initializeAuth } = useAuth();

  useEffect(() => {
    void initializeAuth();
  }, [initializeAuth]);

  return <>{children}</>;
}
