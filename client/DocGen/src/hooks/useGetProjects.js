import { useQuery } from "@tanstack/react-query";
const URL = import.meta.env.VITE_GITHUB_PROJECTS;
function useGetProjects() {
  return useQuery({
    queryFn: async () => {
      const response = await fetch(URL);
      const data = await response.json();
      console.log(data);
      return data;
    },
    queryKey: ["projects"],
  });
}

export default useGetProjects;
