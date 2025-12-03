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

@Service
@AllArgsConstructor
public class ProjectService {
    private final ProjectRepo projectRepo;
    private final UserRepo userRepo;
    private final ProjectUserBridgeRepo projectUserBridgeRepo;
    private final TaskUserBridgeRepo taskUserBridgeRepo;
    private final TasksRepo tasksRepo;


    public BaseResponse createProject(ProjectModel projectRequest){
        Optional<ProjectEntity> projectDbData = projectRepo.findByProjectName(projectRequest.getProjectName());
        if(projectDbData.isPresent()){
            return BaseResponse.builder()
                .responseCode(HttpStatus.CONFLICT)
                .responseDesc("Project Already Exists").build();
        }

        Optional<UserEntity> userDbData = userRepo.findByEmail(projectRequest.getCreatorEmail());
        if(!userDbData.isPresent()){
            return BaseResponse.builder()
                .responseCode(HttpStatus.NOT_FOUND)
                .responseDesc("User Does not Exists").build();
        }

        ProjectEntity projectSaved = projectRepo.save(
          ProjectEntity.builder()
          .projectName(projectRequest.getProjectName())
          .projectDescription(projectRequest.getProjectDescription())
          .projectStatus(projectRequest.getProjectStatus())
          .taskCreatedBy(userDbData.get().getUserId())
          .build()  
        );

        if(projectSaved==null){
            return BaseResponse.builder()
            .responseCode(HttpStatus.METHOD_FAILURE)
            .responseDesc("Unable to Save Data").build();
        }
        return BaseResponse.builder()
        .responseCode(HttpStatus.OK)
        .responseDesc("Success").build();
    }

    public BaseResponse invite(String email, String projectName){

        Optional<UserEntity> userDbData = userRepo.findByEmail(email);

        if(!userDbData.isPresent()){
            return BaseResponse.builder()
                .responseCode(HttpStatus.NOT_FOUND)
                .responseDesc("User Does not Exists").build();
        }

        Optional<ProjectEntity> projectDbdata = projectRepo.findByProjectName(projectName);

        if(!projectDbdata.isPresent()){
            return BaseResponse.builder()
                .responseCode(HttpStatus.NOT_FOUND)
                .responseDesc("Project Does not Exists").build();
        }

        Optional<ProjectUserBridge>  projectInviteDbData = projectUserBridgeRepo.findByUserIdFKAndProjectIdFk(userDbData.get().getUserId(), projectDbdata.get().getProjectId());
        if(projectInviteDbData.isPresent()){
            return BaseResponse.builder()
            .responseCode(HttpStatus.CREATED)
            .responseDesc("Already Invited").build();
        }

        projectUserBridgeRepo.save(
            ProjectUserBridge.builder()
                .userIdFK(userDbData.get().getUserId())
                .projectIdFk(projectDbdata.get().getProjectId())
                .acceptance('p')
                .build()
        );  
        Map<String, String> url = new HashMap<>();
        url.put("inviteLink", "/api/project/projectInviteAccept/"+email+"/"+projectName);// this link will just update the bridge of project and user to a/ link has email and project name
        return BaseResponse.builder()
            .responseCode(HttpStatus.OK)
            .responseDesc("success")
            .data(url)
            .build();
    }

