package com.bharatesh.rag_trial.dto.RegistryEntityDTO;

import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RegistryEntityResponse {
    private Long id;
    private String name;
    private boolean defaultEnabled;
    private String label;

    @Enumerated(EnumType.STRING)
    private MaskingType maskingType;

}
