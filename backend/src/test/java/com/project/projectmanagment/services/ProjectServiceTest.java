package com.project.projectmanagment.services;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

import java.sql.Date;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;

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

@ExtendWith(MockitoExtension.class)
@DisplayName("ProjectService Unit Tests")
class ProjectServiceTest {

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

    @InjectMocks
    private ProjectService projectService;

    private ProjectEntity testProject;
    private ProjectModel testProjectModel;
    private UserEntity testUser;

    @BeforeEach
    void setUp() {
        testUser = UserEntity.builder()
                .userId(1L)
                .name("John Doe")
                .email("john@test.com")
                .password("password123")
                .build();

        testProject = ProjectEntity.builder()
                .projectId(1L)
                .projectName("Test Project")
                .projectDescription("Test Description")
                .projectStatus("ACTIVE")
                .projectCreatedDate(new Date(System.currentTimeMillis()))
                .taskCreatedBy(1L)
                .build();

        testProjectModel = ProjectModel.builder()
                .projectName("Test Project")
                .projectDescription("Test Description")
                .projectStatus("ACTIVE")
                .creatorEmail("john@test.com")
                .build();
    }

    // ==================== CREATE PROJECT TESTS ====================

    @Test
    @DisplayName("Should create project successfully")
    void createProject_Success() {
        when(projectRepo.findByProjectName(anyString())).thenReturn(Optional.empty());
        when(userRepo.findByEmail(anyString())).thenReturn(Optional.of(testUser));
        when(projectRepo.save(any(ProjectEntity.class))).thenReturn(testProject);

        BaseResponse response = projectService.createProject(testProjectModel);

        assertEquals(HttpStatus.OK, response.getResponseCode());
        assertEquals("Success", response.getResponseDesc());
        verify(projectRepo, times(1)).save(any(ProjectEntity.class));
    }

    @Test
    @DisplayName("Should return conflict when project already exists")
    void createProject_AlreadyExists() {
        when(projectRepo.findByProjectName(anyString())).thenReturn(Optional.of(testProject));

        BaseResponse response = projectService.createProject(testProjectModel);

        assertEquals(HttpStatus.CONFLICT, response.getResponseCode());
        assertEquals("Project Already Exists", response.getResponseDesc());
        verify(projectRepo, never()).save(any(ProjectEntity.class));
    }

    @Test
    @DisplayName("Should return not found when creator does not exist")
    void createProject_CreatorNotFound() {
        when(projectRepo.findByProjectName(anyString())).thenReturn(Optional.empty());
        when(userRepo.findByEmail(anyString())).thenReturn(Optional.empty());

        BaseResponse response = projectService.createProject(testProjectModel);

        assertEquals(HttpStatus.NOT_FOUND, response.getResponseCode());
        assertEquals("User Does not Exists", response.getResponseDesc());
    }

    // ==================== INVITE USER TESTS ====================

    @Test
    @DisplayName("Should invite user to project successfully")
    void invite_Success() {
        when(userRepo.findByEmail(anyString())).thenReturn(Optional.of(testUser));
        when(projectRepo.findByProjectName(anyString())).thenReturn(Optional.of(testProject));
        when(projectUserBridgeRepo.findByUserIdFKAndProjectIdFk(anyLong(), anyLong())).thenReturn(Optional.empty());
        when(projectUserBridgeRepo.save(any(ProjectUserBridge.class))).thenReturn(new ProjectUserBridge());

        BaseResponse response = projectService.invite("john@test.com", "Test Project");

        assertEquals(HttpStatus.OK, response.getResponseCode());
        assertNotNull(response.getData());
        verify(projectUserBridgeRepo, times(1)).save(any(ProjectUserBridge.class));
    }

    @Test
    @DisplayName("Should return already invited when user is already in project")
    void invite_AlreadyInvited() {
        ProjectUserBridge bridge = ProjectUserBridge.builder()
                .userIdFK(1L)
                .projectIdFk(1L)
                .acceptance('p')
                .build();

        when(userRepo.findByEmail(anyString())).thenReturn(Optional.of(testUser));
        when(projectRepo.findByProjectName(anyString())).thenReturn(Optional.of(testProject));
        when(projectUserBridgeRepo.findByUserIdFKAndProjectIdFk(anyLong(), anyLong())).thenReturn(Optional.of(bridge));

        BaseResponse response = projectService.invite("john@test.com", "Test Project");

        assertEquals(HttpStatus.CREATED, response.getResponseCode());
        assertEquals("Already Invited", response.getResponseDesc());
    }

    @Test
    @DisplayName("Should return not found when inviting non-existent user")
    void invite_UserNotFound() {
        when(userRepo.findByEmail(anyString())).thenReturn(Optional.empty());

        BaseResponse response = projectService.invite("unknown@test.com", "Test Project");

        assertEquals(HttpStatus.NOT_FOUND, response.getResponseCode());
    }

    // ==================== ACCEPT INVITATION TESTS ====================

    @Test
    @DisplayName("Should accept invitation successfully")
    void acceptInvitation_Success() {
        ProjectUserBridge bridge = ProjectUserBridge.builder()
                .userIdFK(1L)
                .projectIdFk(1L)
                .acceptance('p')
                .build();

        when(userRepo.findByEmail(anyString())).thenReturn(Optional.of(testUser));
        when(projectRepo.findByProjectName(anyString())).thenReturn(Optional.of(testProject));
        when(projectUserBridgeRepo.findByUserIdFKAndProjectIdFk(anyLong(), anyLong())).thenReturn(Optional.of(bridge));
        when(projectUserBridgeRepo.save(any(ProjectUserBridge.class))).thenReturn(bridge);

        BaseResponse response = projectService.acceptInvitation("john@test.com", "Test Project");

        assertEquals(HttpStatus.OK, response.getResponseCode());
        verify(projectUserBridgeRepo, times(1)).save(any(ProjectUserBridge.class));
    }

