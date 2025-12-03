package com.project.projectmanagment.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.project.projectmanagment.models.response.BaseResponse;
import com.project.projectmanagment.models.user.UserLoginDto;
import com.project.projectmanagment.models.user.UserModel;
import com.project.projectmanagment.services.UserService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;




@CrossOrigin(origins = "*", allowedHeaders = "*")
@RestController
@RequestMapping("/api/user")
public class UsersController {

    private final UserService userService;

    public UsersController(UserService userService){
        this.userService = userService;
    }

    @PostMapping("/signup")
    public ResponseEntity<BaseResponse> insertUser(@RequestBody UserModel userRequest) {
        BaseResponse response = userService.insertUserData(userRequest);
        return new ResponseEntity<>(response, response.getResponseCode());
    }

    @PostMapping("/login")
    public ResponseEntity<BaseResponse> insertUser(@RequestBody UserLoginDto userLoginRequest) {
        BaseResponse response = userService.loginUserServiceMethod(userLoginRequest);
        return new ResponseEntity<>(response, response.getResponseCode());
    }
    
    @PutMapping("/updateUser/{email}")
    public ResponseEntity<BaseResponse> updateUser(@PathVariable String email, @RequestBody UserModel userUpdateRequest) {
        BaseResponse response = userService.updateUserServiceMethod(email, userUpdateRequest);
        return new ResponseEntity<>(response, response.getResponseCode());
    }

    @GetMapping("/getUserByEmail")
    public ResponseEntity<?> getUserMethod(@RequestParam(value = "email") String email) {
        BaseResponse response = userService.getUserServiceMethod(email);
        return new ResponseEntity<>(response, response.getResponseCode());
    }

    @GetMapping("/getAllUsers")
    public ResponseEntity<?> getUserMethod() {
        BaseResponse response = userService.getAllUserServiceMethod();
        return new ResponseEntity<>(response, response.getResponseCode());
    }

    @DeleteMapping("/deleteUser/{email}")
    public ResponseEntity<?> deleteUser(@PathVariable String email){
        BaseResponse response = userService.getDeleteUserServiceMethod(email);
        return new ResponseEntity<>(response, response.getResponseCode());
    }

    @GetMapping("/getUsersByProject")
    public ResponseEntity<?> getUserByProject(@RequestParam(value = "projectName") String projectName) {
        BaseResponse response = userService.getUserByProject(projectName);
        return new ResponseEntity<>(response, response.getResponseCode());
    }

    // get Users by projects
    // get Users by tasks
    // get Users by completed tasks
    
}