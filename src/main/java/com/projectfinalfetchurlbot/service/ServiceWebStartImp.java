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
public class ServiceWebStartImp implements ServiceWebStart {

    @Autowired
    private Database db;

    @Autowired
    private Redis rd;       

    @Override
    public void start() {
        JSONObject json = new JSONObject();
        Jedis redis = rd.connect();
        //String sql = "select * from web where web_status = 1";
        String sql = "select * from web where web_status = 1 AND (detail != 'present_results' OR detail IS NULL)";
        try {
            Connection conn = db.connectDatase();
            ResultSet result = db.getResultSet(conn, sql);
            while (result.next()) {
                json.put("web_id", result.getInt("web_id"));
                json.put("web_name", result.getString("web_name"));
                json.put("url", result.getString("url"));
                json.put("type", result.getString("type"));
                json.put("type_detail", result.getString("type_detail"));
                json.put("web_status", result.getString("web_status"));
                json.put("season", result.getString("season"));
                json.put("base_url", result.getString("base_url"));
                redis.rpush("link_queue", json.toString());
            }
            conn.close();
            redis.close();
        } catch (SQLException | JSONException e) {
            e.getMessage();
        }
    }
    
    @Override
    public void startPresentResults() {
        JSONObject json = new JSONObject();
        Jedis redis = rd.connect();
        String sql = "select * from web where web_status = 1 AND detail = 'present_results'";
        try {
            Connection conn = db.connectDatase();
            ResultSet result = db.getResultSet(conn, sql);
            while (result.next()) {
                json.put("web_id", result.getInt("web_id"));
                json.put("web_name", result.getString("web_name"));
                json.put("url", result.getString("url"));
                json.put("type", result.getString("type"));
                json.put("type_detail", result.getString("type_detail"));
                json.put("web_status", result.getString("web_status"));
                json.put("season", result.getString("season"));
                json.put("base_url", result.getString("base_url"));
                redis.rpush("link_queue", json.toString());
            }
            conn.close();
            redis.close();
        } catch (SQLException | JSONException e) {
            e.getMessage();
        }
    }
}
