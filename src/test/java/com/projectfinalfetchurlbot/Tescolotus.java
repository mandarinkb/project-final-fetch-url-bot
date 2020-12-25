package com.projectfinalfetchurlbot;

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

public class Tescolotus extends CategoryFilter{
	public static final String HTML = "input.html"; 
	public static List<String>list = new ArrayList<>();
	public static List<String>listUrlDetail = new ArrayList<>();
	//public static String baseUrl = "https://shoponline.tescolotus.com";
	
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
	
	
	
    public  void page(String url){ 
    	try {
    		Document doc = Jsoup.connect(url)
                    .userAgent("Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) snap Chromium/83.0.4103.61 Chrome/83.0.4103.61 Safari/537.36")
                    .timeout(600000)
                    .maxBodySize(0)
                    .get();//
            Elements eles = doc.select(".list-item.list-item-large");
            for (Element ele : eles) {
            	String category = ele.select(".name").html();
            	System.out.println(category); 
            	// ตัดหมวดหมู่ดังกล่าวออก
            	if(this.tescolotusFilter(category)) {
                    Element eleTitle = ele.select("a").first();
                    //String strUrl = eleTitle.attr("href");                    
                    String strUrl = eleTitle.absUrl("href");
                    //String categoryUrl = strUrl;
                    
                    //category = category.replace(",", "");
                    //category = category.replace("&amp; ", "");
                    
                    //System.out.println(category); 
                    System.out.println("change => "+this.changeCategory(category)); 
                    
                    //String newCategory = els.getCategory(category); // แปลง category ใหม่
                    //System.out.println(category); 

            }
            }
    	}catch(Exception e) {
    		System.out.println(e.getMessage());
    	}
    }	
    public void content(String url){  
    	try {
        	System.out.println(url);       	
        	boolean checkNextPage = true;       	
        	while(checkNextPage) {       		
        	Document doc = Jsoup.connect(url).timeout(60 * 1000).get();
        	//urlDetail
        	Elements elesUrlDetail = doc.select(".tile-content");
            for (Element ele : elesUrlDetail) {
                Element eleUrl = ele.select("a").first();
                String urlDetail = eleUrl.absUrl("href");
                //System.out.println(urlDetail); 
                listUrlDetail.add(urlDetail);
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
            System.out.println(url);    
    		System.out.println();
        	}
    	}catch(Exception e) {
    		System.out.println(e.getMessage());
    	}
    }
    public void contentDetail(String url){
    	try {
        	Document doc = Jsoup.connect(url).timeout(60 * 1000).get();
        	Elements elesUrlDetail = doc.select(".product-image__container");
            for (Element ele : elesUrlDetail) {
                Element eleUrl = ele.select("img").first();
                String image = eleUrl.attr("src");
                System.out.println(image);       
        	}
            String name = doc.select(".product-details-tile__title").html();
            System.out.println(name); 
            
            String priceAll = doc.select(".offer-text").first().html();
            System.out.println(priceAll); 
            String[] parts = priceAll.split("บาท");
            String part1 = parts[0].replace("ราคาพิเศษ ", "");
            String part2 = parts[1].replace(" จากราคาปกติ ", "");
            String part3 = parts[2].replace(" ประหยัด ", "");
            
            System.out.println(part1); 
            System.out.println(part2); 
            System.out.println(part3); 
            
            
            String icon = "https://www.tescolotus.com/assets/theme2018/tl-theme/img/logo.png";
            System.out.println(icon); 
            
            System.out.println(url); 
    	}catch(Exception e) {
    		System.out.println(e.getMessage());
    	}

    }
	
    public static void main(String[] args){
        
        Tescolotus t = new Tescolotus();
        
        String url = "https://shoponline.tescolotus.com/groceries/th-TH/promotions/";
        t.page(url);
        
/*        list.remove( list.size() - 1 );  // del last element
        System.out.println(list);
      
        for(String listUrl : list) {
        	t.content(listUrl);
        }
*/        
/*        int i = 0;
        for(String urlDetail : listUrlDetail) {
        	t.contentDetail(urlDetail);
        	System.out.println(++i);        	
        }
*/       
 
    }
}
