package com.projectfinalfetchurlbot.controller;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.projectfinalfetchurlbot.dao.Redis;
import com.projectfinalfetchurlbot.function.ClearIndex;
import com.projectfinalfetchurlbot.function.DateTimes;
import com.projectfinalfetchurlbot.function.Elasticsearch;
import com.projectfinalfetchurlbot.function.OtherFunc;
import com.projectfinalfetchurlbot.service.ServicePremierLeagueAndThaiPremierLeague;
import com.projectfinalfetchurlbot.service.ServiceWebStart;

import redis.clients.jedis.Jedis;

@Component
public class Controller {

    @Autowired
    private ClearIndex clearIndex;

    @Autowired
    private DateTimes dateTimes;

    @Autowired
    private ServiceWebStart serviceWebStart;

    @Autowired
    private Redis rd;

    @Autowired
    private ServicePremierLeagueAndThaiPremierLeague servicePreAndThai;
    
    @Autowired
    private Elasticsearch elasticsearch;
    
    @Autowired
    private OtherFunc func;

    @Scheduled(cron = "#{@cronExpression_1}") 
    public void runTask_1() {
        System.out.println(dateTimes.interDateTime() + " : web scrapping runTask_1 start");
/*        System.out.println("search date => " + dateTimes.newInterDate());
        String dataFixturesTh = elasticsearch.searchFixtures(dateTimes.newInterDate(), "fixtures_thaipremierleague");  //query โดยระบุวันที่วันนี้ ลงในตารางผลบอลไทยพรีเมียลีก 
        String dataFixturesPre = elasticsearch.searchFixtures(dateTimes.newInterDate(), "fixtures_premierleague");     //query โดยระบุวันที่วันนี้ ลงในตารางผลบอลพรีเมียลีก

        boolean valueFixturesTh = func.haveFixturesData(dataFixturesTh);  // ตรวจเช็คว่ามีการเเข่งขันไทยพรีเมียลีกหรือไม่ (กรมี จะเป็น true)
        boolean valueFixturesPre = func.haveFixturesData(dataFixturesPre); // ตรวจเช็คว่ามีการเเข่งขันพรีเมียลีกหรือไม่ (กรมี จะเป็น true)
        
        if(valueFixturesTh){
            System.out.println("   find fixtures_thaipremierleague");
        }else{
            System.out.println("   didn't find fixtures_thaipremierleague");
        }
        
        if(valueFixturesPre){ 
            System.out.println("   find fixtures_premierleague");
        }else{
            System.out.println("   didn't find fixtures_premierleague");
        }
        
        if (valueFixturesTh || valueFixturesPre) {  // กรณีมีการแข่งขันไทยพรีเมียลีก หรือ พรีเมีย ให้ทำงาน
*/          serviceWebStart.startPresentResults();
            Jedis redis = rd.connect();
            String url;
            JSONObject json;
            String type;

            boolean check = true;
            while (check) {
                url = redis.rpop("link_queue");
                if (url != null) {
                    json = new JSONObject(url);
                    type = json.getString("type");
                    if ("results_thaipremierleague".equals(type) ) {//&& valueFixturesTh             //ผลการแข่งขันไทยลีก (ปัจจุบัน)
                        String season = json.getString("season");
                        clearIndex.deleteResultsBySeason(season, "results_thaipremierleague");
                        servicePreAndThai.getPages(url);
                    }
                    if ("results_premierleague".equals(type) ) {//&& valueFixturesPre                 //ผลการแข่งขันพรีเมียร์ลีก อังกฤษ (ปัจจุบัน)
                        String season = json.getString("season");
                        clearIndex.deleteResultsBySeason(season, "results_premierleague");
                        servicePreAndThai.getPages(url);
                    }
                } else {
                    check = false;
                }
            }     
//        } 
        System.out.println(dateTimes.interDateTime() + " : web scrapping runTask_1 stop");
    }

