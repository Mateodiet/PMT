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
import com.project.projectmanagment.models.project.ProjectModel;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class ProjectControllerTest {

    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    @Order(1)
    public void testGetAllProjects() {
        ResponseEntity<BaseResponse> response = restTemplate.getForEntity("/api/project/getAllProjects", BaseResponse.class);
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    @Order(2)
    public void testCreateProject() {
        ProjectModel project = new ProjectModel();
        project.setProjectName("New Project");
        project.setCreatorEmail("test@example.com");
        project.setProjectDescription("Test project description");

        ResponseEntity<BaseResponse> response = restTemplate.postForEntity("/api/project/create", project, BaseResponse.class);
        assertNotNull(response);
    }

    @Test
    @Order(3)
    public void testGetProject() {
        ResponseEntity<BaseResponse> response = restTemplate.getForEntity("/api/project/getProject?projectName=New Project", BaseResponse.class);
        assertNotNull(response);
    }

    @Test
    @Order(4)
    public void testInviteUser() {
        ResponseEntity<BaseResponse> response = restTemplate.getForEntity("/api/project/invite/test@example.com/New Project", BaseResponse.class);
        assertNotNull(response);
    }

    @Test
    @Order(5)
    public void testProjectInviteAccept() {
        ResponseEntity<BaseResponse> response = restTemplate.getForEntity("/api/project/projectInviteAccept/test@example.com/New Project", BaseResponse.class);
        assertNotNull(response);
    }

    @Test
    @Order(6)
    public void testUpdateProject() {
        ProjectModel updateProject = new ProjectModel();
        updateProject.setProjectDescription("Updated Project Description");

        HttpEntity<ProjectModel> requestEntity = new HttpEntity<>(updateProject);
        ResponseEntity<BaseResponse> response = restTemplate.exchange("/api/project/updateProject/New Project", HttpMethod.PUT, requestEntity, BaseResponse.class);
        assertNotNull(response);
    }

    @Test
    @Order(7)
    public void testDeleteProject() {
        ResponseEntity<BaseResponse> response = restTemplate.exchange("/api/project/deleteProject/New Project", HttpMethod.DELETE, null, BaseResponse.class);
        assertNotNull(response);
    }
}
