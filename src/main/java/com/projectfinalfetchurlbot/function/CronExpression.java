package com.projectfinalfetchurlbot.function;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


@Service
public class CronExpression {
    @Autowired
    private Query query;
        
    public String cronExpressionTask_1(){
       return query.StrExcuteQuery("SELECT Cron_expression FROM Schedule WHERE Project_name = 'web scrapping' AND Function_name = 'runTask_1'"); 
    }
    public String cronExpressionTask_2(){
       return query.StrExcuteQuery("SELECT Cron_expression FROM Schedule WHERE Project_name = 'web scrapping' AND Function_name = 'runTask_2'");  
    }
}
