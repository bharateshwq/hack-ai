import { useState, useMemo } from "react";
import { Button } from "../components/retroui/Button";
import { Input } from "../components/retroui/Input";
import ProjectCard from "../components/ProjectCard";
import StatsCard from "../components/StatsCard";
import { CardSkeletonLoader } from "../components/SkeletonLoader";
import EmptyState from "../components/EmptyState";
import { useNavigate } from "react-router-dom";
import { Select } from "../components/retroui/Select";
import { CheckCircle, Clock, FolderKanban } from "lucide-react";
import useGetProjects from "../hooks/useGetProjects";

export default function Dashboard() {
  const navigate = useNavigate();
  const { data: projects = [], isLoading } = useGetProjects();

  const [searchQuery, setSearchQuery] = useState("");
  const [sortBy, setSortBy] = useState("date");
  const [statusFilter, setStatusFilter] = useState("all");

  const stats = useMemo(() => {
    if (!Array.isArray(projects))
      return {
        total: 0,
        completed: 0,
        active: 0,
        avgTime: "0m",
      };

    return {
      total: projects.length,
      completed: projects.filter((p) => p.status === "Completed").length,
      active: projects.filter((p) => p.status === "Processing").length,
      avgTime: projects.length > 0 ? "2.5m" : "0m",
    };
  }, [projects]);

  const filteredProjects = useMemo(() => {
    if (!Array.isArray(projects)) return [];

    return projects
      .filter((p) => {
        const searchText = searchQuery.toLowerCase();

        const matchesSearch =
          p.name.toLowerCase().includes(searchText) ||
          p.git_url.toLowerCase().includes(searchText) ||
          p.git_branch.toLowerCase().includes(searchText) ||
          p.team_name.toLowerCase().includes(searchText) ||
          p.manager_name.toLowerCase().includes(searchText);

        // Backend DOES NOT send status → default match all
        const matchesStatus =
          statusFilter === "all" || (p.status && p.status === statusFilter); // optional

        return matchesSearch && matchesStatus;
      })
      .sort((a, b) => {
        if (sortBy === "date") {
          return new Date(b.created_at || 0) - new Date(a.created_at || 0);
        }

        // backend doesn't include status, so fallback OR remove
        const order = { Completed: 0, Processing: 1, Queued: 2 };
        return order[a.status] - order[b.status];
      });
  }, [projects, searchQuery, sortBy, statusFilter]);

  return (
    <div className="flex flex-col bg-linear-to-br from-background to-background">
      <main className="flex-1 px-4 py-12 max-w-6xl mx-auto w-full">
        {/* Header */}
        <div className="mb-12">
          <h1 className="text-4xl font-bold text-foreground mb-2">
            Project Dashboard
          </h1>
          <p className="text-muted-foreground">
            Track and manage your submitted projects
          </p>
        </div>

        {/* Stats */}
        <div className="grid grid-cols-1 md:grid-cols-4 gap-6 mb-12">
          <StatsCard
            label="Total Projects"
            value={stats.total}
            icon={<FolderKanban className="w-6 h-6 text-primary" />}
          />

          <StatsCard
            label="PI's Identified"
            value={stats.completed}
            icon={<CheckCircle className="w-6 h-6 text-green-500" />}
          />

          {/* <StatsCard
            label="Processing"
            value={stats.active}
            icon={<Clock className="w-6 h-6 text-blue-500" />}
          /> */}

          <StatsCard
            label="Avg. Time"
            value={stats.avgTime}
            icon={<Clock className="w-6 h-6 text-amber-500" />}
          />
        </div>

        {/* Search + Sort + Filter */}
        <div className="flex flex-col gap-4 mb-8">
          <div className="flex flex-col md:flex-row gap-4">
            <Input
              placeholder="Search by repository or branch..."
              value={searchQuery}
              onChange={(e) => setSearchQuery(e.target.value)}
              className="flex-1"
            />

            {/* Sort Dropdown */}
            <Select value={sortBy} onValueChange={setSortBy}>
              <Select.Trigger className="w-60">
                <Select.Value placeholder="Sort by" />
              </Select.Trigger>

              <Select.Content>
                <Select.Group>
                  <Select.Item value="date">Sort by Date</Select.Item>
                  <Select.Item value="status">Sort by Status</Select.Item>
                </Select.Group>
              </Select.Content>
            </Select>
          </div>

          {/* Status Filter Buttons */}
          <div className="flex gap-2 flex-wrap">
            {["all", "Queued", "Processing", "Completed"].map((status) => (
              <Button
                key={status}
                onClick={() => setStatusFilter(status)}
                className={`px-4 py-2 rounded-lg text-sm font-medium transition-colors ${
                  statusFilter === status
                    ? "bg-primary text-primary-foreground"
                    : "bg-muted text-foreground hover:bg-muted/80"
                }`}
              >
                {status === "all" ? "All Status" : status}
              </Button>
            ))}
          </div>
        </div>

        <h2 className="text-2xl font-bold text-foreground mb-6">Projects</h2>

        {isLoading ? (
          <CardSkeletonLoader count={6} />
        ) : projects.length === 0 ? (
          <EmptyState
            icon="📋"
            title="No projects yet"
            description="Submit your first project to get started with automated documentation generation"
            action={{
              label: "Submit First Project",
              onClick: () => navigate("/submit"),
            }}
          />
        ) : filteredProjects.length === 0 ? (
          <EmptyState
            icon="📋"
            title="No matching projects"
            description="Try adjusting your search or filters"
          />
        ) : (
          <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
            {filteredProjects.map((project) => (
              <div key={project.id} className="cursor-pointer">
                <ProjectCard project={project} />
              </div>
            ))}
          </div>
        )}
      </main>
    </div>
  );
}
