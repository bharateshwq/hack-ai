package com.bharatesh.rag_trial.Repositories;


import com.bharatesh.rag_trial.Models.Guardrail;
import com.bharatesh.rag_trial.Models.RegistryEntity;
import com.bharatesh.rag_trial.Models.GuardrailConfig;
import lombok.extern.slf4j.Slf4j;

import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import jakarta.annotation.PostConstruct;

import java.util.List;

@Component
@DependsOn("registryEntityLoaderRepository")
@Slf4j
public class DefaultGuardrailLoader {

    private final GuardrailRepository guardrailRepo;
    private final RegistryEntityRepository registryRepo;
    private final GuardrailConfigRepository configRepo;

    public DefaultGuardrailLoader(
            GuardrailRepository guardrailRepo,
            RegistryEntityRepository registryRepo,
            GuardrailConfigRepository configRepo
    ) {
        this.guardrailRepo = guardrailRepo;
        this.registryRepo = registryRepo;
        this.configRepo = configRepo;
    }

    @PostConstruct
     @Transactional
    public void createDefaultGuardrail() {

        // Skip if already created
        if (guardrailRepo.count() > 0) {
            log.info("Guardrail already exists. Skipping default creation.");
            return;
        }

        log.info("Creating default guardrail...");

        Guardrail guardrail = new Guardrail();
        guardrail.setName("DEFAULT_GLOBAL_GUARDRAIL");
        // guardrail.setDescription("Contains all GLOBAL PII entities");

        guardrail = guardrailRepo.save(guardrail);

        // Fetch all GLOBAL entities
        List<RegistryEntity> globalEntities = registryRepo.findByLabel("GLOBAL");
        // log.info("GLoable Entities" +globalEntities.toString());

        for (RegistryEntity entity : globalEntities) {
            GuardrailConfig cfg = new GuardrailConfig();
            cfg.setGuardrail(guardrail);
            cfg.setEntity(entity);
            configRepo.save(cfg);
        }

        // log.info("Default Guardrail created with {} GLOBAL entities.",
        //         globalEntities.size());
    }
}

