package com.example.tft_stat_checker_native.View;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SortedList;

import com.example.tft_stat_checker_native.Modal.ChampionData;
import com.example.tft_stat_checker_native.Modal.JSONResourceReader;
import com.example.tft_stat_checker_native.R;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;

public class FragmentUnitViewer extends Fragment {

    // page components
    private RecyclerView championsList;
    private ChampionListAdapter listAdapter;
    private LinearLayoutManager layoutManager;

    // data
    private ArrayList<ChampionData> champions;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View contentView = inflater.inflate(R.layout.fragment_unit_viewer, container, false);
        iniComponents(contentView);
        iniRecyclerView();
        return contentView;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            this.champions = ChampionData.buildListFromJSON(new JSONArray(JSONResourceReader.readResource(R.raw.champions, getContext())));
        } catch (JSONException err) {
            this.champions = new ArrayList<>();
        }
    }

    public static FragmentUnitViewer createInstance() {
        Bundle args = new Bundle();
        FragmentUnitViewer frag = new FragmentUnitViewer();
        frag.setArguments(args);
        return frag;
    }

    private void iniComponents(View view) {
        this.championsList = view.findViewById(R.id.champions_list);
        this.listAdapter = new ChampionListAdapter(getContext(), champions);
        this.layoutManager = new LinearLayoutManager(getContext());
    }

    private void iniRecyclerView() {
        this.championsList.setLayoutManager(layoutManager);
        this.championsList.setAdapter(listAdapter);
        this.championsList.hasFixedSize();
    }

    public void onReselect() {

    }
}

class ChampionListAdapter extends RecyclerView.Adapter<ChampionListViewHolder>{

    private SortedList<ChampionData> sortedListData;
    private LayoutInflater itemInflater;

    private final SortedList.Callback<ChampionData> sortedListCallBack = new SortedList.Callback<ChampionData>() {
        @Override public int compare(ChampionData o1, ChampionData o2) { return o1.compareTo(o2); }
        @Override public void onChanged(int position, int count) {notifyItemRangeChanged(position, count); }
        @Override public boolean areContentsTheSame(ChampionData oldItem, ChampionData newItem) { return oldItem.compareTo(newItem) == 0; }
        @Override public boolean areItemsTheSame(ChampionData item1, ChampionData item2) { return item1.compareTo(item2) == 0; }
        @Override public void onInserted(int position, int count) { notifyItemRangeInserted(position, count); }
        @Override public void onRemoved(int position, int count) { notifyItemRangeRemoved(position, count); }
        @Override public void onMoved(int fromPosition, int toPosition) { notifyItemMoved(fromPosition, toPosition); }
    };

    public void replaceAll(ArrayList<ChampionData> list) {
        sortedListData.replaceAll(list);
    }

    public ChampionListAdapter(Context context, ArrayList<ChampionData> list) {
        this.sortedListData = new SortedList<>(ChampionData.class, this.sortedListCallBack);
        sortedListData.addAll(list);
        this.itemInflater = LayoutInflater.from(context);
    }

    @NonNull
    @Override
    public ChampionListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ChampionListViewHolder(itemInflater.inflate(R.layout.champions_list_item, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ChampionListViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return sortedListData.size();
    }
}

class ChampionListViewHolder extends RecyclerView.ViewHolder {
    private ImageView icon;
    private TextView championName;
    public ChampionListViewHolder(@NonNull View itemView) {
        super(itemView);
        this.icon = itemView.findViewById(R.id.unit_icon);
        this.championName = itemView.findViewById(R.id.unit_name);
    }

    public ImageView getIcon() {
        return icon;
    }

    public TextView getChampionName() {
        return championName;
    }
}