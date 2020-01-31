package com.example.tft_stat_checker_native;

import androidx.appcompat.widget.PopupMenu;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
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
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class MainActivity extends Activity {

    RequestQueue requestQueue;
    SummonerData summonerData;
    SummonerRankedData summonerRankedData;
    MatchHistoryList matchHistoryList;

    String searchTarget = "";
    String platform = "";

    String currentSearchTarget = "";
    String currentPlatform = "";

    HashSet<String> searchHistory;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        readSearchHistory();

        requestQueue = Volley.newRequestQueue(this);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        iniRecyclerView();
        iniSearchBar();
        iniSwipeRefresh();
        iniKeyboard();
        iniChangeRegionButton();
    }

    public void iniKeyboard() {
        View root = findViewById(R.id.root_view);
        setupUI(root);
    }

    public void setupUI(View view) {
        // Set up touch listener for non-text box views to hide keyboard.
        if (!(view instanceof EditText)) {
            view.setOnTouchListener((View v, MotionEvent event) -> {
                TextView searchText = findViewById(R.id.searchText);
                hideSoftKeyboard();
                searchText.clearFocus();
                return false;
            });
        }

        //If a layout container, iterate over children and seed recursion.
        if (view instanceof ViewGroup) {
            for (int i = 0; i < ((ViewGroup) view).getChildCount(); i++) {
                View innerView = ((ViewGroup) view).getChildAt(i);
                setupUI(innerView);
            }
        }
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
            EditText t = findViewById(R.id.searchText);
            int start = (int) t.getY();
            SwipeRefreshLayout layout = findViewById(R.id.match_history_card_list_container);
            layout.setProgressViewOffset(true, start, start + 200);
            layout.setDistanceToTriggerSync(300);
        });
    }

    public void iniChangeRegionButton() {
        Button changeRegionButton = findViewById(R.id.change_region_button);
        changeRegionButton.setOnClickListener((view) -> {
            ChangeRegionDialog dialog = new ChangeRegionDialog(this, this.platform);
            dialog.setOnDismissListener((arg) -> {
                this.platform = dialog.getSelectedPlatform();
                Log.d("selected platform", this.platform);
            });
            dialog.show();
        });
    }

    public void showLoading() {
        ProgressBar loadingIndicator = findViewById(R.id.loading_indicator);
        loadingIndicator.setVisibility(View.VISIBLE);
    }

    public void hideLoading() {
        ProgressBar loadingIndicator = findViewById(R.id.loading_indicator);
        loadingIndicator.setVisibility(View.INVISIBLE);
    }

    public void refreshData(){
        clearData();
        showLoading();
        fetchSummonerData(searchTarget, "NA");
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
                    onNetWorkRequestError(error);
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

    private void addSearchHistory(String s) {
        SharedPreferences sharedPreferences = getSharedPreferences("storage", MODE_PRIVATE);
        searchHistory.add(s);
        sharedPreferences.edit().putStringSet("searchHistory", searchHistory).commit();
        AutoCompleteTextView searchText = findViewById(R.id.searchText);
        AutoCompleteArrayAdapter adapter = (AutoCompleteArrayAdapter) searchText.getAdapter();
        adapter.addItem(s);
    }

    private void readSearchHistory() {
        SharedPreferences sharedPreferences = getSharedPreferences("storage", MODE_PRIVATE);
        Set<String> data = sharedPreferences.getStringSet("searchHistory", new HashSet<String>());
        this.searchHistory = new HashSet<>();
        data.forEach((searchItem) -> searchHistory.add(searchItem));
    }

    private void iniSearchBar() {
        AutoCompleteTextView searchText = findViewById(R.id.searchText);
        searchText.setOnEditorActionListener((TextView v, int actionID, KeyEvent evt) -> {
            searchTarget = searchText.getText().toString();
            refreshData();
            hideSoftKeyboard();
            searchText.clearFocus();
            return true;
        });

        searchText.setOnFocusChangeListener((view, focus) -> { if (focus) { searchText.showDropDown(); } });

        ArrayList<String> autoCompleteNameList = new ArrayList<>();
        this.searchHistory.forEach((name) -> autoCompleteNameList.add(name));

        ArrayAdapter<String> names = new ArrayAdapter<>(this, R.layout.auto_complete_item, autoCompleteNameList);
        searchText.setAdapter(names);
    }

    public void hideSoftKeyboard() {
        if (getCurrentFocus() != null) {
            InputMethodManager inputMethodManager = (InputMethodManager) this.getSystemService(Activity.INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(this.getCurrentFocus().getWindowToken(), 0);
        }
    }
}