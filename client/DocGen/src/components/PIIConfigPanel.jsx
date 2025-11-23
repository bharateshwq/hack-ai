import { Search } from "lucide-react";
import React, { useMemo, useState } from "react";
import { Input } from "./retroui/Input";
import { Button } from "./retroui/Button";
import { useMutation, useQuery } from "@tanstack/react-query";
import { Switch } from "./retroui/Switch";
// import { useParams } from "react-router-dom";

export default function PIIConfigPanel({ guardrailId }) {
  // const params = useParams();
  // const projectId = params.id;
  // const queryClient = useQueryClient();
  const [searchTerm, setSearchTerm] = useState("");
  const [selected, setSelected] = useState({});

  // Fetch guardrail entities if guardrailId exists
  const { data: guardrailData, isLoading } = useQuery({
    queryKey: ["guardrail", guardrailId],
    queryFn: async () => {
      const response = await fetch(
        `${import.meta.env.VITE_CREATE_GUARDRAIL_PROJECT}/${guardrailId}`
      );
      if (!response.ok) throw new Error("Failed to fetch guardrail");
      const data = await response.json();
      console.log(data);

      return data;
    },
    enabled: !!guardrailId,
    //   onSuccess: (data) => {

    //     // Set selected entities from guardrail entities
    //     if (data.entities && Array.isArray(data.entities)) {
    //   const selectedIds = {};

    //   data.entities.forEach((entity) => {
    //     if (entity.enabled) {
    //       selectedIds[entity.id] = true;  // Only store enabled ones
    //     }
    //   });
    //   setSelected(selectedIds);
    // }
  });

  // Set selected entities from guardrail entities on first load
  React.useEffect(() => {
    if (guardrailData && Array.isArray(guardrailData.entities)) {
      const selectedIds = {};
      guardrailData.entities.forEach((entity) => {
        if (entity.enabled) {
          selectedIds[entity.id] = true;
        }
      });
      setSelected(selectedIds);
    }
  }, [guardrailData]);

  const selectedPIIIds = useMemo(() => {
    return Object.entries(selected)
      .filter(([, isSelected]) => isSelected)
      .map(([id]) => parseInt(id));
  }, [selected]);

  const saveSelectedPIIMutation = useMutation({
    mutationFn: async () => {
      const payload = {
        registryEntityIds: selectedPIIIds,
      };
      const response = await fetch(
        `${
          import.meta.env.VITE_CREATE_GUARDRAIL_PROJECT
        }/${guardrailId}/config`,
        {
          method: "POST",
          headers: {
            "Content-Type": "application/json",
          },
          body: JSON.stringify(payload),
        }
      );
      if (!response.ok) {
        throw new Error("Failed to save selected PII types");
      }
      return response.json();
    },
  });

  const handleSave = () => {
    if (guardrailId) {
      saveSelectedPIIMutation.mutate();
    }
  };

  if (guardrailId === null || isLoading) {
    return (
      <div className="flex flex-col h-full bg-card border-r border-border">
        <div className="p-4 text-center text-muted-foreground">
          Select a guardrail to configure PII types.
        </div>
      </div>
    );
  }

  return (
    <div className="flex flex-col h-full bg-card border-r border-border">
      {/* Header */}
      <div className="p-4 border-b border-border">
        <h2 className="text-lg font-semibold mb-4">Configure Guardrail</h2>

        {/* Search */}
        <div className="relative mb-2">
          <Search className="absolute left-3 top-1/2 -translate-y-1/2 w-4 h-4 text-muted-foreground" />
          <Input
            placeholder="Search PII types..."
            value={searchTerm}
            onChange={(e) => setSearchTerm(e.target.value)}
            className="pl-10"
          />
        </div>
      </div>

      {/* Section title */}
      <div className="p-4 border-b border-border flex items-center justify-between">
        <p className="text-sm font-semibold">PII Types</p>
        <Button
          onClick={handleSave}
          className="text-xs font-medium text-foreground hover:underline"
        >
          Save
        </Button>
      </div>

      {/* PII List */}
      <div className="flex-1 overflow-y-auto p-4 space-y-2">
        {(guardrailData.entities || []).map((pii) => (
          <div
            key={pii.id}
            className="flex items-center justify-between p-3 rounded-md bg-background border border-border"
          >
            <p className="text-sm font-medium truncate">{pii.name}</p>
            <Switch
              checked={!!selected[pii.id]}
              onCheckedChange={() =>
                setSelected((prev) => ({ ...prev, [pii.id]: !prev[pii.id] }))
              }
            />
          </div>
        ))}
      </div>

      {/* Footer (optional) */}
    </div>
  );
}
