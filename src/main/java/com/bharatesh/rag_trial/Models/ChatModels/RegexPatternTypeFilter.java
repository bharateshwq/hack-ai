package com.bharatesh.rag_trial.Models.ChatModels;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RegexPatternTypeFilter {
    private String regex; // Regex string
    private double score;   // Score for confidence
}

