import { EmptyState } from '../components/feedback/EmptyState';

export function ChatPage(): JSX.Element {
  return (
    <section className="space-y-4">
      <h2 className="text-lg font-semibold text-slate-900">聊天</h2>
      <div className="rounded-xl border border-slate-200 bg-white p-4 text-sm text-slate-500">会话列表区域</div>
      <EmptyState title="欢迎使用聊天" description="选择左侧会话或创建新会话开始对话。" />
    </section>
  );
}
