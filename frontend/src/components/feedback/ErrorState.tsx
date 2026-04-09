interface ErrorStateProps {
  title?: string;
  message: string;
  onRetry?: () => void;
}

export function ErrorState({ title = '请求失败', message, onRetry }: ErrorStateProps): JSX.Element {
  return (
    <div className="rounded-xl border border-rose-200 bg-rose-50 p-4" role="alert" aria-live="assertive">
      <p className="text-sm font-semibold text-rose-700">{title}</p>
      <p className="mt-1 text-sm text-rose-600">{message}</p>
      {onRetry ? (
        <button
          type="button"
          onClick={onRetry}
          className="mt-3 rounded-md bg-rose-600 px-3 py-2 text-sm text-white transition hover:bg-rose-700"
        >
          重试
        </button>
      ) : null}
    </div>
  );
}
