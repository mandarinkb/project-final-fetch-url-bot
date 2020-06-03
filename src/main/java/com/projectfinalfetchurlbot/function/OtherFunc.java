package com.projectfinalfetchurlbot.function;

import org.springframework.stereotype.Service;

@Service
public class OtherFunc {

    public String getNewLinkImage(String url) {
        url = url.replace("../../../..", "");
        url = url.replace("../..", "");
        url = url.replace("/..", "");
        url = url.replace("..", "");
        return url;
    }
    
    //for makro
    public String getMenuId(String category) {
    	String menuId = null ;
        switch(category) 
        { 
            case "ผักและผลไม้": 
            	menuId = "3874";
                break; 
            case "เนื้อสัตว์": 
            	menuId = "3896";
                break; 
            case "ปลาและอาหารทะเล": 
            	menuId = "4147";
                break; 
            case "นม เนย ไข่ และผลิตภัณฑ์แช่เย็น": 
            	menuId = "3353";
                break; 
            case "เบเกอรี่": 
            	menuId = "3803";
                break; 
            case "อาหารแช่แข็ง": 
            	menuId = "3932";
                break; 
            case "อาหารแห้ง": 
            	menuId = "2465";
                break; 
            case "เครื่องดื่มและขนมขบเคี้ยว": 
            	menuId = "2462";
                break; 
            case "อุปกรณ์และของใช้ในครัวเรือน": 
            	menuId = "2460";
                break; 
            case "ผลิตภัณฑ์ทำความสะอาด": 
            	menuId = "4112";
                break; 
            case "เครื่องเขียนและอุปกรณ์สำนักงาน": 
            	menuId = "2464";
                break; 
            case "เครื่องใช้ไฟฟ้า": 
            	menuId = "2461";
                break; 
            case "สุขภาพและความงาม": 
            	menuId = "2466";
                break; 
            case "สมาร์ทและไลฟ์สไตล์": 
            	menuId = "4056";
                break; 
            case "แม่และเด็ก": 
            	menuId = "2467";
                break; 
            case "ผลิตภัณฑ์สำหรับสัตว์เลี้ยง": 
            	menuId = "2468";
                break; 
            default: 
                System.out.println("no match"); 
        } 
    	return menuId;
    }   

}
