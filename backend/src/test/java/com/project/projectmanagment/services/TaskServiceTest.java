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

import com.project.projectmanagment.entities.bridges.TaskUserBridge;
import com.project.projectmanagment.entities.project.ProjectEntity;
import com.project.projectmanagment.entities.task.ProjectTask;
import com.project.projectmanagment.entities.task.TaskHistory;
import com.project.projectmanagment.entities.user.UserEntity;
import com.project.projectmanagment.models.response.BaseResponse;
import com.project.projectmanagment.models.task.TaskUserAssignModel;
import com.project.projectmanagment.models.task.TasksModel;
import com.project.projectmanagment.repositories.project.ProjectRepo;
import com.project.projectmanagment.repositories.project.ProjectUserBridgeRepo;
import com.project.projectmanagment.repositories.task.TaskHistoryRepo;
import com.project.projectmanagment.repositories.task.TaskUserBridgeRepo;
import com.project.projectmanagment.repositories.task.TasksRepo;
import com.project.projectmanagment.repositories.user.UserRepo;

@ExtendWith(MockitoExtension.class)
@DisplayName("TaskService Unit Tests")
class TaskServiceTest {

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
    private TaskHistoryRepo taskHistoryRepo;

    @Mock
    private EmailService emailService;

    @InjectMocks
    private TaskService taskService;

    private ProjectTask testTask;
    private TasksModel testTaskModel;
    private ProjectEntity testProject;
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
                .build();

        testTask = ProjectTask.builder()
                .taskId(1L)
                .taskName("Test Task")
                .taskDescription("Test Description")
                .taskStatus("TODO")
                .priority("MEDIUM")
                .projectIdFk(1L)
                .taskCreatedBy(1L)
                .taskCreatedAt(new Date(System.currentTimeMillis()))
                .build();

