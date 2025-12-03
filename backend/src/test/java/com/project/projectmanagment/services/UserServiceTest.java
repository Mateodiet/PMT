package com.project.projectmanagment.services;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
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
import com.project.projectmanagment.entities.project.ProjectEntity;
import com.project.projectmanagment.entities.user.UserEntity;
import com.project.projectmanagment.models.response.BaseResponse;
import com.project.projectmanagment.models.user.UserLoginDto;
import com.project.projectmanagment.models.user.UserModel;
import com.project.projectmanagment.repositories.project.ProjectRepo;
import com.project.projectmanagment.repositories.project.ProjectUserBridgeRepo;
import com.project.projectmanagment.repositories.user.UserRepo;

@ExtendWith(MockitoExtension.class)
@DisplayName("UserService Unit Tests")
class UserServiceTest {

    @Mock
    private UserRepo userRepo;

    @Mock
    private ProjectUserBridgeRepo projectUserBridgeRepo;

    @Mock
    private ProjectRepo projectRepo;

    @InjectMocks
    private UserService userService;

    private UserEntity testUser;
    private UserModel testUserModel;
    private UserLoginDto testLoginDto;

    @BeforeEach
    void setUp() {
        testUser = UserEntity.builder()
                .userId(1L)
                .name("John Doe")
                .email("john@test.com")
                .password("password123")
                .role("MEMBER")
                .contactNumber("+33612345678")
                .createdAt(new Date(System.currentTimeMillis()))
                .isActive(true)
                .build();

        testUserModel = new UserModel();
        testUserModel.setName("John Doe");
        testUserModel.setEmail("john@test.com");
        testUserModel.setPassword("password123");
        testUserModel.setRole("MEMBER");
        testUserModel.setContactNumber("+33612345678");

        testLoginDto = new UserLoginDto();
        testLoginDto.setEmail("john@test.com");
        testLoginDto.setPassword("password123");
    }

    // ==================== INSERT USER TESTS ====================

    @Test
    @DisplayName("Should insert user successfully when email does not exist")
    void insertUserData_Success() {
        when(userRepo.findByEmail(anyString())).thenReturn(Optional.empty());
        when(userRepo.save(any(UserEntity.class))).thenReturn(testUser);

        BaseResponse response = userService.insertUserData(testUserModel);

        assertEquals(HttpStatus.OK, response.getResponseCode());
        assertEquals("success", response.getResponseDesc());
        verify(userRepo, times(1)).save(any(UserEntity.class));
    }

    @Test
    @DisplayName("Should return conflict when email already exists")
    void insertUserData_EmailExists() {
        when(userRepo.findByEmail(anyString())).thenReturn(Optional.of(testUser));

        BaseResponse response = userService.insertUserData(testUserModel);

        assertEquals(HttpStatus.CONFLICT, response.getResponseCode());
        assertEquals("Record Already Exists", response.getResponseDesc());
        verify(userRepo, never()).save(any(UserEntity.class));
    }

    // ==================== LOGIN USER TESTS ====================

    @Test
    @DisplayName("Should login successfully with correct credentials")
    void loginUserServiceMethod_Success() {
        when(userRepo.findByEmail(anyString())).thenReturn(Optional.of(testUser));

        BaseResponse response = userService.loginUserServiceMethod(testLoginDto);

        assertEquals(HttpStatus.OK, response.getResponseCode());
        assertEquals("success", response.getResponseDesc());
    }

    @Test
    @DisplayName("Should fail login when user does not exist")
    void loginUserServiceMethod_UserNotFound() {
        when(userRepo.findByEmail(anyString())).thenReturn(Optional.empty());

        BaseResponse response = userService.loginUserServiceMethod(testLoginDto);

        assertEquals(HttpStatus.NOT_FOUND, response.getResponseCode());
        assertEquals("User Does not Exists", response.getResponseDesc());
    }

    @Test
    @DisplayName("Should fail login with wrong password")
    void loginUserServiceMethod_WrongPassword() {
        testLoginDto.setPassword("wrongpassword");
        when(userRepo.findByEmail(anyString())).thenReturn(Optional.of(testUser));

        BaseResponse response = userService.loginUserServiceMethod(testLoginDto);

        assertEquals(HttpStatus.NOT_FOUND, response.getResponseCode());
    }

    // ==================== UPDATE USER TESTS ====================

    @Test
    @DisplayName("Should update user successfully")
    void updateUserServiceMethod_Success() {
        UserModel updateModel = new UserModel();
        updateModel.setName("Jane Doe Updated");
        
        when(userRepo.findByEmail(anyString())).thenReturn(Optional.of(testUser));
        when(userRepo.save(any(UserEntity.class))).thenReturn(testUser);

        BaseResponse response = userService.updateUserServiceMethod("john@test.com", updateModel);

        assertEquals(HttpStatus.OK, response.getResponseCode());
        assertEquals("success", response.getResponseDesc());
        verify(userRepo, times(1)).save(any(UserEntity.class));
    }

