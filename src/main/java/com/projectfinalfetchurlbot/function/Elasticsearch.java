package com.projectfinalfetchurlbot.function;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;

@Service
public class Elasticsearch {
    @Value("${elasticsearch_ip}")
    private String elasticsearch_ip;
    
    @Value("${index_web_scrapping_category}")
    private String index_category;
    
    public void inputElasticsearch(String body,String index) {
        try {
            HttpResponse<String> response = Unirest.post(elasticsearch_ip+index+"/text")
                    .header("Content-Type", "application/json")
                    .header("Cache-Control", "no-cache")
                    .body(body)
                    .asString();
        } catch (UnirestException ex) {
            Logger.getLogger(Elasticsearch.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public void deleteIndex(String index) {
        try {
            HttpResponse<String> response = Unirest.delete(elasticsearch_ip+index)
                    .header("Accept", "*/*")
                    .header("cache-control", "no-cache")
                    .asString();
        } catch (UnirestException ex) {
            Logger.getLogger(Elasticsearch.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public String getCategory(String category) {
        String elsValue = null;
        try {
        	Unirest.setTimeouts(0, 0);
        	HttpResponse<String> response = Unirest.post(elasticsearch_ip+index_category+"/_search")
        	  .header("Content-Type", "application/json")
        	  .body("{\"query\": {\"bool\": {\"must\": {\"match_phrase\": {\"tag\": \""+category+"\"}}}}}")
        	  .asString();

            elsValue = response.getBody();
        } catch (UnirestException ex) {
            Logger.getLogger(Elasticsearch.class.getName()).log(Level.SEVERE, null, ex);
        }
    	
        JSONObject objResultsValue = new JSONObject(elsValue);
        JSONObject objHits = objResultsValue.getJSONObject("hits");
        JSONArray arrHits = objHits.getJSONArray("hits");

        String newCategory = null;
        for (int i = 0; i < arrHits.length(); i++) {
            JSONObject objSource = arrHits.getJSONObject(i).getJSONObject("_source");	            
            newCategory = objSource.getString("category");
        }
    	return newCategory;	
    }

}
