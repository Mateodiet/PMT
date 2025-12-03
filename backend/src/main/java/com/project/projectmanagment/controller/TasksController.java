package com.project.projectmanagment.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.project.projectmanagment.models.response.BaseResponse;
import com.project.projectmanagment.models.task.TaskUserAssignModel;
import com.project.projectmanagment.models.task.TasksModel;
import com.project.projectmanagment.services.TaskService;

import lombok.AllArgsConstructor;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;




@CrossOrigin(origins = "*", allowedHeaders = "*")
@RestController
@RequestMapping("/api/tasks")
@AllArgsConstructor
public class TasksController {
    
    private final TaskService taskService;

    @PostMapping("/createtask")
    public ResponseEntity<?> createTask(@RequestBody TasksModel tasksRequest) {
        BaseResponse response = taskService.createTask(tasksRequest);
        return new ResponseEntity<>(response, response.getResponseCode());
    }
    
    @PutMapping("/updateTask/{taskName}")
    public ResponseEntity<?> updateTask(@PathVariable String taskName, @RequestBody TasksModel updatetaskRequest) {
        BaseResponse response = taskService.updateTask(taskName, updatetaskRequest);
        return new ResponseEntity<>(response, response.getResponseCode());
    }

    @DeleteMapping("/deleteTask/{taskName}")
    public ResponseEntity<?> deletetask(@PathVariable String taskName){
        BaseResponse response = taskService.deleteTask(taskName);
        return new ResponseEntity<>(response, response.getResponseCode());
    }

    @GetMapping("/getAllTasks")
    public ResponseEntity<?> getMethodName() {
        BaseResponse response = taskService.getAllTasks();
        return new ResponseEntity<>(response, response.getResponseCode());
    }

    @GetMapping("/getTask")
    public ResponseEntity<?> getTask(@RequestParam String taskName) {
        BaseResponse response = taskService.getTask(taskName);
        return new ResponseEntity<>(response, response.getResponseCode());
    }

    @GetMapping("/getTaskByProject")
    public ResponseEntity<?> getTaskAgainstProject(@RequestParam String projectName) {
        BaseResponse response = taskService.getTaskAgainstProject(projectName);
        return new ResponseEntity<>(response, response.getResponseCode());
    }
    
    @PostMapping("/assignTask")
    public ResponseEntity<?> assignTask(@RequestBody TaskUserAssignModel assignUserTask) {
        BaseResponse response = taskService.assigntask(assignUserTask);
        return new ResponseEntity<>(response, response.getResponseCode());
    }
    
    @GetMapping("/getAssignedTasks")
    public ResponseEntity<?> getAssignedTasks() {
        BaseResponse response = taskService.getAssigntask();
        return new ResponseEntity<>(response, response.getResponseCode());
    }
    
    @GetMapping("/getAssignedToUser")
    public ResponseEntity<?> getTasksAssignedToUsers(@RequestParam String email) {
        BaseResponse response = taskService.getAssignedToTask(email);
        return new ResponseEntity<>(response, response.getResponseCode());
    }
    
    // get tasks by specific projects
    // get task by project
    // completed task
    // assign task
    
}
