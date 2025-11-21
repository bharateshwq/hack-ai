package com.bharatesh.rag_trial.Models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.annotation.Transient;

import com.fasterxml.jackson.annotation.JsonManagedReference;

@Entity
@Table(name = "guardrail")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Guardrail {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name")
    private String name;


    @OneToMany(mappedBy = "guardrail", cascade = CascadeType.ALL, orphanRemoval = true)
        @JsonManagedReference
    private List<GuardrailConfig> config = new ArrayList<>();


    @OneToMany(mappedBy = "guardrail", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ProjectSetting> projectSettings = new ArrayList<>();
     @Transient
    private Boolean attached = false;


// getters and setters
}
