interface LoadingStateProps {
  text?: string;
  fullScreen?: boolean;
}

export function LoadingState({ text = '加载中...', fullScreen = false }: LoadingStateProps): JSX.Element {
  const className = fullScreen
    ? 'flex min-h-screen items-center justify-center'
    : 'flex min-h-[200px] items-center justify-center';

  return (
    <div className={className} role="status" aria-busy="true" aria-live="polite">
      <div className="rounded-xl border border-slate-200 bg-white px-4 py-3 text-sm text-slate-600 shadow-sm">
        {text}
      </div>
    </div>
  );
}
