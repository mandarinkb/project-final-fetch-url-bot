package com.projectfinalfetchurlbot.function;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ClearIndex {
    @Autowired
    private Elasticsearch els;  
    
    @Autowired
    private DateTimes dateTimes;
    
    public void deleteResultsBySeason(String season, String index) {
        try {
            List<String> listId = new ArrayList<>();
            // ค้นหา id ทั้งหมด
            String resultsValue = els.searchResultsBySeason(season, index);
            JSONObject objResultsValue = new JSONObject(resultsValue);
            JSONObject objHits = objResultsValue.getJSONObject("hits");
            JSONArray arrHits = objHits.getJSONArray("hits");
            for (int i = 0; i < arrHits.length(); i++) {
                String id = arrHits.getJSONObject(i).getString("_id");
                listId.add(id);
            }
            // ลบ docs by id
            for (String id : listId) {
                els.deleteResultsById(id, index);
            }
            System.out.println(dateTimes.interDateTime() + " : "+"delete docs in "+index+" season "+season+" success.");
        } catch (Exception e) {
            System.out.println("index not found.");
            System.out.println(e.getMessage());
        }
    }
    
}
