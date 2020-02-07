package com.example.tft_stat_checker_native;

import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class MainActivity extends FragmentActivity {

    public static final int EDIT_SEARCH_TEXT = 1;
    public static final String SEARCH_TEXT_RESULT_KEY = "SEARCH_TEXT_KEY";
    public static final String PLATFORM_RESULT_KEY = "PLATFORM_RESULT_KEY";
    public static final String SEARCH_HISTORY_KEY = "SEARCH_HISTORY_KEY";

    RequestQueue requestQueue;
    SummonerData summonerData;
    SummonerRankedData summonerRankedData;
    MatchHistoryList matchHistoryList;

    String searchTarget = "";
    String platform = "NA";

    // page elements
    ProgressBar loadingIndicator;
    TextView searchText;
    TextView changeRegionButton;
    ConstraintLayout searchBar;
    RecyclerView matchHistoryRecyclerView;
    RecyclerViewListAdapter matchHistoryListAdapter;

    // search history
    private SearchHistoryManager searchHistoryManager;
    private SharedPreferences.Editor searchHistoryEditor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestQueue = Volley.newRequestQueue(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        iniRecyclerView();
        iniSwipeRefresh();
        iniSearchBar();
    }

    @Override
    protected void onStart() {
        super.onStart();
        iniSearchHistory();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case MainActivity.EDIT_SEARCH_TEXT: {
                if (resultCode == MainActivity.RESULT_OK) {
                    String newSearchText = data.getStringExtra(MainActivity.SEARCH_TEXT_RESULT_KEY);
                    String newPlatform = data.getStringExtra(MainActivity.PLATFORM_RESULT_KEY);
                    if (newSearchText.length() > 0) {
                        searchText.setText(newSearchText);
                        searchTarget = newSearchText;

                        changeRegionButton.setText(newPlatform);
                        platform = newPlatform;
                        refreshData();
                    }
                }
                break;
            }
        }
    }

    public void iniSearchBar() {
        this.searchText = findViewById(R.id.search_text);
        this.changeRegionButton = findViewById(R.id.change_region_button);
        searchBar = findViewById(R.id.search_bar_container);
        // go to search activity on press
        searchBar.setOnClickListener((view) -> {
            Intent editSearchText = new Intent(this, EditSearchParams.class);
            editSearchText.putExtra(MainActivity.SEARCH_TEXT_RESULT_KEY, this.searchTarget);
            editSearchText.putExtra(MainActivity.PLATFORM_RESULT_KEY, this.platform);
            editSearchText.putExtra(MainActivity.SEARCH_HISTORY_KEY, this.searchHistoryManager.toJSON().toString());
            startActivityForResult(editSearchText, MainActivity.EDIT_SEARCH_TEXT);
        });
    }

    public void iniSwipeRefresh() {
        // set-up onRefresh listener
        SwipeRefreshLayout container = findViewById(R.id.match_history_card_list_container);
        container.setOnRefreshListener(() -> {
            // do on refresh
            requestQueue.cancelAll((Request<?> request) -> true);
            refreshData();
        });

        // offset top
        // refresh trigger offset
        getWindow().getDecorView().post(() -> {
            ConstraintLayout searchBar = findViewById(R.id.search_bar_container);
            int start = (int) searchBar.getY();
            SwipeRefreshLayout layout = findViewById(R.id.match_history_card_list_container);
            layout.setProgressViewOffset(true, start, start + 200);
            layout.setDistanceToTriggerSync(300);
        });
    }

    private void iniRecyclerView() {
        this.matchHistoryRecyclerView = findViewById(R.id.match_history_card_list);
        this.matchHistoryRecyclerView.hasFixedSize();

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setInitialPrefetchItemCount(10);
        this.matchHistoryRecyclerView.setLayoutManager(layoutManager);

        this.matchHistoryListAdapter =  new RecyclerViewListAdapter(this, this.requestQueue);

        this.matchHistoryRecyclerView.setAdapter(matchHistoryListAdapter);

        this.matchHistoryRecyclerView.setVerticalScrollBarEnabled(true);

        matchHistoryListAdapter.setOnCardClickListener((int position) -> {

            switch (matchHistoryListAdapter.getItemStatus(position)) {
                case RecyclerViewListAdapter.FAILED: {
                    matchHistoryListAdapter.reLoadData(position);
                    break;
                }
                case RecyclerViewListAdapter.LOADED: {
                    //Intent viewMatchDetail = new Intent();
                    String data = matchHistoryListAdapter.getMatchDataAt(position).getJson().toString();
                    Intent viewMatchDetail = new Intent(this, ViewMatchDetail.class);
                    viewMatchDetail.putExtra("matchData", data);
                    viewMatchDetail.putExtra("platform", this.platform);
                    startActivity(viewMatchDetail);
                    break;
                }
            }
        });

        if (summonerData != null && summonerRankedData != null) {
            updatePlayerCard();
        }

        if (matchHistoryList != null) {
            updateMatchHistoryList();
        }
    }

    private void iniSearchHistory() {
        this.searchHistoryEditor = getPreferences(Context.MODE_PRIVATE).edit();
        this.searchHistoryManager = new SearchHistoryManager(getPreferences(Context.MODE_PRIVATE));
    }

    private void showLoading() {
        if (this.loadingIndicator == null) {
            this.loadingIndicator = findViewById(R.id.loading_indicator);
        }
        this.loadingIndicator.setVisibility(View.VISIBLE);
    }

    private void hideLoading() {
        if (this.loadingIndicator == null) {
            this.loadingIndicator = findViewById(R.id.loading_indicator);
        }
        this.loadingIndicator.setVisibility(View.INVISIBLE);
    }

    private void refreshData(){
        clearData();
        showLoading();
        fetchSummonerData(this.searchTarget, this.platform);
        matchHistoryListAdapter.setPlatform(this.platform);
        addSearchHistory(searchTarget, platform);
    }

    private void addSearchHistory(String name, String platform) {
        if (name.length() > 0) {
            this.searchHistoryManager.add(new SearchHistoryData(name, platform), this.searchHistoryEditor);
        }
    }

    private void clearData() {
        RecyclerView target = findViewById(R.id.match_history_card_list);
        RecyclerViewListAdapter targetAdapter = (RecyclerViewListAdapter) target.getAdapter();
        targetAdapter.removeAllItems();
        summonerData = null;
        summonerRankedData = null;
        matchHistoryList = null;
    }

    private void fetchSummonerData(String name, String platform) {
        StringRequest req = API.getSummonerByName(
                name,
                platform,
                (String res) -> {
                    // received data close queue
                    Log.d("getSummonerByName", res);
                    try {
                        JSONObject jsonRes = new JSONObject(res);
                        if (jsonRes.has("status")) {
                            // handle server status error
                            // usually caused by exceeding request limit
                        } else {
                            // create data obj
                            SummonerData data = new SummonerData(jsonRes);

                            // store data
                            this.summonerData = data;

                            // fetch ranked data
                            fetchSummonerRankedData(summonerData.getId(), platform);
                        }
                    } catch(org.json.JSONException e) {
                        Log.d("getSummonerByName", "Bad Response(Malformed JSON response): " + e.toString());
                    }
                },
                (VolleyError error) -> {
                    API.onError(error);
                    if (error.networkResponse.statusCode == 404) {
                        SwipeRefreshLayout loader = findViewById(R.id.match_history_card_list_container);
                        loader.setRefreshing(false);
                        hideLoading();
                        Toast.makeText(this, "Summoner Not Found", Toast.LENGTH_SHORT).show();
                    } else {
                        onNetWorkRequestError(error);
                    }
                }
        );
        requestQueue.add(req);
    }

    private void fetchSummonerRankedData(String id, String platform) {
        StringRequest req = API.getSummonerRankedData(
                id,
                platform,
                (String res) -> {
                    // received data close queue
                    Log.d("getSummonerRankedData", res);
                    try {
                        JSONArray obj = new JSONArray(res);
                        // create data obj
                        JSONObject jsonRes = obj.getJSONObject(0);
                        SummonerRankedData data = new SummonerRankedData(jsonRes);

                        // store data
                        this.summonerRankedData = data;

                        // update UI
                        this.updatePlayerCard();

                        // fetch match history list
                        this.fetchMatchHistoryList(summonerData.getPuuid(), platform);

                    } catch(org.json.JSONException e) {
                        Log.d("getSummonerRankedData", "Bad Response(Malformed JSON response): " + e.toString());
                    }
                },
                (VolleyError error) -> {
                    API.onError(error);
                    onNetWorkRequestError(error);
                }
        );
        requestQueue.add(req);
    }

    private void fetchMatchHistoryList(String puuid, String platform) {
        StringRequest req = API.getMatchHistoryList(
                puuid,
                100,
                platform,
                (String res) -> {
                    Log.d("fetchMatchHistoryList", res);

                    try {
                        // convert data
                        JSONArray l = new JSONArray(res);
                        MatchHistoryList matchHistory = new MatchHistoryList(l);

                        // store data
                        this.matchHistoryList = matchHistory;

                        // update list
                        this.updateMatchHistoryList();

                        // set loading indicator
                        SwipeRefreshLayout layout = findViewById(R.id.match_history_card_list_container);
                        layout.setRefreshing(false);
                        hideLoading();

                    } catch (JSONException error) {
                        Log.d("fetchMatchHistoryList", "Bad Response(Malformed JSON response): " + error.toString());
                    }
                },
                (VolleyError error) -> {
                    API.onError(error);
                    onNetWorkRequestError(error);
                }
        );
        requestQueue.add(req);
    }

    private void onNetWorkRequestError(VolleyError error) {
        SwipeRefreshLayout loader = findViewById(R.id.match_history_card_list_container);
        loader.setRefreshing(false);
        hideLoading();
        Toast.makeText(this, "NetWork Error", Toast.LENGTH_SHORT).show();
    }

    private void updateMatchHistoryList() {
        RecyclerView target = findViewById(R.id.match_history_card_list);
        RecyclerViewListAdapter targetAdapter = (RecyclerViewListAdapter) target.getAdapter();

        targetAdapter.appendAllItems(matchHistoryList.getList());
        targetAdapter.setMoreToLoad(false);
    }

    private void updatePlayerCard() {
        RecyclerView rv = findViewById(R.id.match_history_card_list);
        RecyclerViewListAdapter targetAdapter = (RecyclerViewListAdapter) rv.getAdapter();
        targetAdapter.setListHeaderData(new ListHeaderData(summonerData, summonerRankedData));
    }

    private void updatePlatform() {
        TextView changePlatformButton = findViewById(R.id.change_region_button);
        changePlatformButton.setText(this.platform);
    }
}