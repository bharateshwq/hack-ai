package com.bharatesh.rag_trial.Services;

import java.util.List;

import org.springframework.stereotype.Service;

import com.bharatesh.rag_trial.Models.Guardrail;
import com.bharatesh.rag_trial.Models.GuardrailConfig;
import com.bharatesh.rag_trial.Models.RegistryEntity;
import com.bharatesh.rag_trial.Repositories.GuardrailRepository;
import com.bharatesh.rag_trial.Repositories.RegistryEntityRepository;

@Service
public class GuardrailService {

    private final GuardrailRepository repo;
    private final RegistryEntityRepository registryRepo;

    public GuardrailService(GuardrailRepository repo, RegistryEntityRepository registryRepo) {
        this.repo = repo;
        this.registryRepo = registryRepo;
    }

    public List<Guardrail> getAll() {
        return repo.findAll();
    }

    public Guardrail get(Long id) {
        return repo.findById(id).orElseThrow();
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
