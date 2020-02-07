package com.example.tft_stat_checker_native.Modal;

import com.example.tft_stat_checker_native.Modal.MatchData;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.HashMap;

public class MatchHistoryListData {
    private ArrayList<MatchData> list;
    private HashMap<String, MatchData> map;

    public MatchHistoryListData(JSONArray list) throws JSONException {
        this.list = new ArrayList<>();
        this.map = new HashMap<>();

        for (int i = 0; i < list.length(); i++) {
            MatchData data = new MatchData(list.getString(i));
            this.map.put(list.getString(i), data);
            this.list.add(data);
        }
    }

    public ArrayList<MatchData> getList() {
        return list;
    }

    public HashMap<String, MatchData> getMap() {
        return map;
    }
}
