package com.bharatesh.rag_trial.Controllers;

import com.bharatesh.rag_trial.Services.IngestionService;
import com.bharatesh.rag_trial.Models.*;
import org.springframework.web.bind.annotation.*;

import java.util.Map;


@RestController
@RequestMapping("/api")
public class ConfigController {


    private final IngestionService ingestionService;
    private final RagConfigStore configStore;


    public ConfigController(IngestionService ingestionService, RagConfigStore configStore) {
        this.ingestionService = ingestionService;
        this.configStore = configStore;
    }


    @PostMapping("/update-config")
    public Map<String, Object> update(@RequestParam String git_url,
                                      @RequestParam String git_branch) throws Exception {
        RagConfig config = new RagConfig(git_url, git_branch, false);
        ingestionService.ingest(config);
        return Map.of("status", "config updated");
    }


    @GetMapping("/config-status")
    public Map<String, Object> status() {
        return Map.of("config_found", configStore.isConfigAvailable());
    }
}
