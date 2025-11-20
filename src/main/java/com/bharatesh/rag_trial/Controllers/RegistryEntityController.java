package com.bharatesh.rag_trial.Controllers;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.bharatesh.rag_trial.Models.Guardrail;
import com.bharatesh.rag_trial.Models.RegistryEntity;
import com.bharatesh.rag_trial.Services.RegistryEntityService;
import com.bharatesh.rag_trial.dto.GuardrailConfigRequest;
import com.bharatesh.rag_trial.dto.RegistryEntityDTO.RegistryEntityResponse;

import java.util.List;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;


@RestController
@RequestMapping("/api/registry-entities")
public class RegistryEntityController {


private final RegistryEntityService service;


public RegistryEntityController(RegistryEntityService service) { this.service = service; }



@GetMapping
public List<RegistryEntityResponse> all(@RequestParam(required = false) String search) {
    return service.getAll(search).stream()
            .map(this::toDto)
            .toList();
}

// Helper method to map entity to DTO
private RegistryEntityResponse toDto(RegistryEntity entity) {
    return new RegistryEntityResponse(
            entity.getId(),
            entity.getName(),
            entity.isDefaultEnabled(),
            entity.getLabel(),
            entity.getMaskingType()
    );
}


@GetMapping("/{id}")
public RegistryEntity get(@PathVariable Long id) { return service.get(id); }


@PostMapping
public RegistryEntity create(@RequestBody RegistryEntity e) { return service.create(e); }


@PutMapping("/{id}")
public RegistryEntity update(@PathVariable Long id, @RequestBody RegistryEntity e) { return service.update(id, e); }


@DeleteMapping("/{id}")
public void delete(@PathVariable Long id) { service.delete(id); }

}
