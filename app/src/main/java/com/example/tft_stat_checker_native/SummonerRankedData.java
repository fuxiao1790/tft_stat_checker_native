package com.example.tft_stat_checker_native;

import org.json.JSONException;
import org.json.JSONObject;

public class SummonerRankedData {
    private final String leagueId;
    private final String queueType;
    private final String tier;
    private final String rank;
    private final String summonerId;
    private final String summonerName;
    private final int leaguePoints;
    private final int wins;
    private final int losses;
    private final boolean veteran;
    private final boolean inactive;
    private final boolean freshBlood;
    private final boolean hotStreak;

    public SummonerRankedData(JSONObject jsonRes) throws JSONException {
        this.leagueId = jsonRes.getString("queueType");
        this.queueType = jsonRes.getString("queueType");
        this.tier = jsonRes.getString("tier");
        this.rank = jsonRes.getString("rank");
        this.summonerId = jsonRes.getString("summonerId");
        this.summonerName = jsonRes.getString("summonerName");
        this.leaguePoints = jsonRes.getInt("leaguePoints");
        this.wins = jsonRes.getInt("wins");
        this.losses = jsonRes.getInt("losses");
        this.veteran = jsonRes.getBoolean("veteran");
        this.inactive = jsonRes.getBoolean("inactive");
        this.freshBlood = jsonRes.getBoolean("freshBlood");
        this.hotStreak = jsonRes.getBoolean("hotStreak");
    }

    public SummonerRankedData(String leagueId, String queueType, String tier, String rank, String summonerId, String summonerName, int leaguePoints, int wins, int losses, boolean veteran, boolean inactive, boolean freshBlood, boolean hotStreak ) {
        this.leagueId = leagueId;
        this.queueType = queueType;
        this.tier = tier;
        this.rank = rank;
        this.summonerId = summonerId;
        this.summonerName = summonerName;
        this.leaguePoints = leaguePoints;
        this.wins = wins;
        this.losses = losses;
        this.veteran = veteran;
        this.inactive = inactive;
        this.freshBlood = freshBlood;
        this.hotStreak = hotStreak;
    }

    public String getLeagueId() {
        return leagueId;
    }

    public String getQueueType() {
        return queueType;
    }

    public String getTier() {
        return tier;
    }

    public String getRank() {
        return rank;
    }

    public String getSummonerId() {
        return summonerId;
    }

    public String getSummonerName() {
        return summonerName;
    }

    public int getLeaguePoints() {
        return leaguePoints;
    }

    public int getWins() {
        return wins;
    }

    public int getLosses() {
        return losses;
    }

    public boolean isVeteran() {
        return veteran;
    }

    public boolean isInactive() {
        return inactive;
    }

    public boolean isFreshBlood() {
        return freshBlood;
    }

    public boolean isHotStreak() {
        return hotStreak;
    }
}
