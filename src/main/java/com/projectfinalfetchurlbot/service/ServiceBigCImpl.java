package com.projectfinalfetchurlbot.service;
import org.json.JSONArray;
import org.json.JSONObject;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;
import com.projectfinalfetchurlbot.dao.Redis;
import com.projectfinalfetchurlbot.function.CategoryFilter;
import com.projectfinalfetchurlbot.function.DateTimes;
import com.projectfinalfetchurlbot.function.Elasticsearch;
import com.projectfinalfetchurlbot.function.OtherFunc;

import redis.clients.jedis.Jedis;

@Service
public class ServiceBigCImpl implements ServiceBigC{
	@Autowired
	private Elasticsearch els;
	
    @Autowired
    private Redis rd;  
    
    @Autowired
    private CategoryFilter categoryFilter;

    @Autowired
    private DateTimes dateTimes;
    
    @Autowired
    private OtherFunc otherFunc;

	@Override
	public void classifyCategoryUrl(String objStr) {
		Jedis redis = rd.connect();
		JSONObject json = new JSONObject(objStr);
		String url = json.getString("url");
		try {
			String elsValue = null;
			// เชื่อมต่อ bigc api
        	Unirest.setTimeouts(0, 0);
        	HttpResponse<String> response = Unirest.post("https://www.bigc.co.th/api/categories/mainCategory?_store=2")
        	  .header("Cookie", "__cfduid=d9a40b9d9b244c28a4c0760f1fca09b871608172638")
        	  .asString();
            elsValue = response.getBody();
            
            // แกะข้อมูลที่ได้จาก bigc api
	        JSONObject objCategory = new JSONObject(elsValue);
	        JSONArray arrCategory= objCategory.getJSONArray("result");
	        for (int i = 0; i < arrCategory.length(); i++) {       
	            String categoryName = arrCategory.getJSONObject(i).getString("name");
	            
	            System.out.println("category ==> "+categoryName);
            	// ตัด category เหล่านี้ออกไป
            	if(categoryFilter.bigcFilter(categoryName)) {
            		//get cate_id
            		int cateId = arrCategory.getJSONObject(i).getInt("entity_id");
            		String newCategory = els.getCategory(categoryName); 
            		
            		System.out.println("new category ==> "+newCategory);
            		
                	json.put("category", newCategory);
                	json.put("cateId", String.valueOf(cateId));
    	            redis.rpush("categoryUrl", json.toString());// จัดเก็บลง redis เพื่อหา detail ต่อ 
    	            System.out.println(dateTimes.thaiDateTime() +" fetch bigC ==> "+categoryName);  
            	}
	        }      	 			
		}catch(Exception e) {
			System.out.println(e.getMessage());
		}
	}

	@Override
	public void categoryUrlDetail(String objStr) {
		Jedis redis = rd.connect();
		String baseUrl = "https://www.bigc.co.th/";
		try {
			JSONObject obj = new JSONObject(objStr);
			String category = obj.getString("category");
			String cateId = obj.getString("cateId");
        	//call bigCapi
        	String elasValue = els.bigCApi(cateId, "1");
        	//get last page
            int lastPage = otherFunc.lastPage(elasValue);
            System.out.println("category => "+category);
            System.out.println("lastPage => "+lastPage);
            // วนหา pagination ของ page นั้นๆ
            for(int j = 1; j <= lastPage; j++) {
            	String bigCValue = els.bigCApi(cateId, Integer.toString(j));
            	// ดึงข้อมูล
    			JSONObject json = new JSONObject(bigCValue);
    			JSONObject result = json.getJSONObject("result");
    			JSONArray arrItems = result.getJSONArray("items");
    			for (int k = 0; k < arrItems.length(); k++) {
    				JSONObject objItems = arrItems.getJSONObject(k);
    				String productUrl = baseUrl + objItems.getString("url_key");
    				obj.put("bigc_data", objItems.toString());
    				obj.put("productUrl", productUrl);
    				redis.rpush("detailUrl", obj.toString());
    				System.out.println(dateTimes.thaiDateTime() +" fetch ==> "+productUrl);
    			}
            }
		}catch(Exception e) {
			System.out.println(e.getMessage());
		}			
	}
}
