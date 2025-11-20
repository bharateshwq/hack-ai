package com.bharatesh.rag_trial.Services;

import java.util.List;

import org.springframework.stereotype.Service;

import com.bharatesh.rag_trial.Models.ProjectSetting;
import com.bharatesh.rag_trial.Repositories.ProjectSettingRepository;

@Service
public class ProjectSettingService {


private final ProjectSettingRepository repo;


public ProjectSettingService(ProjectSettingRepository repo) { this.repo = repo; }


public List<ProjectSetting> getAll() { return repo.findAll(); }
public ProjectSetting get(Long id) { return repo.findById(id).orElseThrow(); }
public ProjectSetting create(ProjectSetting ps) { return repo.save(ps); }
public ProjectSetting update(Long id, ProjectSetting ps) {
ProjectSetting ex = get(id);
ex.setProject(ps.getProject());
ex.setGuardrail(ps.getGuardrail());
return repo.save(ex);
}
public void delete(Long id) { repo.deleteById(id); }
}
