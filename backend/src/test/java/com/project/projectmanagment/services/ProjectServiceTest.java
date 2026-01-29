package com.project.projectmanagment.services;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.util.Optional;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;

import com.project.projectmanagment.entities.bridges.ProjectUserBridge;
import com.project.projectmanagment.entities.project.ProjectEntity;
import com.project.projectmanagment.entities.user.UserEntity;
import com.project.projectmanagment.models.project.ProjectModel;
import com.project.projectmanagment.models.response.BaseResponse;
import com.project.projectmanagment.repositories.project.ProjectRepo;
import com.project.projectmanagment.repositories.project.ProjectUserBridgeRepo;
import com.project.projectmanagment.repositories.task.TaskUserBridgeRepo;
import com.project.projectmanagment.repositories.task.TasksRepo;
import com.project.projectmanagment.repositories.user.UserRepo;

@ExtendWith(MockitoExtension.class)
public class ProjectServiceTest {

    @Mock
    private ProjectRepo projectRepo;

    @Mock
    private UserRepo userRepo;

    @Mock
    private ProjectUserBridgeRepo projectUserBridgeRepo;

    @Mock
    private TaskUserBridgeRepo taskUserBridgeRepo;

    @Mock
    private TasksRepo tasksRepo;

    @Mock
    private EmailService emailService;

    @InjectMocks
    private ProjectService projectService;

    private UserEntity testUser;
    private ProjectEntity testProject;
    private ProjectUserBridge testBridge;

    @BeforeEach
    void setUp() {
        testUser = UserEntity.builder()
                .userId(1L)
                .email("test@test.com")
                .name("Test User")
                .password("password123")
                .build();

        testProject = ProjectEntity.builder()
                .projectId(1L)
                .projectName("TestProject")
                .projectDescription("Test Description")
                .projectStatus("ACTIVE")
                .taskCreatedBy(1L)
                .build();

        testBridge = ProjectUserBridge.builder()
                .projUserId(1L)
                .userIdFK(1L)
                .projectIdFk(1L)
                .projectRole("ADMIN")
                .acceptance('a')
                .build();
    }

    // ==================== CREATE PROJECT TESTS ====================

    @Test
    void createProject_Success() {
        ProjectModel model = new ProjectModel();
        model.setProjectName("NewProject");
        model.setProjectDescription("Description");
        model.setCreatorEmail("test@test.com");
        model.setProjectStatus("ACTIVE");

        when(projectRepo.findByProjectName("NewProject")).thenReturn(Optional.empty());
        when(userRepo.findByEmail("test@test.com")).thenReturn(Optional.of(testUser));
        when(projectRepo.save(any(ProjectEntity.class))).thenReturn(testProject);
        when(projectUserBridgeRepo.save(any(ProjectUserBridge.class))).thenReturn(testBridge);

        BaseResponse response = projectService.createProject(model);

        assertEquals(HttpStatus.OK, response.getResponseCode());
        assertEquals("Success", response.getResponseDesc());
    }

    @Test
    void createProject_ProjectAlreadyExists() {
        ProjectModel model = new ProjectModel();
        model.setProjectName("TestProject");
        model.setCreatorEmail("test@test.com");

        when(projectRepo.findByProjectName("TestProject")).thenReturn(Optional.of(testProject));

        BaseResponse response = projectService.createProject(model);

        assertEquals(HttpStatus.CONFLICT, response.getResponseCode());
        assertEquals("Project Already Exists", response.getResponseDesc());
    }

    @Test
    void createProject_UserNotFound() {
        ProjectModel model = new ProjectModel();
        model.setProjectName("NewProject");
        model.setCreatorEmail("unknown@test.com");

        when(projectRepo.findByProjectName("NewProject")).thenReturn(Optional.empty());
        when(userRepo.findByEmail("unknown@test.com")).thenReturn(Optional.empty());

        BaseResponse response = projectService.createProject(model);

        assertEquals(HttpStatus.NOT_FOUND, response.getResponseCode());
        assertEquals("User Does not Exists", response.getResponseDesc());
    }

    // ==================== INVITE TESTS ====================

