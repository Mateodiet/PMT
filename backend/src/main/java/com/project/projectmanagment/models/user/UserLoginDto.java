package com.project.projectmanagment.models.user;

import lombok.Data;

@Data
public class UserLoginDto {
    private String email;
    private String password;
}
