package com.bharatesh.rag_trial.Services;

import jakarta.annotation.PostConstruct;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

@Service
public class VectorStoreResetService {

    private final JdbcTemplate jdbcTemplate;

    public VectorStoreResetService(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @PostConstruct
    public void resetVectorStore() {
        try {
            jdbcTemplate.execute("DROP TABLE IF EXISTS vectors;");
            jdbcTemplate.execute("DROP TABLE IF EXISTS documents;");
            // Recreate tables (or rely on initialize-schema: true)
            System.out.println("[DB] VectorStore tables dropped and will be recreated.");
        } catch (Exception e) {
            System.err.println("[DB] Error resetting vector tables: " + e.getMessage());
        }
    }
}

