package com.example.tft_stat_checker_native;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class EditSearchParams extends FragmentActivity {

    private EditText searchTextField;
    private Button changeRegionButton;
    private RecyclerView searchHistoryList;

    private String searchText;
    private String platform;

    private ArrayList<SearchHistoryData> searchHistoryData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_search_params);

        iniData();
        iniPageComponents();
        iniSearchTextField();
        iniChangeRegionButton();
        iniSearchHistoryList();

        searchTextField.requestFocus();
    }

    private void iniData() {
        // data from intent
        Bundle bundle = getIntent().getExtras();
        searchText = bundle.getString(MainActivity.SEARCH_TEXT_RESULT_KEY);
        platform = bundle.getString(MainActivity.PLATFORM_RESULT_KEY);
        try {
            searchHistoryData = SearchHistoryManager.buildListFromJSONArray(new JSONArray(bundle.getString(MainActivity.SEARCH_HISTORY_KEY, "")));
        } catch(JSONException error) {
            searchHistoryData = new ArrayList<>();
        }
    }

    private void iniSearchHistoryList() {
        // add layout manager
        searchHistoryList.setLayoutManager(new LinearLayoutManager(this));

        // add adapter
        SearchHistoryListAdapter adapter = new SearchHistoryListAdapter(this.searchHistoryData, this);
        searchHistoryList.setAdapter(adapter);

        // item on click listener
        adapter.setListItemOnClickListener((index) -> {
            updatePlatform(this.searchHistoryData.get(index).getPlatform());
            updateSearchText(this.searchHistoryData.get(index).getSummonerName());
            searchButtonOnPress();
        });

        // optimizations
        searchHistoryList.hasFixedSize();
    }

    private void iniPageComponents() {
        searchTextField = findViewById(R.id.search_text);
        changeRegionButton = findViewById(R.id.change_region_button);
        searchHistoryList = findViewById(R.id.search_history_list);
    }

    private void iniChangeRegionButton() {
        changeRegionButton.setText(this.platform);

        changeRegionButton.setOnClickListener((view) -> {
            ChangeRegionDialog changeRegionDialog = new ChangeRegionDialog();
            changeRegionDialog.setDefaultHighlightedItem(platform);
            changeRegionDialog.setOnDialogConfirmListener((platform) -> updatePlatform(platform));
            changeRegionDialog.show(getSupportFragmentManager(), "wtf?");
        });
    }

    private void iniSearchTextField() {
        searchTextField.setText(this.searchText);
        searchTextField.setOnEditorActionListener((target, key, event) -> {
            this.updateSearchText(searchTextField.getText().toString());
            this.searchButtonOnPress();
            return false;
        });
    }

    private void searchButtonOnPress() {
        Intent output = new Intent();
        output.putExtra(MainActivity.SEARCH_TEXT_RESULT_KEY, this.searchText);
        output.putExtra(MainActivity.PLATFORM_RESULT_KEY, this.platform);
        setResult(Activity.RESULT_OK, output);
        finish();
    }

    private void updateSearchText(String searchText) {
        this.searchText = searchText;
        this.searchTextField.setText(searchText);
    }

    private void updatePlatform(String platform) {
        this.platform = platform;
        this.changeRegionButton.setText(platform);
    }
}

class SearchHistoryListAdapter extends RecyclerView.Adapter<SearchHistoryListViewHolder> {

    private ArrayList<SearchHistoryData> listData;
    private LayoutInflater layoutInflater;
    private ListItemOnClickListener listItemOnClickListener;

    public SearchHistoryListAdapter(ArrayList<SearchHistoryData> listData, Context ctx) {
        this.listData = listData;
        this.layoutInflater = LayoutInflater.from(ctx);
    }

    public void setListItemOnClickListener(ListItemOnClickListener listItemOnClickListener) {
        this.listItemOnClickListener = listItemOnClickListener;
    }

    @NonNull
    @Override
    public SearchHistoryListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new SearchHistoryListViewHolder(layoutInflater.inflate(R.layout.search_history_list_item, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull SearchHistoryListViewHolder holder, int position) {
        holder.getSummonerName().setText(listData.get(position).getSummonerName());
        holder.getPlatform().setText(listData.get(position).getPlatform());

        if (this.listItemOnClickListener != null) {
            holder.itemView.setOnClickListener((view) -> listItemOnClickListener.onItemClicked(position));
        }
    }

    @Override
    public int getItemCount() {
        return listData.size();
    }
}

class SearchHistoryListViewHolder extends RecyclerView.ViewHolder{
    private TextView summonerName;
    private TextView platform;

    public SearchHistoryListViewHolder(@NonNull View itemView) {
        super(itemView);
        this.summonerName = itemView.findViewById(R.id.summoner_name);
        this.platform = itemView.findViewById(R.id.platform);
    }

    public TextView getPlatform() {
        return platform;
    }

    public TextView getSummonerName() {
        return summonerName;
    }
}

class SearchHistoryData implements Comparable<SearchHistoryData>{
    private String summonerName;
    private String platform;

    public SearchHistoryData(String summonerName, String platform) {
        this.summonerName = summonerName;
        this.platform = platform;
    }

    public SearchHistoryData(JSONObject data) throws JSONException {
        this.summonerName = data.getString("summonerName");
        this.platform = data.getString("platform");
    }

    public String getSummonerName() {
        return summonerName;
    }

    public String getPlatform() {
        return platform;
    }

    public JSONObject toJSON() throws JSONException{
        JSONObject obj = new JSONObject();
        obj.put("summonerName", summonerName);
        obj.put("platform", platform);
        return obj;
    }

    @Override
    public int compareTo(SearchHistoryData other) {
        if (this.summonerName.equals(other.summonerName) && this.platform.equals(other.platform)) {
            return 0;
        } else {
            return -1;
        }
    }
}