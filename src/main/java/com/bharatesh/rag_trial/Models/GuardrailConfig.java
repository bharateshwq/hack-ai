package com.bharatesh.rag_trial.Models;

import com.fasterxml.jackson.annotation.JsonBackReference;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "guardrail_config")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class GuardrailConfig {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    @ManyToOne
    @JoinColumn(name = "guardrail_id")
        @JsonBackReference
    private Guardrail guardrail;


    @ManyToOne
    @JoinColumn(name = "entity_id")
    private RegistryEntity entity;


// getters and setters
}
