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
import com.project.projectmanagment.entities.project.ProjectEntity;
import com.project.projectmanagment.entities.task.ProjectTask;
import com.project.projectmanagment.entities.user.UserEntity;
import com.project.projectmanagment.models.response.BaseResponse;
import com.project.projectmanagment.models.user.UserLoginDto;
import com.project.projectmanagment.models.user.UserModel;
import com.project.projectmanagment.repositories.project.ProjectRepo;
import com.project.projectmanagment.repositories.project.ProjectUserBridgeRepo;
import com.project.projectmanagment.repositories.user.UserRepo;

import lombok.AllArgsConstructor;


@Service
@AllArgsConstructor
public class UserService {

    private final UserRepo userRepo;
    private final ProjectUserBridgeRepo projectUserBridgeRepo;
    private final ProjectRepo projectRepo;

    public BaseResponse insertUserData(UserModel userRequest){

        BaseResponse response = new BaseResponse();

        Optional<UserEntity> userData = userRepo.findByEmail(userRequest.getEmail());
        if(userData.isPresent()){
            response.setResponseCode(HttpStatus.CONFLICT);
            response.setResponseDesc("Record Already Exists");
            return response;
        }
        
        UserEntity userdata = userRepo.save(UserEntity.builder()
            .email(userRequest.getEmail())
            .name(userRequest.getName())
            .password(userRequest.getPassword())
            .role(userRequest.getRole())
            .contactNumber(userRequest.getContactNumber())
            .createdAt(new Date(System.currentTimeMillis()))
            .isActive(true)
            .password(userRequest.getPassword()).build());


        if(userdata==null){
            response.setResponseCode(HttpStatus.INTERNAL_SERVER_ERROR);
            response.setResponseDesc("Unable to save record");
            return response;
        }

        response.setResponseCode(HttpStatus.OK);
        response.setResponseDesc("success");
        return response;
    }


    public BaseResponse loginUserServiceMethod(UserLoginDto userLoginRequest){

        Optional<UserEntity> userDbData = userRepo.findByEmail(userLoginRequest.getEmail());

        if(!userDbData.isPresent()){
            return BaseResponse.builder()
                .responseCode(HttpStatus.NOT_FOUND)
                .responseDesc("User Does not Exists").build();
        }
        
        UserEntity userData = userDbData.get();

        if(userData.getPassword().equals(userLoginRequest.getPassword())){
            return BaseResponse.builder()
            .responseCode(HttpStatus.OK)
            .responseDesc("success")
            .data(null)
            .build();
        }

        return BaseResponse.builder()
                .responseCode(HttpStatus.NOT_FOUND)
                .responseDesc("User Does not Exists").build();

    }


    public BaseResponse updateUserServiceMethod(String email, UserModel userUpdateRequest){
        Optional<UserEntity> userDbData = userRepo.findByEmail(email);

        if(!userDbData.isPresent()){
            return BaseResponse.builder()
                .responseCode(HttpStatus.NOT_FOUND)
                .responseDesc("User Does not Exists").build();
        }

        UserEntity user = userDbData.get();

        user.setEmail(userUpdateRequest.getEmail()==null || userUpdateRequest.getEmail().isEmpty()? user.getEmail():userUpdateRequest.getEmail());

        user.setPassword(userUpdateRequest.getPassword()==null || userUpdateRequest.getPassword().isEmpty()? user.getPassword():userUpdateRequest.getPassword());

        user.setName(userUpdateRequest.getName()==null || userUpdateRequest.getName().isEmpty()? user.getName():userUpdateRequest.getName());

        user.setRole(userUpdateRequest.getRole()==null || userUpdateRequest.getRole().isEmpty()? user.getRole():userUpdateRequest.getRole());

        user.setContactNumber(userUpdateRequest.getContactNumber()==null || userUpdateRequest.getContactNumber().isEmpty()? user.getContactNumber():userUpdateRequest.getContactNumber());

        user.setActive(userUpdateRequest.getIsActive()==null? user.isActive():userUpdateRequest.getIsActive());
    
        userRepo.save(user);
        user.setPassword("****");
        // private boolean isActive=true;
        return BaseResponse.builder()
            .responseCode(HttpStatus.OK)
            .responseDesc("success")
            .data(user)
            .build();
    }

    public BaseResponse getUserServiceMethod(String email){
        Optional<UserEntity> userDbData = userRepo.findByEmail(email);

        if(!userDbData.isPresent()){
            return BaseResponse.builder()
                .responseCode(HttpStatus.NOT_FOUND)
                .responseDesc("User Does not Exists").build();
        }
        UserEntity user = userDbData.get();
        user.setPassword("****");
        return BaseResponse.builder()
            .responseCode(HttpStatus.OK)
            .responseDesc("success")
            .data(user)
            .build();
    }

    public BaseResponse getAllUserServiceMethod(){
        List<UserEntity> userDbData = userRepo.findAll();

        if(userDbData.isEmpty()){
            return BaseResponse.builder()
                .responseCode(HttpStatus.NOT_FOUND)
                .responseDesc("User Does not Exists").build();
        }
        userDbData.parallelStream().forEach(user -> user.setPassword("****"));
        
        return BaseResponse.builder()
            .responseCode(HttpStatus.OK)
            .responseDesc("success")
            .data(userDbData)
            .build();
    }


    public BaseResponse getDeleteUserServiceMethod(String email){
        Optional<UserEntity> userDbData = userRepo.findByEmail(email);

        if(!userDbData.isPresent()){
            return BaseResponse.builder()
                .responseCode(HttpStatus.NOT_FOUND)
                .responseDesc("User Does not Exists").build();
        }

        userRepo.delete(userDbData.get());
        
        return BaseResponse.builder()
            .responseCode(HttpStatus.OK)
            .responseDesc("success")
            .data(null)
            .build();
    }

    public BaseResponse getUserByProject(String projectName){
        Optional<ProjectEntity> projectDbData = projectRepo.findByProjectName(projectName);

        if(projectDbData.isEmpty()){
            return BaseResponse.builder()
                .responseCode(HttpStatus.NOT_FOUND)
                .responseDesc("User Does not Exists").build();
        }

        List<ProjectUserBridge> projectUserIds = projectUserBridgeRepo.findByProjectIdFk(projectDbData.get().getProjectId());
        Map<String, Object> userMap = new HashMap<>();

        userMap.put("projectName", projectName);
        userMap.put("assigned", userRepo.findAllById(projectUserIds.stream().map(ProjectUserBridge::getUserIdFK).collect(Collectors.toList())));
        
        return BaseResponse.builder()
            .responseCode(HttpStatus.OK)
            .responseDesc("success")
            .data(userMap)
            .build();
    }

}
