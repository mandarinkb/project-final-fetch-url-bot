package com.projectfinalfetchurlbot.function;

import org.json.JSONObject;
import org.springframework.stereotype.Service;

@Service
public class ApiResponse {
    public String status(int status , String meassage){
        JSONObject obj = new JSONObject();
        obj.put("status", status);
        obj.put("message", meassage);
        return obj.toString();
    }
}