    @Test
    void invite_Success() {
        when(userRepo.findByEmail("member@test.com")).thenReturn(Optional.of(
                UserEntity.builder().userId(2L).email("member@test.com").name("Member").build()
        ));
        when(projectRepo.findByProjectName("TestProject")).thenReturn(Optional.of(testProject));
        when(projectUserBridgeRepo.findByUserIdFKAndProjectIdFk(2L, 1L)).thenReturn(Optional.empty());
        when(projectUserBridgeRepo.save(any(ProjectUserBridge.class))).thenReturn(testBridge);

        BaseResponse response = projectService.invite("member@test.com", "TestProject");

        assertEquals(HttpStatus.OK, response.getResponseCode());
        verify(emailService).sendProjectInvitation(anyString(), anyString(), anyString(), anyString());
    }

    @Test
    void invite_UserNotFound() {
        when(userRepo.findByEmail("unknown@test.com")).thenReturn(Optional.empty());

        BaseResponse response = projectService.invite("unknown@test.com", "TestProject");

        assertEquals(HttpStatus.NOT_FOUND, response.getResponseCode());
    }

    @Test
    void invite_ProjectNotFound() {
        when(userRepo.findByEmail("member@test.com")).thenReturn(Optional.of(testUser));
        when(projectRepo.findByProjectName("UnknownProject")).thenReturn(Optional.empty());

        BaseResponse response = projectService.invite("member@test.com", "UnknownProject");

        assertEquals(HttpStatus.NOT_FOUND, response.getResponseCode());
    }

    @Test
    void invite_AlreadyInvited() {
        when(userRepo.findByEmail("member@test.com")).thenReturn(Optional.of(
                UserEntity.builder().userId(2L).email("member@test.com").build()
        ));
        when(projectRepo.findByProjectName("TestProject")).thenReturn(Optional.of(testProject));
        when(projectUserBridgeRepo.findByUserIdFKAndProjectIdFk(2L, 1L)).thenReturn(Optional.of(testBridge));

        BaseResponse response = projectService.invite("member@test.com", "TestProject");

        // CORRIGÉ : Le service retourne CONFLICT quand déjà invité
        assertEquals(HttpStatus.CONFLICT, response.getResponseCode());
    }

    // ==================== ACCEPT INVITATION TESTS ====================

    @Test
    void acceptInvitation_Success() {
        ProjectUserBridge pendingBridge = ProjectUserBridge.builder()
                .projUserId(1L)
                .userIdFK(1L)
                .projectIdFk(1L)
                .projectRole("MEMBER")
                .acceptance('p')
                .build();

        when(userRepo.findByEmail("test@test.com")).thenReturn(Optional.of(testUser));
        when(projectRepo.findByProjectName("TestProject")).thenReturn(Optional.of(testProject));
        when(projectUserBridgeRepo.findByUserIdFKAndProjectIdFk(1L, 1L)).thenReturn(Optional.of(pendingBridge));

        BaseResponse response = projectService.acceptInvitation("test@test.com", "TestProject");

        assertEquals(HttpStatus.OK, response.getResponseCode());
        verify(projectUserBridgeRepo).save(any(ProjectUserBridge.class));
    }

    @Test
    void acceptInvitation_UserNotFound() {
        when(userRepo.findByEmail("unknown@test.com")).thenReturn(Optional.empty());

        BaseResponse response = projectService.acceptInvitation("unknown@test.com", "TestProject");

        assertEquals(HttpStatus.NOT_FOUND, response.getResponseCode());
    }

    @Test
    void acceptInvitation_NotInvited() {
        when(userRepo.findByEmail("test@test.com")).thenReturn(Optional.of(testUser));
        when(projectRepo.findByProjectName("TestProject")).thenReturn(Optional.of(testProject));
        when(projectUserBridgeRepo.findByUserIdFKAndProjectIdFk(1L, 1L)).thenReturn(Optional.empty());

        BaseResponse response = projectService.acceptInvitation("test@test.com", "TestProject");

        assertEquals(HttpStatus.NOT_FOUND, response.getResponseCode());
        // CORRIGÉ : Message exact du service
        assertEquals("You were not invited to this project", response.getResponseDesc());
    }

