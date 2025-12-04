package com.project.projectmanagment.models.task;

import java.sql.Date;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

@JsonIgnoreProperties(ignoreUnknown = true)
@Data
public class TasksModel {
    private String taskName;
    private String taskDescription;
    private String taskStatus;
    private String priority; // LOW, MEDIUM, HIGH, CRITICAL
    private Date taskDueDate; // Date d'échéance
    private String projectName;
    private String creatorEmail;
}