import { useEffect, useRef, useState } from "react";
import { Card } from "./retroui/Card";
import { ToggleGroup, ToggleGroupItem } from "./retroui/ToggleGroup";

const getRiskColor = (risk) => {
  switch (risk) {
    case "CRITICAL":
      return "text-destructive"
    case "HIGH":
      return "text-orange-500"
    case "MEDIUM":
      return "text-yellow-600"
    case "LOW":
      return "text-green-700"
    default:
      return "text-foreground"
  }
}



export default function StreamLogViewer({ logs }) {
  const scrollContainerRef = useRef(null);
  const previousLengthRef = useRef(logs.length);
  const [selectedRisks, setSelectedRisks] = useState(["CRITICAL", "HIGH", "MEDIUM"]);

  useEffect(() => {
    if (scrollContainerRef.current && logs.length > previousLengthRef.current) {
      scrollContainerRef.current.scrollTop = 0;
    }
    previousLengthRef.current = logs.length;
  }, [logs]);

  const filteredLogs = logs.filter((log) => selectedRisks.includes(log.risk_level));

  return (
    <div className="h-full flex flex-col bg-background">
      <div className="p-4 border-b  flex flex-row items-start border-border">
        <ToggleGroup className="items-start " type="multiple" value={selectedRisks} onValueChange={setSelectedRisks} >
          <ToggleGroupItem 
            value="CRITICAL" 
            variant="outlined"
            aria-label="Filter Critical"
            className="w-32 data-[state=on]:bg-destructive/5 data-[state=on]:text-destructive"
          >
            <span className="font-semibold text-destructive">CRITICAL</span>
          </ToggleGroupItem>
          <ToggleGroupItem 
            value="HIGH" 
                        variant="outlined"

            aria-label="Filter High"
            className="w-32  data-[state=on]:bg-orange-500/5 data-[state=on]:text-orange-500"
          >
            <span className="font-semibold text-orange-500">HIGH</span>
          </ToggleGroupItem>
          <ToggleGroupItem 
            value="MEDIUM" 
                        variant="outlined"

            aria-label="Filter Medium"
            className="w-32  data-[state=on]:bg-yellow-600/5 data-[state=on]:text-yellow-600"
          >
            <span className="font-semibold text-yellow-600">MEDIUM</span>
          </ToggleGroupItem>
        </ToggleGroup>
      </div>
      <div ref={scrollContainerRef} className="flex-1 overflow-y-auto scrollbar-thin">
        <div className="p-4 space-y-4">
          {filteredLogs.length === 0 ? (
            <div className="text-center py-12 text-muted-foreground">
              <p className="font-mono">Waiting for stream data...</p>
            </div>
          ) : (
            filteredLogs.map((log) => (
              <Card
                key={log.id}
                className={`p-3 border-2  w-full text-xs font-mono transition-all cursor-pointer hover:shadow-[2px_2px_0px_0px_rgba(0,0,0,0.1)] `}
              >
                <div className="flex items-start justify-between gap-2">
                  <div className="flex-1 space-y-1">
                    <div className="flex items-center gap-2">
                      <span className="text-muted-foreground">[{log.timestamp.split("T")[1].split(".")[0]}]</span>
                      <span className={`font-bold ${getRiskColor(log.risk_level)}`}>{log.risk_level}</span>
                    </div>
                    <div className="text-foreground">
                     {log.entity_type && <> <span className="text-muted-foreground font-bold">{log.entity_type}</span>
                      <span className="text-muted-foreground"> | </span>
                      </>}
                      <span className="break-all">{log.text}</span>
                    </div>
                    <div className="text-muted-foreground space-x-3 text-xs">
                      <span>Score: {log.score.toFixed(2)}</span>
                      <span>ID: {log.correlation_id}</span>
                    </div>
                  </div>
                </div>
              </Card>
            ))
          )}
        </div>
      </div>
    </div>
  );
}
