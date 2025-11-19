package com.bharatesh.rag_trial.Services;

import com.bharatesh.rag_trial.Models.RagConfig;
import com.bharatesh.rag_trial.Models.RagConfigStore;
import org.eclipse.jgit.api.Git;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.ai.transformer.splitter.TextSplitter;
import org.springframework.ai.transformer.splitter.TokenTextSplitter;
import org.springframework.ai.vectorstore.VectorStore;

import java.nio.file.Files;
import java.nio.file.Path;

@Service
public class IngestionService {
    private static final Logger log = LoggerFactory.getLogger(IngestionService.class);
    private final VectorStore vectorStore;
    private final RagConfigStore configStore;


    public IngestionService(VectorStore vectorStore, RagConfigStore configStore) {
        this.vectorStore = vectorStore;
        this.configStore = configStore;
    }


    public void ingest(RagConfig config) throws Exception {
        log.info("[INGEST] Starting ingestion process for URL: {} and branch: {}", config.getGitUrl(), config.getGitBranch());


        Path cloneDir = Files.createTempDirectory("repo-clone-");
        log.info("[INGEST] Temporary directory created at: {}", cloneDir);


        log.info("[GIT] Cloning repository...");
        Git.cloneRepository()
                .setURI(config.getGitUrl())
                .setBranch(config.getGitBranch())
                .setDirectory(cloneDir.toFile())
                .call();
        log.info("[GIT] Clone completed successfully.");


        log.info("[READER] Reading codebase files...");
        DirectoryCodeBaseReader reader = new DirectoryCodeBaseReader(cloneDir);
        var docs = reader.read();
        log.info("[READER] {} documents loaded from repository.", docs.size());


        log.info("[CHUNK] Splitting documents into chunks...");
        TextSplitter splitter = new TokenTextSplitter(500, 350, 5, 10000, true);
        var chunks = splitter.apply(docs);
        log.info("[CHUNK] {} chunks generated.", chunks.size());


        log.info("[VECTOR] Storing chunks in vector store...");
        vectorStore.accept(chunks);
        log.info("[VECTOR] Vector store updated successfully.");


        log.info("[CONFIG] Updating configuration state...");
        config.setInitialized(true);
        configStore.saveConfig(config);
        log.info("[CONFIG] Configuration saved successfully.");
    }
}
