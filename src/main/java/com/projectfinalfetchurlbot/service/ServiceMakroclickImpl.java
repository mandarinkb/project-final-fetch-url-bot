package com.projectfinalfetchurlbot.service;

import java.text.DecimalFormat;

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
import com.projectfinalfetchurlbot.function.OtherFunc;

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
    
    @Autowired
    private OtherFunc otherFunc;

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
	            System.out.println("category ==> "+category);
	            
	            if(categoryFilter.makroFilter(category)) {
		            String menuId = categoryFilter.getMenuId(category);
		            String newCategory = els.getCategory(category);
		            System.out.println("new category ==> "+newCategory);
		            
		            json.put("category", newCategory);
		            json.put("menuId", menuId);
		            redis.rpush("categoryUrl", json.toString());// จัดเก็บลง redis เพื่อหา detail ต่อ
		            System.out.println(dateTimes.thaiDateTime() +" fetch makro ==> "+category);
	            }
            }				
		}catch(Exception e) {
			System.out.println(e.getMessage());
		}		
	}

	@Override
	public void categoryUrlDetail(String objStr) {
		Jedis redis = rd.connect();
	    String urlDetail = "https://www.makroclick.com/th/products/";
		try {
			JSONObject obj = new JSONObject(objStr);
			String category = obj.getString("category");
			String menuId = obj.getString("menuId");
			String elas = els.makroApi(menuId, "1");
			int total = otherFunc.totalPage(elas); // หา page ทั้งหมดก่อน
            System.out.println("category => "+category);
            System.out.println("lastPage => "+total);
            
			// วนหา pagination ของ page นั้นๆ
			for(int j = 1; j <= total; j++) {
				String elasValue = els.makroApi(menuId, Integer.toString(j));
				JSONObject objValue = new JSONObject(elasValue);
				JSONArray arrContent = objValue.getJSONArray("content");
				for (int k = 0; k < arrContent.length(); k++) {
					JSONObject objItems = arrContent.getJSONObject(k);
					String productUrl = urlDetail + objItems.getString("productCode");
    				obj.put("makro_data", objItems.toString());
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
