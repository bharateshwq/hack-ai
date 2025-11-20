package com.bharatesh.rag_trial.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GuardrailConfigResponse {
    // private Long id;
    private Long entityId;
    private String entityName; // optional, if you want to include
}