    @Test
    void acceptInvitation_AlreadyAccepted() {
        when(userRepo.findByEmail("test@test.com")).thenReturn(Optional.of(testUser));
        when(projectRepo.findByProjectName("TestProject")).thenReturn(Optional.of(testProject));
        when(projectUserBridgeRepo.findByUserIdFKAndProjectIdFk(1L, 1L)).thenReturn(Optional.of(testBridge));

        BaseResponse response = projectService.acceptInvitation("test@test.com", "TestProject");

        assertEquals(HttpStatus.CONFLICT, response.getResponseCode());
    }

    // ==================== GET PROJECT TESTS ====================

    @Test
    void getProject_Success() {
        when(projectRepo.findByProjectName("TestProject")).thenReturn(Optional.of(testProject));

        BaseResponse response = projectService.getProject("TestProject");

        assertEquals(HttpStatus.OK, response.getResponseCode());
        assertNotNull(response.getData());
    }

    @Test
    void getProject_NotFound() {
        when(projectRepo.findByProjectName("Unknown")).thenReturn(Optional.empty());

        BaseResponse response = projectService.getProject("Unknown");

        assertEquals(HttpStatus.NOT_FOUND, response.getResponseCode());
    }

    // ==================== GET ALL PROJECTS TEST ====================

    @Test
    void getAllProjects_Success() {
        when(projectRepo.findAll()).thenReturn(List.of(testProject));

        BaseResponse response = projectService.getAllProjects();

        assertEquals(HttpStatus.OK, response.getResponseCode());
    }

    // ==================== DELETE PROJECT TEST ====================

    @Test
    void deleteProject_Success() {
        when(projectRepo.findByProjectName("TestProject")).thenReturn(Optional.of(testProject));
        when(tasksRepo.findByProjectIdFk(1L)).thenReturn(List.of());
        when(projectUserBridgeRepo.findByProjectIdFk(1L)).thenReturn(List.of());

        BaseResponse response = projectService.getDeleteProjectServiceMethod("TestProject");

        assertEquals(HttpStatus.OK, response.getResponseCode());
        verify(projectRepo).delete(testProject);
    }

    @Test
    void deleteProject_NotFound() {
        when(projectRepo.findByProjectName("Unknown")).thenReturn(Optional.empty());

        BaseResponse response = projectService.getDeleteProjectServiceMethod("Unknown");

        assertEquals(HttpStatus.NOT_FOUND, response.getResponseCode());
    }

    // ==================== UPDATE PROJECT TEST ====================

    @Test
    void updateProject_Success() {
        ProjectModel updateModel = new ProjectModel();
        updateModel.setProjectDescription("Updated Description");
        updateModel.setProjectStatus("COMPLETED");

        when(projectRepo.findByProjectName("TestProject")).thenReturn(Optional.of(testProject));
        when(projectRepo.save(any(ProjectEntity.class))).thenReturn(testProject);

        BaseResponse response = projectService.updateProjectServiceMethod("TestProject", updateModel);

        assertEquals(HttpStatus.OK, response.getResponseCode());
    }

    // ==================== GET PROJECT MEMBERS TEST ====================

    @Test
    void getProjectMembers_Success() {
        when(projectRepo.findByProjectName("TestProject")).thenReturn(Optional.of(testProject));
        when(projectUserBridgeRepo.findByProjectIdFk(1L)).thenReturn(List.of(testBridge));
        when(userRepo.findByUserIdIn(List.of(1L))).thenReturn(List.of(testUser));

        BaseResponse response = projectService.getProjectMembers("TestProject");

        assertEquals(HttpStatus.OK, response.getResponseCode());
    }

    @Test
    void getProjectMembers_ProjectNotFound() {
        when(projectRepo.findByProjectName("Unknown")).thenReturn(Optional.empty());

        BaseResponse response = projectService.getProjectMembers("Unknown");

        assertEquals(HttpStatus.NOT_FOUND, response.getResponseCode());
    }
}