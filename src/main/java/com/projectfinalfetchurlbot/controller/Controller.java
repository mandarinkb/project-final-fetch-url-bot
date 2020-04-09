package com.projectfinalfetchurlbot.controller;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.projectfinalfetchurlbot.dao.Redis;
import com.projectfinalfetchurlbot.function.DateTimes;
import com.projectfinalfetchurlbot.service.ServiceTescolotus;
import com.projectfinalfetchurlbot.service.ServiceWeb;

import redis.clients.jedis.Jedis;

@Component
public class Controller {

    @Autowired
    private DateTimes dateTimes;

    @Autowired
    private ServiceWeb serviceWeb;

    @Autowired
    private ServiceTescolotus serviceTescolotus;
    
    @Autowired
    private Redis rd;

    @Scheduled(cron = "#{@cronExpression_1}") 
    public void runTask_1() {
        System.out.println(dateTimes.interDateTime() + " : web scrapping runTask_1 start");
        serviceWeb.start();
        Jedis redis = rd.connect();
        boolean checkStartUrl = true;
        boolean checkCategorytUrl = true;
        while (checkStartUrl) {
        	String obj = redis.rpop("startUrl");
        	if (obj != null) {
        		serviceTescolotus.classifyCategoryUrl(obj);
            } else {
            	checkStartUrl = false;
            }
        }
        
        while (checkCategorytUrl) {
        	String obj = redis.rpop("categorytUrl");
        	if (obj != null) {
        		serviceTescolotus.categoryUrlDetail(obj);
            } else {
            	checkCategorytUrl = false;
            }
        }
              
        System.out.println(dateTimes.interDateTime() + " : web scrapping runTask_1 stop");
    }

    @Scheduled(cron = "#{@cronExpression_2}") 
    public void runTask_2() {

    }

}
