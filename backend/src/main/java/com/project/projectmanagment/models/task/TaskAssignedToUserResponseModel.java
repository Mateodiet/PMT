package com.project.projectmanagment.models.task;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.project.projectmanagment.entities.task.ProjectTask;

import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class TaskAssignedToUserResponseModel {
    private String email;
    private String name;
    private ProjectTask tasks;
}
