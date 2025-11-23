import { Card } from "@/components/retroui/Card";

export default function StatsCard({ label, value, icon }) {
  return (
    <Card className="p-8 w-56 hover:border-primary/50 transition-colors">
      <Card.Header className="flex flex-row items-center justify-between p-0">
        <div>
          <p className="text-sm text-muted-foreground mb-2">{label}</p>
          <p className="text-3xl font-bold text-foreground">{value}</p>
        </div>

        <div className="text-4xl opacity-20">{icon}</div>
      </Card.Header>
    </Card>
  );
}
