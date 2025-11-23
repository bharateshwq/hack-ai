import React, { useState } from "react";

const Accordion = ({ items }) => {
  const [openIndex, setOpenIndex] = useState(null);

  const handleToggle = (idx) => {
    setOpenIndex(openIndex === idx ? null : idx);
  };

  return (
    <div className="space-y-4 max-w-2xl mx-auto w-full">
      {items.map((item, idx) => (
        <div key={idx} className="border rounded-lg bg-card">
          <button
            className="w-full text-left px-4 py-3 font-semibold flex justify-between items-center focus:outline-none"
            onClick={() => handleToggle(idx)}
            aria-expanded={openIndex === idx}
          >
            <span>{item.title}</span>
            <span>{openIndex === idx ? "-" : "+"}</span>
          </button>
          {openIndex === idx && (
            <div className="px-4 pb-4 text-muted-foreground animate-fadeIn">
              {item.content}
            </div>
          )}
        </div>
      ))}
    </div>
  );
};

export default Accordion;
