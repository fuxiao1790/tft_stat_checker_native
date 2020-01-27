package com.example.tft_stat_checker_native;

import android.util.Log;

import java.util.HashMap;
import java.util.Map;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

public class API {
    public static StringRequest getSummonerByName(String name, String platform, Response.Listener<String> onSuccess, Response.ErrorListener onError) {
        Config.init();
        final String platformURL = Config.getPlatforms().get(platform);
        final String route = "/tft/summoner/v1/summoners/by-name";
        final String data = "/" + name + "?summonerName=" + name;

        Log.d("getSummonerByName: ", platformURL + route + data);
        StringRequest request = new StringRequest(Request.Method.GET, platformURL + route + data, onSuccess, onError) {
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> headers = new HashMap<>();
                headers.put("X-Riot-Token", Config.token);
                return headers;
            }
        };

        return request;
    }

    public static StringRequest getSummonerRankedData(String id, String platform, Response.Listener<String> onSuccess, Response.ErrorListener onError) {
        Config.init();
        final String platformURL = Config.getPlatforms().get(platform);
        final String route = "/tft/league/v1/entries/by-summoner/";
        final String data = id;

        Log.d("getSummonerRankedData: ", platformURL + route + data);
        StringRequest request = new StringRequest(Request.Method.GET, platformURL + route + data, onSuccess, onError) {
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> headers = new HashMap<>();
                headers.put("X-Riot-Token", Config.token);
                return headers;
            }
        };

        return request;
    }

    public static StringRequest getMatchHistoryList(String puuid, int count, String platform, Response.Listener<String> onSuccess, Response.ErrorListener onError) {
        Config.init();
        final String platformURL = Config.getRegions().get(getRegionNameByPlatform(platform));
        final String route = "/tft/match/v1/matches/by-puuid/";
        final String data = puuid + "/ids?" + "count=" + count;

        Log.d("getMatchHistoryList: ", platformURL + route + data);
        StringRequest request = new StringRequest(Request.Method.GET, platformURL + route + data, onSuccess, onError) {
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> headers = new HashMap<>();
                headers.put("X-Riot-Token", Config.token);
                return headers;
            }
        };

        return request;
    }

    public static StringRequest getMatchResultByMatchID(String matchID, String platform, Response.Listener<String> onSuccess, Response.ErrorListener onError) {
        Config.init();
        final String platformURL = Config.getRegions().get(getRegionNameByPlatform(platform));
        final String route = "/tft/match/v1/matches/";
        final String data = matchID;

        Log.d("getMatchResultByMatchID", platformURL + route + data);
        StringRequest request = new StringRequest(Request.Method.GET, platformURL + route + data, onSuccess, onError) {
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> headers = new HashMap<>();
                headers.put("X-Riot-Token", Config.token);
                return headers;
            }
        };

        return request;
    }

    public static StringRequest getSummonerByPUUID(String puuid, String platform, Response.Listener<String> onSuccess, Response.ErrorListener onError) {
        Config.init();
        final String platformURL = Config.getPlatforms().get(platform);
        final String route = "/tft/summoner/v1/summoners/by-puuid";
        final String data = "/" + puuid + "?encryptedPUUID=" + puuid;

        Log.d("getSummonerByPUUID", platformURL + route + data);
        StringRequest request = new StringRequest(Request.Method.GET, platformURL + route + data, onSuccess, onError) {
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> headers = new HashMap<>();
                headers.put("X-Riot-Token", Config.token);
                return headers;
            }
        };

        return request;
    }

    public static void onError(VolleyError error) {
        Log.d("NetWork Request Failed", error.toString());
    }

    public static String getRegionNameByPlatform(String platform) {
        if (platform.equals("NA")) {
            return "AMERICAS";
        } else if (platform.equals("BR")) {
            return "AMERICAS";
        } else if (platform.equals("LA")) {
            return "AMERICAS";
        } else if (platform.equals("EUNE")) {
            return "EUROPE";
        } else if (platform.equals("EUW")) {
            return "EUROPE";
        } else if (platform.equals("TR")) {
            return "EUROPE";
        } else if (platform.equals("RU")) {
            return "EUROPE";
        } else if (platform.equals("JP")) {
            return "ASIA";
        } else if (platform.equals("KR")) {
            return "ASIA";
        } else if (platform.equals("OC")) {
            return "ASIA";
        } else {
            return "";
        }
    }
}

class Config{
    private static HashMap<String, String> platforms;
    private static HashMap<String, String> regions;
    private static boolean init = false;
    public static final String token = "RGAPI-65f16ae3-dcbc-42b6-94d1-818f3a8c3bc7";
    public static final String playerIconCDN = "https://ddragon.leagueoflegends.com/cdn/9.24.1/img/profileicon/";

    public static void init() {
        if (!init) {
            platforms = new HashMap<>();
            platforms.put("NA", "https://na1.api.riotgames.com");
            platforms.put("BR", "https://br1.api.riotgames.com");
            platforms.put("EUNE", "https://eun1.api.riotgames.com");
            platforms.put("EUW", "https://euw1.api.riotgames.com");
            platforms.put("JP", "https://jp1.api.riotgames.com");
            platforms.put("KR", "https://kr.api.riotgames.com");
            platforms.put("LA", "https://la1.api.riotgames.com");
            platforms.put("OC", "https://oc1.api.riotgames.com");
            platforms.put("TR", "https://tr1.api.riotgames.com");
            platforms.put("RU", "https://ru.api.riotgames.com");

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
