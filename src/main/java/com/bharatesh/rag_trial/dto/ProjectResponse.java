package com.bharatesh.rag_trial.dto;

import java.util.List;

import jakarta.persistence.Column;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProjectResponse {
    private Long id;
    private String name;
    private String gitUrl;

    private String gitBranch;

    private String teamName;

    private String managerName;

    private List<ProjectConfigResponse> config;  
}


