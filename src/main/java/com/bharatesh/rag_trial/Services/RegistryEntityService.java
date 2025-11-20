package com.bharatesh.rag_trial.Services;

import java.util.List;

import org.springframework.stereotype.Service;

import com.bharatesh.rag_trial.Models.Guardrail;
import com.bharatesh.rag_trial.Models.GuardrailConfig;
import com.bharatesh.rag_trial.Models.RegistryEntity;
import com.bharatesh.rag_trial.Repositories.RegistryEntityRepository;

@Service
public class RegistryEntityService {


private final RegistryEntityRepository repo;


public RegistryEntityService(RegistryEntityRepository repo) { this.repo = repo; }


public List<RegistryEntity> getAll(String search) {
    if (search == null || search.isBlank()) {
        return repo.findAll();
    }
    return repo.findByNameContainingIgnoreCase(search);
}
public RegistryEntity get(Long id) { return repo.findById(id).orElseThrow(); }
public RegistryEntity create(RegistryEntity e) { return repo.save(e); }
public RegistryEntity update(Long id, RegistryEntity e) {
RegistryEntity ex = get(id);
ex.setName(e.getName());
return repo.save(ex);
}
public void delete(Long id) { repo.deleteById(id); }




}
