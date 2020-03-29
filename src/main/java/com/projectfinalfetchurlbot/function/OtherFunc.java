package com.projectfinalfetchurlbot.function;

import org.json.JSONException;
import org.json.JSONObject;
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

    String statsOfTeamEnKey(String inputKey) {
        String key = "";
        if ("จำนวนนักเตะ".equals(inputKey)) {
            key = "players";
        }
        if ("ทำประตู".equals(inputKey)) {
            key = "goals";
        }
        if ("ทำแอสซิสต์".equals(inputKey)) {
            key = "assists";
        }
        if ("คลีนชีท".equals(inputKey)) {
            key = "clean_sheets";
        }
        if ("ใบเหลือง".equals(inputKey)) {
            key = "yellow_cards";
        }
        if ("ใบเหลืองแดง".equals(inputKey)) {
            key = "yellow_red_cards";
        }
        if ("ใบแดง".equals(inputKey)) {
            key = "red_cards";
        }
        return key;
    }

    String detailPlayerToEnKey(String inputKey) {
        String key = "";
        if ("วันเกิด".equals(inputKey)) {
            key = "birthday";
        }
        if ("อายุ".equals(inputKey)) {
            key = "age";
        }
        if ("ส่วนสูง".equals(inputKey)) {
            key = "height";
        }
        if ("สัญชาติ".equals(inputKey)) {
            key = "nationality";
        }
        if ("สวมเสื้อเบอร์".equals(inputKey)) {
            key = "squad_nember";
        }
        if ("ตำแหน่ง".equals(inputKey)) {
            key = "position";
        }
        if ("ถนัดเท้า".equals(inputKey)) {
            key = "footed";
        }
        if ("เซ็นสัญญาเมื่อ".equals(inputKey)) {
            key = "sign_contract";
        }
        if ("สิ้นสุดสัญญา".equals(inputKey)) {
            key = "end_contract";
        }
        if ("สโมสรเดิม".equals(inputKey)) {
            key = "original_club";
        }
        if ("ปัจจุบันสังกัดทีมชาติ".equals(inputKey)) {
            key = "currently_national_team";
        }
        if ("อดีตสังกัดทีมชาติ".equals(inputKey)) {
            key = "former_national_team";
        }
        return key;
    }

    String titleToEnKey(String inputKey) {
        String key = "";
        if ("ทำประตูสุงสุด".equals(inputKey)) {
            key = "goals";
        }
        if ("ทำแอสซิสต์สุงสุด".equals(inputKey)) {
            key = "assists";
        }
        if ("ลงเล่นมากที่สุด".equals(inputKey)) {
            key = "minutes_played";
        }
        return key;
    }

    String staffTeamDetailToEnKey(String inputKey) {
        String key = "";
        if ("ชื่ออังกฤษ".equals(inputKey)) {
            key = "en_name";
        }
        if ("ชื่อไทย".equals(inputKey)) {
            key = "th_name";
        }
        if ("วันเกิด".equals(inputKey)) {
            key = "birthday";
        }
        if ("อายุ".equals(inputKey)) {
            key = "age";
        }
        if ("สัญชาติ".equals(inputKey)) {
            key = "nationality";
        }
        if ("ตำแหน่งในปัจจุบัน".equals(inputKey)) {
            key = "position";
        }
        if ("ทีม".equals(inputKey)) {
            key = "team";
        }
        if ("สิ้นสุดสัญญา".equals(inputKey)) {
            key = "end_contract";
        }
        if ("รูปแบบแผนที่ชอบ".equals(inputKey)) {
            key = "football_plan";
        }
        return key;
    }
    
    public boolean haveFixturesData(String inputJsonStr) {
        boolean data;
        JSONObject json = new JSONObject(inputJsonStr);
        try {
            JSONObject jsonHits = json.getJSONObject("hits");
            JSONObject jsonTotal = jsonHits.getJSONObject("total");
            int value = jsonTotal.getInt("value");
            if (value == 0) {  //กรณีที่ไม่มีข้อมูลใน json
                data = false;
            } else {           //กรณีที่มีข้อมูลใน json
                data = true;
            }
        } catch (JSONException e) {  //กรณีไม่มี index หรือกรณีอื่นๆ
            data = false;
            System.out.println(e.getMessage());
        }
        return data;
    }
}
