package com.bharatesh.rag_trial.Services;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.bharatesh.rag_trial.Models.Guardrail;
import com.bharatesh.rag_trial.Models.GuardrailConfig;
import com.bharatesh.rag_trial.Models.Project;
import com.bharatesh.rag_trial.Models.ProjectSetting;
import com.bharatesh.rag_trial.Models.RegistryEntity;
import com.bharatesh.rag_trial.Repositories.GuardrailRepository;
import com.bharatesh.rag_trial.Repositories.ProjectRepository;
import com.bharatesh.rag_trial.Repositories.ProjectSettingRepository;
import com.bharatesh.rag_trial.Repositories.RegistryEntityRepository;
import com.bharatesh.rag_trial.dto.GuardrailDetailResponse;
import com.bharatesh.rag_trial.dto.GuardrailDetailResponse.EntityWithEnabledStatus;

@Service
public class GuardrailService {

    private final GuardrailRepository repo;
    private final RegistryEntityRepository registryRepo;
    private final ProjectSettingRepository projectSettingRepo;
    private final ProjectRepository projectRepo;

    public GuardrailService(GuardrailRepository repo, RegistryEntityRepository registryRepo,
            ProjectSettingRepository projectSettingRepo, ProjectRepository projectRepo) {
        this.repo = repo;
        this.registryRepo = registryRepo;
        this.projectSettingRepo = projectSettingRepo;
        this.projectRepo = projectRepo;
    }

    public List<Guardrail> getAll() {
        return repo.findAll();
    }

    public Guardrail get(Long id) {
        return repo.findById(id).orElseThrow();
    }

    public GuardrailDetailResponse getDetailWithEntities(Long id) {
        Guardrail guardrail = repo.findById(id).orElseThrow();
        List<RegistryEntity> allEntities = registryRepo.findAll();
        
        // Get enabled entity IDs for this guardrail
        Set<Long> enabledEntityIds = guardrail.getConfig().stream()
                .map(cfg -> cfg.getEntity().getId())
                .collect(Collectors.toSet());
        
        // Map all entities with enabled status
        List<EntityWithEnabledStatus> entities = allEntities.stream()
                .map(entity -> new EntityWithEnabledStatus(
                        entity.getId(),
                        entity.getName(),
                        enabledEntityIds.contains(entity.getId())
                ))
                .toList();
        
        return new GuardrailDetailResponse(
                guardrail.getId(),
                guardrail.getName(),
                entities
        );
    }

    public Guardrail create(Guardrail g, Long projectId) {
        Guardrail saved = repo.save(g);
        Project project = projectRepo.findById(projectId)
                .orElseThrow(() -> new RuntimeException("Project not found"));

        // 3. Create ProjectSetting
        ProjectSetting ps = new ProjectSetting();
        ps.setProject(project);
        ps.setGuardrail(saved);

        // 4. Save ProjectSetting
        projectSettingRepo.save(ps);

        return repo.save(g);
    }

     public Guardrail create(Guardrail g) {
       

        return repo.save(g);
    }

    public Guardrail update(Long id, Guardrail g) {
        Guardrail ex = get(id);
        ex.setName(g.getName());
        return repo.save(ex);
    }

    public void delete(Long id) {
        repo.deleteById(id);
    }

    @Transactional
    public void detachFromProject(Long guardrailId, Long projectId) {
        projectRepo.findById(projectId)
                .orElseThrow(() -> new RuntimeException("Project not found"));
        projectSettingRepo.deleteByGuardrail_IdAndProject_Id(guardrailId, projectId);
    }

    @Transactional
    public void attachToProject(Long guardrailId, Long projectId) {
        Guardrail guardrail = repo.findById(guardrailId)
                .orElseThrow(() -> new RuntimeException("Guardrail not found"));
        Project project = projectRepo.findById(projectId)
                .orElseThrow(() -> new RuntimeException("Project not found"));

        // Check if already attached
        boolean alreadyAttached = projectSettingRepo.findByProject_Id(projectId).stream()
                .anyMatch(ps -> ps.getGuardrail().getId().equals(guardrailId));
        
        if (!alreadyAttached) {
            ProjectSetting ps = new ProjectSetting();
            ps.setProject(project);
            ps.setGuardrail(guardrail);
            projectSettingRepo.save(ps);
        }
    }

    public Guardrail assignRegistryEntities(Long guardrailId, List<Long> entityIds) {
        Guardrail g = repo.findById(guardrailId)
                .orElseThrow(() -> new RuntimeException("Guardrail not found"));
        List<RegistryEntity> entities = registryRepo.findAllById(entityIds);
        g.getConfig().clear();

        for (RegistryEntity re : entities) {
            GuardrailConfig cfg = new GuardrailConfig();
            cfg.setGuardrail(g);
            cfg.setEntity(re);

            g.getConfig().add(cfg);
        }

        return repo.save(g);

    }

}
