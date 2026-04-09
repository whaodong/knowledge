import { Navigate, Outlet, useLocation } from 'react-router-dom';
import { useAuth } from '../../stores/auth-store';
import { LoadingState } from '../feedback/LoadingState';

const DEFAULT_AFTER_LOGIN_PATH = '/knowledge';

export function PublicOnlyRoute(): JSX.Element {
  const {
    state: { isInitialized, isAuthenticated }
  } = useAuth();
  const location = useLocation();

  if (!isInitialized) {
    return <LoadingState text="正在初始化登录态..." fullScreen />;
  }

  if (isAuthenticated) {
    const searchParams = new URLSearchParams(location.search);
    const redirect = searchParams.get('redirect');
    return <Navigate to={redirect || DEFAULT_AFTER_LOGIN_PATH} replace />;
  }

  return <Outlet />;
}
