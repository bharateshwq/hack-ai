import React from "react";
import { useNavigate } from "react-router-dom";
import { Button } from "../components/retroui/Button";
import { Zap, FileText, BarChart3 } from "lucide-react";
import { Card } from "../components/retroui/Card";
import Accordion from "../components/Accordion";
import Features from "./Features";
function Landing() {
  const navigate = useNavigate();
  return (
    <div className="flex flex-col ">
      <main className="flex-1 flex items-center justify-center px-4 py-20">
        <div className="max-w-2xl w-full text-center">
          {/* Hero Section */}
          <div className="space-y-6 mb-12">
            <div className="inline-flex items-center justify-center w-12 h-12 rounded-lg bg-primary/10 mb-4">
              <svg
                className="w-6 h-6 text-primary"
                fill="none"
                stroke="currentColor"
                viewBox="0 0 24 24"
              >
                <path
                  strokeLinecap="round"
                  strokeLinejoin="round"
                  strokeWidth={2}
                  d="M9 12l2 2 4-4m5.618-4.016A11.955 11.955 0 0112 2.944a11.955 11.955 0 01-8.618 3.04A12.02 12.02 0 003 9c0 5.591 3.824 10.29 9 11.622c5.176-1.332 9-6.03 9-11.622 0-1.042-.133-2.052-.382-3.016z"
                />
              </svg>
            </div>

            <h1 className="text-5xl font-bold tracking-tight text-foreground">
              DataVeil — PII Masking
            </h1>

            <p className="text-xl text-muted-foreground">
              Submit your GitHub repository — DataVeil analyzes the codebase,
              masks personal information, and generates rich documentation and
              logs in minutes.
            </p>
          </div>

          {/* CTA Button */}
          <div className="flex gap-4 justify-center mb-16">
            <Button
              size="lg"
              onClick={() => navigate("/submit")}
              className="px-8"
            >
              Submit a Project
            </Button>
            <Button
              size="lg"
              variant="outline"
              onClick={() => navigate("/dashboard")}
            >
              View Dashboard
            </Button>
          </div>

          {/* Feature Highlights */}
          <div className="grid grid-cols-1 md:grid-cols-3 gap-6">
            {[
              {
                icon: <Zap className="w-7 h-7 text-primary mx-auto" />,
                title: "Lightning Fast",
                desc: "Analyze any repository in minutes",
              },
              {
                icon: <FileText className="w-7 h-7 text-blue-500 mx-auto" />,
                title: "Rich Docs",
                desc: "Generate comprehensive documentation",
              },
              {
                icon: <BarChart3 className="w-7 h-7 text-amber-500 mx-auto" />,
                title: "Detailed Logs",
                desc: "Track analysis progress and results",
              },
            ].map((feature, i) => (
              <Card
                key={i}
                className="p-6 bg-card border border-border hover:border-primary/50 transition-colors"
              >
                <div className="text-center">{feature.icon}</div>
                <Card.Header className="text-center">
                  <Card.Title>{feature.title}</Card.Title>
                  <Card.Description>{feature.desc}</Card.Description>
                </Card.Header>
              </Card>
            ))}
          </div>
        </div>
      </main>
      {/* <Features /> */}
    </div>
  );
}

export default Landing;
