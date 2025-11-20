package com.bharatesh.rag_trial.dto;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProjectSettingsConfigRequest {
       @JsonProperty("guardrailIds")
        private List<Long> guardrailIds;

        
    
}
