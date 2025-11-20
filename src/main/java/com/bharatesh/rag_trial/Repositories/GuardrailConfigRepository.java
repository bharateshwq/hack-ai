package com.bharatesh.rag_trial.Repositories;

import com.bharatesh.rag_trial.Models.GuardrailConfig;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GuardrailConfigRepository extends JpaRepository<GuardrailConfig, Long> {}

