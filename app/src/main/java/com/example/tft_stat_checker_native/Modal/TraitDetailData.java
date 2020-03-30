package com.example.tft_stat_checker_native.Modal;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class TraitDetailData {
    private String name;
    private String innate;
    private String description;
    private ArrayList<Integer> sets;

    public TraitDetailData(JSONObject data) {
        try { this.name = data.getString("name"); } catch(JSONException err) { this.name = ""; }
        try { this.innate = data.getString("innate"); } catch(JSONException err) { this.innate = ""; }
        try { this.description = data.getString("description"); } catch(JSONException err) { this.description = ""; }
        try {
            JSONArray arr = data.getJSONArray("sets");
            this.sets = new ArrayList<>();
            for (int i = 0; i < arr.length(); i++) {
                sets.add(arr.getInt(i));
            }
        } catch(JSONException err) {
            this.sets = new ArrayList<>();
        }
    }

    public static ArrayList<TraitDetailData> buildListFromJSON(JSONArray list) {
        try {
            ArrayList<TraitDetailData> re = new ArrayList<>();
            for (int i = 0; i < list.length(); i++) {
                re.add(new TraitDetailData(list.getJSONObject(i)));
            }
            return re;
        } catch(JSONException err) {
            return new ArrayList<>();
        }
    }

    public String getName() {
        return name;
    }

    public ArrayList<Integer> getSets() {
        return sets;
    }

    public String getDescription() {
        return description;
    }

    public String getInnate() {
        return innate;
    }
}
