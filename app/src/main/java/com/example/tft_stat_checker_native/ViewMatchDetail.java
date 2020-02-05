package com.example.tft_stat_checker_native;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

public class ViewMatchDetail extends Activity {

    MatchData data;
    RequestQueue requestQueue;
    String platform;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_match_detail);

        requestQueue = Volley.newRequestQueue(this);

        iniMatchData();
        iniBackButton();
        iniRecyclerView();
    }

    public void iniBackButton() {
        Button backButton = findViewById(R.id.back_button);
        backButton.setOnClickListener((View view) -> finish());
    }

    public void iniMatchData() {
        Bundle bundle = getIntent().getExtras();
        String message = bundle.getString("matchData");
        this.platform = bundle.getString("platform");
        Log.d("JSON", message);
        try {
            JSONObject json = new JSONObject(message);
            MatchData data = new MatchData(json);
            data.getParticipants().sort((ParticipantData a, ParticipantData b) -> a.getPlacement() - b.getPlacement());
            this.data = data;
        } catch(JSONException error) {
            Log.d("JSONException", error.toString());
        }
    }

    public void iniRecyclerView() {
        if (data != null) {
            RecyclerView rv = findViewById(R.id.participant_list);
            rv.setLayoutManager(new LinearLayoutManager(this));
            ParticipantListAdapter adapter = new ParticipantListAdapter(data.getParticipants(), requestQueue, this);
            adapter.setPlatform(this.platform);
            rv.setAdapter(adapter);
            rv.hasFixedSize();
        }
    }
}
