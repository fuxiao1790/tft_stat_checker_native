package com.example.tft_stat_checker_native.Modal;

import org.json.JSONException;
import org.json.JSONObject;

public class SearchHistoryData implements Comparable<SearchHistoryData>{
    private static final String SUMMONER_NAME_JSON_KEY = "SUMMONER_NAME_JSON_KEY";
    private static final String PLATFORM_NAME_JSON_KEY = "PLATFORM_NAME_JSON_KEY";
    private static final String TIME_STAMP_JSON_KEY = "TIME_STAMP_JSON_KEY";

    private String summonerName;
    private String platform;
    private long timeStamp;

    public SearchHistoryData(String summonerName, String platform) {
        this.summonerName = summonerName;
        this.platform = platform;
        this.timeStamp = System.currentTimeMillis();
    }

    public SearchHistoryData(JSONObject data) throws JSONException {
        this.summonerName = data.getString(SearchHistoryData.SUMMONER_NAME_JSON_KEY);
        this.platform = data.getString(SearchHistoryData.PLATFORM_NAME_JSON_KEY);
        this.timeStamp = data.getLong(SearchHistoryData.TIME_STAMP_JSON_KEY);
    }

    public JSONObject toJSON() throws JSONException{
        JSONObject obj = new JSONObject();
        obj.put(SearchHistoryData.SUMMONER_NAME_JSON_KEY, summonerName);
        obj.put(SearchHistoryData.PLATFORM_NAME_JSON_KEY, platform);
        obj.put(SearchHistoryData.TIME_STAMP_JSON_KEY, timeStamp);
        return obj;
    }

    public void setNewTimeStamp() { this.timeStamp = System.currentTimeMillis(); }

    public String getSummonerName() {
        return summonerName;
    }
    public long getTimeStamp() { return timeStamp; }
    public String getPlatform() { return platform; }

    @Override
    public int compareTo(SearchHistoryData other) {
        // sort by time stamp
        if (this.timeStamp - other.timeStamp > 0) {
            return -1;
        } else if (this.timeStamp - other.timeStamp < 0) {
            return 1;
        } else {
            return 0;
        }

        // sort by name alphabetical order
//        int nameDiff = this.summonerName.compareTo(other.summonerName);
//        int platformDiff = this.platform.compareTo(other.platform);
//        if (nameDiff == 0 && platformDiff == 0) {
//            return 0;
//        } else if (nameDiff != 0) {
//            return nameDiff;
//        } else {
//            return platformDiff;
//        }
    }
}