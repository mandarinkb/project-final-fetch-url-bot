package com.projectfinalfetchurlbot.service;

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

	@Override
	public void classifyCategoryUrl(String objStr) {
		Jedis redis = rd.connect();
		JSONObject json = new JSONObject(objStr);
		String url = json.getString("url");
		try {
    		Document doc = Jsoup.connect(url)
		            .userAgent("Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) snap Chromium/83.0.4103.61 Chrome/83.0.4103.61 Safari/537.36")
		            .timeout(60000)
		            .maxBodySize(0)
		            .get();
            Elements eles = doc.select(".swiper-wrapper");
            for (Element ele : eles.select("a")) {
            	String category = ele.text();
            	// ตัด category เหล่านี้ออกไป
            	if(categoryFilter.bigcFilter(category)) {
            		
            		//get cate_id
            		String cateId = categoryFilter.getCateId(category);
            		String newCategory = els.getCategory(category);           	
                	
                	json.put("category", newCategory);
                	json.put("cateId", cateId);
    	            redis.rpush("detailUrl", json.toString());// จัดเก็บลง redis เพื่อหา detail ต่อ
    	            System.out.println(dateTimes.thaiDateTime() +" fetch bigC ==> "+cateId);  
            	}
            }
			
		}catch(Exception e) {
			System.out.println(e.getMessage());
		}
	}

}