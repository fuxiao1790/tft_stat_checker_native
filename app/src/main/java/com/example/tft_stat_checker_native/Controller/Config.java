package com.example.tft_stat_checker_native.Controller;

import java.util.HashMap;

public class Config {
    private static HashMap<String, String> platforms;
    private static HashMap<String, String> regions;
    private static boolean init = false;
    public static final String token = "RGAPI-8e524296-292b-45c2-8cee-206b121653db";
    public static final String playerIconCDN = "https://ddragon.leagueoflegends.com/cdn/9.24.1/img/profileicon/";

    public static void init() {
        if (!init) {
            platforms = new HashMap<>();
            platforms.put("BR", "https://br1.api.riotgames.com");
            platforms.put("EUNE", "https://eun1.api.riotgames.com");
            platforms.put("EUW", "https://euw1.api.riotgames.com");
            platforms.put("JP", "https://jp1.api.riotgames.com");
            platforms.put("KR", "https://kr.api.riotgames.com");
            platforms.put("LAS", "https://la1.api.riotgames.com");
            platforms.put("OC", "https://oc1.api.riotgames.com");
            platforms.put("TR", "https://tr1.api.riotgames.com");
            platforms.put("RU", "https://ru.api.riotgames.com");
            platforms.put("NA", "https://na1.api.riotgames.com");

            regions = new HashMap<>();
            regions.put("AMERICAS", "https://americas.api.riotgames.com");
            regions.put("ASIA", "https://asia.api.riotgames.com");
            regions.put("EUROPE", "https://europe.api.riotgames.com");

            init = true;
        }
    }

    public static HashMap<String, String> getPlatforms() {
        return platforms;
    }

    public static HashMap<String, String> getRegions() {
        return regions;
    }
}
