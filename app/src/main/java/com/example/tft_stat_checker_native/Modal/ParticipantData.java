package com.example.tft_stat_checker_native.Modal;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class ParticipantData {
    private int goldLeft;
    private int lastRound;
    private int level;
    private int placement;
    private int playersEliminated;
    private String puuid;
    private double timeEliminated;
    private int totalDamageToPlayers;
    private ArrayList<UnitData> units;
    private ArrayList<TraitData> traits;

    private SummonerData summonerData;
    private SummonerRankedData summonerRankedData;

    public ParticipantData(JSONObject participant) throws JSONException {
        this.goldLeft = participant.getInt("gold_left");
        this.lastRound = participant.getInt("last_round");
        this.level = participant.getInt("level");
        this.placement = participant.getInt("placement");
        this.playersEliminated = participant.getInt("players_eliminated");
        this.puuid = participant.getString("puuid");
        this.timeEliminated = participant.getDouble("time_eliminated");
        this.totalDamageToPlayers = participant.getInt("total_damage_to_players");
        this.units = new ArrayList<>();

        JSONArray units = participant.getJSONArray("units");
        for (int i = 0; i < units.length(); i++) {
            this.units.add(new UnitData(units.getJSONObject(i)));
        }

        JSONArray traits = participant.getJSONArray("traits");
        this.traits = new ArrayList<>();
        for (int i = 0; i < traits.length(); i++) {
            this.traits.add(new TraitData(traits.getJSONObject(i)));
        }
        this.traits.sort((TraitData a, TraitData b) -> b.getStyle() - a.getStyle());
    }

    public void loadSummonerData(SummonerData summonerData, SummonerRankedData summonerRankedData) {
        this.summonerData = summonerData;
        this.summonerRankedData = summonerRankedData;
    }

    public SummonerData getSummonerData() { return summonerData; }
    public SummonerRankedData getSummonerRankedData() { return summonerRankedData; }
    public int getGoldLeft() {
        return goldLeft;
    }
    public int getLastRound() {
        return lastRound;
    }
    public int getLevel() {
        return level;
    }
    public int getPlacement() {
        return placement;
    }
    public int getPlayersEliminated() {
        return playersEliminated;
    }
    public String getPuuid() {
        return puuid;
    }
    public double getTimeEliminated() {
        return timeEliminated;
    }
    public int getTotalDamageToPlayers() {
        return totalDamageToPlayers;
    }
    public ArrayList<UnitData> getUnits() {
        return units;
    }
    public ArrayList<TraitData> getTraits() {
        return traits;
    }
}