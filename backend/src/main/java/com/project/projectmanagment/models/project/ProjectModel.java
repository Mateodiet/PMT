package com.project.projectmanagment.models.project;

import java.sql.Date;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProjectModel {
    private String projectName;
    private String projectDescription;
    private Date projectStartDate;
    private String projectStatus;
    private String creatorEmail;
}