package com.bharatesh.rag_trial.Controllers;

import java.util.List;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.bharatesh.rag_trial.Models.Guardrail;
import com.bharatesh.rag_trial.Services.GuardrailService;
import com.bharatesh.rag_trial.dto.GuardrailConfigRequest;
import com.bharatesh.rag_trial.dto.GuardrailConfigResponse;
import com.bharatesh.rag_trial.dto.GuardrailResponse;

import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api/guardrails")
@Slf4j
public class GuardrailController {


private final GuardrailService service;


public GuardrailController(GuardrailService service) { this.service = service; }

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



@GetMapping("/{id}")
public Guardrail get(@PathVariable Long id) { return service.get(id); }


@PostMapping
public Guardrail create(@RequestBody Guardrail g) { 
    return service.create(g); }


@PutMapping("/{id}")
public Guardrail update(@PathVariable Long id, @RequestBody Guardrail g) { return service.update(id, g); }


@DeleteMapping("/{id}")
public void delete(@PathVariable Long id) { service.delete(id); }


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