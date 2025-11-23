import { useState, useRef } from "react";
import { Plus, PlusIcon, Send, Trash2, X } from "lucide-react";
import { Button } from "./retroui/Button";
import { Input } from "./retroui/Input";
import { useMutation, useQueryClient } from "@tanstack/react-query";
import { Badge } from "./retroui/Badge";
import CreateGuardrail from "./CreateGuardrail";

export default function GuardrailChat({
  projectName = "Guardrail Chat",
  guardrails = [],
  projectId,
  onSelectGuardrail,
  selectedGuardrailId,
}) {
  const [messages, setMessages] = useState(() => [
    {
      id: "1",
      role: "user",
      content:
        "My credit card is 4111-1111-1111-1111 and my email is test@example.com.",
      timestamp: new Date(),
    },
    {
      id: "2",
      role: "assistant",
      content: "I'm sorry, but I cannot store that information...",
      timestamp: new Date(),
    },
  ]);

  const [input, setInput] = useState("");
  const [isLoading, setIsLoading] = useState(false);
  const messagesEndRef = useRef(null);
  const queryClient = useQueryClient();

  const chatMutation = useMutation({
    mutationFn: async (query) => {
      const response = await fetch(
        String(import.meta.env.VITE_GUARDRAIL_CHAT_API) + `/${projectId}`,
        {
          method: "POST",
          headers: {
            "Content-Type": "application/json",
          },
          body: JSON.stringify({ query }),
        }
      );
      if (!response.ok) {
        throw new Error("Failed to get response from backend");
      }
      return response.json();
    },
  });

  const removeGuardrailMutation = useMutation({
    mutationFn: async (guardrailId) => {
      const response = await fetch(
        `${
          import.meta.env.VITE_CREATE_GUARDRAIL_PROJECT
        }/${guardrailId}/${projectId}`,
        {
          method: "DELETE",
          headers: {
            "Content-Type": "application/json",
          },
        }
      );
      if (!response.ok) {
        throw new Error("Failed to remove guardrail");
      }
      // return response.json();
    },
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ["project", projectId] });
    },
  });

  const handleSendMessage = async () => {
    if (!input.trim()) return;

    const userMessage = {
      id: Date.now().toString(),
      role: "user",
      content: input,
      timestamp: new Date(),
    };

    setMessages((prev) => [...prev, userMessage]);
    setInput("");
    setIsLoading(true);

    chatMutation.mutate(input, {
      onSuccess: (data) => {
        const assistantMessage = {
          id: (Date.now() + 1).toString(),
          role: "assistant",
          content: data.ai_response || "No response from backend.",
          timestamp: new Date(),
          sanitized: data.sanitized_response || "",
        };
        setMessages((prev) => [...prev, assistantMessage]);
        setIsLoading(false);
      },
      onError: () => {
        const errorMessage = {
          id: (Date.now() + 2).toString(),
          role: "assistant",
          content: "Error: Could not get response from backend.",
          timestamp: new Date(),
        };
        setMessages((prev) => [...prev, errorMessage]);
        setIsLoading(false);
      },
    });
  };

  const handleClearChat = () => {
    if (window.confirm("Are you sure you want to clear all messages?")) {
      setMessages([]);
    }
  };

  return (
    <div className="flex flex-col h-full bg-background overflow-hidden">
      {/* Header */}
      <div className="border-b border-border p-4 shrink-0">
        <h2 className="text-lg font-semibold text-foreground mb-1">
          {projectName}
        </h2>
        <div className="text-sm flex flex-col gap-2 text-muted-foreground">
          Active Guardrails:{" "}
          <div className="inline-flex flex-row my-1 gap-2 h-8">
            {guardrails.map((chip) => (
              <Badge
                key={chip.guardrail_id}
                variant={
                  selectedGuardrailId === chip.guardrail_id
                    ? "default"
                    : "outline"
                }
                size="md"
                className="m-0 items-center text-xs gap-2 inline-flex px-3 py-2 cursor-pointer hover:opacity-80 transition-opacity"
                onClick={() => onSelectGuardrail?.(chip.guardrail_id)}
              >
                <span>{chip.guardrail_name}</span>
                <button
                  onClick={(e) => {
                    e.stopPropagation();
                    removeGuardrailMutation.mutate(chip.guardrail_id);
                  }}
                  className="ml-1 hover:opacity-70 transition-opacity"
                  aria-label={`Remove ${chip.guardrail_name}`}
                >
                  <X size={16} />
                </button>
              </Badge>
            ))}
            <CreateGuardrail>
              <button className="h-full size-8 outline-2 outline-foreground p-2 hover:opacity-70 transition-opacity flex items-center justify-between">
                <PlusIcon size={16} />
              </button>
            </CreateGuardrail>
          </div>
        </div>
      </div>

      {/* Messages area */}
      <div className="flex-1 overflow-y-auto min-h-0 p-4 space-y-4">
        {messages.map((msg) => (
          <div
            key={msg.id}
            className={`flex ${
              msg.role === "user" ? "justify-end" : "justify-start"
            }`}
          >
            <div
              className={`max-w-md rounded-lg px-4 py-2 ${
                msg.role === "user"
                  ? "bg-primary text-primary-foreground"
                  : "bg-muted text-foreground border border-border"
              }`}
            >
              {msg.role === "assistant" && (
                <div className="flex flex-col gap-2 mb-2 border-b border-current/10 pb-1">
                  <div className="flex items-center gap-2">
                    <span className="text-xs font-semibold">Assistant</span>
                    {/* <span className="text-xs opacity-60">gpt-3.5-turbo</span> */}
                  </div>
                  {/* <p className="text-sm">{msg.content}</p> */}
                  {msg.sanitized && (
                    <div className="mt-2 p-2 rounded bg-yellow-100 text-yellow-900 text-xs border border-yellow-300">
                      <strong>Sanitized Input:</strong> {msg.sanitized}
                      <div className="mt-1">
                        This is the information sent to the LLM. DataVeil masks
                        personal information before sending.
                      </div>
                    </div>
                  )}
                </div>
              )}
              {msg.role !== "assistant" && (
                <p className="text-sm">{msg.content}</p>
              )}
              <p className="text-[10px] opacity-60 mt-1">
                {msg.timestamp.toLocaleTimeString([], {
                  hour: "2-digit",
                  minute: "2-digit",
                })}
              </p>
            </div>
          </div>
        ))}

        {isLoading && (
          <div className="flex justify-start">
            <div className="bg-muted border border-border rounded-lg px-3 py-2">
              <div className="flex gap-1">
                <div className="w-2 h-2 bg-muted-foreground rounded-full animate-bounce" />
                <div
                  className="w-2 h-2 bg-muted-foreground rounded-full animate-bounce"
                  style={{ animationDelay: "0.1s" }}
                />
                <div
                  className="w-2 h-2 bg-muted-foreground rounded-full animate-bounce"
                  style={{ animationDelay: "0.2s" }}
                />
              </div>
            </div>
          </div>
        )}

        <div ref={messagesEndRef} />
      </div>

      {/* Input area */}
      <div className="border-t border-border p-4 space-y-3 shrink-0">
        <Button
          variant="outline"
          onClick={handleClearChat}
          className="w-full flex items-center justify-center gap-2"
        >
          <Trash2 className="w-4 h-4" />
          Clear Chat
        </Button>

        <div className="flex gap-2">
          <Input
            value={input}
            onChange={(e) => setInput(e.target.value)}
            placeholder="Type a message…"
            onKeyDown={(e) => e.key === "Enter" && handleSendMessage()}
          />

          <Button
            onClick={handleSendMessage}
            disabled={!input.trim() || isLoading}
            className="flex items-center gap-2"
          >
            <Send className="w-4 h-4" />
            Send
          </Button>
        </div>
      </div>
    </div>
  );
}
