import { useState } from "react";
import { Button } from "../components/retroui/Button";
import { Input } from "../components/retroui/Input";
import { useNavigate } from "react-router-dom";
import toast from "react-hot-toast";
import useCreateProject from "../hooks/useCreateProject";
import { Loader } from "../components/retroui/Loader";
import { useQueryClient } from "@tanstack/react-query";
export default function Submit() {
  const queryClient = useQueryClient();
  const navigate = useNavigate();
  const createProjectMutation = useCreateProject();
  const [formData, setFormData] = useState({
    repoUrl: "",
    branchName: "",
    name: "",
    teamName: "",
    managerName: "",
  });

  const [errors, setErrors] = useState({});

  const validateForm = () => {
    const newErrors = {};

    if (!formData.name.trim()) {
      newErrors.name = "Project name is required";
    }
    if (!formData.teamName.trim()) {
      newErrors.teamName = "Team name is required";
    }
    if (!formData.managerName.trim()) {
      newErrors.managerName = "Manager name is required";
    }

    // if (!formData.repoUrl.trim()) {
    //   newErrors.repoUrl = "Repository URL is required";
    // } else if (
    //   !/^https:\/\/github\.com\/[\w-]+\/[\w-]+\/?$/.test(
    //     formData.repoUrl.trim()
    //   )
    // ) {
    //   newErrors.repoUrl =
    //     "Please enter a valid GitHub URL (e.g., https://github.com/user/repo)";
    // }

    if (!formData.branchName.trim()) {
      newErrors.branchName = "Branch name is required";
    } else if (!/^[a-zA-Z0-9_\-/.]+$/.test(formData.branchName.trim())) {
      newErrors.branchName = "Branch name contains invalid characters";
    }

    setErrors(newErrors);
    return Object.keys(newErrors).length === 0;
  };

  const handleSubmit = async (e) => {
    e.preventDefault();


    if (!validateForm()) {
      toast.error("Please fix the errors in the form");
      return;
    }

    const payload = {
      name: formData.name,
      team_name: formData.teamName,
      manager_name: formData.managerName,
      git_url: formData.repoUrl,
      git_branch: formData.branchName,
    };

    // console.log(payload);

    createProjectMutation.mutate(
      { payload:payload, navigate:navigate },
      {
        onSuccess: () => {
          queryClient.invalidateQueries({ queryKey: ["projects"] });
          toast.success("Successfully created the Project");
          navigate("/dashboard");
        },
        onError: (err) => {
          console.log(err);
          toast.error("Something went wrong, please try again later!");
        },
      }
    );
  };

  return (
    <div className="flex flex-col bg-linear-to-br from-background to-background">
      <main className="flex-1 flex items-center justify-center px-4 py-20">
        <div className="w-full max-w-md">
          <div className="bg-card rounded-lg border border-border p-12 shadow-sm">
            <h1 className="text-3xl font-bold text-foreground mb-2">
              Submit Project
            </h1>
            <p className="text-muted-foreground mb-8">
              Provide your project details to begin analysis
            </p>

            <form onSubmit={handleSubmit} className="space-y-6">
              {/* Project Name */}
              <div className="space-y-2">
                <label className="text-sm font-medium text-foreground">
                  Project Name
                </label>
                <Input
                  type="text"
                  placeholder="AI Code Analyzer"
                  value={formData.name}
                  onChange={(e) =>
                    setFormData({ ...formData, name: e.target.value })
                  }
                  className={errors.name ? "border-destructive" : ""}
                />
                {errors.name && (
                  <p className="text-sm text-destructive">{errors.name}</p>
                )}
              </div>

              {/* Team Name */}
              <div className="space-y-2">
                <label className="text-sm font-medium text-foreground">
                  Team Name
                </label>
                <Input
                  type="text"
                  placeholder="Platform Team"
                  value={formData.teamName}
                  onChange={(e) =>
                    setFormData({ ...formData, teamName: e.target.value })
                  }
                  className={errors.teamName ? "border-destructive" : ""}
                />
                {errors.teamName && (
                  <p className="text-sm text-destructive">{errors.teamName}</p>
                )}
              </div>

              {/* Manager Name */}
              <div className="space-y-2">
                <label className="text-sm font-medium text-foreground">
                  Manager Name
                </label>
                <Input
                  type="text"
                  placeholder="John Doe"
                  value={formData.managerName}
                  onChange={(e) =>
                    setFormData({ ...formData, managerName: e.target.value })
                  }
                  className={errors.managerName ? "border-destructive" : ""}
                />
                {errors.managerName && (
                  <p className="text-sm text-destructive">
                    {errors.managerName}
                  </p>
                )}
              </div>

              {/* Repository URL */}
              <div className="space-y-2">
                <label className="text-sm font-medium text-foreground">
                  GitHub Repository URL
                </label>
                <Input
                  type="url"
                  placeholder="https://github.com/user/repo"
                  value={formData.repoUrl}
                  onChange={(e) =>
                    setFormData({ ...formData, repoUrl: e.target.value })
                  }
                  className={errors.repoUrl ? "border-destructive" : ""}
                />
                {errors.repoUrl && (
                  <p className="text-sm text-destructive">{errors.repoUrl}</p>
                )}
              </div>

              {/* Branch Name */}
              <div className="space-y-2">
                <label className="text-sm font-medium text-foreground">
                  Branch Name
                </label>
                <Input
                  type="text"
                  placeholder="main"
                  value={formData.branchName}
                  onChange={(e) =>
                    setFormData({ ...formData, branchName: e.target.value })
                  }
                  className={errors.branchName ? "border-destructive" : ""}
                />
                {errors.branchName && (
                  <p className="text-sm text-destructive">
                    {errors.branchName}
                  </p>
                )}
              </div>

              {/* Submit Button */}
              <Button
                type="submit"
                size="lg"
                className="w-full"
                disabled={createProjectMutation.isPending}
              >
                {createProjectMutation.isPending ? (
                  <Loader className="mx-auto" />
                ) : (
                  "Submit Project"
                )}
              </Button>
            </form>
          </div>

          <p className="text-xs text-muted-foreground text-center mt-6">
            Your submission will be queued and processed shortly.
          </p>
        </div>
      </main>
    </div>
  );
}
