import { useQuery } from "@tanstack/react-query";

const BASE_URL = import.meta.env.VITE_GITHUB_PROJECTS;

function useProject(projectId) {
  return useQuery({
    queryFn: async () => {
      const response = await fetch(`${BASE_URL}/${projectId}`);
      if (!response.ok) {
        throw new Error("Failed to fetch project");
      }
      const data = await response.json();
      return data;
    },
    queryKey: ["project", projectId],
    enabled: !!projectId,
  });
}

export default useProject;
