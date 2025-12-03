package com.project.projectmanagment.entities.bridges;

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
@Table(name = "task_user_bridge_tl")
public class TaskUserBridge {
    @Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long taskUserId;

    private Long userIdFK;
    private Long taskIdFk;
    private Date assignmentDate;

    @PrePersist
    public void prePersist() {
        this.assignmentDate = new Date(System.currentTimeMillis());
    }
}