    @Test
    @DisplayName("Should return not found when updating non-existent user")
    void updateUserServiceMethod_UserNotFound() {
        when(userRepo.findByEmail(anyString())).thenReturn(Optional.empty());

        BaseResponse response = userService.updateUserServiceMethod("unknown@test.com", testUserModel);

        assertEquals(HttpStatus.NOT_FOUND, response.getResponseCode());
        verify(userRepo, never()).save(any(UserEntity.class));
    }

    // ==================== GET USER TESTS ====================

    @Test
    @DisplayName("Should get user by email successfully")
    void getUserServiceMethod_Success() {
        when(userRepo.findByEmail(anyString())).thenReturn(Optional.of(testUser));

        BaseResponse response = userService.getUserServiceMethod("john@test.com");

        assertEquals(HttpStatus.OK, response.getResponseCode());
        assertNotNull(response.getData());
    }

    @Test
    @DisplayName("Should return not found for non-existent email")
    void getUserServiceMethod_NotFound() {
        when(userRepo.findByEmail(anyString())).thenReturn(Optional.empty());

        BaseResponse response = userService.getUserServiceMethod("unknown@test.com");

        assertEquals(HttpStatus.NOT_FOUND, response.getResponseCode());
    }

    // ==================== GET ALL USERS TESTS ====================

    @Test
    @DisplayName("Should get all users successfully")
    void getAllUserServiceMethod_Success() {
        List<UserEntity> users = Arrays.asList(testUser, 
            UserEntity.builder().userId(2L).name("Jane").email("jane@test.com").password("pass").build());
        when(userRepo.findAll()).thenReturn(users);

        BaseResponse response = userService.getAllUserServiceMethod();

        assertEquals(HttpStatus.OK, response.getResponseCode());
        assertNotNull(response.getData());
    }

    @Test
    @DisplayName("Should return not found when no users exist")
    void getAllUserServiceMethod_Empty() {
        when(userRepo.findAll()).thenReturn(Collections.emptyList());

        BaseResponse response = userService.getAllUserServiceMethod();

        assertEquals(HttpStatus.NOT_FOUND, response.getResponseCode());
    }

    // ==================== DELETE USER TESTS ====================

    @Test
    @DisplayName("Should delete user successfully")
    void getDeleteUserServiceMethod_Success() {
        when(userRepo.findByEmail(anyString())).thenReturn(Optional.of(testUser));
        doNothing().when(userRepo).delete(any(UserEntity.class));

        BaseResponse response = userService.getDeleteUserServiceMethod("john@test.com");

        assertEquals(HttpStatus.OK, response.getResponseCode());
        verify(userRepo, times(1)).delete(any(UserEntity.class));
    }

    @Test
    @DisplayName("Should return not found when deleting non-existent user")
    void getDeleteUserServiceMethod_NotFound() {
        when(userRepo.findByEmail(anyString())).thenReturn(Optional.empty());

        BaseResponse response = userService.getDeleteUserServiceMethod("unknown@test.com");

        assertEquals(HttpStatus.NOT_FOUND, response.getResponseCode());
        verify(userRepo, never()).delete(any(UserEntity.class));
    }

    // ==================== GET USER BY PROJECT TESTS ====================

    @Test
    @DisplayName("Should get users by project successfully")
    void getUserByProject_Success() {
        ProjectEntity project = ProjectEntity.builder()
                .projectId(1L)
                .projectName("Test Project")
                .build();
        
        List<ProjectUserBridge> bridges = Arrays.asList(
            ProjectUserBridge.builder().userIdFK(1L).projectIdFk(1L).build()
        );

        when(projectRepo.findByProjectName(anyString())).thenReturn(Optional.of(project));
        when(projectUserBridgeRepo.findByProjectIdFk(anyLong())).thenReturn(bridges);
        when(userRepo.findAllById(any())).thenReturn(Arrays.asList(testUser));

        BaseResponse response = userService.getUserByProject("Test Project");

        assertEquals(HttpStatus.OK, response.getResponseCode());
        assertNotNull(response.getData());
    }

    @Test
    @DisplayName("Should return not found for non-existent project")
    void getUserByProject_ProjectNotFound() {
        when(projectRepo.findByProjectName(anyString())).thenReturn(Optional.empty());

        BaseResponse response = userService.getUserByProject("Unknown Project");

        assertEquals(HttpStatus.NOT_FOUND, response.getResponseCode());
    }
}
