package com.project.projectmanagment.repositories.project;


import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.project.projectmanagment.entities.bridges.ProjectUserBridge;

@Repository
public interface ProjectUserBridgeRepo extends JpaRepository<ProjectUserBridge, Long>{
    Optional<ProjectUserBridge> findByUserIdFKAndProjectIdFk(Long userId, Long ProjectId);

    List<ProjectUserBridge> findByProjectIdFk(Long projectId);
}
