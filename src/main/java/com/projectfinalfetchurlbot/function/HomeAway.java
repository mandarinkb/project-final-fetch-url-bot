package com.projectfinalfetchurlbot.function;

import org.springframework.stereotype.Service;

@Service
public class HomeAway {
    
    public String getHomeAway(String home, String away) {
        String mix;
        return mix = home + " - " + away;
    }
}
