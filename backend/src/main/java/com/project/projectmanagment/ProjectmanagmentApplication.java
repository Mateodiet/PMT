package com.project.projectmanagment;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan("com.project.*")
@EntityScan(basePackages = { "com.project.*" })
public class ProjectmanagmentApplication {

	public static void main(String[] args) {
		SpringApplication.run(ProjectmanagmentApplication.class, args);
		System.out.println("Hello\n URL http://127.0.0.1:8081/");
	}

}
