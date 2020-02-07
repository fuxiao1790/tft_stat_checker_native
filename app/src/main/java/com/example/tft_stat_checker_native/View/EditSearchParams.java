package com.example.tft_stat_checker_native.View;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
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
import androidx.recyclerview.widget.SortedList;

import com.example.tft_stat_checker_native.ListItemOnClickListener;
import com.example.tft_stat_checker_native.Modal.SearchHistoryData;
import com.example.tft_stat_checker_native.R;
import com.example.tft_stat_checker_native.SearchHistoryManager;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;

public class EditSearchParams extends FragmentActivity {

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

    private LayoutInflater layoutInflater;
    private ListItemOnClickListener listItemOnClickListener;

    private SortedList<SearchHistoryData> sortedListData;
    private final SortedList.Callback<SearchHistoryData> sortedListCallBack = new SortedList.Callback<SearchHistoryData>() {
        @Override public int compare(SearchHistoryData o1, SearchHistoryData o2) { return o1.compareTo(o2); }
        @Override public void onChanged(int position, int count) {notifyItemRangeChanged(position, count); }
        @Override public boolean areContentsTheSame(SearchHistoryData oldItem, SearchHistoryData newItem) { return oldItem.compareTo(newItem) == 0; }
        @Override public boolean areItemsTheSame(SearchHistoryData item1, SearchHistoryData item2) { return item1.compareTo(item2) == 0; }
        @Override public void onInserted(int position, int count) { notifyItemRangeInserted(position, count); }
        @Override public void onRemoved(int position, int count) { notifyItemRangeRemoved(position, count); }
        @Override public void onMoved(int fromPosition, int toPosition) { notifyItemMoved(fromPosition, toPosition); }
    };

    public SearchHistoryListAdapter(ArrayList<SearchHistoryData> listData, Context ctx) {
        this.sortedListData = new SortedList<>(SearchHistoryData.class, this.sortedListCallBack);
        this.sortedListData.addAll(listData);
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
        holder.getSummonerName().setText(sortedListData.get(position).getSummonerName());
        holder.getPlatform().setText(sortedListData.get(position).getPlatform());

        if (this.listItemOnClickListener != null) {
            holder.itemView.setOnClickListener((view) -> listItemOnClickListener.onItemClicked(position));
        }
    }

    public void replaceAll(ArrayList<SearchHistoryData> data) {
        sortedListData.replaceAll(data);
    }

    @Override public int getItemCount() {
        return sortedListData.size();
    }

    public SearchHistoryData getDataAtPosition(int position) {
        return this.sortedListData.get(position);
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