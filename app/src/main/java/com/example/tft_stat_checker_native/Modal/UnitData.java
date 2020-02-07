package com.example.tft_stat_checker_native.Modal;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class UnitData {
    private String characterID;
    private String name;
    private int rarity;
    private int tier;
    private ArrayList<ItemData> items;

    public UnitData(String characterID, String name, int rarity, int tier, ArrayList<ItemData> items) {
        this.characterID = characterID;
        this.name = name;
        this.rarity = rarity;
        this.tier = tier;
        this.items = items;
    }

    public UnitData(JSONObject unit) throws JSONException {
        this.characterID = unit.getString("character_id");
        this.name = unit.getString("name");
        this.rarity = unit.getInt("rarity");
        this.tier = unit.getInt("tier");
        this.items = new ArrayList<>();

        JSONArray itemList = unit.getJSONArray("items");
        for (int i = 0; i < itemList.length(); i++) {
            this.items.add(new ItemData(itemList.getInt(i)));
        }
    }

    public String getCharacterID() {
        return characterID;
    }
    public String getName() {
        return name;
    }
    public int getRarity() {
        return rarity;
    }
    public int getTier() {
        return tier;
    }
    public ArrayList<ItemData> getItems() {
        return items;
    }
}