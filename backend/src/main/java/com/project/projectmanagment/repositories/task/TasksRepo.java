package com.project.projectmanagment.repositories.task;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.project.projectmanagment.entities.task.ProjectTask;

@Repository
public interface TasksRepo extends JpaRepository<ProjectTask, Long>{
    List<ProjectTask> findByProjectIdFk(Long projectId);

    List<ProjectTask> findByTaskStatus(String taskStatus);

    List<ProjectTask> findByTaskIdIn(List<Long> taskIds);

    Optional<ProjectTask> findByTaskName(String taskName);

    long countByTaskStatus(String taskStatus);
}
