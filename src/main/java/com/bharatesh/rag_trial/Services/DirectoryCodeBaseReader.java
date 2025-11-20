package com.bharatesh.rag_trial.Services;

import org.springframework.ai.document.Document;

import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.*;


public class DirectoryCodeBaseReader {
    private final Path root;
    private final List<String> allowedExtensions = List.of(".js", ".jsx", ".ts", ".tsx", ".json", ".md", ".html", ".css","java");


    public DirectoryCodeBaseReader(Path root) {
        this.root = root;
    }


    public List<Document> read() throws Exception {
        List<Document> docs = new ArrayList<>();
        Files.walk(root)
                .filter(Files::isRegularFile)
                .filter(p -> allowedExtensions.stream().anyMatch(p.toString()::endsWith))
                .forEach(path -> {
                    try {
                        String content = Files.readString(path, StandardCharsets.UTF_8);
                        docs.add(new Document(content, Map.of("filename", path.toString())));
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                });
        return docs;
    }
}
