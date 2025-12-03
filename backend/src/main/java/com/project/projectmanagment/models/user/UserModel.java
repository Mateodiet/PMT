package com.project.projectmanagment.models.user;

import java.sql.Date;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.Data;

@JsonIgnoreProperties(ignoreUnknown = true)
@Data
public class UserModel {
    private String name;
    private String email;
    private String password;
    private String role;
    private String contactNumber;
    private Date createdAt;
    private Boolean isActive;
}
