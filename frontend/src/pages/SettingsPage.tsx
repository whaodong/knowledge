export function SettingsPage(): JSX.Element {
  return (
    <section className="space-y-4">
      <h2 className="text-lg font-semibold text-slate-900">设置</h2>
      <div className="grid gap-4 md:grid-cols-2">
        <div className="rounded-xl border border-slate-200 bg-white p-4 text-sm text-slate-600">账号设置分组</div>
        <div className="rounded-xl border border-slate-200 bg-white p-4 text-sm text-slate-600">系统设置分组</div>
      </div>
    </section>
  );
}
