package com.bharatesh.rag_trial.Services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import com.bharatesh.rag_trial.Config.PythonFilterProperties;
import com.bharatesh.rag_trial.Models.Guardrail;
import com.bharatesh.rag_trial.Models.ProjectSetting;
import com.bharatesh.rag_trial.Models.RegistryEntity;
import com.bharatesh.rag_trial.Repositories.ProjectSettingRepository;
import com.bharatesh.rag_trial.Repositories.RegistryEntityRepository;
import com.bharatesh.rag_trial.dto.ChatDTOs.AnalyzeFilterRequest;
import com.bharatesh.rag_trial.dto.ChatDTOs.FilteredResponse;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class ChatService {

    private final ProjectSettingRepository projectSettingRepository;
    private final RegistryEntityRepository registryRepo;
    private final WebClient webClient;
    private final PythonFilterProperties pythonFilterProperties;

    public ChatService(
            ProjectSettingRepository projectSettingRepository,
            RegistryEntityRepository registryRepo,
            WebClient webClient,
            PythonFilterProperties pythonFilterProperties) {
        this.projectSettingRepository = projectSettingRepository;
        this.registryRepo = registryRepo;
        this.webClient = webClient;
        this.pythonFilterProperties = pythonFilterProperties;
    }

    public List<String> getProjectEntityList(Long projectId) {
        List<ProjectSetting> settings = projectSettingRepository.findByProject_Id(projectId);

        if (settings.isEmpty()) {
            throw new RuntimeException("No guardrails configured for project " + projectId);
        }

        List<Guardrail> guardrails = settings.stream()
                .map(ProjectSetting::getGuardrail)
                .distinct()
                .toList();

        // STEP 2 — From guardrails, fetch all registry entities
        List<Long> entityIds = guardrails.stream()
                .flatMap(g -> g.getConfig().stream())
                .map(cfg -> cfg.getEntity().getId())
                .distinct()
                .toList();
        List<RegistryEntity> entities = registryRepo.findAllById(entityIds);

        List<String> entityNames = entities.stream()
                .map(RegistryEntity::getName)
                .distinct()
                .toList();
        return entityNames;
    }

    public FilteredResponse getSanitizedQuery(String query, List<String> entityNames) {
        String url = pythonFilterProperties.getBaseUrl() + "/analyze";

        AnalyzeFilterRequest request = new AnalyzeFilterRequest(
                query,
                "en",
                entityNames
        // List.of(customEntity)
        );

        log.info("Sending request to Python filter service: " + request);

        FilteredResponse filteredResponse = webClient
                .post()
                .uri(url)
                .bodyValue(request) // Spring automatically converts to JSON
                .retrieve()
                .bodyToMono(FilteredResponse.class)
                .block();
        return filteredResponse;

    }

    

}
