import React, { useState, useMemo } from "react";
import { Dialog } from "./retroui/Dialog";
import { Button } from "./retroui/Button";
import { Input } from "./retroui/Input";
import { useMutation, useQueryClient, useQuery } from "@tanstack/react-query";
import { useParams } from "react-router-dom";
import { Search } from "lucide-react";

const CreateGuardrail = ({ children }) => {
  const [open, setOpen] = useState(false);
  const [searchTerm, setSearchTerm] = useState("");
  const params = useParams();
  const projectId = params.id;
  const queryClient = useQueryClient();

  // Fetch available guardrails
  const { data: guardrails = [], isLoading: guardrailsLoading } = useQuery({
    queryKey: ["guardrails", projectId],
    queryFn: async () => {
      const response = await fetch(
        `${import.meta.env.VITE_CREATE_GUARDRAIL_PROJECT}/${projectId}`
      );
      if (!response.ok) throw new Error("Failed to fetch guardrails");
      return response.json();
    },
    enabled: open,
  });

  // Filter guardrails based on search
  const filteredGuardrails = useMemo(() => {
    if (!guardrails || !Array.isArray(guardrails) || guardrails.length === 0)
      return;
    return guardrails.filter((g) =>
      g.name.toLowerCase().includes(searchTerm.toLowerCase())
    );
  }, [guardrails, searchTerm]);

  const createGuardrailMutation = useMutation({
    mutationFn: async (guardrailName) => {
      const payload = { name: guardrailName };
      const response = await fetch(
        import.meta.env.VITE_CREATE_GUARDRAIL_PROJECT + `/${projectId}`,
        {
          method: "POST",
          headers: { "Content-Type": "application/json" },
          body: JSON.stringify(payload),
        }
      );
      if (!response.ok) throw new Error("Failed to create guardrail");
      return response.json();
    },
  });

  const attachGuardrailMutation = useMutation({
    mutationFn: async (guardrailId) => {
      const response = await fetch(
        `${
          import.meta.env.VITE_CREATE_GUARDRAIL_PROJECT
        }/${projectId}/${guardrailId}`,
        {
          method: "POST",
          headers: { "Content-Type": "application/json" },
        }
      );
      console.log(response);
      if (!response.ok) throw new Error("Failed to attach guardrail");
      //   return response.json()
    },
    onSuccess: () => {
      setOpen(false);
      queryClient.invalidateQueries({ queryKey: ["project", projectId] });
    },
  });

  return (
    <Dialog open={open} onOpenChange={setOpen}>
      <Dialog.Trigger asChild>{children}</Dialog.Trigger>
      <Dialog.Content size="md">
        <Dialog.Header>
          <h2 className="text-lg font-semibold">Guardrails</h2>
        </Dialog.Header>

        <div className="px-4 py-4 space-y-4">
          <div className="flex gap-2 mb-4">
            <div className="relative flex-1">
              <Search className="absolute left-3 top-1/2 -translate-y-1/2 w-4 h-4 text-muted-foreground" />
              <Input
                placeholder="Search or create guardrail name..."
                value={searchTerm}
                onChange={(e) => setSearchTerm(e.target.value)}
                className="pl-10"
              />
            </div>
            <Button
              disabled={!searchTerm.trim()}
              onClick={() => {
                if (searchTerm.trim()) {
                  createGuardrailMutation.mutate(searchTerm, {
                    onSuccess: async () => {
                      queryClient.invalidateQueries({
                        queryKey: ["project", projectId],
                      });
                      setSearchTerm("");
                      setOpen(false);
                    },
                  });
                }
              }}
            >
              Create New
            </Button>
          </div>

          {guardrailsLoading ? (
            <p className="text-center text-muted-foreground text-sm py-4">
              Loading guardrails...
            </p>
          ) : filteredGuardrails?.length === 0 || !filteredGuardrails ? (
            <p className="text-center text-muted-foreground text-sm py-4">
              No guardrails found
            </p>
          ) : (
            <div className="space-y-2 max-h-64 overflow-y-auto">
              {filteredGuardrails.map((guardrail) => (
                <div
                  key={guardrail.id}
                  className="flex items-center justify-between p-3 rounded-md bg-muted border border-border"
                >
                  <div>
                    <p className="text-sm font-medium">{guardrail.name}</p>
                    {guardrail.attached && (
                      <p className="text-xs text-muted-foreground">Attached</p>
                    )}
                  </div>
                  <Button
                    size="sm"
                    disabled={guardrail.attached}
                    variant={guardrail.attached ? "outline" : "default"}
                    onClick={() => attachGuardrailMutation.mutate(guardrail.id)}
                  >
                    {guardrail.attached ? "Attached" : "Attach"}
                  </Button>
                </div>
              ))}
            </div>
          )}
        </div>
        <Dialog.Footer>
          <Button
            type="button"
            variant="outline"
            onClick={() => setOpen(false)}
          >
            Cancel
          </Button>
        </Dialog.Footer>
      </Dialog.Content>
    </Dialog>
  );
};

export default CreateGuardrail;
