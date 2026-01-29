package com.project.projectmanagment.services;

import java.sql.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.project.projectmanagment.entities.bridges.ProjectUserBridge;
import com.project.projectmanagment.entities.bridges.TaskUserBridge;
import com.project.projectmanagment.entities.project.ProjectEntity;
import com.project.projectmanagment.entities.task.ProjectTask;
import com.project.projectmanagment.entities.user.UserEntity;
import com.project.projectmanagment.models.project.ProjectModel;
import com.project.projectmanagment.models.response.BaseResponse;
import com.project.projectmanagment.repositories.project.ProjectRepo;
import com.project.projectmanagment.repositories.project.ProjectUserBridgeRepo;
import com.project.projectmanagment.repositories.task.TaskUserBridgeRepo;
import com.project.projectmanagment.repositories.task.TasksRepo;
import com.project.projectmanagment.repositories.user.UserRepo;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@AllArgsConstructor
@Slf4j
public class ProjectService {
    private final ProjectRepo projectRepo;
    private final UserRepo userRepo;
    private final ProjectUserBridgeRepo projectUserBridgeRepo;
    private final TaskUserBridgeRepo taskUserBridgeRepo;
    private final TasksRepo tasksRepo;
    private final EmailService emailService;

    public BaseResponse createProject(ProjectModel projectRequest) {
        Optional<ProjectEntity> projectDbData = projectRepo.findByProjectName(projectRequest.getProjectName());
        if (projectDbData.isPresent()) {
            return BaseResponse.builder()
                .responseCode(HttpStatus.CONFLICT)
                .responseDesc("Project Already Exists").build();
        }

        Optional<UserEntity> userDbData = userRepo.findByEmail(projectRequest.getCreatorEmail());
        if (!userDbData.isPresent()) {
            return BaseResponse.builder()
                .responseCode(HttpStatus.NOT_FOUND)
                .responseDesc("User Does not Exists").build();
        }

        ProjectEntity projectSaved = projectRepo.save(
            ProjectEntity.builder()
                .projectName(projectRequest.getProjectName())
                .projectDescription(projectRequest.getProjectDescription())
                .projectStartDate(projectRequest.getProjectStartDate())
                .projectStatus(projectRequest.getProjectStatus())
                .taskCreatedBy(userDbData.get().getUserId())
                .build()
        );

        if (projectSaved == null) {
            return BaseResponse.builder()
                .responseCode(HttpStatus.INTERNAL_SERVER_ERROR)
                .responseDesc("Unable to Save Data").build();
        }

        // Ajouter le créateur comme ADMIN du projet
        projectUserBridgeRepo.save(
            ProjectUserBridge.builder()
                .userIdFK(userDbData.get().getUserId())
                .projectIdFk(projectSaved.getProjectId())
                .projectRole("ADMIN")
                .acceptance('a')
                .build()
        );

        log.info("Project '{}' created by {}", projectRequest.getProjectName(), projectRequest.getCreatorEmail());

        return BaseResponse.builder()
            .responseCode(HttpStatus.OK)
            .responseDesc("Success")
            .data(projectSaved)
            .build();
    }

    public BaseResponse invite(String email, String projectName) {
        return inviteWithRole(email, projectName, "MEMBER", null);
    }

