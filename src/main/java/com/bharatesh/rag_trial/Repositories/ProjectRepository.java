package com.bharatesh.rag_trial.Repositories;

import com.bharatesh.rag_trial.Models.Project;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface ProjectRepository extends JpaRepository<Project, Long> {
    List<Project> findByNameContainingIgnoreCaseOrGitUrlContainingIgnoreCaseOrTeamNameContainingIgnoreCaseOrManagerNameContainingIgnoreCase(
            String name,
            String gitUrl,
            String teamName,
            String managerName
    );
}