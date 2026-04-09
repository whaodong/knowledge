interface EmptyStateProps {
  title: string;
  description: string;
}

export function EmptyState({ title, description }: EmptyStateProps): JSX.Element {
  return (
    <div className="rounded-xl border border-dashed border-slate-300 bg-white p-6 text-center">
      <p className="text-base font-semibold text-slate-800">{title}</p>
      <p className="mt-2 text-sm text-slate-500">{description}</p>
    </div>
  );
}
