import { NavLink } from 'react-router-dom';

const navItems = [
  { to: '/knowledge', label: '知识库' },
  { to: '/chat', label: '聊天' },
  { to: '/settings', label: '设置' }
];

export function Sidebar(): JSX.Element {
  return (
    <aside className="w-64 border-r border-slate-200 bg-white p-4">
      <p className="mb-6 text-lg font-semibold text-slate-900">Knowledge AI</p>
      <nav className="space-y-2" aria-label="主导航">
        {navItems.map((item) => (
          <NavLink
            key={item.to}
            to={item.to}
            className={({ isActive }) =>
              `block rounded-md px-3 py-2 text-sm transition ${
                isActive ? 'bg-emerald-100 text-emerald-700' : 'text-slate-700 hover:bg-slate-100'
              }`
            }
          >
            {item.label}
          </NavLink>
        ))}
      </nav>
    </aside>
  );
}
