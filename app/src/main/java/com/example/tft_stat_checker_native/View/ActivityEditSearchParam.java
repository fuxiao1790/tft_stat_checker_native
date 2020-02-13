package com.example.tft_stat_checker_native.View;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.Button;
import android.widget.EditText;

import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tft_stat_checker_native.Controller.SearchHistoryManager;
import com.example.tft_stat_checker_native.Modal.SearchHistoryData;
import com.example.tft_stat_checker_native.R;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;

public class ActivityEditSearchParam extends FragmentActivity {

    private EditText searchTextField;
    private Button changeRegionButton;
    private RecyclerView searchHistoryList;
    private SearchHistoryListAdapter searchHistoryListAdapter;

    private String searchText;
    private String platform;

    private ArrayList<SearchHistoryData> searchHistoryData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_search_params);

        iniData();
        iniPageComponents();
        iniChangeRegionButton();
        iniSearchHistoryList();
        iniSearchTextField();

        searchTextField.requestFocus();
    }

    private void iniData() {
        // data from intent
        Bundle bundle = getIntent().getExtras();
        searchText = bundle.getString(FragmentSearchSummoner.SEARCH_TEXT_RESULT_KEY);
        platform = bundle.getString(FragmentSearchSummoner.PLATFORM_RESULT_KEY);
        try {
            searchHistoryData = SearchHistoryManager.buildListFromJSONArray(new JSONArray(bundle.getString(FragmentSearchSummoner.SEARCH_HISTORY_KEY, "")));
        } catch(JSONException error) {
            searchHistoryData = new ArrayList<>();
        }
    }

    private void iniSearchHistoryList() {
        // add layout manager
        searchHistoryList.setLayoutManager(new LinearLayoutManager(this));

        // add adapter
        this.searchHistoryListAdapter = new SearchHistoryListAdapter(this.searchHistoryData, this);
        this.searchHistoryList.setAdapter(this.searchHistoryListAdapter);

        // item on click listener
        this.searchHistoryListAdapter.setListItemOnClickListener((index) -> {
            SearchHistoryData data = this.searchHistoryListAdapter.getDataAtPosition(index);
            updatePlatform(data.getPlatform());
            updateSearchText(data.getSummonerName());
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
            ChangeRegionBottomSheet changeRegionDialog = new ChangeRegionBottomSheet();
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
        searchTextField.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) { }
            @Override public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) { }
            @Override public void afterTextChanged(Editable editable) {
                // query search result
                // searchTextField.getText().toString()
                ArrayList<SearchHistoryData> filteredData = new ArrayList<>();
                searchHistoryData.forEach((element) -> {
                    if (element.getSummonerName().toLowerCase().contains(searchTextField.getText().toString().toLowerCase())) {
                        filteredData.add(element);
                    }
                });
                searchHistoryListAdapter.replaceAll(filteredData);
            }
        });
    }

    private void searchButtonOnPress() {
        Intent output = new Intent();
        output.putExtra(FragmentSearchSummoner.SEARCH_TEXT_RESULT_KEY, this.searchText);
        output.putExtra(FragmentSearchSummoner.PLATFORM_RESULT_KEY, this.platform);
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