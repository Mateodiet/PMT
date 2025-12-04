package com.project.projectmanagment.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.project.projectmanagment.models.project.ProjectModel;
import com.project.projectmanagment.models.response.BaseResponse;
import com.project.projectmanagment.services.ProjectService;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;




@CrossOrigin(origins = "*", allowedHeaders = "*")
@RestController
@RequestMapping("/api/project")
public class ProjectController {
    private final ProjectService projectService;

    public ProjectController(ProjectService projectService){
        this.projectService = projectService;
    }
    
    @PostMapping("/create")
    public ResponseEntity<BaseResponse> insertUser(@RequestBody ProjectModel projectRequest) {
        BaseResponse response = projectService.createProject(projectRequest);
        return new ResponseEntity<>(response, response.getResponseCode());
    }

    @GetMapping("/invite/{email}/{projectName}")
    public ResponseEntity<BaseResponse> inviteUser(@PathVariable String email, @PathVariable String projectName) {
        BaseResponse response = projectService.invite(email, projectName);
        return new ResponseEntity<>(response, response.getResponseCode());
    }

    @GetMapping("/projectInviteAccept/{email}/{projectName}")
    public ResponseEntity<BaseResponse> getMethodName(@PathVariable String email, @PathVariable String projectName) {
        BaseResponse response = projectService.acceptInvitation(email, projectName);
        return new ResponseEntity<>(response, response.getResponseCode());
    }
    
    @PutMapping("/updateProject/{projectName}")
    public ResponseEntity<?> updateProject(@PathVariable String projectName, @RequestBody ProjectModel projectUpdateRequest) {
        BaseResponse response = projectService.updateProjectServiceMethod(projectName, projectUpdateRequest);
        return new ResponseEntity<>(response, response.getResponseCode());
    }

    @DeleteMapping("/deleteProject/{projectName}")
    public ResponseEntity<?> deleteProject(@PathVariable String projectName){
        BaseResponse response = projectService.getDeleteProjectServiceMethod(projectName);
        return new ResponseEntity<>(response, response.getResponseCode());
    }

    @GetMapping("/getAllProjects")
    public  ResponseEntity<?> getAll() {
        BaseResponse response = projectService.getAllProjects();
        return new ResponseEntity<>(response, response.getResponseCode());
    }

    @GetMapping("/getProject")
    public  ResponseEntity<?> getProject(@RequestParam String projectName ) {
        BaseResponse response = projectService.getProject(projectName);
        return new ResponseEntity<>(response, response.getResponseCode());
    }
    

    @GetMapping("/inviteWithRole/{email}/{projectName}/{role}")
    public ResponseEntity<BaseResponse> inviteWithRole(
            @PathVariable String email, 
            @PathVariable String projectName,
            @PathVariable String role){
        BaseResponse response = projectService.inviteWithRole(email, projectName, role, null);
        return ResponseEntity.status(response.getResponseCode()).body(response);
    }

    @PutMapping("/updateMemberRole/{email}/{projectName}/{role}")
    public ResponseEntity<BaseResponse> updateMemberRole(
            @PathVariable String email, 
            @PathVariable String projectName,
            @PathVariable String role){
        BaseResponse response = projectService.updateMemberRole(email, projectName, role);
        return ResponseEntity.status(response.getResponseCode()).body(response);
    }

    @GetMapping("/getProjectMembers/{projectName}")
    public ResponseEntity<BaseResponse> getProjectMembers(@PathVariable String projectName){
        BaseResponse response = projectService.getProjectMembers(projectName);
        return ResponseEntity.status(response.getResponseCode()).body(response);
    }
}
