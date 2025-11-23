import { Button } from "./retroui/Button";
export default function EmptyState({ icon, title, description, action }) {
  return (
    <div className="flex flex-col items-center justify-center py-12 px-4 bg-card rounded-lg border border-border">
      <div className="text-5xl mb-4">{icon}</div>
      <h3 className="text-lg font-semibold text-foreground mb-2">{title}</h3>
      <p className="text-sm text-muted-foreground max-w-sm text-center mb-6">
        {description}
      </p>
      {action && (
        <Button
          onClick={action.onClick}
          className="px-6 py-2 bg-primary text-primary-foreground rounded-lg hover:bg-primary/90 transition-colors text-sm font-medium"
        >
          {action.label}
        </Button>
      )}
    </div>
  );
}
