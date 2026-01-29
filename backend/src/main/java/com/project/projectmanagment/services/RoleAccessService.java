package com.project.projectmanagment.services;

import java.util.Optional;

import org.springframework.stereotype.Service;

import com.project.projectmanagment.entities.bridges.ProjectUserBridge;
import com.project.projectmanagment.entities.project.ProjectEntity;
import com.project.projectmanagment.entities.user.UserEntity;
import com.project.projectmanagment.repositories.project.ProjectRepo;
import com.project.projectmanagment.repositories.project.ProjectUserBridgeRepo;
import com.project.projectmanagment.repositories.user.UserRepo;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class RoleAccessService {

    private final UserRepo userRepo;
    private final ProjectRepo projectRepo;
    private final ProjectUserBridgeRepo projectUserBridgeRepo;

    /**
     * Vérifie si l'utilisateur est ADMIN du projet
     */
    public boolean isProjectAdmin(String userEmail, String projectName) {
        return hasRole(userEmail, projectName, "ADMIN");
    }

    /**
     * Vérifie si l'utilisateur est ADMIN ou MEMBER du projet
     */
    public boolean canModify(String userEmail, String projectName) {
        Optional<UserEntity> user = userRepo.findByEmail(userEmail);
        if (!user.isPresent()) return false;

        Optional<ProjectEntity> project = projectRepo.findByProjectName(projectName);
        if (!project.isPresent()) return false;

        Optional<ProjectUserBridge> membership = projectUserBridgeRepo
            .findByUserIdFKAndProjectIdFk(user.get().getUserId(), project.get().getProjectId());

        if (!membership.isPresent()) return false;
        if (membership.get().getAcceptance() != 'a') return false;

        String role = membership.get().getProjectRole();
        return role.equals("ADMIN") || role.equals("MEMBER");
    }

    /**
     * Vérifie si l'utilisateur peut voir le projet (tous les rôles acceptés)
     */
    public boolean canView(String userEmail, String projectName) {
        Optional<UserEntity> user = userRepo.findByEmail(userEmail);
        if (!user.isPresent()) return false;

        Optional<ProjectEntity> project = projectRepo.findByProjectName(projectName);
        if (!project.isPresent()) return false;

        Optional<ProjectUserBridge> membership = projectUserBridgeRepo
            .findByUserIdFKAndProjectIdFk(user.get().getUserId(), project.get().getProjectId());

        return membership.isPresent() && membership.get().getAcceptance() == 'a';
    }

    /**
     * Vérifie si l'utilisateur a un rôle spécifique
     */
    public boolean hasRole(String userEmail, String projectName, String requiredRole) {
        Optional<UserEntity> user = userRepo.findByEmail(userEmail);
        if (!user.isPresent()) return false;

        Optional<ProjectEntity> project = projectRepo.findByProjectName(projectName);
        if (!project.isPresent()) return false;

        Optional<ProjectUserBridge> membership = projectUserBridgeRepo
            .findByUserIdFKAndProjectIdFk(user.get().getUserId(), project.get().getProjectId());

        if (!membership.isPresent()) return false;
        if (membership.get().getAcceptance() != 'a') return false;

        return membership.get().getProjectRole().equals(requiredRole);
    }

    /**
     * Récupère le rôle d'un utilisateur dans un projet
     */
    public String getUserRole(String userEmail, String projectName) {
        Optional<UserEntity> user = userRepo.findByEmail(userEmail);
        if (!user.isPresent()) return null;

        Optional<ProjectEntity> project = projectRepo.findByProjectName(projectName);
        if (!project.isPresent()) return null;

        Optional<ProjectUserBridge> membership = projectUserBridgeRepo
            .findByUserIdFKAndProjectIdFk(user.get().getUserId(), project.get().getProjectId());

        if (!membership.isPresent()) return null;

        return membership.get().getProjectRole();
    }
}