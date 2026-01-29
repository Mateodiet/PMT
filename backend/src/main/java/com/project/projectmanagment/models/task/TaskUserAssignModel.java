package com.project.projectmanagment.models.task;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TaskUserAssignModel {
    private String taskName;
    private String userEmail;
    private String assignedByEmail;
}