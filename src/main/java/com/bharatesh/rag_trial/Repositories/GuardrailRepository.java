package com.bharatesh.rag_trial.Repositories;

import com.bharatesh.rag_trial.Models.Guardrail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GuardrailRepository extends JpaRepository<Guardrail, Long> {
    Guardrail findByName(String name);
}
