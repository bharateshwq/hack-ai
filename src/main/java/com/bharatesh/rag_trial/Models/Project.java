package com.bharatesh.rag_trial.Models;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "project")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Project {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    @Column(name = "git_url")
    private String gitUrl;

    @Column(name = "git_branch")
    private String gitBranch;

    @Column(name = "team_name")
    private String teamName;

    @Column(name = "manager_name")
    private String managerName;

    @OneToMany(mappedBy = "project", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ProjectSetting> settings = new ArrayList<>();
    // Getters & Setters

}
