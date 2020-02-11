package com.example.tft_stat_checker_native.View;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SortedList;

import com.example.tft_stat_checker_native.Controller.ListItemOnClickListener;
import com.example.tft_stat_checker_native.Modal.SearchHistoryData;
import com.example.tft_stat_checker_native.R;

import java.util.ArrayList;

public class SearchHistoryListAdapter extends RecyclerView.Adapter<SearchHistoryListViewHolder> {

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
            holder.itemView.setOnClickListener((view) -> listItemOnClickListener.onItemClicked(holder.getLayoutPosition()));
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