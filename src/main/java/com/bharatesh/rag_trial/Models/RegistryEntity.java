package com.bharatesh.rag_trial.Models;

import jakarta.persistence.*;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

import com.bharatesh.rag_trial.Models.ChatModels.CustomEntity;
import com.bharatesh.rag_trial.dto.RegistryEntityDTO.MaskingType;


@Entity
@Table(name = "registry_entities")
@Data
public class RegistryEntity {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    private String name;

    private boolean defaultEnabled;

    private String label;

    @Enumerated(EnumType.STRING)
    private MaskingType maskingType;

    
    


    @OneToMany(mappedBy = "entity", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<GuardrailConfig> configs = new ArrayList<>();


// getters and setters
}