    public BaseResponse acceptInvitation(String email, String projectName){

        Optional<UserEntity> userDbData = userRepo.findByEmail(email);

        if(!userDbData.isPresent()){
            return BaseResponse.builder()
                .responseCode(HttpStatus.NOT_FOUND)
                .responseDesc("User Does not Exists").build();
        }

        Optional<ProjectEntity> projectDbdata = projectRepo.findByProjectName(projectName);

        if(!projectDbdata.isPresent()){
            return BaseResponse.builder()
                .responseCode(HttpStatus.NOT_FOUND)
                .responseDesc("Project Does not Exists").build();
        }
        
        Optional<ProjectUserBridge>  projectInviteDbData = projectUserBridgeRepo.findByUserIdFKAndProjectIdFk(userDbData.get().getUserId(), projectDbdata.get().getProjectId());
        if(!projectInviteDbData.isPresent()){
            return BaseResponse.builder()
            .responseCode(HttpStatus.NOT_FOUND)
            .responseDesc("You were not invited").build();
        }
        ProjectUserBridge projectInviteData = projectInviteDbData.get();
        if(projectInviteData.getAcceptance() == 'a'){
            return BaseResponse.builder()
            .responseCode(HttpStatus.NOT_FOUND)
            .responseDesc("You have already accepted the ivite to project "+projectName).build();
        }
        projectInviteData.setAcceptance('a');
        projectUserBridgeRepo.save(projectInviteData);

        return BaseResponse.builder()
            .responseCode(HttpStatus.OK)
            .responseDesc("success")
            .data(null)
            .build();
    }

    public BaseResponse updateProjectServiceMethod(String projectName, ProjectModel projectUpdateRequest){
        Optional<ProjectEntity> projectDbdata = projectRepo.findByProjectName(projectName);

        if(!projectDbdata.isPresent()){
            return BaseResponse.builder()
                .responseCode(HttpStatus.NOT_FOUND)
                .responseDesc("Project Does not Exists").build();
        }

        ProjectEntity projectData = projectDbdata.get();

        if(projectUpdateRequest.getProjectStatus() != null && !projectUpdateRequest.getProjectStatus().equals(projectData.getProjectStatus())){
            projectData.setProjectStatus(projectUpdateRequest.getProjectStatus());
            projectData.setProjectStatusUpdatedDate(new Date(System.currentTimeMillis()));
        }

        projectData.setProjectName(projectUpdateRequest.getProjectName()!=null && !projectUpdateRequest.getProjectName().isEmpty()?
            projectUpdateRequest.getProjectName():projectData.getProjectName()
        );

        projectData.setProjectDescription(projectUpdateRequest.getProjectDescription()!=null && !projectUpdateRequest.getProjectDescription().isEmpty()?
            projectUpdateRequest.getProjectDescription():projectData.getProjectDescription()
        );

        projectData.setProjectStatus(projectUpdateRequest.getProjectStatus()!=null && !projectUpdateRequest.getProjectStatus().isEmpty()?
            projectUpdateRequest.getProjectStatus():projectData.getProjectStatus()
        );
        
        projectRepo.save(projectData);

        return BaseResponse.builder()
            .responseCode(HttpStatus.OK)
            .responseDesc("success")
            .data(null)
            .build();
    }


    public BaseResponse getDeleteProjectServiceMethod(String projectName){
        Optional<ProjectEntity> projectDbdata = projectRepo.findByProjectName(projectName);

        if(!projectDbdata.isPresent()){
            return BaseResponse.builder()
                .responseCode(HttpStatus.NOT_FOUND)
                .responseDesc("Project Does not Exists").build();
        }

        List<ProjectTask> projectTasksList = tasksRepo.findByProjectIdFk(projectDbdata.get().getProjectId());

        if(!projectTasksList.isEmpty()){

            List<TaskUserBridge> tasUserList = taskUserBridgeRepo.findByTaskIdFkIn(projectTasksList.stream()
                                             .map(ProjectTask::getTaskId)  
                                             .collect(Collectors.toList()));
            if(!tasUserList.isEmpty()){
                taskUserBridgeRepo.deleteAll(tasUserList);
            }

            tasksRepo.deleteAll(projectTasksList);
        }

        projectRepo.delete(projectDbdata.get());
        
        return BaseResponse.builder()
            .responseCode(HttpStatus.OK)
            .responseDesc("success")
            .data(null)
            .build();
    }

    public BaseResponse getAllProjects(){
        return BaseResponse.builder()
            .responseCode(HttpStatus.OK)
            .responseDesc("success")
            .data(projectRepo.findAll())
            .build();
    }


    public BaseResponse getProject(String projectName){

        Optional<ProjectEntity> projectDbdata = projectRepo.findByProjectName(projectName);

        if(!projectDbdata.isPresent()){
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
}
