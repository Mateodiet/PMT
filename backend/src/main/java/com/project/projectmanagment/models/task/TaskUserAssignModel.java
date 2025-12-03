package com.project.projectmanagment.models.task;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.Data;

@JsonIgnoreProperties(ignoreUnknown = true)
@Data
public class TaskUserAssignModel {
    private String taskName;
    private String userEmail;
}
