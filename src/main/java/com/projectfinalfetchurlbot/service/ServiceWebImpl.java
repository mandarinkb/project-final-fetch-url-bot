package com.projectfinalfetchurlbot.service;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.projectfinalfetchurlbot.dao.Database;
import com.projectfinalfetchurlbot.dao.Redis;

import redis.clients.jedis.Jedis;

@Service
public class ServiceWebImpl implements ServiceWeb {

    @Autowired
    private Database db;

    @Autowired
    private Redis rd;       

    @Override
    public void start() {
        JSONObject json = new JSONObject();
        Jedis redis = rd.connect();
        String sql = "select * from Web where Web_status = 1";
        try {
            Connection conn = db.connectDatase();
            ResultSet result = db.getResultSet(conn, sql);
            while (result.next()) {
                json.put("web_id", result.getInt("Web_id"));
                json.put("web_name", result.getString("Web_name"));
                json.put("url", result.getString("Url"));
                json.put("web_status", result.getString("web_status"));
                json.put("icon_url", result.getString("Icon_url"));
                redis.rpush("startUrl", json.toString());
            }
            conn.close();
            redis.close();
        } catch (SQLException | JSONException e) {
            e.getMessage();
        }
    }

}
