package com.example.tft_stat_checker_native;

import org.json.JSONException;
import org.json.JSONObject;

public class SummonerData {
    private final String id;
    private final String accountId;
    private final String puuid;
    private final String name;
    private final int profileIconId;
    private final int summonerLevel;
    private final long revisionDate;

    public SummonerData(String id, String accountId, String puuid, String name, int profileIconId, int summonerLevel, long revisionDate) {
        this.id = id;
        this.accountId = accountId;
        this.puuid = puuid;
        this.name = name;
        this.profileIconId = profileIconId;
        this.summonerLevel = summonerLevel;
        this.revisionDate = revisionDate;
    }

    public SummonerData(JSONObject jsonRes) throws JSONException {
        id = jsonRes.getString("id");
        accountId = jsonRes.getString("accountId");
        puuid = jsonRes.getString("puuid");
        name = jsonRes.getString("name");
        profileIconId = jsonRes.getInt("profileIconId");
        summonerLevel = jsonRes.getInt("summonerLevel");
        revisionDate = jsonRes.getInt("revisionDate");
    }

    public String getName() {
        return name;
    }

    public int getProfileIconId() {
        return profileIconId;
    }

    public int getSummonerLevel() {
        return summonerLevel;
    }

    public long getRevisionDate() {
        return revisionDate;
    }

    public String getAccountId() {
        return accountId;
    }

    public String getId() {
        return id;
    }

    public String getPuuid() {
        return puuid;
    }
}
