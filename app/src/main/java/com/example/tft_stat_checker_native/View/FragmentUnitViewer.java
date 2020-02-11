package com.example.tft_stat_checker_native.View;

import android.content.Context;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SortedList;

import com.example.tft_stat_checker_native.Controller.ListItemOnClickListener;
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
    private GridLayoutManager layoutManager;

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
        this.layoutManager = new GridLayoutManager(getContext(), 3);
    }

    private void iniRecyclerView() {
        this.championsList.addItemDecoration(new ChampionListItemDecoration());
        this.championsList.setLayoutManager(layoutManager);
        this.championsList.setAdapter(listAdapter);
        this.championsList.hasFixedSize();
        this.listAdapter.setListItemOnClickListener((index) -> Log.d("Champion List", "index: " + index));
    }

    public void onReselect() {

    }
}

class ChampionListAdapter extends RecyclerView.Adapter<ChampionListViewHolder>{

    private Drawable unitBorderRarity0;
    private Drawable unitBorderRarity1;
    private Drawable unitBorderRarity2;
    private Drawable unitBorderRarity3;
    private Drawable unitBorderRarity4;
    private Drawable unitBorderRarity5;

    private SortedList<ChampionData> sortedListData;
    private LayoutInflater itemInflater;
    private Context context;
    private ListItemOnClickListener listItemOnClickListener;

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
        this.context = context;

        this.unitBorderRarity0 = ContextCompat.getDrawable(context, R.drawable.unit_border_rarity0);
        this.unitBorderRarity1 = ContextCompat.getDrawable(context, R.drawable.unit_border_rarity1);
        this.unitBorderRarity2 = ContextCompat.getDrawable(context, R.drawable.unit_border_rarity2);
        this.unitBorderRarity3 = ContextCompat.getDrawable(context, R.drawable.unit_border_rarity3);
        this.unitBorderRarity4 = ContextCompat.getDrawable(context, R.drawable.unit_border_rarity4);
        this.unitBorderRarity5 = ContextCompat.getDrawable(context, R.drawable.unit_border_rarity5);
    }

    @NonNull
    @Override
    public ChampionListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ChampionListViewHolder(itemInflater.inflate(R.layout.champions_list_item, parent, false));
    }

    public void setListItemOnClickListener(ListItemOnClickListener listItemOnClickListener) {
        this.listItemOnClickListener = listItemOnClickListener;
    }

    @Override
    public void onBindViewHolder(@NonNull ChampionListViewHolder holder, int position) {
        holder.itemView.setOnClickListener((view) -> listItemOnClickListener.onItemClicked(holder.getLayoutPosition()));

        holder.getChampionName().setText(sortedListData.get(position).getChampion());

        String drawableName = sortedListData.get(position).getChampion().toLowerCase();
        int id = context.getResources().getIdentifier("tft2_" + drawableName, "drawable", context.getPackageName());
        holder.getIcon().setImageResource(id);
        switch(sortedListData.get(position).getCost()) {
            case 1: { holder.getIcon().setForeground(this.unitBorderRarity0); break; }
            case 2: { holder.getIcon().setForeground(this.unitBorderRarity1); break; }
            case 3: { holder.getIcon().setForeground(this.unitBorderRarity2); break; }
            case 4: { holder.getIcon().setForeground(this.unitBorderRarity3); break; }
            case 5: { holder.getIcon().setForeground(this.unitBorderRarity4); break; }
            case 7: { holder.getIcon().setForeground(this.unitBorderRarity5); break; }
        }
        holder.getIcon().setClipToOutline(true);
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
        this.icon.setClipToOutline(true);
    }

    public ImageView getIcon() {
        return icon;
    }

    public TextView getChampionName() {
        return championName;
    }
}

class ChampionListItemDecoration extends RecyclerView.ItemDecoration {
    @Override
    public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
        outRect.top = 16;
        outRect.bottom = 16;
    }
}