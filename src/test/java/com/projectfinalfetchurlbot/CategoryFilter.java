package com.projectfinalfetchurlbot;

public class CategoryFilter {
	public boolean tescolotusFilter(String category) {
		// ตัดเหล่านี้ออก
		if(category.matches("ดูทั้งหมด") || 
		   category.matches("สินค้าอื่นๆ") ||
		   category.matches("ต้อนรับเปิดเทอม")) {
		   return false;
		}else {  // นอกนั้นทำงานปกติ
		   return true;
		}
	}
	
	public boolean makroFilter(String category) {
		// ตัดเหล่านี้ออก
		if(category.matches("สมาร์ทและไลฟ์สไตล์") ) {
		   return false;
		}else {  // นอกนั้นทำงานปกติ
		   return true;
		}
	}
	
	public boolean bigcFilter(String category) {
		// ตัดเหล่านี้ออก
		if(category.equals("สินค้ารับเปิดเทอม") ||	
		   category.equals("สินค้าบริการส่งด่วน") ||
           category.equals("อุปกรณ์กีฬา/ แคมปิ้ง/ เดินทาง") ||
           category.equals("ยานยนต์") ||
           category.equals("ร้าน Pure") ||
           category.equals("สินค้าแบรนด์เบสิโค")) {
		   return false;
		}else {  // นอกนั้นทำงานปกติ
		   return true;
		}
	}
	
	public boolean lazadaFilter(String category) {
		// ตัดเหล่านี้ออก
		if(category.equals("อุปกรณ์เสริม อิเล็กทรอนิกส์") ||	
		   category.equals("กีฬาและ การเดินทาง") ||
           category.equals("ยานยนต์ และอุปกรณ์")) {
		   return false;
		}else {  // นอกนั้นทำงานปกติ
		   return true;
		}		
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

    public String getCateId(String category) {
    	String cate_id = null ;
        switch(category) 
        { 
            case "สินค้ารับเปิดเทอม": 
            	cate_id = "9590";
                break; 
            case "สินค้าบริการส่งด่วน": 
            	cate_id = "7680";
                break; 
            case "อาหารสด": 
            	cate_id = "2062";
                break; 
            case "อาหารแห้ง": 
            	cate_id = "3";
                break; 
            case "เครื่องดื่ม": 
            	cate_id = "60";
                break; 
            case "ขนมขบเคี้ยวและลูกอม": 
            	cate_id = "108";
                break; 
            case "สุขภาพและความงาม": 
            	cate_id = "143";
                break; 
            case "แม่และเด็ก": 
            	cate_id = "191";
                break; 
            case "ของใช้ในครัวเรือน": 
            	cate_id = "233";
                break; 
            case "เครื่องใช้ไฟฟ้า และอิเล็กทรอนิกส์": 
            	cate_id = "299";
                break; 
            case "เครื่องเขียน และอุปกรณ์สำนักงาน": 
            	cate_id = "344";
                break; 
            case "เครื่องแต่งกาย": 
            	cate_id = "7348";
                break; 
            case "อุปกรณ์กีฬา/ แคมปิ้ง/ เดินทาง": 
            	cate_id = "7383";
                break; 
            case "ยานยนต์": 
            	cate_id = "7384";
                break; 
            case "สัตว์เลี้ยง/ สินค้าเทศกาล": 
            	cate_id = "383";
                break; 
            case "ร้าน Pure": 
            	cate_id = "6716";
                break; 
            case "สินค้าแบรนด์เบสิโค": 
            	cate_id = "6791";
                break; 
            default: 
                System.out.println("no match"); 
        } 
    	return cate_id;
    }
}