    /**
     * Invite un utilisateur à rejoindre un projet avec un rôle spécifique
     */
    public BaseResponse inviteWithRole(String email, String projectName, String role, String invitedByEmail) {
        Optional<UserEntity> userDbData = userRepo.findByEmail(email);

        if (!userDbData.isPresent()) {
            return BaseResponse.builder()
                .responseCode(HttpStatus.NOT_FOUND)
                .responseDesc("User Does not Exists. Please ask them to register first.").build();
        }

        Optional<ProjectEntity> projectDbdata = projectRepo.findByProjectName(projectName);

        if (!projectDbdata.isPresent()) {
            return BaseResponse.builder()
                .responseCode(HttpStatus.NOT_FOUND)
                .responseDesc("Project Does not Exists").build();
        }

        Optional<ProjectUserBridge> projectInviteDbData = projectUserBridgeRepo.findByUserIdFKAndProjectIdFk(
            userDbData.get().getUserId(),
            projectDbdata.get().getProjectId()
        );

        if (projectInviteDbData.isPresent()) {
            return BaseResponse.builder()
                .responseCode(HttpStatus.CONFLICT)
                .responseDesc("User already invited or member of this project").build();
        }

        // Valider le rôle
        String validRole = validateRole(role);

        ProjectUserBridge savedBridge = projectUserBridgeRepo.save(
            ProjectUserBridge.builder()
                .userIdFK(userDbData.get().getUserId())
                .projectIdFk(projectDbdata.get().getProjectId())
                .projectRole(validRole)
                .acceptance('p') // pending
                .build()
        );

        // Générer le lien d'invitation
        String inviteLink = "http://localhost/api/project/projectInviteAccept/" + email + "/" + projectName;

        // Envoyer l'email d'invitation
        String inviterName = invitedByEmail != null ? invitedByEmail : "An administrator";
        emailService.sendProjectInvitation(email, projectName, inviterName, inviteLink);

        log.info("Invitation sent to {} for project '{}' with role {}", email, projectName, validRole);

        Map<String, Object> response = new HashMap<>();
        response.put("inviteLink", inviteLink);
        response.put("role", validRole);
        response.put("invitedEmail", email);
        response.put("status", "pending");

        return BaseResponse.builder()
            .responseCode(HttpStatus.OK)
            .responseDesc("Invitation sent successfully")
            .data(response)
            .build();
    }

    public BaseResponse acceptInvitation(String email, String projectName) {
        Optional<UserEntity> userDbData = userRepo.findByEmail(email);

        if (!userDbData.isPresent()) {
            return BaseResponse.builder()
                .responseCode(HttpStatus.NOT_FOUND)
                .responseDesc("User Does not Exists").build();
        }

        Optional<ProjectEntity> projectDbdata = projectRepo.findByProjectName(projectName);

        if (!projectDbdata.isPresent()) {
            return BaseResponse.builder()
                .responseCode(HttpStatus.NOT_FOUND)
                .responseDesc("Project Does not Exists").build();
        }

        Optional<ProjectUserBridge> projectInviteDbData = projectUserBridgeRepo.findByUserIdFKAndProjectIdFk(
            userDbData.get().getUserId(),
            projectDbdata.get().getProjectId()
        );

        if (!projectInviteDbData.isPresent()) {
            return BaseResponse.builder()
                .responseCode(HttpStatus.NOT_FOUND)
                .responseDesc("You were not invited to this project").build();
        }

        ProjectUserBridge projectInviteData = projectInviteDbData.get();
        if (projectInviteData.getAcceptance() == 'a') {
            return BaseResponse.builder()
                .responseCode(HttpStatus.CONFLICT)
                .responseDesc("You have already accepted the invite to project " + projectName).build();
        }

        projectInviteData.setAcceptance('a');
        projectUserBridgeRepo.save(projectInviteData);

        log.info("User {} accepted invitation to project '{}'", email, projectName);

        return BaseResponse.builder()
            .responseCode(HttpStatus.OK)
            .responseDesc("Invitation accepted successfully")
            .build();
    }

    /**
     * Met à jour le rôle d'un membre dans un projet
     */
    public BaseResponse updateMemberRole(String email, String projectName, String newRole) {
        Optional<UserEntity> userDbData = userRepo.findByEmail(email);

        if (!userDbData.isPresent()) {
            return BaseResponse.builder()
                .responseCode(HttpStatus.NOT_FOUND)
                .responseDesc("User Does not Exists").build();
        }

        Optional<ProjectEntity> projectDbdata = projectRepo.findByProjectName(projectName);

        if (!projectDbdata.isPresent()) {
            return BaseResponse.builder()
                .responseCode(HttpStatus.NOT_FOUND)
                .responseDesc("Project Does not Exists").build();
        }

        Optional<ProjectUserBridge> projectMemberData = projectUserBridgeRepo.findByUserIdFKAndProjectIdFk(
            userDbData.get().getUserId(),
            projectDbdata.get().getProjectId()
        );

        if (!projectMemberData.isPresent()) {
            return BaseResponse.builder()
                .responseCode(HttpStatus.NOT_FOUND)
                .responseDesc("User is not a member of this project").build();
        }

        ProjectUserBridge memberData = projectMemberData.get();
        String validRole = validateRole(newRole);
        memberData.setProjectRole(validRole);
        projectUserBridgeRepo.save(memberData);

        log.info("Role updated for {} in project '{}' to {}", email, projectName, validRole);

        Map<String, String> response = new HashMap<>();
        response.put("email", email);
        response.put("newRole", validRole);

        return BaseResponse.builder()
            .responseCode(HttpStatus.OK)
            .responseDesc("Role updated successfully")
            .data(response)
            .build();
    }

