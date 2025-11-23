import { Card } from "@/components/retroui/Card";
import { LinkIcon, GitBranch, Users, User } from "lucide-react";
import { useNavigate } from "react-router-dom";
import { Button } from "./retroui/Button";
export default function ProjectCard({ project }) {
  const navigate = useNavigate();
  return (
    <Card className="p-6 max-w-72 hover:border-primary/50 transition-colors cursor-pointer">
      <Card.Header>
        <Card.Title className="text-xl font-semibold">
          {project.name}
        </Card.Title>
      </Card.Header>

      <div className="space-y-3 text-sm">
        <div className="flex items-center gap-2 text-muted-foreground">
          <a
            onClick={(e) => {
              e.stopPropagation();
            }}
            target="_blank"
            href={project.git_url}
          >
            <LinkIcon className="w-4 h-4" />
          </a>
          <span title={project.git_url} className="truncate ">
            {project.git_url}
          </span>
        </div>

        <div className="flex items-center gap-2 text-muted-foreground">
          <GitBranch className="w-4 h-4" />
          <span>{project.git_branch}</span>
        </div>

        <div className="flex items-center gap-2 text-muted-foreground">
          <Users className="w-4 h-4" />
          <span>{project.team_name}</span>
        </div>

        <div className="flex items-center gap-2 text-muted-foreground">
          <User className="w-4 h-4" />
          <span>{project.manager_name}</span>
        </div>
        <Button
          onClick={() => navigate(`/guardrail/${project.id}`)}
          className="text-sm text-black font-medium transition-colors"
        >
          Configure Guardrail →
        </Button>
      </div>
    </Card>
  );
}
