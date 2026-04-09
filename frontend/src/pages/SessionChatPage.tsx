import { EmptyState } from '../components/feedback/EmptyState';

export function SessionChatPage(): JSX.Element {
  return (
    <section className="space-y-4">
      <h2 className="text-lg font-semibold text-slate-900">会话详情</h2>
      <div className="rounded-xl border border-slate-200 bg-white p-4 text-sm text-slate-500">消息列表区域</div>
      <div className="rounded-xl border border-slate-200 bg-white p-4 text-sm text-slate-500">输入区</div>
      <EmptyState title="当前会话暂无消息" description="你可以从输入框发送第一条消息。" />
    </section>
  );
}
