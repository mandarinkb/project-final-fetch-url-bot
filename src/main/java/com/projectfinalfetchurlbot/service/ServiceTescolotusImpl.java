package com.projectfinalfetchurlbot.service;

import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.projectfinalfetchurlbot.dao.Redis;
import com.projectfinalfetchurlbot.function.DateTimes;
import com.projectfinalfetchurlbot.function.Elasticsearch;

import redis.clients.jedis.Jedis;

@Service
public class ServiceTescolotusImpl implements ServiceTescolotus{
	@Autowired
	private Elasticsearch els;
	
    @Autowired
    private Redis rd;  

    @Autowired
    private DateTimes dateTimes;
	
	@Override
	public void classifyCategoryUrl(String obj) {
		Jedis redis = rd.connect();
		JSONObject json = new JSONObject(obj);
		String url = json.getString("url");
		
    	try {
    		Document doc = Jsoup.connect(url).timeout(60 * 1000).get();
            Elements eles = doc.select(".list-item.list-item-large");
            for (Element ele : eles) {
            	String category = ele.select(".name").html();
            	// ตัดหมวดหมู่ดังกล่าวออก
            	if(!category.matches("ดูทั้งหมด") && !category.matches("แผนกเสื้อผ้า") && !category.matches("สินค้าอื่นๆ")) {
                    Element eleTitle = ele.select("a").first();
                    //String strUrl = eleTitle.attr("href");                    
                    String strUrl = eleTitle.absUrl("href");
                    String categoryUrl = strUrl;
                    
                    category = category.replace(",", "");
                    category = category.replace("&amp; ", "");
                    
                    String newCategory = els.getCategory(category); // แปลง category ใหม่
                    
                    json.put("category",newCategory);
                    json.put("url",categoryUrl);
                    redis.rpush("categoryUrl", json.toString());
            	}

            }
    	}catch(Exception e) {
    		System.out.println(e.getMessage());
    		redis.rpush("startUrl", obj); //กรณี error ให้ยัดลง redis ที่รับมาอีกรอบ
    	}
	}

	@Override
	public void categoryUrlDetail(String obj) {
		Jedis redis = rd.connect();
		JSONObject json = new JSONObject(obj);
		String url = json.getString("url");
    	try {     	
        	boolean checkNextPage = true;       	
        	while(checkNextPage) {  
            	Document doc = Jsoup.connect(url).timeout(60 * 1000).get();
            	//urlDetail
            	Elements elesUrlDetail = doc.select(".tile-content");
                for (Element ele : elesUrlDetail) {
                    Element eleUrl = ele.select("a").first();
                    String urlDetail = eleUrl.absUrl("href");
                    //System.out.println(urlDetail); 
                    json.put("url",urlDetail);
                    redis.rpush("detailUrl", json.toString());
            	}  		
            	//nextpage
            	Elements elesNextPage = doc.select(".pagination--page-selector-wrapper");
        		Element eleNextPage = elesNextPage.select(".pagination-btn-holder").last();
                Element eleA = eleNextPage.select("a").first();
                //String urlNextPage = eleA.attr("href");
                String urlNextPage = eleA.absUrl("href");    
                if(urlNextPage == "") {
                	checkNextPage = false;
                }
                url = urlNextPage;
                System.out.println(dateTimes.thaiDateTime() +" fetch ==> "+url);    
        	}
    	}catch(Exception e) {
    		System.out.println(e.getMessage());
    		redis.rpush("categorytUrl", obj); //กรณี error ให้ยัดลง redis ที่รับมาอีกรอบ
    	}
	}

}