    /**
     * Supprime un membre d'un projet
     */
    public BaseResponse removeMember(String email, String projectName) {
        Optional<UserEntity> userDbData = userRepo.findByEmail(email);

        if (!userDbData.isPresent()) {
            return BaseResponse.builder()
                .responseCode(HttpStatus.NOT_FOUND)
                .responseDesc("User Does not Exists").build();
        }

        Optional<ProjectEntity> projectDbdata = projectRepo.findByProjectName(projectName);

        if (!projectDbdata.isPresent()) {
            return BaseResponse.builder()
                .responseCode(HttpStatus.NOT_FOUND)
                .responseDesc("Project Does not Exists").build();
        }

        Optional<ProjectUserBridge> projectMemberData = projectUserBridgeRepo.findByUserIdFKAndProjectIdFk(
            userDbData.get().getUserId(),
            projectDbdata.get().getProjectId()
        );

        if (!projectMemberData.isPresent()) {
            return BaseResponse.builder()
                .responseCode(HttpStatus.NOT_FOUND)
                .responseDesc("User is not a member of this project").build();
        }

        projectUserBridgeRepo.delete(projectMemberData.get());

        log.info("Member {} removed from project '{}'", email, projectName);

        return BaseResponse.builder()
            .responseCode(HttpStatus.OK)
            .responseDesc("Member removed successfully")
            .build();
    }

    /**
     * Récupère les membres d'un projet avec leurs rôles
     */
    public BaseResponse getProjectMembers(String projectName) {
        Optional<ProjectEntity> projectDbdata = projectRepo.findByProjectName(projectName);

        if (!projectDbdata.isPresent()) {
            return BaseResponse.builder()
                .responseCode(HttpStatus.NOT_FOUND)
                .responseDesc("Project Does not Exists").build();
        }

        List<ProjectUserBridge> members = projectUserBridgeRepo.findByProjectIdFk(projectDbdata.get().getProjectId());

        if (members.isEmpty()) {
            return BaseResponse.builder()
                .responseCode(HttpStatus.OK)
                .responseDesc("No members found")
                .data(List.of())
                .build();
        }

        List<Long> userIds = members.stream()
            .map(ProjectUserBridge::getUserIdFK)
            .collect(Collectors.toList());

        List<UserEntity> users = userRepo.findByUserIdIn(userIds);

                List<Map<String, Object>> memberList = members.stream().map(member -> {
            Map<String, Object> memberInfo = new HashMap<>();
            Optional<UserEntity> user = users.stream()
                .filter(u -> u.getUserId().equals(member.getUserIdFK()))
                .findFirst();

            if (user.isPresent()) {
                memberInfo.put("userId", user.get().getUserId());
                memberInfo.put("email", user.get().getEmail());
                memberInfo.put("name", user.get().getName());
                memberInfo.put("role", member.getProjectRole());
                memberInfo.put("status", member.getAcceptance() == 'a' ? "accepted" : "pending");
                memberInfo.put("joinedAt", member.getAssignmentDate());
            }
            return memberInfo;
        }).filter(m -> !m.isEmpty()).collect(Collectors.toList());

        return BaseResponse.builder()
            .responseCode(HttpStatus.OK)
            .responseDesc("success")
            .data(memberList)
            .build();
    }

