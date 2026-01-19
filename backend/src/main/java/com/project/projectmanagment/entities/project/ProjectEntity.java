package com.project.projectmanagment.entities.project;



import java.sql.Date;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "project_tl")
public class ProjectEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long projectId;

    @Column(unique = true)
    private String projectName;
    private String projectDescription;
    private Date projectCreatedDate;
    private Date projectStatusUpdatedDate;
    private String projectStatus;
    private Long taskCreatedBy;// FK points to user table userID

    @PrePersist
    public void prePersist() {
        this.projectCreatedDate = new Date(System.currentTimeMillis());
    }
    
}
