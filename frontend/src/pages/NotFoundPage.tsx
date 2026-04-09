import { Link } from 'react-router-dom';

export function NotFoundPage(): JSX.Element {
  return (
    <div className="flex min-h-screen items-center justify-center px-4">
      <div className="rounded-xl border border-slate-200 bg-white p-6 text-center">
        <h1 className="text-lg font-semibold text-slate-900">页面不存在</h1>
        <p className="mt-2 text-sm text-slate-500">你访问的地址不存在或已被移除。</p>
        <Link to="/knowledge" className="mt-4 inline-block rounded-md bg-slate-900 px-3 py-2 text-sm text-white">
          返回首页
        </Link>
      </div>
    </div>
  );
}
