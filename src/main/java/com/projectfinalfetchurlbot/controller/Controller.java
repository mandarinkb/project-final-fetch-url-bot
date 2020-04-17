package com.projectfinalfetchurlbot.controller;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.projectfinalfetchurlbot.dao.Redis;
import com.projectfinalfetchurlbot.function.DateTimes;
import com.projectfinalfetchurlbot.function.Query;
import com.projectfinalfetchurlbot.service.ServiceTescolotus;

import redis.clients.jedis.Jedis;

@Component
public class Controller {

    @Autowired
    private DateTimes dateTimes;

    @Autowired
    private ServiceTescolotus serviceTescolotus;
    
    @Autowired
    private Redis rd;
    
    @Autowired
    private Query q;

    //@Scheduled(cron = "#{@cronExpression_1}") 
    @Scheduled(cron = "0 0/1 * 1/1 * ?") // เรียกใช้งานทุกๆ 1 นาที
    public void runTask_1() {   	
        System.out.println(dateTimes.interDateTime() + " : fetch url bot db_1 start");        
        String db1 = q.StrExcuteQuery("select DATABASE_STATUS from SWITCH_DATABASE where DATABASE_NAME = 'db_1';");
        String str = "1";
        // เช็คสถานะการสลับ database ว่าให้ db ไหนทำงาน
        if(db1.equals(str)) {
        	task("db_1");
        }
            
        System.out.println(dateTimes.interDateTime() + " : fetch url bot db_1 stop");
    }

    //@Scheduled(cron = "#{@cronExpression_2}") 
    @Scheduled(cron = "0 0/1 * 1/1 * ?") // เรียกใช้งานทุกๆ 1 นาที
    public void runTask_2() {
    	System.out.println(dateTimes.interDateTime() + " : fetch url bot db_2 start");
        String db2 = q.StrExcuteQuery("select DATABASE_STATUS from SWITCH_DATABASE where DATABASE_NAME = 'db_2';");
        String str = "1";
        if(db2.equals(str)) {
        	task("db_2");
        }
        System.out.println(dateTimes.interDateTime() + " : fetch url bot db_2 stop");
    }
    
    public void task(String dbName) {
        Jedis redis = rd.connect();
        boolean checkStartUrl = true;
        boolean checkCategorytUrl = true;
        // หา url เริ่มต้นจาก db
        while (checkStartUrl) {
        	String obj = redis.rpop("startUrl");
        	if (obj != null) {
            	JSONObject json = new JSONObject(obj);
            	json.put("database", dbName);
            	obj = json.toString();
        		serviceTescolotus.classifyCategoryUrl(obj);
            } else {
            	checkStartUrl = false;
            }
        }
        // หาประเภทของ url
        while (checkCategorytUrl) {
        	String obj = redis.rpop("categorytUrl");
        	if (obj != null) {
        		serviceTescolotus.categoryUrlDetail(obj);
            } else {
            	checkCategorytUrl = false;
            }
        }
    }
}
