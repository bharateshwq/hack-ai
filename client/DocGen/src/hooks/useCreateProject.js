import { useMutation } from "@tanstack/react-query";
const URL = import.meta.env.VITE_GITHUB_PROJECTS;

function useCreateProject() {
  return useMutation({
    mutationFn: createProject,
  });
}

async function createProject({ payload }) {
  console.log(payload);
  // eslint-disable-next-line no-useless-catch
  try {
    const response = await fetch(URL, {
      method: "POST",
      headers: {
        "Content-Type": "application/json",
      },
      body: JSON.stringify(payload),
    });
    return response;
  } catch (error) {
    throw error;
  }
}

export default useCreateProject;
