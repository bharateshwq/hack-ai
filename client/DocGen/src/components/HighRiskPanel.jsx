"use client"

import { AlertTriangle, Activity } from "lucide-react";
import { useEffect, useRef } from "react";
import { Card } from "./retroui/Card";

const getRiskColor = (risk) => {
  switch (risk) {
    case "CRITICAL":
      return "border-red-700";     // Dark red
    case "HIGH":
      return "border-orange-500";  // Dark orange
    case "MEDIUM":
      return "border-yellow-200";  // Dark yellow
    case "LOW":
      return "border-green-800";   // Dark green
    default:
      return "border-gray-800";    // Dark gray for default
  }
}

export default function HighRiskPanel({ logs }) {
   const criticalCount = logs.filter((log) => log.risk_level === "CRITICAL").length
  const highCount = logs.filter((log) => log.risk_level === "HIGH").length

  const scrollContainerRef = useRef(null)
  const previousLengthRef = useRef(logs.length)

  useEffect(() => {
    if (scrollContainerRef.current && logs.length > previousLengthRef.current) {
      scrollContainerRef.current.scrollTop = 0
    }
    previousLengthRef.current = logs.length
  }, [logs])

  return (
    <div className="flex flex-col h-full">
      <div className="border-b-4 border-foreground/30 px-4 py-4 bg-card sticky top-0 z-10 shadow-[3px_3px_0px_0px_rgba(0,0,0,0.08)]">
        <div className="flex items-center gap-2 mb-4">
          {/* <AlertTriangle className="w-5 h-5 text-accent" /> */}
          <h2 className="text-sm font-bold text-foreground tracking-wide">HIGH RISK ALERTS</h2>
        </div>
        <div className="grid grid-cols-2 gap-2 text-xs">
          <div className="bg-destructive/10 border-2 border-destructive/40 rounded-sm px-3 py-2 shadow-[2px_2px_0px_0px_rgba(0,0,0,0.08)]">
            <div className="text-destructive font-bold text-lg">{criticalCount}</div>
            <div className="text-destructive/70 text-xs font-mono">CRITICAL</div>
          </div>
          <div className="bg-orange-500/10 border-2 border-orange-500/40 rounded-sm px-3 py-2 shadow-[2px_2px_0px_0px_rgba(0,0,0,0.08)]">
            <div className="text-orange-500 font-bold text-lg">{highCount}</div>
            <div className="text-orange-500/70 text-xs font-mono">HIGH</div>
          </div>
        </div>
      </div>

      {/* Cards */}
      <div className="flex-1 overflow-y-auto scrollbar-thin p-4 space-y-3 bg-background">
        {logs.length === 0 ? (
          <div className="text-center py-12 text-muted-foreground">
            <Activity className="w-8 h-8 mx-auto mb-2 opacity-50" />
            <p className="text-xs font-mono">No high-risk entities</p>
          </div>
        ) : (
          logs.map((log) => (
            <Card
              key={log.id}
              className={`border-2 flex flex-col p-4 transition-all cursor-pointer hover:shadow-[3px_3px_0px_0px_rgba(0,0,0,0.15)] rounded-sm ${getRiskColor(log.risk_level)}`}
            >
              <div className="flex items-start gap-3 mb-3">
                <span className="text-xl">{log.risk_level === "CRITICAL" ? "●" : "▲"}</span>
                <div className="flex-1 min-w-0">
                  <div className="font-bold text-sm mb-1 truncate">{log.entity_type}</div>
                  <div className="text-xs opacity-90 break-all font-mono">{log.text}</div>
                </div>
              </div>
              <div className="space-y-1 text-xs opacity-75 border-t border-foreground/10 pt-2">
                <div className="font-mono">Score: {log.score.toFixed(2)}</div>
                <div className="truncate font-mono">ID: {log.correlation_id}</div>
                <div className="text-[10px] font-mono">{new Date(log.timestamp).toLocaleTimeString()}</div>
              </div>
            </Card>
          ))
        )}
      </div>
    </div>

  )
}

