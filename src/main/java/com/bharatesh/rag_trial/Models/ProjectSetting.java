package com.bharatesh.rag_trial.Models;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "project_settings")
@Data
public class ProjectSetting {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    @ManyToOne
    @JoinColumn(name = "project_id")
    private Project project;


    @ManyToOne
    @JoinColumn(name = "guardrail_id")
    private Guardrail guardrail;


// getters and setters
}