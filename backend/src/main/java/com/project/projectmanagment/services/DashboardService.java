package com.project.projectmanagment.services;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.project.projectmanagment.entities.task.ProjectTask;
import com.project.projectmanagment.models.response.BaseResponse;
import com.project.projectmanagment.repositories.project.ProjectRepo;
import com.project.projectmanagment.repositories.task.TasksRepo;

import lombok.AllArgsConstructor;

@AllArgsConstructor
@Service
public class DashboardService {
    
    private final ProjectRepo projectRepo;
    private final TasksRepo tasksRepo;

    public BaseResponse getAllProjectsCount(){
        Map<String, Object> projectData = new HashMap<>();
        projectData.put("count", projectRepo.count());
        projectData.put("projectList", projectRepo.findAll());
        
        return BaseResponse.builder()
            .responseCode(HttpStatus.OK)
            .responseDesc("success")
            .data(projectData)
            .build();
    }

    public BaseResponse getInProgressTasks(String taskStatus){

        List<ProjectTask> tasksDataByStatus = tasksRepo.findByTaskStatus(taskStatus);

        long countByTask = tasksRepo.countByTaskStatus(taskStatus);

        Map<String, Object> projectData = new HashMap<>();

        projectData.put("count", countByTask);
        projectData.put("projectList", tasksDataByStatus);

        return BaseResponse.builder()
            .responseCode(HttpStatus.OK)
            .responseDesc("success")
            .data(projectData)
            .build();
    }
}
