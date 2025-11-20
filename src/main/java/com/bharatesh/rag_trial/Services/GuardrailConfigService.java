package com.bharatesh.rag_trial.Services;

import java.util.List;

import org.springframework.stereotype.Service;

import com.bharatesh.rag_trial.Models.Guardrail;
import com.bharatesh.rag_trial.Models.GuardrailConfig;
import com.bharatesh.rag_trial.Models.RegistryEntity;
import com.bharatesh.rag_trial.Repositories.GuardrailConfigRepository;

@Service
public class GuardrailConfigService {


private final GuardrailConfigRepository repo;


public GuardrailConfigService(GuardrailConfigRepository repo) { this.repo = repo; }


public List<GuardrailConfig> getAll() { return repo.findAll(); }
public GuardrailConfig get(Long id) { return repo.findById(id).orElseThrow(); }
public GuardrailConfig create(GuardrailConfig c) { return repo.save(c); }
public GuardrailConfig update(Long id, GuardrailConfig c) {
GuardrailConfig ex = get(id);
ex.setEntity(c.getEntity());
ex.setGuardrail(c.getGuardrail());
return repo.save(ex);
}
public void delete(Long id) { repo.deleteById(id); }


}