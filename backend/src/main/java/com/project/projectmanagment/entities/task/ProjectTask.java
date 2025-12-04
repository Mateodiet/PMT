package com.project.projectmanagment.entities.task;

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
@Table(name = "project_task_tl")
public class ProjectTask {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long taskId;

    @Column(unique = true)
    private String taskName;
    private String taskDescription;
    private String taskStatus;
    private String priority; // LOW, MEDIUM, HIGH, CRITICAL
    private Date taskDueDate; // Date d'échéance
    private Date taskCreatedAt;
    private Date taskCompletedAt; // Date de fin (quand status = DONE)
    private Long projectIdFk;
    private Long taskCreatedBy; // FK reference to user table

    @PrePersist
    public void prePersist() {
        this.taskCreatedAt = new Date(System.currentTimeMillis());
        if (this.priority == null) {
            this.priority = "MEDIUM";
        }
    }
}