package com.bharatesh.rag_trial.Controllers;

import com.bharatesh.rag_trial.Models.Project;
import com.bharatesh.rag_trial.Services.ProjectService;
import com.bharatesh.rag_trial.dto.ProjectConfigResponse;
import com.bharatesh.rag_trial.dto.ProjectResponse;
import com.bharatesh.rag_trial.dto.ProjectSettingsConfigRequest;

import lombok.extern.slf4j.Slf4j;

import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/projects")
@Slf4j
public class ProjectController {

    private final ProjectService service;

    public ProjectController(ProjectService service) {
        this.service = service;
    }

    @GetMapping
    public List<ProjectResponse> getProjects(@RequestParam(required = false) String search) {
        List<Project> projects;
        if (search == null || search.isBlank()) {
            projects = service.getAll();
        } else {
            projects = service.search(search);

        }
        // log.info("Projects found: " + projects.toString());

        return projects.stream()
                .map(this::toDto)
                .toList();

    }

    // Helper method to convert Guardrail entity to DTO
    private ProjectResponse toDto(Project project) {
        List<ProjectConfigResponse> configResponses = project.getSettings().stream()
                .map(cfg -> new ProjectConfigResponse(
                        // cfg.getId(),
                        cfg.getGuardrail().getId(),
                        cfg.getGuardrail().getName() // optional
                ))
                .toList();

        return new ProjectResponse(
                project.getId(),
                project.getName(),
                project.getGitUrl(),
                project.getGitBranch(),
                project.getTeamName(),
                project.getManagerName(),
                configResponses);
    }

    @GetMapping("/{id}")
    public Project getById(@PathVariable Long id) {
        return service.getById(id);
    }

    @PostMapping
    public ProjectResponse create(@RequestBody Project project) {
        log.info("Creating project: " + project.toString());
        Project saved = service.create(project);
        return toDto(saved);
    }

    @PutMapping("/{id}")
    public Project update(@PathVariable Long id, @RequestBody Project project) {
        return service.update(id, project);
    }

    @DeleteMapping("/{id}")
    public String delete(@PathVariable Long id) {
        service.delete(id);
        return "Project deleted successfully";
    }

    @PostMapping("/{id}/config")
    public ProjectResponse addConfig(
            @PathVariable Long id,
            @RequestBody ProjectSettingsConfigRequest configKeys) {
        Project project = service.addConfig(id, configKeys.getGuardrailIds());

        List<ProjectConfigResponse> configResponses = project.getSettings().stream()
                .map(cfg -> new ProjectConfigResponse(
                        // cfg.getId(),
                        cfg.getGuardrail().getId(),
                        cfg.getGuardrail().getName() // optional
                ))
                .toList();

        return new ProjectResponse(project.getId(),
                project.getName(),
                project.getGitUrl(),
                project.getGitBranch(),
                project.getTeamName(),
                project.getManagerName(), configResponses);

    }

}
