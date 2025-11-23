import { useQuery } from "@tanstack/react-query";

export function useRegistryEntities() {
  return useQuery({
    queryKey: ["registry-entities"],
    queryFn: async () => {
      const res = await fetch(import.meta.env.VITE_GUARDRAIL_API);
      if (!res.ok) throw new Error("Failed to fetch registry entities");
      return res.json();
    },
  });
}
