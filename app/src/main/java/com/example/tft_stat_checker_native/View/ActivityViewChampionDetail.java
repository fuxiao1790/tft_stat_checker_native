package com.example.tft_stat_checker_native.View;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.Nullable;

import com.example.tft_stat_checker_native.Modal.ChampionData;
import com.example.tft_stat_checker_native.Modal.JSONResourceReader;
import com.example.tft_stat_checker_native.Modal.TraitDetailData;
import com.example.tft_stat_checker_native.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class ActivityViewChampionDetail extends Activity {
    private ArrayList<TraitDetailData> allTraitDetailData;
    private ChampionData championData;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_champion_detail);

        iniData();
    }

    private void iniData() {
        Bundle bundle = getIntent().getExtras();
        try {
            this.championData = new ChampionData(new JSONObject(bundle.getString("championData")));
            this.allTraitDetailData = TraitDetailData.buildListFromJSON(new JSONArray(JSONResourceReader.readResource(R.raw.traits, this)));
        } catch(JSONException err) {
            Log.d("JSONException", err.toString());
        }
    }
}
