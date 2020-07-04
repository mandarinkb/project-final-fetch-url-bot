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

import redis.clients.jedis.Jedis;

@Service
public class ServiceMakroclickImpl implements ServiceMakroclick{
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
		                        .timeout(600000)
		                        .maxBodySize(0)
		                        .get();//
            Elements eles = doc.select(".MenuCategoryPopOver__MenuListView-sc-77t7qb-2"); 
            for (Element ele : eles) {
	            String category = ele.select("p").html();
	            
	            if(categoryFilter.makroFilter(category)) {
		            String menuId = categoryFilter.getMenuId(category);
		            String newCategory = els.getCategory(category);
		            json.put("category", newCategory);
		            json.put("menuId", menuId);
		            redis.rpush("detailUrl", json.toString());// จัดเก็บลง redis เพื่อหา detail ต่อ
		            System.out.println(dateTimes.thaiDateTime() +" fetch makro ==> "+menuId);
	            }
            }				
		}catch(Exception e) {
			System.out.println(e.getMessage());
		}		
	}
}
