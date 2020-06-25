package com.projectfinalfetchurlbot.service;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.projectfinalfetchurlbot.dao.Redis;
import com.projectfinalfetchurlbot.function.CategoryFilter;
import com.projectfinalfetchurlbot.function.DateTimes;
import com.projectfinalfetchurlbot.function.Elasticsearch;

import redis.clients.jedis.Jedis;

@Service
public class ServiceLazadaImpl implements ServiceLazada{
    @Autowired
    private CategoryFilter categoryFilter;
	
	@Autowired
	private Elasticsearch els;
	
    @Autowired
    private Redis rd;  

    @Autowired
    private DateTimes dateTimes;

	@Override
	public void classifyCategoryUrl(String objStr) {
		Jedis redis = rd.connect();
		JSONObject json = new JSONObject(objStr);
		String url = json.getString("url");
		try {
    		Document doc = Jsoup.connect(url)
    				            .userAgent("Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) snap Chromium/83.0.4103.61 Chrome/83.0.4103.61 Safari/537.36")
    				            .timeout(600000)
    				            .maxBodySize(0)
    				            .get();//
            Elements eles = doc.select(".lzd-site-menu-root");
            
            JSONObject objCategory = new JSONObject();
            List<JSONObject> list = new ArrayList<>(); 
            for(Element ele : eles.select(".lzd-site-menu-root-item")) {
            	objCategory = new JSONObject();
            	String id = ele.attr("id");
            	String category = ele.select("span").html();
            	objCategory.put("class","." + id);
            	objCategory.put("category", category);
            	list.add(objCategory);	// เก้บ class และ category ลง list
            } 
            //นำ list ที่เก็บไว้มาแสดง
            JSONArray arr = new JSONArray(list.toString());
            for (int i = 0; i < arr.length(); i++) {
            	JSONObject obj = arr.getJSONObject(i);
            	String cl = obj.getString("class");
            	String originalCategory = obj.getString("category");
            	
            	if(categoryFilter.lazadaFilter(originalCategory)) {           	
            		Elements elesSubCategory = eles.select(cl);
            		for(Element ele : elesSubCategory.select(".lzd-site-menu-sub-item")) {
                        Element eleUrl = ele.select("a").first();
                        String name = ele.select("span").first().html();
                        String newCategory = els.getCategory(name);
                        // กรณีไม่พบ category ใน elasticsearch
                        if(newCategory != null) {
                        	String urlDetail = eleUrl.absUrl("href"); 
                        	
                            json.put("category",newCategory);
                            json.put("url",urlDetail);
                            redis.rpush("detailUrl", json.toString());// จัดเก็บ url ลง redis เพื่อหา detail ต่อ
                		
                            System.out.println(dateTimes.thaiDateTime() +" fetch ==> "+urlDetail);       	                           
                        }
            		}           		
            	}
          	
            	
/*
            	// กรณี บ้านและไลฟ์สไตล์ ให้จัดเก็บข้อมูลบางส่วน(sub)
            	if(originalCategory.equals("บ้านและไลฟ์สไตล์")) {
            		
            		// แปลงและจัดเก็บ 
            		String newCategory = els.getCategory(originalCategory); // แปลง category ใหม่
            		
            		// เก็บ Sub ลง list ก่อน
            		Elements elesSubCategory = eles.select(cl);
            		for(Element ele : elesSubCategory.select(".lzd-site-menu-sub-item")) {
                        String subCategory = ele.select("span").first().html(); 
                        // เก็บเฉพาะ อุปกรณ์ทำความสะอาดและซักรีด(detail)
                        if(subCategory.equals("อุปกรณ์ทำความสะอาดและซักรีด")) {
                    		for(Element e : ele.select(".lzd-site-menu-grand-item")) {
                                Element eleDetail = e.select("a").first();
                                String urlDetail = eleDetail.absUrl("href");   // จัดเก็บ url ลง redis เพื่อหา content ต่อ
                                //String name = e.select("span").html();
                                
                                json.put("category",newCategory);
                                json.put("url",urlDetail);
                                redis.rpush("detailUrl", json.toString());// จัดเก็บ url ลง redis เพื่อหา detail ต่อ
                    		
                                System.out.println(dateTimes.thaiDateTime() +" fetch ==> "+urlDetail);    
                    		}
                        }
            		}
            		
            	}else {
            		// แปลงและจัดเก็บ 
            		String newCategory = els.getCategory(originalCategory); // แปลง category ใหม่
 
            		// กรณีอื่นๆ เก็บ detail ได้เลย
            		Elements elesSubCategory = eles.select(cl);
            		for(Element ele : elesSubCategory.select(".lzd-site-menu-grand-item")) {
                        Element eleUrl = ele.select("a").first();
                        String urlDetail = eleUrl.absUrl("href");    // จัดเก็บ url ลง redis เพื่อหา content ต่อ
                        //String name = ele.select("span").html();
                        
                        json.put("category",newCategory);
                        json.put("url",urlDetail);
                        redis.rpush("detailUrl", json.toString());// จัดเก็บ url ลง redis เพื่อหา detail ต่อ  
            		
                        System.out.println(dateTimes.thaiDateTime() +" fetch ==> "+urlDetail); 
            		}
            	}
            	
*/            	
            }   
			
		}catch(Exception e) {
    		System.out.println(e.getMessage());
    		redis.rpush("startUrl", objStr); //กรณี error ให้ยัดลง redis ที่รับมาอีกรอบ
    	}
	}

}