    @Scheduled(cron = "#{@cronExpression_2}") 
    public void runTask_2() {
        System.out.println(dateTimes.interDateTime() + " : web scrapping runTask_2 start");
        serviceWebStart.start();
        Jedis redis = rd.connect();
        String url;
        JSONObject json;
        String type;

        boolean check = true;
        while (check) {
            url = redis.rpop("link_queue");
            if (url != null) {
                json = new JSONObject(url);
                type = json.getString("type");
                if ("results_thaipremierleague".equals(type)) {                 //ผลการแข่งขันไทยลีก (อดีต)
                    String season = json.getString("season");
                    clearIndex.deleteResultsBySeason(season, "results_thaipremierleague"); //ลบ docs ใน results_thaipremierleague จาก season นั้นๆ 
                    servicePreAndThai.getPages(url);
                }
                if ("results_premierleague".equals(type)) {                     //ผลการแข่งขันพรีเมียร์ลีก อังกฤษ (อดีต)
                    String season = json.getString("season");
                    clearIndex.deleteResultsBySeason(season, "results_premierleague");  //ลบ docs ใน results_premierleague จาก season นั้นๆ
                    servicePreAndThai.getPages(url);
                }
                if ("fixtures_thaipremierleague".equals(type)) {                //ตารางแข่งขันฟุตบอลไทยลีก
                    elasticsearch.deleteIndex("fixtures_thaipremierleague");    //ลบ index ของเก่า
                    servicePreAndThai.getPages(url);
                }
                if ("fixtures_premierleague".equals(type)) {                    //ตารางแข่งขันฟุตบอลพรีเมียร์ลีก อังกฤษ
                    elasticsearch.deleteIndex("fixtures_premierleague");        //ลบ index ของเก่า
                    servicePreAndThai.getPages(url);
                }
                if ("league_table_thaipremierleague".equals(type)) {            //ตารางคะแนนไทยลีก
                    elasticsearch.deleteIndex("league_table_thaipremierleague");//ลบ index ของเก่า
                    servicePreAndThai.getPages(url);
                }
                if ("league_table_premierleague".equals(type)) {                //ตารางคะแนนพรีเมียร์ลีก อังกฤษ
                    elasticsearch.deleteIndex("league_table_premierleague");    //ลบ index ของเก่า
                    servicePreAndThai.getPages(url);
                }           
                if ("teams_thaipremierleague".equals(type)) {                   //สโมสรฟุตบอล thaipremierleague 
                    elasticsearch.deleteIndex("present_teams_thaipremierleague");           //ลบ index ของเก่า
                    elasticsearch.deleteIndex("present_teams_detail_thaipremierleague");    //ลบ index ของเก่า
                    elasticsearch.deleteIndex("present_players_detail_thaipremierleague");  //ลบ index ของเก่า
                    servicePreAndThai.getPages(url);
                }
                if ("teams_premierleague".equals(type)) {                       //สโมสรฟุตบอล premierleague
                    elasticsearch.deleteIndex("present_teams_premierleague");               //ลบ index ของเก่า
                    elasticsearch.deleteIndex("present_teams_detail_premierleague");        //ลบ index ของเก่า
                    elasticsearch.deleteIndex("present_players_detail_premierleague");      //ลบ index ของเก่า
                    servicePreAndThai.getPages(url);
                }
 
                if ("score_analyze_thaipremierleague".equals(type)) {           //วิเคราะห์บอลไทยลีก
                    elasticsearch.deleteIndex("score_analyze_thaipremierleague");   //ลบ index ของเก่า
                    servicePreAndThai.getPages(url);
                }
                if ("score_analyze_premierleague".equals(type)) {               //วิเคราะห์บอลพรีเมียร์ลีก อังกฤษ
                    elasticsearch.deleteIndex("score_analyze_premierleague");       //ลบ index ของเก่า
                    servicePreAndThai.getPages(url);
                }                
            } else {
                check = false;
            }
        }
        System.out.println(dateTimes.interDateTime() + " : web scrapping runTask_2 stop");
    }

}
