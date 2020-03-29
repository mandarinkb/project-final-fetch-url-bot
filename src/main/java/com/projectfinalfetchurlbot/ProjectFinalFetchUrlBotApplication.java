package com.projectfinalfetchurlbot;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableScheduling;

import com.projectfinalfetchurlbot.function.CronExpression;


@SpringBootApplication
@EnableScheduling  //เปิดใช้งาน
public class ProjectFinalFetchUrlBotApplication {
    @Autowired
    private CronExpression cron;
	public static void main(String[] args) {
		SpringApplication.run(ProjectFinalFetchUrlBotApplication.class, args);
	}
	
    @Bean
    public String cronExpression_1() {
        return cron.cronExpressionTask_1();
    }
    @Bean
    public String cronExpression_2() {
        return cron.cronExpressionTask_2();
    }	

}
