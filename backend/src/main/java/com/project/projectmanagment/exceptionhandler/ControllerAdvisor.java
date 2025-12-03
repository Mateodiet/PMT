package com.project.projectmanagment.exceptionhandler;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@ControllerAdvice
public class ControllerAdvisor extends ResponseEntityExceptionHandler {
    
    	@ExceptionHandler(Exception.class)
	public ResponseEntity<Object> handleGenericException(Exception ex, WebRequest request) {
        System.err.println(ex.toString());
        System.err.println(request);
		Map<String, Object> body = new HashMap<>();
		body.put("responseCode", "500");
		body.put("responseDesc", "error");
		body.put("data", null);

		return new ResponseEntity<>(body, HttpStatus.OK);
	}
}
