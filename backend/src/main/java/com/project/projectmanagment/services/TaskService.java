package com.project.projectmanagment.services;

import java.sql.Date;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.project.projectmanagment.entities.bridges.TaskUserBridge;
import com.project.projectmanagment.entities.project.ProjectEntity;
import com.project.projectmanagment.entities.task.ProjectTask;
import com.project.projectmanagment.entities.user.UserEntity;
import com.project.projectmanagment.models.response.BaseResponse;
import com.project.projectmanagment.models.task.TaskAssignedToUserResponseModel;
import com.project.projectmanagment.models.task.TaskUserAssignModel;
import com.project.projectmanagment.models.task.TasksModel;
import com.project.projectmanagment.repositories.project.ProjectRepo;
import com.project.projectmanagment.repositories.project.ProjectUserBridgeRepo;
import com.project.projectmanagment.repositories.task.TaskUserBridgeRepo;
import com.project.projectmanagment.repositories.task.TasksRepo;
import com.project.projectmanagment.repositories.user.UserRepo;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class TaskService {
    
    private final ProjectRepo projectRepo;
    private final UserRepo userRepo;
    private final ProjectUserBridgeRepo projectUserBridgeRepo;
    private final TaskUserBridgeRepo taskUserBridgeRepo;
    private final TasksRepo tasksRepo;

    public BaseResponse createTask(TasksModel taskRequest){
        Optional<ProjectTask> getTaskDataDb = tasksRepo.findByTaskName(taskRequest.getTaskName());
        if(getTaskDataDb.isPresent()){
            return BaseResponse.builder()
                .responseCode(HttpStatus.CONFLICT)
                .responseDesc("Task Already Exists").build();
        }

        Optional<ProjectEntity> projectData = projectRepo.findByProjectName(taskRequest.getProjectName());

        if(!projectData.isPresent()){
            return BaseResponse.builder()
                .responseCode(HttpStatus.NOT_FOUND)
                .responseDesc("Project Does not Exists").build();
        }

        Optional<UserEntity> userDbData = userRepo.findByEmail(taskRequest.getCreatorEmail());

        if(!userDbData.isPresent()){
            return BaseResponse.builder()
            .responseCode(HttpStatus.NOT_FOUND)
            .responseDesc("This user does not exists").build();
        }

        ProjectTask task = tasksRepo.save(
            ProjectTask.builder()
            .taskName(taskRequest.getTaskName())
            .taskDescription(taskRequest.getTaskDescription())
            .taskStatus(taskRequest.getTaskStatus())
            .taskCreatedAt(new Date(System.currentTimeMillis()))
            .projectIdFk(projectData.get().getProjectId())
            .taskCreatedBy(userDbData.get().getUserId())
            .build()
        );

        return BaseResponse.builder()
        .responseCode(HttpStatus.OK)
        .responseDesc("success")
        .data(task)
        .build();
    }

    public BaseResponse updateTask(String taskName, TasksModel taskRequest){

        Optional<ProjectTask> getTaskDataDb = tasksRepo.findByTaskName(taskRequest.getTaskName());
        if(!getTaskDataDb.isPresent()){
            return BaseResponse.builder()
                .responseCode(HttpStatus.NOT_FOUND)
                .responseDesc("Task Does not Exists").build();
        }

        ProjectTask task = getTaskDataDb.get();

        if(taskRequest.getProjectName()!=null || !taskRequest.getProjectName().isEmpty()){
            Optional<ProjectEntity> projectData = projectRepo.findByProjectName(taskRequest.getProjectName());
            if(projectData.isPresent()){
                task.setProjectIdFk(projectData.get().getProjectId());
            }
        }

        if(taskRequest.getCreatorEmail()!=null || !taskRequest.getCreatorEmail().isEmpty()){
            Optional<UserEntity> userDbData = userRepo.findByEmail(taskRequest.getCreatorEmail());

            if(userDbData.isPresent()){
                task.setTaskCreatedBy(userDbData.get().getUserId());
            }
        }

        task.setTaskName(
            taskRequest.getTaskName()==null || taskRequest.getProjectName().isEmpty()?
            task.getTaskName():taskRequest.getTaskName()
        );

        task.setTaskDescription(
            taskRequest.getTaskDescription()==null || taskRequest.getTaskDescription().isEmpty()?
            task.getTaskDescription():taskRequest.getTaskDescription()
        );

        task.setTaskStatus(
            taskRequest.getTaskStatus()==null || taskRequest.getTaskStatus().isEmpty()?
            task.getTaskStatus():taskRequest.getTaskStatus()
        );

        tasksRepo.save(task);

        return BaseResponse.builder()
        .responseCode(HttpStatus.OK)
        .responseDesc("success")
        .data(task)
        .build();
    }

    public BaseResponse deleteTask(String taskName){
        try{
            Optional<ProjectTask> getTaskDataDb = tasksRepo.findByTaskName(taskName);

            if(!getTaskDataDb.isPresent()){
                return BaseResponse.builder()
                    .responseCode(HttpStatus.NOT_FOUND)
                    .responseDesc("Task Does not Exists").build();
            }
    
            Optional<TaskUserBridge> taskBridge = taskUserBridgeRepo.findByTaskIdFk(getTaskDataDb.get().getTaskId());
            if(taskBridge.isPresent()){
                taskUserBridgeRepo.deleteByTaskIdFk(getTaskDataDb.get().getTaskId());
            }
    
            tasksRepo.deleteById(getTaskDataDb.get().getTaskId());
        }catch(Exception e){
            System.out.println(e);
            e.printStackTrace();
        }

        return BaseResponse.builder()
        .responseCode(HttpStatus.OK)
        .responseDesc("success")
        .build();
    }


    public BaseResponse getAllTasks(){
        return BaseResponse.builder()
        .responseCode(HttpStatus.OK)
        .responseDesc("success")
        .data(tasksRepo.findAll())
        .build();
    }

    public BaseResponse getTask(String taskName){
        Optional<ProjectTask> getTaskDataDb = tasksRepo.findByTaskName(taskName);
        if (!getTaskDataDb.isPresent()) {
            return BaseResponse.builder()
                .responseCode(HttpStatus.NOT_FOUND)
                .responseDesc("Task Does not Exists").build();
        }

        return BaseResponse.builder()
        .responseCode(HttpStatus.OK)
        .responseDesc("success")
        .data(getTaskDataDb.get())
        .build();
    }


    public BaseResponse getTaskAgainstProject(String projectName){

        Optional<ProjectEntity> projectDbdata = projectRepo.findByProjectName(projectName);

        if(!projectDbdata.isPresent()){
            return BaseResponse.builder()
                .responseCode(HttpStatus.NOT_FOUND)
                .responseDesc("Project Does not Exists").build();
        }


        List<ProjectTask> getTaskDataDb = tasksRepo.findByProjectIdFk(projectDbdata.get().getProjectId());
        if (getTaskDataDb.isEmpty()) {
            return BaseResponse.builder()
                .responseCode(HttpStatus.NOT_FOUND)
                .responseDesc("Task Does not Exists").build();
        }
        
        return BaseResponse.builder()
        .responseCode(HttpStatus.OK)
        .responseDesc("success")
        .data(getTaskDataDb)
        .build();
    }

    public BaseResponse assigntask(TaskUserAssignModel assignUserTask){
        Optional<ProjectTask> getTaskDataDb = tasksRepo.findByTaskName(assignUserTask.getTaskName());
        if (!getTaskDataDb.isPresent()) {
            return BaseResponse.builder()
                .responseCode(HttpStatus.NOT_FOUND)
                .responseDesc("Task Does not Exists").build();
        }

        Optional<UserEntity> userDbData = userRepo.findByEmail(assignUserTask.getUserEmail());

        if(!userDbData.isPresent()){
            return BaseResponse.builder()
            .responseCode(HttpStatus.NOT_FOUND)
            .responseDesc("This user does not exists").build();
        }


        Optional<TaskUserBridge> taskUserBridgeData = taskUserBridgeRepo.findByUserIdFKAndTaskIdFk(userDbData.get().getUserId(), getTaskDataDb.get().getTaskId());

        if(taskUserBridgeData.isPresent()){
            return BaseResponse.builder()
                .responseCode(HttpStatus.CONFLICT)
                .responseDesc("Task Already Assigned").build();
        }

        taskUserBridgeRepo.save(
            TaskUserBridge.builder()
            .userIdFK(userDbData.get().getUserId())
            .taskIdFk(getTaskDataDb.get().getTaskId())
            .assignmentDate(new Date(System.currentTimeMillis()))
            .build()
        );

        return BaseResponse.builder()
        .responseCode(HttpStatus.OK)
        .responseDesc("success")
        .data(getTaskDataDb)
        .build();
    }

    public BaseResponse getAssigntask(){

        List<ProjectTask> getTaskDataDb = tasksRepo.findAll();
        if (getTaskDataDb.isEmpty()) {
            return BaseResponse.builder()
                .responseCode(HttpStatus.NOT_FOUND)
                .responseDesc("Task Does not Exists").build();
        }

        List<TaskUserBridge> assignedTaskList = taskUserBridgeRepo.findByTaskIdFkIn(getTaskDataDb.stream()
                                  .map(ProjectTask::getTaskId)
                                  .collect(Collectors.toList()));
        
        if (assignedTaskList.isEmpty()) {
            return BaseResponse.builder()
                .responseCode(HttpStatus.NOT_FOUND)
                .responseDesc("Task Does not Exists").build();
        }

        // assigned list
        List<ProjectTask> assigedTasksDetailList = tasksRepo.findByTaskIdIn(
            assignedTaskList.stream()
                .map(TaskUserBridge::getTaskIdFk)  // Extract taskIdFk from each TaskUserBridge
                .collect(Collectors.toList())
        );

        if (assigedTasksDetailList.isEmpty()) {
            return BaseResponse.builder()
                .responseCode(HttpStatus.NOT_FOUND)
                .responseDesc("Task Does not Exists").build();
        }

        // Assigned to users
        List<UserEntity> tasksAssigedToUsers = userRepo.findByUserIdIn(
            assignedTaskList.stream()
                .map(TaskUserBridge::getUserIdFK)  // Extract taskIdFk from each TaskUserBridge
                .collect(Collectors.toList())
        );

        List<TaskAssignedToUserResponseModel> userTaskList = new ArrayList<>();

        for(TaskUserBridge usertask : assignedTaskList){
            Optional<UserEntity> userEntity = tasksAssigedToUsers.stream()
            .filter(user -> user.getUserId().equals(usertask.getUserIdFK())) // Filter by userId
            .findFirst();

            Optional<ProjectTask> projectTask = assigedTasksDetailList.stream()
                .filter(task -> task.getTaskId().equals(usertask.getTaskIdFk())) // Filter by taskId
                .findFirst(); // Get the first match

            if(projectTask.isPresent() && userEntity.isPresent()){
                TaskAssignedToUserResponseModel tmpObj = new TaskAssignedToUserResponseModel();
                tmpObj.setEmail(userEntity.get().getEmail());
                tmpObj.setName(userEntity.get().getName());
                tmpObj.setTasks(projectTask.get());
                userTaskList.add(tmpObj);
            }

        }

        return BaseResponse.builder()
        .responseCode(HttpStatus.OK)
        .responseDesc("success")
        .data(userTaskList)
        .build();
    }

    public BaseResponse getAssignedToTask(String email){
        Optional<UserEntity> user = userRepo.findByEmail(email);

        if(!user.isPresent()){
            return BaseResponse.builder()
                .responseCode(HttpStatus.NOT_FOUND)
                .responseDesc("User Does not Exists").build();
        }

        List<TaskUserBridge> taskUserData = taskUserBridgeRepo.findByUserIdFK(user.get().getUserId());


        if(taskUserData.isEmpty()){
            return BaseResponse.builder()
            .responseCode(HttpStatus.NOT_FOUND)
            .responseDesc("Task Does not Exists").build();
        }

        List<ProjectTask> assignedtaskToUsers = tasksRepo.findByTaskIdIn(
            taskUserData.stream()
            .map(TaskUserBridge::getTaskIdFk)  // Extract taskIdFk from each TaskUserBridge
            .collect(Collectors.toList())
        );

        if(assignedtaskToUsers.isEmpty()){
            return BaseResponse.builder()
            .responseCode(HttpStatus.NOT_FOUND)
            .responseDesc("Task Does not Exists").build();
        }

        Map<String, Object> assigednTasks = new HashMap<>();
        assigednTasks.put("name", user.get().getName());
        assigednTasks.put("email", user.get().getEmail());
        assigednTasks.put("taskList", assignedtaskToUsers);

        return BaseResponse.builder()
        .responseCode(HttpStatus.OK)
        .responseDesc("success")
        .data(assigednTasks)
        .build();
    }
    
}
