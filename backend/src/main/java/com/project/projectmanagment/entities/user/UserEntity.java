package com.project.projectmanagment.entities.user;

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
@Table(name = "user_tl")
public class UserEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)  // CHANGÉ : AUTO → IDENTITY
    private Long userId;
    private String name;
    @Column(unique = true)
    private String email;
    private String password;
    private String role;
    private String contactNumber;
    private Date createdAt;
    private boolean isActive=true;

    @PrePersist
    public void prePersist() {
        this.createdAt = new Date(System.currentTimeMillis());
    }
}