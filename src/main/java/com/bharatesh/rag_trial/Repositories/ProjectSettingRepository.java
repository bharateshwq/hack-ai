package com.bharatesh.rag_trial.Repositories;

import com.bharatesh.rag_trial.Models.ProjectSetting;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProjectSettingRepository extends JpaRepository<ProjectSetting, Long> {
    List<ProjectSetting> findByProject_Id(Long projectId);
    void deleteByGuardrail_IdAndProject_Id(Long guardrailId, Long projectId);
}

