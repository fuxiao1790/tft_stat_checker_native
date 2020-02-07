package com.example.tft_stat_checker_native.Controller;

import android.content.SharedPreferences;
import android.util.Log;

import com.example.tft_stat_checker_native.Modal.SearchHistoryData;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class SearchHistoryManager {
    private ArrayList<SearchHistoryData> searchHistory;

    public SearchHistoryManager(SharedPreferences storage) {
        this.searchHistory = new ArrayList<>();
        try {
            String jsonString = storage.getString("searchHistory", "tag");
            JSONArray jsonArray = new JSONArray(jsonString);
            for (int i = 0; i < jsonArray.length(); i++) {
                searchHistory.add(new SearchHistoryData((JSONObject) jsonArray.get(i)));
            }
        } catch(JSONException error) {
            Log.d("JSONException", error.toString());
        }
        Log.d("SearchHistoryManager", this.searchHistory.size() + "");
    }

    public void add(SearchHistoryData data, SharedPreferences.Editor editor) {
        for (int i = 0; i < searchHistory.size(); i++) {
            if (data.getSummonerName().equals(searchHistory.get(i).getSummonerName()) && data.getPlatform().equals(searchHistory.get(i).getPlatform())) {
                searchHistory.get(i).setNewTimeStamp();
                saveChanges(editor);
                return;
            }
        }
        searchHistory.add(data);
        saveChanges(editor);
    }

    private void saveChanges(SharedPreferences.Editor editor) {
        String jsonString = toJSON().toString();
        if (jsonString != null) {
            editor.putString("searchHistory", toJSON().toString());
            editor.apply();
        }
    }

    public JSONArray toJSON() {
        try {
            JSONArray array = new JSONArray();
            for(int i = 0; i < searchHistory.size(); i++) {
                array.put(searchHistory.get(i).toJSON());
            }
            return array;
        } catch(JSONException error) {
            return null;
        }
    }

    public static ArrayList<SearchHistoryData> buildListFromJSONArray(JSONArray data) throws JSONException {
        ArrayList<SearchHistoryData> list = new ArrayList<>();
        for (int i = 0; i < data.length(); i++) {
            list.add(new SearchHistoryData(data.getJSONObject(i)));
        }
        return list;
    }
}
