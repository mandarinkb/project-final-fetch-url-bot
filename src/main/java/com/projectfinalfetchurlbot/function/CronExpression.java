package com.projectfinalfetchurlbot.function;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


@Service
public class CronExpression {
    @Autowired
    private Query query;
        
    public String cronExpressionTask_1(){
       return query.StrExcuteQuery("SELECT cron_expression FROM schedule WHERE project_name = 'web scrapping' AND function_name = 'runTask_1'"); 
    }
    public String cronExpressionTask_2(){
       return query.StrExcuteQuery("SELECT cron_expression FROM schedule WHERE project_name = 'web scrapping' AND function_name = 'runTask_2'");  
    }
}
