package com.bharatesh.rag_trial.Services;

import com.bharatesh.rag_trial.Models.Guardrail;
import com.bharatesh.rag_trial.Models.GuardrailConfig;
import com.bharatesh.rag_trial.Models.Project;
import com.bharatesh.rag_trial.Models.ProjectSetting;
import com.bharatesh.rag_trial.Models.RagConfig;
import com.bharatesh.rag_trial.Models.RegistryEntity;
import com.bharatesh.rag_trial.Repositories.GuardrailRepository;
import com.bharatesh.rag_trial.Repositories.ProjectRepository;
import com.bharatesh.rag_trial.Repositories.ProjectSettingRepository;

import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
public class ProjectService {

    private final ProjectRepository repo;
    private final GuardrailRepository guardrailRepository;
    private final ProjectSettingRepository projectSettingRepository;
    private final IngestionService ingestionService;

    public ProjectService(ProjectRepository repo, GuardrailRepository guardrailRepository,
            ProjectSettingRepository projectSettingRepository, IngestionService ingestionService) {
        this.repo = repo;
        this.guardrailRepository = guardrailRepository;
        this.projectSettingRepository = projectSettingRepository;
        this.ingestionService = ingestionService;
    }

    public List<Project> getAll() {
        return repo.findAll();
    }

    public Project getById(Long id) {
        return repo.findById(id)
                .orElseThrow(() -> new RuntimeException("Project not found"));
    }

    public Project create(Project project) {
        RagConfig config = new RagConfig(project.getGitUrl(), project.getGitBranch(), false);

        try {
            ingestionService.ingest(config);
        } catch (Exception e) {
            log.error("Ingestion failed for project: " + project.getName(), e);
            throw new RuntimeException("Ingestion failed");
            // TODO: handle exception
        }
        Project saved = repo.save(project);
        // 2. Fetch the default guardrail from DB
        Guardrail defaultGuardrail = guardrailRepository.findByName("DEFAULT_GLOBAL_GUARDRAIL");
        ProjectSetting defaultSetting = new ProjectSetting();
        defaultSetting.setGuardrail(defaultGuardrail);
        defaultSetting.setProject(project);

        projectSettingRepository.save(defaultSetting);

        saved.getSettings().add(defaultSetting);

        return saved;
    }

    public Project update(Long id, Project updated) {
        Project existing = getById(id);

        existing.setName(updated.getName());
        existing.setGitUrl(updated.getGitUrl());
        existing.setGitBranch(updated.getGitBranch());
        existing.setTeamName(updated.getTeamName());
        existing.setManagerName(updated.getManagerName());
        RagConfig config = new RagConfig(updated.getGitUrl(), updated.getGitBranch(), false);
        try {
            ingestionService.ingest(config);
        } catch (Exception e) {
            log.error("Ingestion failed for project: " + updated.getName(), e);
            throw new RuntimeException("Ingestion failed");
            // TODO: handle exception
        }

        return repo.save(existing);
    }

    public void delete(Long id) {
        repo.deleteById(id);
    }

    public List<Project> search(String search) {
        return repo
                .findByNameContainingIgnoreCaseOrGitUrlContainingIgnoreCaseOrTeamNameContainingIgnoreCaseOrManagerNameContainingIgnoreCase(
                        search, search, search, search);
    }

    public Project addConfig(Long projectId, List<Long> guardrailIds) {
        Project project = repo.findById(projectId)
                .orElseThrow(() -> new RuntimeException("Project not found"));
        List<Guardrail> guardrails = guardrailRepository.findAllById(guardrailIds);

        project.getSettings().clear();

        for (Guardrail re : guardrails) {
            ProjectSetting cfg = new ProjectSetting();
            cfg.setGuardrail(re);
            cfg.setProject(project);

            project.getSettings().add(cfg);
        }

        return repo.save(project);

    }
}