    @Test
    @DisplayName("Should return not found when not invited")
    void acceptInvitation_NotInvited() {
        when(userRepo.findByEmail(anyString())).thenReturn(Optional.of(testUser));
        when(projectRepo.findByProjectName(anyString())).thenReturn(Optional.of(testProject));
        when(projectUserBridgeRepo.findByUserIdFKAndProjectIdFk(anyLong(), anyLong())).thenReturn(Optional.empty());

        BaseResponse response = projectService.acceptInvitation("john@test.com", "Test Project");

        assertEquals(HttpStatus.NOT_FOUND, response.getResponseCode());
        assertEquals("You were not invited", response.getResponseDesc());
    }

    // ==================== UPDATE PROJECT TESTS ====================

    @Test
    @DisplayName("Should update project successfully")
    void updateProjectServiceMethod_Success() {
        ProjectModel updateModel = ProjectModel.builder()
                .projectDescription("Updated Description")
                .projectStatus("COMPLETED")
                .build();

        when(projectRepo.findByProjectName(anyString())).thenReturn(Optional.of(testProject));
        when(projectRepo.save(any(ProjectEntity.class))).thenReturn(testProject);

        BaseResponse response = projectService.updateProjectServiceMethod("Test Project", updateModel);

        assertEquals(HttpStatus.OK, response.getResponseCode());
        verify(projectRepo, times(1)).save(any(ProjectEntity.class));
    }

    @Test
    @DisplayName("Should return not found when updating non-existent project")
    void updateProjectServiceMethod_NotFound() {
        when(projectRepo.findByProjectName(anyString())).thenReturn(Optional.empty());

        BaseResponse response = projectService.updateProjectServiceMethod("Unknown", testProjectModel);

        assertEquals(HttpStatus.NOT_FOUND, response.getResponseCode());
    }

    // ==================== DELETE PROJECT TESTS ====================

    @Test
    @DisplayName("Should delete project successfully with no tasks")
    void getDeleteProjectServiceMethod_Success_NoTasks() {
        when(projectRepo.findByProjectName(anyString())).thenReturn(Optional.of(testProject));
        when(tasksRepo.findByProjectIdFk(anyLong())).thenReturn(Collections.emptyList());
        doNothing().when(projectRepo).delete(any(ProjectEntity.class));

        BaseResponse response = projectService.getDeleteProjectServiceMethod("Test Project");

        assertEquals(HttpStatus.OK, response.getResponseCode());
        verify(projectRepo, times(1)).delete(any(ProjectEntity.class));
    }

    @Test
    @DisplayName("Should delete project with tasks and assignments")
    void getDeleteProjectServiceMethod_Success_WithTasks() {
        ProjectTask task = ProjectTask.builder().taskId(1L).taskName("Test Task").build();
        List<ProjectTask> tasks = Arrays.asList(task);
        List<TaskUserBridge> taskBridges = Arrays.asList(
            TaskUserBridge.builder().taskIdFk(1L).userIdFK(1L).build()
        );

        when(projectRepo.findByProjectName(anyString())).thenReturn(Optional.of(testProject));
        when(tasksRepo.findByProjectIdFk(anyLong())).thenReturn(tasks);
        when(taskUserBridgeRepo.findByTaskIdFkIn(any())).thenReturn(taskBridges);
        doNothing().when(taskUserBridgeRepo).deleteAll(any());
        doNothing().when(tasksRepo).deleteAll(any());
        doNothing().when(projectRepo).delete(any(ProjectEntity.class));

        BaseResponse response = projectService.getDeleteProjectServiceMethod("Test Project");

        assertEquals(HttpStatus.OK, response.getResponseCode());
        verify(taskUserBridgeRepo, times(1)).deleteAll(any());
        verify(tasksRepo, times(1)).deleteAll(any());
    }

    @Test
    @DisplayName("Should return not found when deleting non-existent project")
    void getDeleteProjectServiceMethod_NotFound() {
        when(projectRepo.findByProjectName(anyString())).thenReturn(Optional.empty());

        BaseResponse response = projectService.getDeleteProjectServiceMethod("Unknown");

        assertEquals(HttpStatus.NOT_FOUND, response.getResponseCode());
    }

    // ==================== GET ALL PROJECTS TESTS ====================

    @Test
    @DisplayName("Should get all projects successfully")
    void getAllProjects_Success() {
        List<ProjectEntity> projects = Arrays.asList(testProject);
        when(projectRepo.findAll()).thenReturn(projects);

        BaseResponse response = projectService.getAllProjects();

        assertEquals(HttpStatus.OK, response.getResponseCode());
        assertNotNull(response.getData());
    }

    // ==================== GET PROJECT TESTS ====================

    @Test
    @DisplayName("Should get project by name successfully")
    void getProject_Success() {
        when(projectRepo.findByProjectName(anyString())).thenReturn(Optional.of(testProject));

        BaseResponse response = projectService.getProject("Test Project");

        assertEquals(HttpStatus.OK, response.getResponseCode());
        assertNotNull(response.getData());
    }

    @Test
    @DisplayName("Should return not found for non-existent project")
    void getProject_NotFound() {
        when(projectRepo.findByProjectName(anyString())).thenReturn(Optional.empty());

        BaseResponse response = projectService.getProject("Unknown");

        assertEquals(HttpStatus.NOT_FOUND, response.getResponseCode());
    }
}
