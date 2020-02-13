package com.example.tft_stat_checker_native.Modal;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class TraitData {
    private String name;
    private int numUnits;
    private int style;
    private int tierCurrent;
    private int tierTotal;

    public TraitData(String name, int numUnits, int style, int tierCurrent, int tierTotal) {
        this.name = name;
        this.numUnits = numUnits;
        this.style = style;
        this.tierCurrent = tierCurrent;
        this.tierTotal = tierTotal;
    }

    public TraitData(JSONObject trait) throws JSONException {
        this.name = trait.getString("name");
        this.numUnits = trait.getInt("num_units");
        this.style = trait.getInt("style");
        this.tierCurrent = trait.getInt("tier_current");
        this.tierTotal = trait.getInt("tier_total");
    }

    public String getName() {
        return name;
    }
    public int getNumUnits() {
        return numUnits;
    }
    public int getStyle() {
        return style;
    }
    public int getTierCurrent() {
        return tierCurrent;
    }
    public int getTierTotal() {
        return tierTotal;
    }
}