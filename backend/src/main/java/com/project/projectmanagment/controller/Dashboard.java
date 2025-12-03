package com.project.projectmanagment.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.project.projectmanagment.models.response.BaseResponse;
import com.project.projectmanagment.services.DashboardService;

import lombok.AllArgsConstructor;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

@AllArgsConstructor
@CrossOrigin(origins = "*", allowedHeaders = "*")
@RestController
@RequestMapping("/api/dashboard")
public class Dashboard {
    //  total projects api
    //  total tasks in progress
    //  total task completed

    private final DashboardService dashboardService;
    
    @GetMapping("/getTotalProjects")
    public ResponseEntity<?> getTotalProjects() {
        BaseResponse response = dashboardService.getAllProjectsCount();
        return new ResponseEntity<>(response, response.getResponseCode());
    }

    @GetMapping("/getTaskByStatus/{taskStatus}")
    public ResponseEntity<?> getTaskInProgress(@PathVariable String taskStatus) {
        BaseResponse response = dashboardService.getInProgressTasks(taskStatus);
        return new ResponseEntity<>(response, response.getResponseCode());
    }
      
}
