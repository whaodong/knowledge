import { EmptyState } from '../components/feedback/EmptyState';

export function KnowledgePage(): JSX.Element {
  return (
    <section className="space-y-4">
      <h2 className="text-lg font-semibold text-slate-900">知识库</h2>
      <div className="rounded-xl border border-slate-200 bg-white p-4">
        <p className="text-sm text-slate-500">筛选区（名称/标签/时间）</p>
      </div>
      <EmptyState title="暂无知识库数据" description="登录联调完成后，可在这里接入真实列表查询接口。" />
    </section>
  );
}
