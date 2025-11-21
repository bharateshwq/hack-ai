package com.bharatesh.rag_trial.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GuardrailDetailResponse {
    private Long id;
    private String name;
    private List<EntityWithEnabledStatus> entities;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class EntityWithEnabledStatus {
        private Long id;
        private String name;
        private Boolean enabled;
    }
}
