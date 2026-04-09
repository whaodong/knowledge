import { useNavigate } from 'react-router-dom';
import { useAuth } from '../../stores/auth-store';

export function Topbar(): JSX.Element {
  const navigate = useNavigate();
  const {
    state: { user },
    logoutNow
  } = useAuth();

  const handleLogout = async (): Promise<void> => {
    await logoutNow();
    navigate('/login', { replace: true });
  };

  return (
    <header className="flex h-16 items-center justify-between border-b border-slate-200 bg-white px-6">
      <h1 className="text-base font-semibold text-slate-900">知识中台</h1>
      <div className="flex items-center gap-3">
        <p className="text-sm text-slate-600">当前用户：{user?.nickname || user?.username || '未登录'}</p>
        <button
          type="button"
          onClick={handleLogout}
          className="rounded-md bg-slate-900 px-3 py-2 text-sm text-white transition hover:bg-slate-700"
        >
          退出登录
        </button>
      </div>
    </header>
  );
}
