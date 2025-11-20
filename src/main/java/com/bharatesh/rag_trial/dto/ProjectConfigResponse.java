package com.bharatesh.rag_trial.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProjectConfigResponse {
    private Long guardrailId;
    private String guardrailName;

}

