  import { useEffect, useState } from "react";
  import StreamLogViewer from "../components/StreamLogViewer";
  import HighRiskPanel from "../components/HighRiskPanel";
const riskLevels = [ "MEDIUM", "HIGH", "CRITICAL"];
const entityTypes = [
  "CREDIT_CARD",
  "CRYPTO",
  "DATE_TIME",
  "EMAIL_ADDRESS",
  "IBAN_CODE",
  "IP_ADDRESS",
  "LOCATION",
  "PERSON",
  "NRP",
  "PHONE_NUMBER",
  "MEDICAL_LICENSE",
  "URL",
];

const generateRandomLogs = (count = 10) => {
  return Array.from({ length: count }, (_, i) => {
    const risk = riskLevels[Math.floor(Math.random() * riskLevels.length)];
    let score;

    // Assign score based on risk
    switch (risk) {
      case "CRITICAL":
        score = (Math.random() * 0.15 + 0.85).toFixed(2);
        break;
      case "HIGH":
        score = (Math.random() * 0.15 + 0.65).toFixed(2);
        break;
      case "MEDIUM":
        score = (Math.random() * 0.2 + 0.45).toFixed(2);
        break;
      case "LOW":
        score = (Math.random() * 0.4 + 0.1).toFixed(2);
        break;
    }

    const entity_type = entityTypes[Math.floor(Math.random() * entityTypes.length)];

    let text;
    switch (entity_type) {
  case "IP_ADDRESS":
    text = `${Math.floor(Math.random() * 255)}.${Math.floor(Math.random() * 255)}.${Math.floor(
      Math.random() * 255
    )}.${Math.floor(Math.random() * 255)}`;
    break;
  case "PHONE_NUMBER":
    text = `+1-555-${Math.floor(1000 + Math.random() * 9000)}`;
    break;
  case "EMAIL_ADDRESS":
    text = `user${Math.floor(Math.random() * 1000)}@mail.com`;
    break;
  case "SSN":
    text = `***-**-${Math.floor(1000 + Math.random() * 9000)}`;
    break;
  case "CREDIT_CARD":
    text = `**** **** **** ${Math.floor(1000 + Math.random() * 9000)}`;
    break;
  case "USERNAME":
    text = `user${Math.floor(Math.random() * 10000)}`;
    break;
  case "API_KEY":
    text = `sk_live_${Math.random().toString(36).substring(2, 12)}`;
    break;
  case "CRYPTO":
    text = `BTC_${Math.random().toString(36).substring(2, 10)}`;
    break;
  case "DATE_TIME":
    text = new Date(Date.now() - Math.floor(Math.random() * 1000000000)).toISOString();
    break;
  case "IBAN_CODE":
    text = `IBAN${Math.random().toString().substring(2, 15)}`;
    break;
  case "LOCATION":
    text = ["New York", "Berlin", "Tokyo", "London"][Math.floor(Math.random() * 4)];
    break;
  case "PERSON":
    text = ["Alice", "Bob", "Charlie", "David"][Math.floor(Math.random() * 4)];
    break;
  case "NRP":
    text = `NRP_${Math.random().toString(36).substring(2, 10)}`;
    break;
  case "MEDICAL_LICENSE":
    text = `ML-${Math.floor(100000 + Math.random() * 900000)}`;
    break;
  case "URL":
    text = `https://www.example${Math.floor(Math.random() * 100)}.com`;
    break;
  default:
    text = "N/A";
}


    return {
      index: i + 1,
      entity_type,
      start: 0,
      end: text.length,
      score: Number(score),
      text,
      correlation_id: `log${i + 1}`,
      risk_level: risk,
      timestamp: new Date().toISOString(),
    };
  });
};



  export default function Logs() {

    const [logs, setLogs] = useState(generateRandomLogs(10));

    useEffect(() => {
      // Connect to SSE endpoint
      const eventSource = new window.EventSource(import.meta.env.VITE_PYTHON_FILTER);

      eventSource.onmessage = (event) => {
        try {
          const data = JSON.parse(event.data);
          if (data.type === "ping") return; // Ignore pings

          const newLog = {
            id: data.correlation_id || `log-${Date.now()}`,
            entity_type: data.entity_type,
            score: data.score,
            text: data.text,
            correlation_id: data.correlation_id,
            risk_level: data.risk_level,
            timestamp: new Date().toISOString(),
          };

          setLogs((prev) => [newLog, ...prev].slice(0, 100)); // Keep last 100 logs
        } catch (err) {
          console.error("Failed to parse SSE message:", err);
        }
      };

      eventSource.onerror = () => {
        console.warn("SSE connection closed");
        eventSource.close();
      };

      return () => eventSource.close();
    }, []);

    const highRiskLogs = logs.filter(
      (log) => log.risk_level === "HIGH" || log.risk_level === "CRITICAL" 
    );

    return (
      <main className="min-h-screen px-4 py-12 w-6xl bg-background">
        {/* Header */}
        <div className="border-b border-border  bg-card px-6 py-4">
          <div className="max-w-7xl mx-auto">
            <h1 className="text-2xl font-semibold text-foreground">Stream Monitor</h1>
            <p className="text-sm text-muted-foreground mt-1">
              Real-time entity detection and risk assessment
            </p>
          </div>
        </div>

        {/* Main Layout */}
        <div className="flex h-[calc(100vh-120px)]">
          {/* Left: Full Stream Log */}
          <div className="flex-1 border-r border-border overflow-hidden">
            <StreamLogViewer logs={logs} />
          </div>

          {/* Right: High Risk Entities */}
          <div className="w-96 overflow-hidden bg-card/50">
            <HighRiskPanel logs={highRiskLogs} totalLogs={logs.length} />
          </div>
        </div>
      </main>
    );
  }
