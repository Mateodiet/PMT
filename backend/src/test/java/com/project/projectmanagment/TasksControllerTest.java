package com.project.projectmanagment;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;

import com.project.projectmanagment.models.response.BaseResponse;
import com.project.projectmanagment.models.task.TasksModel;
import com.project.projectmanagment.models.task.TaskUserAssignModel;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class TasksControllerTest {

    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    @Order(1)
    public void testGetAllTasks() {
        ResponseEntity<BaseResponse> response = restTemplate.getForEntity("/api/tasks/getAllTasks", BaseResponse.class);
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    @Order(2)
    public void testCreateTask() {
        TasksModel task = new TasksModel();
        task.setTaskName("New Task");
        task.setTaskDescription("Task description");
        task.setProjectName("TestProject");

        ResponseEntity<BaseResponse> response = restTemplate.postForEntity("/api/tasks/createtask", task, BaseResponse.class);
        assertNotNull(response);
    }

    @Test
    @Order(3)
    public void testGetTask() {
        ResponseEntity<BaseResponse> response = restTemplate.getForEntity("/api/tasks/getTask?taskName=New Task", BaseResponse.class);
        assertNotNull(response);
    }

    @Test
    @Order(4)
    public void testGetTaskByProject() {
        ResponseEntity<BaseResponse> response = restTemplate.getForEntity("/api/tasks/getTaskByProject?projectName=TestProject", BaseResponse.class);
        assertNotNull(response);
    }

    @Test
    @Order(5)
    public void testUpdateTask() {
        TasksModel updateTask = new TasksModel();
        updateTask.setTaskDescription("Updated Task Description");

        HttpEntity<TasksModel> requestEntity = new HttpEntity<>(updateTask);
        ResponseEntity<BaseResponse> response = restTemplate.exchange("/api/tasks/updateTask/New Task", HttpMethod.PUT, requestEntity, BaseResponse.class);
        assertNotNull(response);
    }

    @Test
    @Order(6)
    public void testAssignTask() {
        TaskUserAssignModel assignTask = new TaskUserAssignModel();
        assignTask.setTaskName("New Task");
        assignTask.setUserEmail("test@example.com");

        ResponseEntity<BaseResponse> response = restTemplate.postForEntity("/api/tasks/assignTask", assignTask, BaseResponse.class);
        assertNotNull(response);
    }

    @Test
    @Order(7)
    public void testGetAssignedTasks() {
        ResponseEntity<BaseResponse> response = restTemplate.getForEntity("/api/tasks/getAssignedTasks", BaseResponse.class);
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    @Order(8)
    public void testGetAssignedToUser() {
        ResponseEntity<BaseResponse> response = restTemplate.getForEntity("/api/tasks/getAssignedToUser?email=test@example.com", BaseResponse.class);
        assertNotNull(response);
    }

    @Test
    @Order(9)
    public void testDeleteTask() {
        ResponseEntity<BaseResponse> response = restTemplate.exchange("/api/tasks/deleteTask/New Task", HttpMethod.DELETE, null, BaseResponse.class);
        assertNotNull(response);
    }
}
