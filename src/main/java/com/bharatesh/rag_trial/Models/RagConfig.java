package com.bharatesh.rag_trial.Models;

public class RagConfig {
    private String gitUrl;
    private String gitBranch;
    private boolean initialized;


    public RagConfig() {
    }


    public RagConfig(String gitUrl, String gitBranch, boolean initialized) {
        this.gitUrl = gitUrl;
        this.gitBranch = gitBranch;
        this.initialized = initialized;
    }


    public String getGitUrl() {
        return gitUrl;
    }

    public void setGitUrl(String gitUrl) {
        this.gitUrl = gitUrl;
    }


    public String getGitBranch() {
        return gitBranch;
    }

    public void setGitBranch(String gitBranch) {
        this.gitBranch = gitBranch;
    }


    public boolean isInitialized() {
        return initialized;
    }

    public void setInitialized(boolean initialized) {
        this.initialized = initialized;
    }
}
