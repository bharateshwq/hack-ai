import useProject from "../hooks/useProject";
import { useParams } from "react-router-dom";
import { useState } from "react";
import PIIConfigPanel from "../components/PIIConfigPanel";
import GuardrailChat from "../components/GuardrailChat";

export default function Guardrail() {
  const params = useParams();
  const projectId = params.id;
  const [selectedGuardrailId, setSelectedGuardrailId] = useState(null);
  const { data: project = {}, isLoading: projectLoading, error: projectError } = useProject(projectId);
  

  if ( projectLoading) {
    return <div className="p-8 text-center">Loading registry entities...</div>;
  }
  if ( projectError) {
    return (
      <div className="p-8 text-center text-red-500">
        Error loading registry entities
      </div>
    );
  }

  // Assume backend returns array of PII types with selection info
  // If not, you may need to map/transform data here
  return (
    <div className="flex w-5xl overflow-hidden bg-background">
      {/* Sidebar */}
      <div className="w-1/3 border-r border-border bg-card flex flex-col h-[80vh] pb-3">
        <PIIConfigPanel  guardrailId={selectedGuardrailId} />
      </div>

      {/* Chat Area */}
      <div className="flex-1 min-w-0 flex flex-col h-[80vh]">
        <GuardrailChat
          projectName={project.name || `Project ${projectId}`}
          guardrails={project.config}
          projectId={projectId}
          onSelectGuardrail={setSelectedGuardrailId}
          selectedGuardrailId={selectedGuardrailId}
        />
      </div>
    </div>
  );
}
