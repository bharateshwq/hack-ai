package com.bharatesh.rag_trial.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GuardrailResponse {
    private Long id;
    private String name;
    private List<GuardrailConfigResponse> config;
}
