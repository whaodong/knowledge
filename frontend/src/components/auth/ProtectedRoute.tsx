import { Navigate, Outlet, useLocation } from 'react-router-dom';
import { useAuth } from '../../stores/auth-store';
import { LoadingState } from '../feedback/LoadingState';

export function ProtectedRoute(): JSX.Element {
  const {
    state: { isInitialized, isAuthenticated }
  } = useAuth();
  const location = useLocation();

  if (!isInitialized) {
    return <LoadingState text="正在初始化登录态..." fullScreen />;
  }

  if (!isAuthenticated) {
    const redirect = `${location.pathname}${location.search}`;
    return <Navigate to={`/login?redirect=${encodeURIComponent(redirect)}`} replace />;
  }

  return <Outlet />;
}
