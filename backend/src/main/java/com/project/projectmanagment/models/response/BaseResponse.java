package com.project.projectmanagment.models.response;

import org.springframework.http.HttpStatus;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BaseResponse {
    private HttpStatus responseCode;
    private String responseDesc;
    private Object data;
}
