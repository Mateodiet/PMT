package com.project.projectmanagment.repositories.task;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.project.projectmanagment.entities.bridges.TaskUserBridge;

import jakarta.transaction.Transactional;

@Repository
public interface TaskUserBridgeRepo extends JpaRepository<TaskUserBridge, Long>{
    List<TaskUserBridge> findByTaskIdFkIn(List<Long> taskIds);

    List<TaskUserBridge> findByUserIdFK(Long userIds);

    @Transactional
    void deleteByTaskIdFk(Long taskIdFk);

    Optional<TaskUserBridge> findByUserIdFKAndTaskIdFk(Long userIdFK, Long taskIdFk);

    Optional<TaskUserBridge> findByTaskIdFk(Long taskIds);
}
