package com.bharatesh.rag_trial.Models;

import org.springframework.stereotype.Component;

@Component
public class RagConfigStore {
    private RagConfig config = null;


    public RagConfig getConfig() {
        return config;
    }

    public void saveConfig(RagConfig c) {
        this.config = c;
    }

    public boolean isConfigAvailable() {
        return config != null && config.isInitialized();
    }
}