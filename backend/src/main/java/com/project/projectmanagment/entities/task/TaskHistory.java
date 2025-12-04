package com.project.projectmanagment.entities.task;

import java.sql.Timestamp;

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
@Table(name = "task_history_tl")
public class TaskHistory {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long historyId;

    private Long taskIdFk; // FK reference to task
    private Long modifiedByUserId; // FK reference to user who made the change
    private String modifiedByEmail; // Email of user who made the change
    
    private String fieldName; // Name of the field that was changed
    private String oldValue; // Previous value
    private String newValue; // New value
    
    @Column(length = 500)
    private String changeDescription; // Human-readable description
    
    private Timestamp modifiedAt;

    @PrePersist
    public void prePersist() {
        this.modifiedAt = new Timestamp(System.currentTimeMillis());
    }
}