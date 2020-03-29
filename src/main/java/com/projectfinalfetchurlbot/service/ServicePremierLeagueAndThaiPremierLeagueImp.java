package com.projectfinalfetchurlbot.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.projectfinalfetchurlbot.dao.Redis;
import com.projectfinalfetchurlbot.function.OtherFunc;

import redis.clients.jedis.Jedis;

@Service
public class ServicePremierLeagueAndThaiPremierLeagueImp implements ServicePremierLeagueAndThaiPremierLeague {

    @Autowired
    private Redis rd;

    @Autowired
    private OtherFunc func;

    @Override
    public void getPages(String url) {
        Jedis redis = rd.connect();
        redis.rpush("pages", url);
    }
/*    
    @Override
    public void getStatsOfTeamPages(String url, String type) {
        JSONObject json = new JSONObject();
        Jedis redis = rd.connect();
        String league = null;
        JSONObject jsonValue = new JSONObject(url);
        String link = jsonValue.getString("link");
        if ("stats_thaipremierleague_players".equals(type)) {
            league = "thaipremierleague";
        }
        if ("stats_premierleague_players".equals(type)) {
            league = "premierleague";
        }  
        json.put("link", link);
        json.put("type", "stats");
        json.put("type_detail", "stats_of_team");
        json.put("league", league);

        redis.rpush("pages", json.toString());
    }
    
    @Override
    public void getTeamDetailPages(String url, String type) {
        JSONObject jsonValue = new JSONObject(url);
        String baseLogoLink = jsonValue.getString("base_logo_link");
        String link = jsonValue.getString("link");
        String BaseLinkPalyer = null;
        String league = null;
        if ("stats_thaipremierleague_players".equals(type)) {
            BaseLinkPalyer = baseLogoLink + "/thaipremierleague";
            league = "thaipremierleague";
        }
        if ("stats_premierleague_players".equals(type)) {
            BaseLinkPalyer = baseLogoLink + "/premierleague";
            league = "premierleague";
        }
        JSONObject json;
        Jedis redis = rd.connect();
        
        try {
            Document doc = Jsoup.connect(link).timeout(60 * 1000).get();
            Elements elements = doc.getElementsByClass("PlayerLeague");
            for (Element ele : elements) {
                json = new JSONObject();
                Elements elesUl = ele.select("ul");
                for (Element eleChildren : elesUl) {
                    Elements elesLi = eleChildren.select("li");
                    Elements elesChildren = elesLi.select("*");
                    for (Element eleLiAll : elesChildren) {
                        if (eleLiAll.tagName().equals("a")) {       // ดูนักเตะทั้งหมด
                            Element eleA = eleLiAll.select("a").first();
                            String linkPlayers = eleA.attr("href");
                            linkPlayers = func.getNewLinkImage(linkPlayers);
                            linkPlayers = BaseLinkPalyer + linkPlayers;
                            
                            json.put("link", linkPlayers);
                            json.put("type", "stats");
                            json.put("type_detail", "team_detail");
                            json.put("league", league);
                            
                            redis.rpush("link_queue", json.toString());  //เพื่อหา link  players profile  อีกครั้ง
                            redis.rpush("pages", json.toString());
                        }
                    }
                }
            }
        } catch (IOException | JSONException e) {
            e.getMessage();
        }
    }

    @Override
    public void getPlayersProfilePages(String url) {
        Jedis redis = rd.connect();
        JSONObject jsonValue = new JSONObject(url);
        String link = jsonValue.getString("link");
        String league = jsonValue.getString("league");
        String baseLink = "http://www.livesoccer888.com";
        try {
            Document doc = Jsoup.connect(link).timeout(60 * 1000).get();
            JSONObject json;
            Elements elesDetailPlayer = doc.getElementsByClass("show-table");
            Element elesTr = elesDetailPlayer.select("tr").first();   // เลือก tr tag แรกสุด
            elesTr.remove();                                  // ลบ tr tag แรกสุด
            Element elesNext = elesDetailPlayer.select("tr").first(); // เลือก tr tag ที่ 2
            elesNext.remove();                                // ลบ tr tag ที่ 2

            Elements eles = elesDetailPlayer.select(".clickable-row");
            for (Element ele : eles) {
                json = new JSONObject();
                String linkProfile = ele.attr("data-href");  // link profile
                linkProfile = baseLink + func.getNewLinkImage(linkProfile);
                json.put("link", linkProfile);
                json.put("type", "stats");
                json.put("type_detail", "players_profile");
                json.put("league", league);

                redis.rpush("pages", json.toString());
            }
        } catch (IOException | JSONException e) {
            e.getMessage();
        }
    }

    @Override
    public void getTeamPage(String url, String type) {
        JSONObject jsonValue = new JSONObject(url);
        String link = jsonValue.getString("link");
        Jedis redis = rd.connect();
        JSONObject json;
        String baseLink = "http://www.livesoccer888.com";
        String BaseLinkPalyer = "";
        if ("staff_thaipremierleague".equals(type)) {
            BaseLinkPalyer = "http://www.livesoccer888.com/thaipremierleague/teams/";
        }
        if ("staff_premierleague".equals(type)) {
            BaseLinkPalyer = "http://www.livesoccer888.com/premierleague/teams/";
        }
        try {
            Document doc = Jsoup.connect(link).timeout(60 * 1000).get();
            Elements elements = doc.select(".content.main-content.left-content");
            String title = elements.select(".title-page").text();
            //System.out.println(title);

            Elements eles = elements.select(".MatchTeamFull");
            for (Element ele : eles) {
                json = new JSONObject();
                Elements a = ele.select("a");
                String strUrl = a.attr("href");
                String linkPage = BaseLinkPalyer + strUrl;
                //listPage.add(linkPage);
                json.put("link", linkPage);

                Elements logoTeam = ele.select(".MatchLogoDivFull");  // logo
                Elements img = logoTeam.select("img");
                String logo = img.attr("src");
                logo = baseLink + func.getNewLinkImage(logo);
                json.put("logo_team", logo);

                String team = ele.select(".getCodeTeam").text();
                json.put("team", team);

                json.put("index", type);
                json.put("type", "staff");
                json.put("type_detail", "link_team");
                redis.rpush("link_queue", json.toString());  //เพื่อหา link ผู้จัดการทีม อีกครั้ง
            }
        } catch (IOException | JSONException e) {
            e.getMessage();
        }       

    }

    @Override
    public void getStaffTeamPage(String link, String url) {
        Jedis redis = rd.connect();
        JSONObject json = new JSONObject();
        JSONObject jsonValue = new JSONObject(url);
        String logo = jsonValue.getString("logo_team");
        String team = jsonValue.getString("team");
        String index = jsonValue.getString("index");

        try {
            Document doc = Jsoup.connect(link).timeout(60 * 1000).get();
            Elements elements = doc.select(".staff_team");
            Elements elesLink = elements.select(".staff_div");
            Elements elesA = elesLink.select("a");
            String linkDetail = elesA.attr("href");
            if(linkDetail.isEmpty()){  // กรณีไม่พบข้อมูลผู้จัดการทีม
                return;
            }
            link = link.replace("index.php", "");
            linkDetail = link + linkDetail;
            
            json.put("link", linkDetail);
            json.put("logo_team", logo);
            json.put("team", team);
            json.put("index", index);
            json.put("type", "staff");
            //json.put("type_detail", "link_staff");
            redis.rpush("pages", json.toString());

        } catch (IOException e) {
            e.getMessage();
        }
    }

*/    

}
