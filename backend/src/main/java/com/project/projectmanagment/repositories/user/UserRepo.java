package com.project.projectmanagment.repositories.user;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.project.projectmanagment.entities.user.UserEntity;

public interface UserRepo extends JpaRepository<UserEntity, Long>{
    public Optional<UserEntity> findByEmail(String email);

    public List<UserEntity> findByUserIdIn(List<Long> userIds);
}
