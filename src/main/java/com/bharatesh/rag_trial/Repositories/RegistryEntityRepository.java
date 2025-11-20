package com.bharatesh.rag_trial.Repositories;

import com.bharatesh.rag_trial.Models.RegistryEntity;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RegistryEntityRepository extends JpaRepository<RegistryEntity, Long> {
    List<RegistryEntity> findByNameContainingIgnoreCase(String search);
    List<RegistryEntity> findByLabel(String label);

}