    public BaseResponse updateProjectServiceMethod(String projectName, ProjectModel projectUpdateRequest) {
        Optional<ProjectEntity> projectDbdata = projectRepo.findByProjectName(projectName);

        if (!projectDbdata.isPresent()) {
            return BaseResponse.builder()
                .responseCode(HttpStatus.NOT_FOUND)
                .responseDesc("Project Does not Exists").build();
        }

        ProjectEntity projectData = projectDbdata.get();

        if (projectUpdateRequest.getProjectStatus() != null && !projectUpdateRequest.getProjectStatus().equals(projectData.getProjectStatus())) {
            projectData.setProjectStatus(projectUpdateRequest.getProjectStatus());
            projectData.setProjectStatusUpdatedDate(new Date(System.currentTimeMillis()));
        }

        if (projectUpdateRequest.getProjectName() != null && !projectUpdateRequest.getProjectName().isEmpty()) {
            projectData.setProjectName(projectUpdateRequest.getProjectName());
        }

        if (projectUpdateRequest.getProjectDescription() != null && !projectUpdateRequest.getProjectDescription().isEmpty()) {
            projectData.setProjectDescription(projectUpdateRequest.getProjectDescription());
        }

        if (projectUpdateRequest.getProjectStartDate() != null) {
            projectData.setProjectStartDate(projectUpdateRequest.getProjectStartDate());
        }

        projectRepo.save(projectData);

        return BaseResponse.builder()
            .responseCode(HttpStatus.OK)
            .responseDesc("success")
            .data(projectData)
            .build();
    }

    public BaseResponse getDeleteProjectServiceMethod(String projectName) {
        Optional<ProjectEntity> projectDbdata = projectRepo.findByProjectName(projectName);

        if (!projectDbdata.isPresent()) {
            return BaseResponse.builder()
                .responseCode(HttpStatus.NOT_FOUND)
                .responseDesc("Project Does not Exists").build();
        }

        List<ProjectTask> projectTasksList = tasksRepo.findByProjectIdFk(projectDbdata.get().getProjectId());

        if (!projectTasksList.isEmpty()) {
            List<TaskUserBridge> tasUserList = taskUserBridgeRepo.findByTaskIdFkIn(
                projectTasksList.stream()
                    .map(ProjectTask::getTaskId)
                    .collect(Collectors.toList())
            );
            if (!tasUserList.isEmpty()) {
                taskUserBridgeRepo.deleteAll(tasUserList);
            }
            tasksRepo.deleteAll(projectTasksList);
        }

        List<ProjectUserBridge> projectMembers = projectUserBridgeRepo.findByProjectIdFk(projectDbdata.get().getProjectId());
        if (!projectMembers.isEmpty()) {
            projectUserBridgeRepo.deleteAll(projectMembers);
        }

        projectRepo.delete(projectDbdata.get());

        log.info("Project '{}' deleted", projectName);

        return BaseResponse.builder()
            .responseCode(HttpStatus.OK)
            .responseDesc("success")
            .build();
    }

    public BaseResponse getAllProjects() {
        return BaseResponse.builder()
            .responseCode(HttpStatus.OK)
            .responseDesc("success")
            .data(projectRepo.findAll())
            .build();
    }

    public BaseResponse getProject(String projectName) {
        Optional<ProjectEntity> projectDbdata = projectRepo.findByProjectName(projectName);

        if (!projectDbdata.isPresent()) {
            return BaseResponse.builder()
                .responseCode(HttpStatus.NOT_FOUND)
                .responseDesc("Project Does not Exists").build();
        }

        return BaseResponse.builder()
            .responseCode(HttpStatus.OK)
            .responseDesc("success")
            .data(projectDbdata.get())
            .build();
    }

    private String validateRole(String role) {
        if (role == null || role.isEmpty()) {
            return "MEMBER";
        }
        String upperRole = role.toUpperCase();
        if (upperRole.equals("ADMIN") || upperRole.equals("MEMBER") || upperRole.equals("OBSERVER")) {
            return upperRole;
        }
        return "MEMBER";
    }
}