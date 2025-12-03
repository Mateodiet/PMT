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
import com.project.projectmanagment.models.user.UserLoginDto;
import com.project.projectmanagment.models.user.UserModel;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class UsersControllerTest {

    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    @Order(1)
    public void testInsertUser() {
        UserModel user = new UserModel();
        user.setEmail("test@example.com");
        user.setPassword("password123");
        user.setName("Test User");

        ResponseEntity<BaseResponse> response = restTemplate.postForEntity("/api/user/signup", user, BaseResponse.class);
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    @Order(2)
    public void testLoginUser() {
        UserLoginDto loginDto = new UserLoginDto();
        loginDto.setEmail("test@example.com");
        loginDto.setPassword("password123");

        ResponseEntity<BaseResponse> response = restTemplate.postForEntity("/api/user/login", loginDto, BaseResponse.class);
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    @Order(3)
    public void testGetUserByEmail() {
        ResponseEntity<BaseResponse> response = restTemplate.getForEntity("/api/user/getUserByEmail?email=test@example.com", BaseResponse.class);
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    @Order(4)
    public void testGetAllUsers() {
        ResponseEntity<BaseResponse> response = restTemplate.getForEntity("/api/user/getAllUsers", BaseResponse.class);
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    @Order(5)
    public void testUpdateUser() {
        UserModel userUpdate = new UserModel();
        userUpdate.setName("Updated Name");

        HttpEntity<UserModel> requestEntity = new HttpEntity<>(userUpdate);
        ResponseEntity<BaseResponse> response = restTemplate.exchange("/api/user/updateUser/test@example.com", HttpMethod.PUT, requestEntity, BaseResponse.class);
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    @Order(6)
    public void testGetUserByProject() {
        ResponseEntity<BaseResponse> response = restTemplate.getForEntity("/api/user/getUsersByProject?projectName=TestProject", BaseResponse.class);
        assertNotNull(response);
    }

    @Test
    @Order(7)
    public void testDeleteUser() {
        ResponseEntity<BaseResponse> response = restTemplate.exchange("/api/user/deleteUser/test@example.com", HttpMethod.DELETE, null, BaseResponse.class);
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }
}
