package com.projectfinalfetchurlbot;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;

public class Lazada extends CategoryFilter{
    public String changeCategory(String category) {
        String elsValue = null;
        try {
        	Unirest.setTimeouts(0, 0);
        	HttpResponse<String> response = Unirest.post("http://127.0.0.1:9200/web_scrapping_categories/_search")
        	  .header("Content-Type", "application/json")
        	  .body("{\"query\": {\"bool\": {\"must\": {\"match_phrase\": {\"tag\": \""+category+"\"}}}}}")
        	  .asString();

            elsValue = response.getBody();
        } catch (UnirestException ex) {
            //Logger.getLogger(Elasticsearch.class.getName()).log(Level.SEVERE, null, ex);
        }
    	
        JSONObject objResultsValue;
        String newCategory = null;
		try {
			objResultsValue = new JSONObject(elsValue);
	        JSONObject objHits = objResultsValue.getJSONObject("hits");
	        JSONArray arrHits = objHits.getJSONArray("hits");

	        
	        for (int i = 0; i < arrHits.length(); i++) {
	            JSONObject objSource = arrHits.getJSONObject(i).getJSONObject("_source");	            
	            newCategory = objSource.getString("category");
	        }
			
			
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

    	return newCategory;	
    }
    public static void getContent(String url) {
        try {
    		Document doc = Jsoup.connect(url)
    				.userAgent("Mozilla/5.0 (Windows NT 6.1) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/41.0.2228.0 Safari/537.36")
    				.timeout(60 * 1000)
    				.get();//
    		//TimeUnit.SECONDS.sleep(5);
            Elements eles = doc.select("head");
            String detail = eles.select("script").get(3).html();
            detail = detail.replace("window.pageData=", "");
            
            JSONObject obj = new JSONObject(detail);
            JSONObject objMods = obj.getJSONObject("mods");
            JSONArray arrListItems = objMods.getJSONArray("listItems");
            for (int i = 0; i < arrListItems.length(); i++) {
            	JSONObject objItems = arrListItems.getJSONObject(i);
            	
            	String image = objItems.getString("image");
            	
            	//เช็คว่ามี key หรือไม่
            	String originalPrice = null;
            	if (objItems.has("originalPrice")) { 
            		originalPrice = objItems.getString("originalPrice");
            	}
            	String price = objItems.getString("price");
            	// String webName
            	String name = objItems.getString("name");
            	// String icon
            	
            	String discount = null;  
            	if (objItems.has("discount")) { 
            		discount = objItems.getString("discount");
            	}
            	// String discountFull
            	// String category
            	String productUrl = objItems.getString("productUrl");
            	productUrl = "https:" + productUrl;
        
            	// เก็บเฉพาะที่มีส่วนลด
            	if(discount != null) {
            		//System.out.println(++ii);
            		System.out.println(image);
            		System.out.println(originalPrice);
            		System.out.println(price);
            		System.out.println(name);
            		System.out.println(discount);
            		System.out.println(productUrl);
            		System.out.println("==========================");
            	}
            }   
        }catch(Exception e) {
        	System.out.println("error => " + e.getMessage());
        }
    }
	
    public void getCategory(String url) {
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
            	
            	if(this.lazadaFilter(originalCategory)) {           	
            		Elements elesSubCategory = eles.select(cl);
            		for(Element ele : elesSubCategory.select(".lzd-site-menu-sub-item")) {
                        Element eleUrl = ele.select("a").first();
                        String name = ele.select("span").first().html();
                        String newCategory = this.changeCategory(name);
                        // กรณีไม่พบ category ใน elasticsearch
                        if(newCategory != null) {
                        	String urlDetail = eleUrl.absUrl("href"); 
                        	System.out.println(urlDetail);
                            System.out.println(name);
                            System.out.println("new category ==>> "+newCategory);
                            System.out.println("===============");
                            
                        }
            		}           		
            	}
            	
            }           
    	} catch(Exception e) {
    		System.out.println("error => " + e.getMessage());
    	}	
    }
    
    public static void main(String[] args) throws IOException, InterruptedException{
    	String url = "https://www.lazada.co.th/#";
    	Lazada l = new Lazada();
    	l.getCategory(url);
    	//l.getContent(url);
    }	
	
}
