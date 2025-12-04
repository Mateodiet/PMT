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
import com.project.projectmanagment.entities.task.TaskHistory;
import com.project.projectmanagment.entities.user.UserEntity;
import com.project.projectmanagment.models.response.BaseResponse;
import com.project.projectmanagment.models.task.TaskAssignedToUserResponseModel;
import com.project.projectmanagment.models.task.TaskUserAssignModel;
import com.project.projectmanagment.models.task.TasksModel;
import com.project.projectmanagment.repositories.project.ProjectRepo;
import com.project.projectmanagment.repositories.project.ProjectUserBridgeRepo;
import com.project.projectmanagment.repositories.task.TaskHistoryRepo;
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
    private final TaskHistoryRepo taskHistoryRepo;
    private final EmailService emailService;

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
            .priority(taskRequest.getPriority() != null ? taskRequest.getPriority() : "MEDIUM")
            .taskDueDate(taskRequest.getTaskDueDate())
            .taskCreatedAt(new Date(System.currentTimeMillis()))
            .projectIdFk(projectData.get().getProjectId())
            .taskCreatedBy(userDbData.get().getUserId())
            .build()
        );

        // Enregistrer dans l'historique - Création
        saveTaskHistory(task.getTaskId(), userDbData.get().getUserId(), userDbData.get().getEmail(),
                "creation", null, task.getTaskName(), "Tâche créée");

        return BaseResponse.builder()
        .responseCode(HttpStatus.OK)
        .responseDesc("success")
        .data(task)
        .build();
    }

    public BaseResponse updateTask(String taskName, TasksModel taskRequest){

        Optional<ProjectTask> getTaskDataDb = tasksRepo.findByTaskName(taskName);
        if(!getTaskDataDb.isPresent()){
            return BaseResponse.builder()
                .responseCode(HttpStatus.NOT_FOUND)
                .responseDesc("Task Does not Exists").build();
        }

        ProjectTask task = getTaskDataDb.get();
        
        // Récupérer l'utilisateur qui fait la modification
        Long modifiedByUserId = null;
        String modifiedByEmail = "system";
        if(taskRequest.getCreatorEmail() != null && !taskRequest.getCreatorEmail().isEmpty()){
            Optional<UserEntity> userDbData = userRepo.findByEmail(taskRequest.getCreatorEmail());
            if(userDbData.isPresent()){
                modifiedByUserId = userDbData.get().getUserId();
                modifiedByEmail = userDbData.get().getEmail();
            }
        }

        // Vérifier et enregistrer chaque modification
        if(taskRequest.getProjectName() != null && !taskRequest.getProjectName().isEmpty()){
            Optional<ProjectEntity> projectData = projectRepo.findByProjectName(taskRequest.getProjectName());
            if(projectData.isPresent()){
                task.setProjectIdFk(projectData.get().getProjectId());
            }
        }

        // Mise à jour du nom
        if(taskRequest.getTaskName() != null && !taskRequest.getTaskName().isEmpty() 
                && !taskRequest.getTaskName().equals(task.getTaskName())){
            saveTaskHistory(task.getTaskId(), modifiedByUserId, modifiedByEmail,
                    "taskName", task.getTaskName(), taskRequest.getTaskName(), "Nom de la tâche modifié");
            task.setTaskName(taskRequest.getTaskName());
        }

        // Mise à jour de la description
        if(taskRequest.getTaskDescription() != null && !taskRequest.getTaskDescription().isEmpty()
                && !taskRequest.getTaskDescription().equals(task.getTaskDescription())){
            saveTaskHistory(task.getTaskId(), modifiedByUserId, modifiedByEmail,
                    "taskDescription", task.getTaskDescription(), taskRequest.getTaskDescription(), "Description modifiée");
            task.setTaskDescription(taskRequest.getTaskDescription());
        }

        // Mise à jour du statut
        if(taskRequest.getTaskStatus() != null && !taskRequest.getTaskStatus().isEmpty()
                && !taskRequest.getTaskStatus().equals(task.getTaskStatus())){
            saveTaskHistory(task.getTaskId(), modifiedByUserId, modifiedByEmail,
                    "taskStatus", task.getTaskStatus(), taskRequest.getTaskStatus(), 
                    "Statut modifié de " + task.getTaskStatus() + " à " + taskRequest.getTaskStatus());
            task.setTaskStatus(taskRequest.getTaskStatus());
            
            // Si le statut passe à DONE, enregistrer la date de complétion
            if("DONE".equalsIgnoreCase(taskRequest.getTaskStatus())){
                task.setTaskCompletedAt(new Date(System.currentTimeMillis()));
            }
        }

        // Mise à jour de la priorité
        if(taskRequest.getPriority() != null && !taskRequest.getPriority().isEmpty()
                && !taskRequest.getPriority().equals(task.getPriority())){
            saveTaskHistory(task.getTaskId(), modifiedByUserId, modifiedByEmail,
                    "priority", task.getPriority(), taskRequest.getPriority(), 
                    "Priorité modifiée de " + task.getPriority() + " à " + taskRequest.getPriority());
            task.setPriority(taskRequest.getPriority());
        }

        // Mise à jour de la date d'échéance
        if(taskRequest.getTaskDueDate() != null && !taskRequest.getTaskDueDate().equals(task.getTaskDueDate())){
            String oldDate = task.getTaskDueDate() != null ? task.getTaskDueDate().toString() : "non définie";
            saveTaskHistory(task.getTaskId(), modifiedByUserId, modifiedByEmail,
                    "taskDueDate", oldDate, taskRequest.getTaskDueDate().toString(), "Date d'échéance modifiée");
            task.setTaskDueDate(taskRequest.getTaskDueDate());
        }

        tasksRepo.save(task);

        // Notifier les utilisateurs assignés de la mise à jour
        notifyAssignedUsers(task.getTaskId(), task.getTaskName(), "La tâche a été mise à jour");

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
    
            // Supprimer l'historique de la tâche
            List<TaskHistory> historyList = taskHistoryRepo.findByTaskIdFkOrderByModifiedAtDesc(getTaskDataDb.get().getTaskId());
            if(!historyList.isEmpty()){
                taskHistoryRepo.deleteAll(historyList);
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

        // Enregistrer dans l'historique
        saveTaskHistory(getTaskDataDb.get().getTaskId(), null, "system",
                "assignment", null, userDbData.get().getEmail(), 
                "Tâche assignée à " + userDbData.get().getName());

        // Récupérer le nom du projet pour l'email
        String projectName = "Projet";
        Optional<ProjectEntity> project = projectRepo.findById(getTaskDataDb.get().getProjectIdFk());
        if(project.isPresent()){
            projectName = project.get().getProjectName();
        }

        // Envoyer notification par email
        emailService.sendTaskAssignmentNotification(
            userDbData.get().getEmail(),
            getTaskDataDb.get().getTaskName(),
            projectName,
            assignUserTask.getAssignedByEmail() != null ? assignUserTask.getAssignedByEmail() : "Un administrateur"
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
                .map(TaskUserBridge::getTaskIdFk)
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
                .map(TaskUserBridge::getUserIdFK)
                .collect(Collectors.toList())
        );

        List<TaskAssignedToUserResponseModel> userTaskList = new ArrayList<>();

        for(TaskUserBridge usertask : assignedTaskList){
            Optional<UserEntity> userEntity = tasksAssigedToUsers.stream()
            .filter(user -> user.getUserId().equals(usertask.getUserIdFK()))
            .findFirst();

            Optional<ProjectTask> projectTask = assigedTasksDetailList.stream()
                .filter(task -> task.getTaskId().equals(usertask.getTaskIdFk()))
                .findFirst();

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
            .map(TaskUserBridge::getTaskIdFk)
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

    // ============================================
    // HISTORIQUE DES TÂCHES
    // ============================================

    /**
     * Récupère l'historique des modifications d'une tâche
     */
    public BaseResponse getTaskHistory(String taskName){
        Optional<ProjectTask> taskData = tasksRepo.findByTaskName(taskName);
        if(!taskData.isPresent()){
            return BaseResponse.builder()
                .responseCode(HttpStatus.NOT_FOUND)
                .responseDesc("Task Does not Exists").build();
        }

        List<TaskHistory> history = taskHistoryRepo.findByTaskIdFkOrderByModifiedAtDesc(taskData.get().getTaskId());

        Map<String, Object> response = new HashMap<>();
        response.put("taskName", taskName);
        response.put("taskId", taskData.get().getTaskId());
        response.put("history", history);

        return BaseResponse.builder()
        .responseCode(HttpStatus.OK)
        .responseDesc("success")
        .data(response)
        .build();
    }

    /**
     * Enregistre une modification dans l'historique
     */
    private void saveTaskHistory(Long taskId, Long userId, String email, 
            String fieldName, String oldValue, String newValue, String description){
        TaskHistory history = TaskHistory.builder()
            .taskIdFk(taskId)
            .modifiedByUserId(userId)
            .modifiedByEmail(email)
            .fieldName(fieldName)
            .oldValue(oldValue)
            .newValue(newValue)
            .changeDescription(description)
            .build();
        taskHistoryRepo.save(history);
    }

    /**
     * Notifie tous les utilisateurs assignés à une tâche
     */
    private void notifyAssignedUsers(Long taskId, String taskName, String updateDescription){
        List<TaskUserBridge> assignments = taskUserBridgeRepo.findByTaskIdFkIn(List.of(taskId));
        if(!assignments.isEmpty()){
            List<Long> userIds = assignments.stream()
                .map(TaskUserBridge::getUserIdFK)
                .collect(Collectors.toList());
            List<UserEntity> users = userRepo.findByUserIdIn(userIds);
            
            for(UserEntity user : users){
                emailService.sendTaskUpdateNotification(user.getEmail(), taskName, updateDescription);
            }
        }
    }
}