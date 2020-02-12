package com.example.tft_stat_checker_native.View;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.tft_stat_checker_native.Controller.SearchHistoryManager;
import com.example.tft_stat_checker_native.Modal.API;
import com.example.tft_stat_checker_native.Modal.MatchHistoryListData;
import com.example.tft_stat_checker_native.Modal.SearchHistoryData;
import com.example.tft_stat_checker_native.Modal.SummonerData;
import com.example.tft_stat_checker_native.Modal.SummonerRankedData;
import com.example.tft_stat_checker_native.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class FragmentSearchSummoner extends Fragment {
    public static final int EDIT_SEARCH_TEXT = 1;
    public static final String SEARCH_TEXT_RESULT_KEY = "SEARCH_TEXT_KEY";
    public static final String PLATFORM_RESULT_KEY = "PLATFORM_RESULT_KEY";
    public static final String SEARCH_HISTORY_KEY = "SEARCH_HISTORY_KEY";

    private RequestQueue requestQueue;

    // data
    private SummonerData summonerData;
    private SummonerRankedData summonerRankedData;
    private MatchHistoryListData matchHistoryList;

    // text displayed
    private String summonerName = "";
    private String platform = "NA";

    // page elements
    private ProgressBar loadingIndicator;
    private TextView summonerNameText;
    private TextView platformText;
    private ConstraintLayout searchBar;
    private RecyclerView matchHistoryRecyclerView;
    private SwipeRefreshLayout pullToRefresh;
    private LinearLayoutManager matchHistoryListLayoutManager;
    private MatchHistoryListAdapter matchHistoryListAdapter;

    // search history
    private SearchHistoryManager searchHistoryManager;
    private SharedPreferences.Editor searchHistoryEditor;

    public static FragmentSearchSummoner createInstance() {
        Bundle args = new Bundle();
        FragmentSearchSummoner frag = new FragmentSearchSummoner();
        frag.setArguments(args);
        return frag;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        ViewGroup contentView = (ViewGroup) inflater.inflate(R.layout.activity_search_summoner, container, false);
        iniComponents(contentView);
        iniRecyclerView();
        iniSwipeRefresh();
        iniSearchBar();
        return contentView;
    }

    @Override
    public void onStart() {
        super.onStart();
        iniSearchHistory();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        requestQueue = Volley.newRequestQueue(getContext());
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @NonNull Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case EDIT_SEARCH_TEXT: {
                if (resultCode == getActivity().RESULT_OK) {
                    String newSearchText = data.getStringExtra(SEARCH_TEXT_RESULT_KEY);
                    String newPlatform = data.getStringExtra(PLATFORM_RESULT_KEY);
                    if (newSearchText.length() > 0) {
                        updateSummonerName(newSearchText);
                        updatePlatform(newPlatform);
                        loadData();
                    }
                }
                break;
            }
        }
    }

    private void iniComponents(ViewGroup container) {
        this.loadingIndicator = container.findViewById(R.id.loading_indicator);
        this.summonerNameText = container.findViewById(R.id.search_text);
        this.platformText = container.findViewById(R.id.platform_text);
        this.searchBar = container.findViewById(R.id.search_bar_container);
        this.matchHistoryRecyclerView = container.findViewById(R.id.match_history_card_list);
        this.pullToRefresh = container.findViewById(R.id.match_history_card_list_container);
    }

    private void iniSearchBar() {
        // go to search activity on press
        searchBar.setOnClickListener((view) -> {
            Intent editSearchText = new Intent(getActivity(), ActivityEditSearchParam.class);
            editSearchText.putExtra(SEARCH_TEXT_RESULT_KEY, summonerName);
            editSearchText.putExtra(PLATFORM_RESULT_KEY, platform);
            editSearchText.putExtra(SEARCH_HISTORY_KEY, searchHistoryManager.toJSON().toString());
            startActivityForResult(editSearchText, EDIT_SEARCH_TEXT);
        });
    }

    private void iniSwipeRefresh() {
        // set-up onRefresh listener
        pullToRefresh.setOnRefreshListener(() -> {
            // do on refresh
            requestQueue.cancelAll((Request<?> request) -> true);
            loadData();
        });

        // offset top
        // refresh trigger offset
        getActivity().getWindow().getDecorView().post(() -> {
            int start = (int) searchBar.getY();
            pullToRefresh.setProgressViewOffset(true, start, start + 200);
            pullToRefresh.setDistanceToTriggerSync(300);
        });

        pullToRefresh.setEnabled(false);
    }

    private void iniRecyclerView() {
        matchHistoryRecyclerView.hasFixedSize();

        matchHistoryListLayoutManager = new LinearLayoutManager(getActivity());
        matchHistoryListLayoutManager.setInitialPrefetchItemCount(10);
        matchHistoryRecyclerView.setLayoutManager(matchHistoryListLayoutManager);

        matchHistoryListAdapter =  new MatchHistoryListAdapter(getActivity(), requestQueue);
        matchHistoryRecyclerView.setAdapter(matchHistoryListAdapter);

        matchHistoryListAdapter.setListItemOnClickListener((int position) -> {
            switch (matchHistoryListAdapter.getItemStatus(position)) {
                case MatchHistoryListAdapter.FAILED: {
                    matchHistoryListAdapter.reLoadData(position);
                    break;
                }
                case MatchHistoryListAdapter.LOADED: {
                    //Intent viewMatchDetail = new Intent();
                    String data = matchHistoryListAdapter.getMatchDataAt(position).getJson().toString();
                    Intent viewMatchDetail = new Intent(getContext(), ActivityViewMatchDetail.class);
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
        searchHistoryEditor = getActivity().getPreferences(Context.MODE_PRIVATE).edit();
        searchHistoryManager = new SearchHistoryManager(getActivity().getPreferences(Context.MODE_PRIVATE));
    }

    private void showLoading() {
        loadingIndicator.setVisibility(View.VISIBLE);
    }

    private void hideLoading() {
        if (!pullToRefresh.isEnabled()) {
            pullToRefresh.setEnabled(true);
        }
        pullToRefresh.setRefreshing(false);
        loadingIndicator.setVisibility(View.INVISIBLE);
    }

    private void onRefresh() {
        loadData();
    }

    private void loadData(){
        clearData();
        showLoading();
        requestQueue.cancelAll((arg) -> true);
        fetchSummonerData(summonerName, platform);
        matchHistoryListAdapter.setPlatform(platform);
        addSearchHistory(summonerName, platform);
    }

    private void addSearchHistory(String name, String platform) {
        if (name.length() > 0) {
            searchHistoryManager.add(new SearchHistoryData(name, platform), searchHistoryEditor);
        }
    }

    private void clearData() {
        matchHistoryListAdapter.removeAllItems();
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
                            summonerData = data;

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
                        hideLoading();
                        Toast.makeText(getContext(), "Summoner Not Found", Toast.LENGTH_SHORT).show();
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
                        summonerRankedData = data;

                        // update UI
                        updatePlayerCard();

                        // fetch match history list
                        fetchMatchHistoryList(summonerData.getPuuid(), platform);

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
                        MatchHistoryListData matchHistory = new MatchHistoryListData(l);

                        // store data
                        matchHistoryList = matchHistory;

                        // update list
                        updateMatchHistoryList();

                        // set loading indicator
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
        hideLoading();
        Toast.makeText(getContext(), "NetWork Error", Toast.LENGTH_SHORT).show();
    }

    private void updateMatchHistoryList() {
        matchHistoryListAdapter.appendAllItems(matchHistoryList.getList());
        matchHistoryListAdapter.setMoreToLoad(false);
    }

    private void updatePlayerCard() {
        matchHistoryListAdapter.setListHeaderData(summonerData, summonerRankedData);
    }

    private void updatePlatform(String platform) {
        this.platform = platform;
        this.platformText.setText(platform);
    }

    private void updateSummonerName(String summonerName) {
        this.summonerName = summonerName;
        this.summonerNameText.setText(summonerName);
    }

    public void onReselect() {
        if (matchHistoryListLayoutManager.findFirstVisibleItemPosition() > 15) {
            matchHistoryRecyclerView.scrollToPosition(15);
            matchHistoryRecyclerView.smoothScrollToPosition(0);
        } else {
            matchHistoryRecyclerView.smoothScrollToPosition(0);
        }
    }

    public boolean onBackPressed() {
        return false;
    }
}
