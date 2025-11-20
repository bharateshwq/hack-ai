package com.bharatesh.rag_trial.dto.ChatDTOs;


import java.util.List;

import com.bharatesh.rag_trial.Models.ChatModels.CustomEntity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;

@Data
@NoArgsConstructor
public class AnalyzeFilterRequest {
    private String text;                           // The text to analyze
    private String language = "en";                // Default language is English
    private List<String> predefinedEntities;       // Predefined recognizers to use
    private List<CustomEntity> customEntities;    // Custom entity patterns

     // ✔ predefined only
    public AnalyzeFilterRequest(String text, String language, List<String> predefinedEntities) {
        this.text = text;
        this.language = language;
        this.predefinedEntities = predefinedEntities;
        this.customEntities = List.of(); // empty
    }

    // ✔ custom only
    public AnalyzeFilterRequest(String text, String language, List<CustomEntity> customEntities, boolean customOnly) {
        this.text = text;
        this.language = language;
        this.predefinedEntities = List.of(); // empty
        this.customEntities = customEntities;
    }

    // ✔ both predefined & custom
    public AnalyzeFilterRequest(
            String text,
            String language,
            List<String> predefinedEntities,
            List<CustomEntity> customEntities
    ) {
        this.text = text;
        this.language = language;
        this.predefinedEntities = predefinedEntities;
        this.customEntities = customEntities;
    }
}

