package com.project.projectmanagment.models.task;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

@JsonIgnoreProperties(ignoreUnknown = true)
@Data
public class TasksModel {
    private String taskName;
    private String taskDescription;
    private String taskStatus;
    private String projectName;
    private String creatorEmail;
}
