export function SkeletonLoader({ count = 3 }) {
  return (
    <div className="space-y-4">
      {Array(count)
        .fill(0)
        .map((_, i) => (
          <div key={i} className="space-y-2">
            <div className="h-4 bg-muted rounded animate-pulse w-3/4" />
            <div className="h-3 bg-muted rounded animate-pulse w-1/2" />
          </div>
        ))}
    </div>
  );
}

export function CardSkeletonLoader({ count = 3 }) {
  return (
    <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
      {Array(count)
        .fill(0)
        .map((_, i) => (
          <div
            key={i}
            className="p-6 max-w-72 rounded-lg bg-card border border-border space-y-3"
          >
            <div className="h-6 bg-muted rounded animate-pulse w-3/4" />
            <div className="space-y-2">
              <div className="h-3 bg-muted rounded animate-pulse" />
              <div className="h-3 bg-muted rounded animate-pulse w-5/6" />
            </div>
          </div>
        ))}
    </div>
  );
}