        testTaskModel = new TasksModel();
        testTaskModel.setTaskName("Test Task");
        testTaskModel.setTaskDescription("Test Description");
        testTaskModel.setTaskStatus("TODO");
        testTaskModel.setPriority("MEDIUM");
        testTaskModel.setProjectName("Test Project");
        testTaskModel.setCreatorEmail("john@test.com");
    }

    // ==================== CREATE TASK TESTS ====================

    @Test
    @DisplayName("Should create task successfully")
    void createTask_Success() {
        when(tasksRepo.findByTaskName(anyString())).thenReturn(Optional.empty());
        when(projectRepo.findByProjectName(anyString())).thenReturn(Optional.of(testProject));
        when(userRepo.findByEmail(anyString())).thenReturn(Optional.of(testUser));
        when(tasksRepo.save(any(ProjectTask.class))).thenReturn(testTask);
        when(taskHistoryRepo.save(any(TaskHistory.class))).thenReturn(new TaskHistory());

        BaseResponse response = taskService.createTask(testTaskModel);

        assertEquals(HttpStatus.OK, response.getResponseCode());
        assertEquals("success", response.getResponseDesc());
        verify(tasksRepo, times(1)).save(any(ProjectTask.class));
        verify(taskHistoryRepo, times(1)).save(any(TaskHistory.class));
    }

    @Test
    @DisplayName("Should return conflict when task already exists")
    void createTask_AlreadyExists() {
        when(tasksRepo.findByTaskName(anyString())).thenReturn(Optional.of(testTask));

        BaseResponse response = taskService.createTask(testTaskModel);

        assertEquals(HttpStatus.CONFLICT, response.getResponseCode());
        assertEquals("Task Already Exists", response.getResponseDesc());
    }

    @Test
    @DisplayName("Should return not found when project does not exist")
    void createTask_ProjectNotFound() {
        when(tasksRepo.findByTaskName(anyString())).thenReturn(Optional.empty());
        when(projectRepo.findByProjectName(anyString())).thenReturn(Optional.empty());

        BaseResponse response = taskService.createTask(testTaskModel);

        assertEquals(HttpStatus.NOT_FOUND, response.getResponseCode());
        assertEquals("Project Does not Exists", response.getResponseDesc());
    }

    @Test
    @DisplayName("Should return not found when creator does not exist")
    void createTask_CreatorNotFound() {
        when(tasksRepo.findByTaskName(anyString())).thenReturn(Optional.empty());
        when(projectRepo.findByProjectName(anyString())).thenReturn(Optional.of(testProject));
        when(userRepo.findByEmail(anyString())).thenReturn(Optional.empty());

        BaseResponse response = taskService.createTask(testTaskModel);

        assertEquals(HttpStatus.NOT_FOUND, response.getResponseCode());
        assertEquals("This user does not exists", response.getResponseDesc());
    }

    // ==================== DELETE TASK TESTS ====================

    @Test
    @DisplayName("Should delete task successfully")
    void deleteTask_Success() {
        when(tasksRepo.findByTaskName(anyString())).thenReturn(Optional.of(testTask));
        when(taskUserBridgeRepo.findByTaskIdFk(anyLong())).thenReturn(Optional.empty());
        when(taskHistoryRepo.findByTaskIdFkOrderByModifiedAtDesc(anyLong())).thenReturn(Collections.emptyList());
        doNothing().when(tasksRepo).deleteById(anyLong());

        BaseResponse response = taskService.deleteTask("Test Task");

        assertEquals(HttpStatus.OK, response.getResponseCode());
        verify(tasksRepo, times(1)).deleteById(anyLong());
    }

    @Test
    @DisplayName("Should delete task with assignments")
    void deleteTask_WithAssignments() {
        TaskUserBridge bridge = TaskUserBridge.builder()
                .taskIdFk(1L)
                .userIdFK(1L)
                .build();

        when(tasksRepo.findByTaskName(anyString())).thenReturn(Optional.of(testTask));
        when(taskUserBridgeRepo.findByTaskIdFk(anyLong())).thenReturn(Optional.of(bridge));
        when(taskHistoryRepo.findByTaskIdFkOrderByModifiedAtDesc(anyLong())).thenReturn(Collections.emptyList());
        doNothing().when(taskUserBridgeRepo).deleteByTaskIdFk(anyLong());
        doNothing().when(tasksRepo).deleteById(anyLong());

        BaseResponse response = taskService.deleteTask("Test Task");

        assertEquals(HttpStatus.OK, response.getResponseCode());
        verify(taskUserBridgeRepo, times(1)).deleteByTaskIdFk(anyLong());
    }

    @Test
    @DisplayName("Should return not found when deleting non-existent task")
    void deleteTask_NotFound() {
        when(tasksRepo.findByTaskName(anyString())).thenReturn(Optional.empty());

        BaseResponse response = taskService.deleteTask("Unknown Task");

        assertEquals(HttpStatus.NOT_FOUND, response.getResponseCode());
    }

    // ==================== GET ALL TASKS TESTS ====================

    @Test
    @DisplayName("Should get all tasks successfully")
    void getAllTasks_Success() {
        List<ProjectTask> tasks = Arrays.asList(testTask);
        when(tasksRepo.findAll()).thenReturn(tasks);

        BaseResponse response = taskService.getAllTasks();

        assertEquals(HttpStatus.OK, response.getResponseCode());
        assertNotNull(response.getData());
    }

    // ==================== GET TASK TESTS ====================

    @Test
    @DisplayName("Should get task by name successfully")
    void getTask_Success() {
        when(tasksRepo.findByTaskName(anyString())).thenReturn(Optional.of(testTask));

        BaseResponse response = taskService.getTask("Test Task");

        assertEquals(HttpStatus.OK, response.getResponseCode());
        assertNotNull(response.getData());
    }

    @Test
    @DisplayName("Should return not found for non-existent task")
    void getTask_NotFound() {
        when(tasksRepo.findByTaskName(anyString())).thenReturn(Optional.empty());

        BaseResponse response = taskService.getTask("Unknown Task");

        assertEquals(HttpStatus.NOT_FOUND, response.getResponseCode());
    }

    // ==================== GET TASK BY PROJECT TESTS ====================

    @Test
    @DisplayName("Should get tasks by project successfully")
    void getTaskAgainstProject_Success() {
        List<ProjectTask> tasks = Arrays.asList(testTask);
        when(projectRepo.findByProjectName(anyString())).thenReturn(Optional.of(testProject));
        when(tasksRepo.findByProjectIdFk(anyLong())).thenReturn(tasks);

        BaseResponse response = taskService.getTaskAgainstProject("Test Project");

        assertEquals(HttpStatus.OK, response.getResponseCode());
        assertNotNull(response.getData());
    }

    @Test
    @DisplayName("Should return not found for non-existent project")
    void getTaskAgainstProject_ProjectNotFound() {
        when(projectRepo.findByProjectName(anyString())).thenReturn(Optional.empty());

        BaseResponse response = taskService.getTaskAgainstProject("Unknown Project");

        assertEquals(HttpStatus.NOT_FOUND, response.getResponseCode());
    }

    @Test
    @DisplayName("Should return not found when project has no tasks")
    void getTaskAgainstProject_NoTasks() {
        when(projectRepo.findByProjectName(anyString())).thenReturn(Optional.of(testProject));
        when(tasksRepo.findByProjectIdFk(anyLong())).thenReturn(Collections.emptyList());

        BaseResponse response = taskService.getTaskAgainstProject("Test Project");

        assertEquals(HttpStatus.NOT_FOUND, response.getResponseCode());
    }

    // ==================== ASSIGN TASK TESTS ====================

    @Test
    @DisplayName("Should assign task to user successfully")
    void assignTask_Success() {
        TaskUserAssignModel assignModel = new TaskUserAssignModel();
        assignModel.setTaskName("Test Task");
        assignModel.setUserEmail("john@test.com");

        when(tasksRepo.findByTaskName(anyString())).thenReturn(Optional.of(testTask));
        when(userRepo.findByEmail(anyString())).thenReturn(Optional.of(testUser));
        when(taskUserBridgeRepo.findByUserIdFKAndTaskIdFk(anyLong(), anyLong())).thenReturn(Optional.empty());
        when(taskUserBridgeRepo.save(any(TaskUserBridge.class))).thenReturn(new TaskUserBridge());
        when(taskHistoryRepo.save(any(TaskHistory.class))).thenReturn(new TaskHistory());
        when(projectRepo.findById(anyLong())).thenReturn(Optional.of(testProject));
        doNothing().when(emailService).sendTaskAssignmentNotification(anyString(), anyString(), anyString(), anyString());

        BaseResponse response = taskService.assigntask(assignModel);

        assertEquals(HttpStatus.OK, response.getResponseCode());
        verify(taskUserBridgeRepo, times(1)).save(any(TaskUserBridge.class));
        verify(emailService, times(1)).sendTaskAssignmentNotification(anyString(), anyString(), anyString(), anyString());
    }

    @Test
    @DisplayName("Should return conflict when task already assigned to user")
    void assignTask_AlreadyAssigned() {
        TaskUserAssignModel assignModel = new TaskUserAssignModel();
        assignModel.setTaskName("Test Task");
        assignModel.setUserEmail("john@test.com");

        TaskUserBridge bridge = TaskUserBridge.builder()
                .taskIdFk(1L)
                .userIdFK(1L)
                .build();

        when(tasksRepo.findByTaskName(anyString())).thenReturn(Optional.of(testTask));
        when(userRepo.findByEmail(anyString())).thenReturn(Optional.of(testUser));
        when(taskUserBridgeRepo.findByUserIdFKAndTaskIdFk(anyLong(), anyLong())).thenReturn(Optional.of(bridge));

        BaseResponse response = taskService.assigntask(assignModel);

        assertEquals(HttpStatus.CONFLICT, response.getResponseCode());
        assertEquals("Task Already Assigned", response.getResponseDesc());
    }

    @Test
    @DisplayName("Should return not found when assigning non-existent task")
    void assignTask_TaskNotFound() {
        TaskUserAssignModel assignModel = new TaskUserAssignModel();
        assignModel.setTaskName("Unknown Task");
        assignModel.setUserEmail("john@test.com");

        when(tasksRepo.findByTaskName(anyString())).thenReturn(Optional.empty());

        BaseResponse response = taskService.assigntask(assignModel);

        assertEquals(HttpStatus.NOT_FOUND, response.getResponseCode());
    }

    // ==================== GET ASSIGNED TO USER TESTS ====================

    @Test
    @DisplayName("Should get tasks assigned to user successfully")
    void getAssignedToTask_Success() {
        List<TaskUserBridge> bridges = Arrays.asList(
            TaskUserBridge.builder().taskIdFk(1L).userIdFK(1L).build()
        );
        List<ProjectTask> tasks = Arrays.asList(testTask);

        when(userRepo.findByEmail(anyString())).thenReturn(Optional.of(testUser));
        when(taskUserBridgeRepo.findByUserIdFK(anyLong())).thenReturn(bridges);
        when(tasksRepo.findByTaskIdIn(any())).thenReturn(tasks);

        BaseResponse response = taskService.getAssignedToTask("john@test.com");

        assertEquals(HttpStatus.OK, response.getResponseCode());
        assertNotNull(response.getData());
    }

    @Test
    @DisplayName("Should return not found for non-existent user")
    void getAssignedToTask_UserNotFound() {
        when(userRepo.findByEmail(anyString())).thenReturn(Optional.empty());

        BaseResponse response = taskService.getAssignedToTask("unknown@test.com");

        assertEquals(HttpStatus.NOT_FOUND, response.getResponseCode());
    }

    @Test
    @DisplayName("Should return not found when user has no assigned tasks")
    void getAssignedToTask_NoTasks() {
        when(userRepo.findByEmail(anyString())).thenReturn(Optional.of(testUser));
        when(taskUserBridgeRepo.findByUserIdFK(anyLong())).thenReturn(Collections.emptyList());

        BaseResponse response = taskService.getAssignedToTask("john@test.com");

        assertEquals(HttpStatus.NOT_FOUND, response.getResponseCode());
    }

    // ==================== GET ALL ASSIGNED TASKS TESTS ====================

    @Test
    @DisplayName("Should get all assigned tasks successfully")
    void getAssigntask_Success() {
        List<ProjectTask> tasks = Arrays.asList(testTask);
        List<TaskUserBridge> bridges = Arrays.asList(
            TaskUserBridge.builder().taskIdFk(1L).userIdFK(1L).build()
        );
        List<UserEntity> users = Arrays.asList(testUser);

        when(tasksRepo.findAll()).thenReturn(tasks);
        when(taskUserBridgeRepo.findByTaskIdFkIn(any())).thenReturn(bridges);
        when(tasksRepo.findByTaskIdIn(any())).thenReturn(tasks);
        when(userRepo.findByUserIdIn(any())).thenReturn(users);

        BaseResponse response = taskService.getAssigntask();

        assertEquals(HttpStatus.OK, response.getResponseCode());
        assertNotNull(response.getData());
    }

    @Test
    @DisplayName("Should return not found when no tasks exist")
    void getAssigntask_NoTasks() {
        when(tasksRepo.findAll()).thenReturn(Collections.emptyList());

        BaseResponse response = taskService.getAssigntask();

        assertEquals(HttpStatus.NOT_FOUND, response.getResponseCode());
    }

    // ==================== GET TASK HISTORY TESTS ====================

    @Test
    @DisplayName("Should get task history successfully")
    void getTaskHistory_Success() {
        List<TaskHistory> history = Arrays.asList(
            TaskHistory.builder()
                .historyId(1L)
                .taskIdFk(1L)
                .fieldName("taskStatus")
                .oldValue("TODO")
                .newValue("IN_PROGRESS")
                .build()
        );

        when(tasksRepo.findByTaskName(anyString())).thenReturn(Optional.of(testTask));
        when(taskHistoryRepo.findByTaskIdFkOrderByModifiedAtDesc(anyLong())).thenReturn(history);

        BaseResponse response = taskService.getTaskHistory("Test Task");

        assertEquals(HttpStatus.OK, response.getResponseCode());
        assertNotNull(response.getData());
    }

    @Test
    @DisplayName("Should return not found for task history of non-existent task")
    void getTaskHistory_TaskNotFound() {
        when(tasksRepo.findByTaskName(anyString())).thenReturn(Optional.empty());

        BaseResponse response = taskService.getTaskHistory("Unknown Task");

        assertEquals(HttpStatus.NOT_FOUND, response.getResponseCode());
    }
}