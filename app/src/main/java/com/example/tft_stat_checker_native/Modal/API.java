package com.example.tft_stat_checker_native.Modal;

import android.util.Log;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.example.tft_stat_checker_native.Controller.Config;

public class API {
    private static final ArrayList<String> platforms = new ArrayList<>(Arrays.asList("NA","BR","LA","EUNE","EUW","TR","RU","JP","KR","OC"));

    public static StringRequest getSummonerByName(String name, String platform, Response.Listener<String> onSuccess, Response.ErrorListener onError) {
        final String platformURL = getPlatformURLByName(platform);
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
        final String platformURL = getPlatformURLByName(platform);
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

    public static StringRequest getSummonerByPUUID(String puuid, String platform, Response.Listener<String> onSuccess, Response.ErrorListener onError) {
        final String platformURL = getPlatformURLByName(platform);
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

    public static StringRequest getMatchHistoryList(String puuid, int count, String platform, Response.Listener<String> onSuccess, Response.ErrorListener onError) {
        final String platformURL = getRegionURLByName(platform);
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
        final String platformURL = getPlatformURLByName(platform);
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

    public static void onError(VolleyError error) {
        Log.d("NetWork Request Failed", error.toString());
    }

    public static String getRegionURLByName(String platform) {
        switch(platform) {
            case "NA": return "https://americas.api.riotgames.com";
            case "BR": return "https://americas.api.riotgames.com";
            case "LA": return "https://americas.api.riotgames.com";
            case "EUNE": return "https://europe.api.riotgames.com";
            case "EUW": return "https://europe.api.riotgames.com";
            case "TR": return "https://europe.api.riotgames.com";
            case "RU": return "https://europe.api.riotgames.com";
            case "JP": return "https://asia.api.riotgames.com";
            case "KR": return "https://asia.api.riotgames.com";
            case "OC": return "https://asia.api.riotgames.com";
        }
        return "";
    }

    public static String getPlatformURLByName(String name) {
        switch(name) {
            case "BR": return "https://br1.api.riotgames.com";
            case "EUNE": return "https://eun1.api.riotgames.com";
            case "EUW": return "https://euw1.api.riotgames.com";
            case "JP": return "https://jp1.api.riotgames.com";
            case "KR": return "https://kr.api.riotgames.com";
            case "LAS": return "https://la1.api.riotgames.com";
            case "OC": return "https://oc1.api.riotgames.com";
            case "TR": return "https://tr1.api.riotgames.com";
            case "RU": return "https://ru.api.riotgames.com";
            case "NA": return "https://na1.api.riotgames.com";
        }
        return "";
    }

    public static ArrayList<String> getPlatforms() {
        return platforms;
    }
}