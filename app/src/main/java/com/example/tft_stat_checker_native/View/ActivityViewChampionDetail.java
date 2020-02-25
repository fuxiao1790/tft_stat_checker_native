package com.example.tft_stat_checker_native.View;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tft_stat_checker_native.Modal.ChampionData;
import com.example.tft_stat_checker_native.Modal.JSONResourceReader;
import com.example.tft_stat_checker_native.Modal.TraitDetailData;
import com.example.tft_stat_checker_native.R;
import com.google.android.flexbox.FlexboxLayout;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

public class ActivityViewChampionDetail extends Activity {

    // data
    private ArrayList<TraitDetailData> allTraitDetailDataList;
    private HashMap<String, TraitDetailData> allTraitDetailDataMap;
    private ChampionData championData;

    //page components
    private ImageView championIcon;
    private TextView championName;
    private FlexboxLayout traitIconContainer;
    private RecyclerView traitList;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_champion_detail);

        iniData();
        iniPageComponents();
        bindDataToComponents();
    }

    private void iniData() {
        Bundle bundle = getIntent().getExtras();
        try {
            this.championData = new ChampionData(new JSONObject(bundle.getString("championData")));

            this.allTraitDetailDataList = TraitDetailData.buildListFromJSON(new JSONArray(JSONResourceReader.readResource(R.raw.traits, this)));

            this.allTraitDetailDataMap = new HashMap<>();
            allTraitDetailDataList.forEach((traitDetailData -> allTraitDetailDataMap.put(traitDetailData.getName(), traitDetailData)));
        } catch(JSONException err) {
            Log.d("JSONException", err.toString());
        }
    }

    private void iniPageComponents() {
        this.championIcon = findViewById(R.id.champion_icon);
        this.championName = findViewById(R.id.champion_name);
        this.traitIconContainer = findViewById(R.id.trait_icon_container);
        this.traitList = findViewById(R.id.trait_list);
    }

    private void bindDataToComponents() {
        championName.setText(championData.getChampion());
    }
}
