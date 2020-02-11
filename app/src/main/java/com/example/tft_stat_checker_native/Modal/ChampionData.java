package com.example.tft_stat_checker_native.Modal;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class ChampionData {
    private String champion;
    private int cost;
    private ArrayList<String> traits;

    public ChampionData(JSONObject json) throws JSONException {
        this.champion = json.getString("champion");
        this.cost = json.getInt("cost");
        this.traits = new ArrayList<>();
        JSONArray temp = json.getJSONArray("traits");
        for (int i = 0; i < temp.length(); i++) {
            traits.add(temp.getString(i));
        }
    }

    public static ArrayList<ChampionData> buildListFromJSON(JSONArray json) {
        ArrayList<ChampionData> list = new ArrayList<>();
        try {
            for (int i = 0; i < json.length(); i++) {
                list.add(new ChampionData(json.getJSONObject(i)));
            }
            return list;
        } catch(JSONException err) {
            return list;
        }
    }

    public String getChampion() {
        return champion;
    }

    public int getCost() {
        return cost;
    }

    public ArrayList<String> getTraits() {
        return traits;
    }

    public int compareTo(ChampionData other) {
        return champion.compareTo(other.getChampion());
    }
}
