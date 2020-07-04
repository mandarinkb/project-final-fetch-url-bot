package com.projectfinalfetchurlbot;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling  //เปิดใช้งาน
public class ProjectFinalFetchUrlBotApplication {
	public static void main(String[] args) {
		SpringApplication.run(ProjectFinalFetchUrlBotApplication.class, args);
	}
}
