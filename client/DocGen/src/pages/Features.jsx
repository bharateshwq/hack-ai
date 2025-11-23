import React from "react";
import { Accordion } from "../components/retroui/Accordion";

const Features = () => {
  return (
    <div
      className="mt-16 mb-16 mx-auto"
      style={{ minWidth: 700, maxWidth: 640 }}
    >
      <Accordion type="single" collapsible className="space-y-8">
        <Accordion.Item value="problem">
          <Accordion.Header>
            1. The problem you chose and why it matters
          </Accordion.Header>
          <Accordion.Content>
            Manual code documentation is time-consuming, error-prone, and often
            neglected, leading to poor onboarding and maintenance. This matters
            because unclear code slows teams and increases technical debt.
          </Accordion.Content>
        </Accordion.Item>
        <Accordion.Item value="solution">
          <Accordion.Header>
            2. What your prompt/AI solution does
          </Accordion.Header>
          <Accordion.Content>
            Our AI analyzes your entire codebase and automatically generates
            rich, contextual documentation for every module, class, and
            function.
          </Accordion.Content>
        </Accordion.Item>
        <Accordion.Item value="result">
          <Accordion.Header>3. The result it generates</Accordion.Header>
          <Accordion.Content>
            You get comprehensive, up-to-date docs and logs, making your project
            easier to understand, maintain, and scale.
          </Accordion.Content>
        </Accordion.Item>
        <Accordion.Item value="beforeafter">
          <Accordion.Header>
            4. Before/After implementation with realistic scenarios
          </Accordion.Header>
          <Accordion.Content>
            Before: Developers spend hours deciphering code. After: New team
            members onboard in minutes, and updates are instantly documented.
          </Accordion.Content>
        </Accordion.Item>
        <Accordion.Item value="numbers">
          <Accordion.Header>
            5. Clear numbers (saves 2hrs per person)
          </Accordion.Header>
          <Accordion.Content>
            On average, our tool saves 2 hours per developer per week previously
            spent on documentation and code comprehension.
          </Accordion.Content>
        </Accordion.Item>
        <Accordion.Item value="orghelp">
          <Accordion.Header>6. How it helps an organization</Accordion.Header>
          <Accordion.Content>
            Organizations benefit from faster onboarding, reduced errors, and
            improved collaboration, leading to higher productivity and lower
            costs.
          </Accordion.Content>
        </Accordion.Item>
        <Accordion.Item value="narrative">
          <Accordion.Header>7. A strong narrative</Accordion.Header>
          <Accordion.Content>
            Imagine a world where documentation is never a bottleneck. Our
            solution empowers teams to focus on innovation, not paperwork.
          </Accordion.Content>
        </Accordion.Item>
      </Accordion>
    </div>
  );
};

export default Features;
