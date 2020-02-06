package com.example.tft_stat_checker_native;

import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
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

public class MainActivity extends FragmentActivity {

    public static final int EDIT_SEARCH_TEXT = 1;
    public static final String SEARCH_TEXT_RESULT_KEY = "SEARCH_TEXT_KEY";
    public static final String PLATFORM_RESULT_KEY = "PLATFORM_RESULT_KEY";

    RequestQueue requestQueue;
    SummonerData summonerData;
    SummonerRankedData summonerRankedData;
    MatchHistoryList matchHistoryList;

    String searchTarget = "";
    String platform = "NA";

    // page elements
    ProgressBar loadingIndicator;
    TextView searchText;
    Button changeRegionButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestQueue = Volley.newRequestQueue(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        iniRecyclerView();
        iniSwipeRefresh();
        iniChangeRegionButton();
        iniSearchBar();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case MainActivity.EDIT_SEARCH_TEXT: {
                if (resultCode == MainActivity.RESULT_OK) {
                    String newSearchText = data.getStringExtra(MainActivity.SEARCH_TEXT_RESULT_KEY);
                    String newPlatform = data.getStringExtra(MainActivity.PLATFORM_RESULT_KEY);
                    searchText.setText(newSearchText);
                    searchTarget = newSearchText;
                    changeRegionButton.setText(newPlatform);
                    platform = newPlatform;
                    refreshData();
                }
                break;
            }
        }
    }

    public void iniSearchBar() {
        this.searchText = findViewById(R.id.search_text);

        // go to search activity on press
        this.searchText.setOnClickListener((view) -> {
            Intent editSearchText = new Intent(this, EditSearchParams.class);
            editSearchText.putExtra(MainActivity.SEARCH_TEXT_RESULT_KEY, this.searchTarget);
            editSearchText.putExtra(MainActivity.PLATFORM_RESULT_KEY, this.platform);
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

    public void iniChangeRegionButton() {
        this.changeRegionButton = findViewById(R.id.change_region_button);
        this.changeRegionButton.setOnClickListener((view) -> {
            ChangeRegionDialog changeRegionDialog = new ChangeRegionDialog();
            changeRegionDialog.setDefaultHighlightedItem(this.platform);
            changeRegionDialog.setOnDialogConfirmListener((selectedPlatform) -> {
                this.platform = selectedPlatform;
                this.updatePlatform();
            });
            changeRegionDialog.show(getSupportFragmentManager(), "wtf?");
        });
    }

    private void iniRecyclerView() {
        RecyclerView target = findViewById(R.id.match_history_card_list);
        target.hasFixedSize();

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setInitialPrefetchItemCount(10);
        target.setLayoutManager(layoutManager);

        RecyclerViewListAdapter targetAdapter =  new RecyclerViewListAdapter(this, this.requestQueue);

        target.setAdapter(targetAdapter);

        target.setVerticalScrollBarEnabled(true);

        targetAdapter.setOnCardClickListener((int position) -> {

            switch (targetAdapter.getItemStatus(position)) {
                case RecyclerViewListAdapter.FAILED: {
                    targetAdapter.reLoadData(position);
                    break;
                }
                case RecyclerViewListAdapter.LOADED: {
                    //Intent viewMatchDetail = new Intent();
                    String data = targetAdapter.getMatchDataAt(position).getJson().toString();
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

    public void showLoading() {
        if (this.loadingIndicator == null) {
            this.loadingIndicator = findViewById(R.id.loading_indicator);
        }
        this.loadingIndicator.setVisibility(View.VISIBLE);
    }

    public void hideLoading() {
        if (this.loadingIndicator == null) {
            this.loadingIndicator = findViewById(R.id.loading_indicator);
        }
        this.loadingIndicator.setVisibility(View.INVISIBLE);
    }

    public void refreshData(){
        clearData();
        showLoading();
        fetchSummonerData(this.searchTarget, this.platform);
        //addSearchHistory(searchTarget);
    }

    public void clearData() {
        RecyclerView target = findViewById(R.id.match_history_card_list);
        RecyclerViewListAdapter targetAdapter = (RecyclerViewListAdapter) target.getAdapter();
        targetAdapter.removeAllItems();
        summonerData = null;
        summonerRankedData = null;
        matchHistoryList = null;
    }

    public void fetchSummonerData(String name, String platform) {
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

    public void fetchSummonerRankedData(String id, String platform) {
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

    public void fetchMatchHistoryList(String puuid, String platform) {
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

    public void onNetWorkRequestError(VolleyError error) {
        SwipeRefreshLayout loader = findViewById(R.id.match_history_card_list_container);
        loader.setRefreshing(false);
        hideLoading();
        Toast.makeText(this, "NetWork Error", Toast.LENGTH_SHORT).show();
    }

    public void updateMatchHistoryList() {
        RecyclerView target = findViewById(R.id.match_history_card_list);
        RecyclerViewListAdapter targetAdapter = (RecyclerViewListAdapter) target.getAdapter();

        targetAdapter.appendAllItems(matchHistoryList.getList());
        targetAdapter.setMoreToLoad(false);
    }

    public void updatePlayerCard() {
        RecyclerView rv = findViewById(R.id.match_history_card_list);
        RecyclerViewListAdapter targetAdapter = (RecyclerViewListAdapter) rv.getAdapter();
        targetAdapter.setListHeaderData(new ListHeaderData(summonerData, summonerRankedData));
    }

    public void updatePlatform() {
        Button changePlatformButton = findViewById(R.id.change_region_button);
        changePlatformButton.setText(this.platform);
    }
}