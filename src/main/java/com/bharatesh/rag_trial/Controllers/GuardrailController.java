package com.bharatesh.rag_trial.Controllers;

import java.security.Guard;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.bharatesh.rag_trial.Models.Guardrail;
import com.bharatesh.rag_trial.Models.ProjectSetting;
import com.bharatesh.rag_trial.Repositories.GuardrailRepository;
import com.bharatesh.rag_trial.Repositories.ProjectSettingRepository;
import com.bharatesh.rag_trial.Services.GuardrailService;
import com.bharatesh.rag_trial.dto.GuardrailConfigRequest;
import com.bharatesh.rag_trial.dto.GuardrailConfigResponse;
import com.bharatesh.rag_trial.dto.GuardrailDetailResponse;
import com.bharatesh.rag_trial.dto.GuardrailResponse;
import com.bharatesh.rag_trial.dto.ProjectGuardrailResponse;

import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api/guardrails")
@Slf4j
public class GuardrailController {


private final GuardrailService service;
private final GuardrailRepository guardrailRepo;
private final ProjectSettingRepository projectSettingRepo;


public GuardrailController(GuardrailService service, GuardrailRepository guardrailRepo, ProjectSettingRepository projectSettingRepo) { 
    this.service = service; 
    this.guardrailRepo = guardrailRepo;
    this.projectSettingRepo = projectSettingRepo;
}

@GetMapping
public List<GuardrailResponse> all() {
    List<Guardrail> guardrails = service.getAll();

    return guardrails.stream()
            .map(this::toDto)
            .toList();
}

// Helper method to convert Guardrail entity to DTO
private GuardrailResponse toDto(Guardrail guardrail) {
    List<GuardrailConfigResponse> configResponses = guardrail.getConfig().stream()
            .map(cfg -> new GuardrailConfigResponse(
                    // cfg.getId(),
                    cfg.getEntity().getId(),
                    cfg.getEntity().getName() // optional
            ))
            .toList();

    return new GuardrailResponse(
            guardrail.getId(),
            guardrail.getName(),
            configResponses
    );
}

@GetMapping("/project-guardrails/{projectId}")
public List<ProjectGuardrailResponse> getProjectGuardrail(@PathVariable Long projectId) {

    List<Guardrail> all = guardrailRepo.findAll();
    List<ProjectSetting> settings = projectSettingRepo.findByProject_Id(projectId);

    Set<Long> attachedIds = settings.stream()
            .map(ps -> ps.getGuardrail().getId())
            .collect(Collectors.toSet());

    return all.stream()
            .map(g -> new ProjectGuardrailResponse(
                    g.getId(),
                    g.getName(),
                    attachedIds.contains(g.getId()),   // compute attached here
                    g.getConfig().stream()
                            .map(cfg -> new GuardrailConfigResponse(
                                    cfg.getEntity().getId(),
                                    cfg.getEntity().getName()
                            ))
                            .toList()
            ))
            .toList();
}


@GetMapping("/{id}")
public GuardrailDetailResponse get(@PathVariable Long id) { 
    return service.getDetailWithEntities(id); 
}

@PostMapping
public Guardrail create(@RequestBody Guardrail g) { 
    return service.create(g); }


@PostMapping("/{projectId}")
public Guardrail create(@RequestBody Guardrail g,@PathVariable Long projectId) { 
    return service.create(g, projectId); }


@PutMapping("/{id}")
public Guardrail update(@PathVariable Long id, @RequestBody Guardrail g) { return service.update(id, g); }


@DeleteMapping("/{id}")
public void delete(@PathVariable Long id) { service.delete(id); }

@DeleteMapping("/{id}/{projectId}")
public void detachFromProject(@PathVariable Long id, @PathVariable Long projectId) { 
    service.detachFromProject(id, projectId); 
}

@PostMapping("/{projectId}/{guardrailId}")
public void attachToProject(@PathVariable Long projectId, @PathVariable Long guardrailId) { 
    service.attachToProject(guardrailId, projectId); 
}

@PostMapping("/{id}/config")
public GuardrailResponse addConfig(
        @PathVariable Long id,
        @RequestBody GuardrailConfigRequest request
) {
    log.info("Assigning Registry Entities {} to Guardrail {}", request.getRegistryEntityIds(), request.toString());
    Guardrail guardrail = service.assignRegistryEntities(id, request.getRegistryEntityIds());

    // Map to DTO
    List<GuardrailConfigResponse> configResponses = guardrail.getConfig().stream()
            .map(cfg -> new GuardrailConfigResponse(
                    // cfg.getId(),
                    cfg.getEntity().getId(),
                    cfg.getEntity().getName() // optional
            ))
            .toList();

    return new GuardrailResponse(
            guardrail.getId(),
            guardrail.getName(),
            configResponses
    );
}
}