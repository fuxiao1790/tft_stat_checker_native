package com.example.tft_stat_checker_native.View;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.tft_stat_checker_native.Modal.API;
import com.example.tft_stat_checker_native.Controller.Config;
import com.example.tft_stat_checker_native.Controller.ListItemOnClickListener;
import com.example.tft_stat_checker_native.Modal.MatchData;
import com.example.tft_stat_checker_native.Modal.SummonerData;
import com.example.tft_stat_checker_native.Modal.SummonerRankedData;
import com.example.tft_stat_checker_native.Modal.TraitData;
import com.example.tft_stat_checker_native.Modal.UnitData;
import com.example.tft_stat_checker_native.R;
import com.google.android.flexbox.FlexboxLayout;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

public class MatchHistoryListAdapter extends RecyclerView.Adapter<MatchHistoryListViewHolder> {

    public static final int LOADING = 1;
    public static final int NONE = 0;
    public static final int LOADED = 2;
    public static final int FAILED = 3;

    private ArrayList<MatchData> data;
    private boolean moreToLoad;
    private SummonerData summonerData;
    private SummonerRankedData summonerRankedData;
    private RequestQueue requestQueue;

    // stores all cards loading state
    private HashMap<String, Integer> loadingState;

    // stores all imageviews inflated for all unit icons in a card
    // matchID => ArrayList
    private HashMap<String, ArrayList<ImageView>> unitIconsStorage;

    // stores all imageviews inflated for all trait icons in a card
    // matchID => ArrayList
    private HashMap<String, ArrayList<ImageView>> traitIconsStorage;

    // on click listener
    private ListItemOnClickListener listItemOnClickListener;

    // platform
    private String platform;

    private LayoutInflater layoutInflater;

    public MatchHistoryListAdapter(Context ctx, RequestQueue queue) {
        this.data = new ArrayList<>();
        this.data.add(new MatchData(0, new ArrayList<>(), new ArrayList<>(), MatchData.TYPE_HEADER, "00000000"));
        this.data.add(new MatchData(0, new ArrayList<>(), new ArrayList<>(), MatchData.TYPE_FOOTER, "00000000"));
        this.moreToLoad = true;
        this.loadingState = new HashMap<>();
        this.requestQueue = queue;
        this.unitIconsStorage = new HashMap<>();
        this.traitIconsStorage = new HashMap<>();
        this.layoutInflater = LayoutInflater.from(ctx);
    }

    public void setPlatform(String platform) {
        this.platform = platform;
    }

    public void setListHeaderData(SummonerData summonerData, SummonerRankedData summonerRankedData) {
        this.summonerData = summonerData;
        this.summonerRankedData = summonerRankedData;
        notifyItemChanged(0);
    }

    public void appendItem (MatchData item) {
        // append to list data
        int insertIndex = data.size();
        data.add(insertIndex - 1, item);

        // update loading state
        loadingState.put(item.getId(), MatchHistoryListAdapter.NONE);

        // update list
        notifyItemInserted(insertIndex - 1);
    }

    public MatchData getMatchDataAt(int index) {
        return data.get(index);
    }

    public void appendAllItems (ArrayList<MatchData> list) {
        // append to list data
        int insertIndex = data.size();
        data.addAll(insertIndex - 1, list);

        // update loading state
        for (int i = 0; i < list.size(); i++) {
            loadingState.put(list.get(i).getId(), MatchHistoryListAdapter.NONE);
        }

        // update list
        notifyItemRangeInserted(insertIndex - 1, list.size());
    }

    public void removeAllItems() {
        for (int i = data.size() - 1; i > -1; i--) {
            if (data.get(i).getType() != MatchData.TYPE_HEADER && data.get(i).getType() != MatchData.TYPE_FOOTER) {
                data.remove(i);
            }
        }
        this.summonerData = null;
        this.summonerRankedData = null;
        this.loadingState.clear();
        this.unitIconsStorage.clear();
        this.traitIconsStorage.clear();
        notifyDataSetChanged();
    }

    public void setMoreToLoad(boolean moreToLoad) {
        this.moreToLoad = moreToLoad;
    }

    public void setListItemOnClickListener(ListItemOnClickListener listItemOnClickListener) {
        this.listItemOnClickListener = listItemOnClickListener;
    }

    @Override
    public MatchHistoryListViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        switch (viewType) {
            case MatchData.TYPE_HEADER: {
                return new MatchHistoryListHeaderViewHolder(layoutInflater.inflate(R.layout.match_history_list_card_header, parent, false));
            }
            case MatchData.TYPE_FOOTER: {
                return new MatchHistoryListFooterViewHolder(layoutInflater.inflate(R.layout.match_history_list_card_footer, parent, false));
            }
            default: {
                return new MatchHistoryListContentViewHolder(layoutInflater.inflate(R.layout.match_history_list_card, parent, false));
            }
        }
    }

    @Override
    public void onBindViewHolder(@NonNull MatchHistoryListViewHolder holder, int position) {
        // update view based on view type
        if (holder instanceof MatchHistoryListContentViewHolder) {

            // if current item has data render item normally
            // else fetch data and update state and notify recylcerview to update
            switch (loadingState.get(data.get(position).getId())) {
                case MatchHistoryListAdapter.LOADED: {
                    renderContent(holder, position);
                    showContent((MatchHistoryListContentViewHolder) holder);
                    break;
                }
                case MatchHistoryListAdapter.NONE: {
                    loadCardData(position);
                    showLoading((MatchHistoryListContentViewHolder) holder);
                    break;
                }
                case MatchHistoryListAdapter.LOADING: {
                    showLoading((MatchHistoryListContentViewHolder) holder);
                    break;
                }
                case MatchHistoryListAdapter.FAILED: {
                    hideContent((MatchHistoryListContentViewHolder) holder);
                    break;
                }
                default: {
                    break;
                }
            }
        } else if (holder instanceof MatchHistoryListHeaderViewHolder && this.summonerData != null) {
            // render header here
            MatchHistoryListHeaderViewHolder temp = (MatchHistoryListHeaderViewHolder) holder;
            temp.getSummonerName().setText(this.summonerData.getName());
            temp.getSummonerRank().setText(this.summonerRankedData.getSummonerRankString());
            temp.getWinRate().setText(this.summonerRankedData.getWinLoseWinRatioString());
            temp.getQueueType().setText(this.summonerRankedData.getQueueType());
            Glide.with(temp.getSummonerIcon())
                    .load(Config.playerIconCDN + this.summonerData.getProfileIconId() + ".png")
                    .diskCacheStrategy(DiskCacheStrategy.DATA)
                    .into(temp.getSummonerIcon());
        }
    }

    private void showLoading(MatchHistoryListContentViewHolder holder) {
        holder.getStatus().setVisibility(View.INVISIBLE);
        holder.getStatus().setText("LOADING");

        holder.getLoadingIndicator().setVisibility(View.VISIBLE);

        holder.getMatchDuration().setVisibility(View.INVISIBLE);
        holder.getMatchDate().setVisibility(View.INVISIBLE);
        holder.getPlacement().setVisibility(View.INVISIBLE);
        holder.getUnitsContainer().setVisibility(View.INVISIBLE);
        holder.getTraitsContainer().setVisibility(View.INVISIBLE);
    }
    private void showContent(MatchHistoryListContentViewHolder holder) {
        holder.getStatus().setVisibility(View.INVISIBLE);
        holder.getStatus().setText("");

        holder.getLoadingIndicator().setVisibility(View.INVISIBLE);

        holder.getMatchDuration().setVisibility(View.VISIBLE);
        holder.getMatchDate().setVisibility(View.VISIBLE);
        holder.getPlacement().setVisibility(View.VISIBLE);
        holder.getUnitsContainer().setVisibility(View.VISIBLE);
        holder.getTraitsContainer().setVisibility(View.VISIBLE);
    }

    private void hideContent(MatchHistoryListContentViewHolder holder) {
        holder.getStatus().setVisibility(View.VISIBLE);
        holder.getStatus().setText("FAILED");

        holder.getLoadingIndicator().setVisibility(View.INVISIBLE);

        holder.getMatchDuration().setVisibility(View.INVISIBLE);
        holder.getMatchDate().setVisibility(View.INVISIBLE);
        holder.getPlacement().setVisibility(View.INVISIBLE);
        holder.getUnitsContainer().setVisibility(View.INVISIBLE);
        holder.getTraitsContainer().setVisibility(View.INVISIBLE);
    }

    private void renderContent(@NonNull MatchHistoryListViewHolder holder, int position) {
        holder.itemView.setOnClickListener((view) -> {
            if (this.listItemOnClickListener != null) {
                listItemOnClickListener.onItemClicked(position);
            }
        });

        MatchHistoryListContentViewHolder temp = (MatchHistoryListContentViewHolder) holder;
        temp.getPlacement().setText(data.get(position).getPlacement() + "");
        if (data.get(position).getPlacement() > 4) {
            temp.getPlacement().setBackgroundColor(ContextCompat.getColor(temp.getPlacement().getContext(), R.color.placementLost));
        } else {
            temp.getPlacement().setBackgroundColor(ContextCompat.getColor(temp.getPlacement().getContext(), R.color.placementWin));
        }
        temp.getMatchDate().setText(data.get(position).getGameDateTimeString());
        temp.getMatchDuration().setText(data.get(position).getGameLengthString());

        // reuse stored imageviews if possible
        if (unitIconsStorage.containsKey(data.get(position).getId())) {
            ArrayList<ImageView> icons = unitIconsStorage.get(data.get(position).getId());
            for (int i = 0; i < icons.size(); i++) {
                if (icons.get(i).getParent() != null) {
                    ((FlexboxLayout) icons.get(i).getParent()).removeView(icons.get(i));
                }
                temp.getUnitsContainer().addView(icons.get(i));
            }
        } else {
            ArrayList<ImageView> icons = new ArrayList<>();
            final FlexboxLayout unitIconContainer = ((MatchHistoryListContentViewHolder) holder).getUnitsContainer();
            for (int i = 0; i < data.get(position).getUnits().size(); i++) {

                // get drawable name from data
                String drawableName = data.get(position).getUnits().get(i).getCharacterID().toLowerCase();
                ImageView unitIcon = (ImageView) layoutInflater.inflate(R.layout.match_history_list_card_unit_icon, unitIconContainer, false);
                // get resource id
                Context ctx = unitIcon.getContext();
                int id = ctx.getResources().getIdentifier(drawableName, "drawable", ctx.getPackageName());

                unitIcon.setImageResource(id);
                unitIcon.setClipToOutline(true);
                unitIcon.setForeground(this.getUnitIconDecoration(data.get(position).getUnits().get(i), ctx));

                icons.add(unitIcon);
                unitIconContainer.addView(unitIcon);
            }
            unitIconsStorage.put(data.get(position).getId(), icons);
        }

        // reuse stored imageviews if possible
        if (traitIconsStorage.containsKey(data.get(position).getId())) {
            ArrayList<ImageView> icons = traitIconsStorage.get(data.get(position).getId());
            for (int i = 0; i < icons.size(); i++) {
                if (icons.get(i).getParent() != null) {
                    ((FlexboxLayout) icons.get(i).getParent()).removeView(icons.get(i));
                }
                temp.getTraitsContainer().addView(icons.get(i));
            }
        } else {
            ArrayList<ImageView> icons = new ArrayList<>();
            final FlexboxLayout traitIconContainer = ((MatchHistoryListContentViewHolder) holder).getTraitsContainer();
            for (int i = 0; i < data.get(position).getTraits().size(); i++) {
                if (data.get(position).getTraits().get(i).getStyle() != 0) {

                    // get drawable name from data
                    String drawableName = data.get(position).getTraits().get(i).getName().toLowerCase();

                    // inflate the imageview
                    ImageView traitIcon = (ImageView) layoutInflater.inflate(R.layout.match_history_list_card_trait_icon, traitIconContainer, false);

                    // get resource id
                    Context ctx = traitIcon.getContext();
                    int id = ctx.getResources().getIdentifier(drawableName, "drawable", ctx.getPackageName());

                    traitIcon.setImageResource(id);

                    // add background
                    traitIcon.setBackgroundResource(this.getTraitIconDecoration(data.get(position).getTraits().get(i)));

                    icons.add(traitIcon);
                    traitIconContainer.addView(traitIcon);
                }
            }
            traitIconsStorage.put(data.get(position).getId(), icons);
        }
    }

    private LayerDrawable getUnitIconDecoration(UnitData unit, Context ctx) {
        // background resource = corner
        // foreground resource = bourder + dots

        Drawable border = ContextCompat.getDrawable(ctx, R.drawable.unit_border_rarity0);
        // add border
        switch (unit.getRarity()) {
            case 0: { border = ContextCompat.getDrawable(ctx, R.drawable.unit_border_rarity0); break; }
            case 1: { border = ContextCompat.getDrawable(ctx, R.drawable.unit_border_rarity1); break; }
            case 2: { border = ContextCompat.getDrawable(ctx, R.drawable.unit_border_rarity2); break; }
            case 3: { border = ContextCompat.getDrawable(ctx, R.drawable.unit_border_rarity3); break; }
            case 4: { border = ContextCompat.getDrawable(ctx, R.drawable.unit_border_rarity4); break; }
            case 5: { border = ContextCompat.getDrawable(ctx, R.drawable.unit_border_rarity5); break; }
        }

        Drawable tier = ContextCompat.getDrawable(ctx, R.drawable.unit_tier1);
        // add tier icons
        switch (unit.getTier()) {
            case 1: { tier = ContextCompat.getDrawable(ctx, R.drawable.unit_tier1); break; }
            case 2: { tier = ContextCompat.getDrawable(ctx, R.drawable.unit_tier2); break; }
            case 3: { tier = ContextCompat.getDrawable(ctx, R.drawable.unit_tier3); break; }
        }

        return new LayerDrawable(new Drawable[]{border, tier});
    }

    private int getTraitIconDecoration(TraitData traitData) {
        switch(traitData.getStyle()) {
            case 1: { return R.drawable.trait_bg_tier1; }
            case 2: { return R.drawable.trait_bg_tier2; }
            case 3: { return R.drawable.trait_bg_tier3; }
            case 4: { return R.drawable.trait_bg_tier3; }
        }
        return 0;
    }

    public int getItemStatus(int position) {
        return loadingState.get(data.get(position).getId());
    }

    public void reLoadData(int position) {
        if (loadingState.get(data.get(position).getId()) == FAILED) {
            this.loadCardData(position);
        }
    }

    private void loadCardData(int position) {
        // set loading state to fetching
        loadingState.put(data.get(position).getId(), LOADING);

        // fetch data and update recyclerview
        StringRequest req = API.getMatchResultByMatchID(
                data.get(position).getId(),
                this.platform,
                (String res) -> {
                    Log.d("getMatchResultByMatchID", res);
                    try {
                        // load data into item
                        JSONObject jsonRes = new JSONObject(res);
                        data.get(position).loadData(jsonRes, this.summonerData.getPuuid());

                        // set loading state to LOADED
                        loadingState.put(data.get(position).getId(), LOADED);

                        // update view
                        notifyItemChanged(position);
                    } catch(JSONException error) {
                        // set loading state to FAILED
                        loadingState.put(data.get(position).getId(), FAILED);

                        // update view
                        notifyItemChanged(position);

                        // logging
                        Log.d("getMatchResultByMatchID", "Bad Response(Malformed JSON response): " + error.toString());
                    }
                },
                (VolleyError error) -> {
                    API.onError(error);
                    loadingState.put(data.get(position).getId(), FAILED);
                    notifyItemChanged(position);
                }
        );
        requestQueue.add(req);
    }

    @Override
    public void onViewRecycled(@NonNull MatchHistoryListViewHolder holder) {
        if (holder instanceof MatchHistoryListContentViewHolder) {
            if (((MatchHistoryListContentViewHolder) holder).getTraitsContainer() != null) {
                ((MatchHistoryListContentViewHolder) holder).getTraitsContainer().removeAllViews();
            }
            if (((MatchHistoryListContentViewHolder) holder).getUnitsContainer() != null) {
                ((MatchHistoryListContentViewHolder) holder).getUnitsContainer().removeAllViews();
            }
        }
    }

    @Override
    public int getItemCount() {
        if (data.size() < 3) {
            if (summonerData == null || summonerRankedData == null) {
                return 0;
            } else {
                return 1;
            }
        } else {
            if (moreToLoad) {
                return data.size();
            } else {
                return data.size() - 1;
            }
        }
    }

    @Override
    public int getItemViewType(int position) {
        return data.get(position).getType();
    }
}

class MatchHistoryListViewHolder extends RecyclerView.ViewHolder {
    public MatchHistoryListViewHolder(@NonNull View itemView) {
        super(itemView);
    }
}

class MatchHistoryListContentViewHolder extends MatchHistoryListViewHolder {
    private TextView placement;
    private TextView matchDate;
    private TextView matchDuration;
    private TextView status;
    private FlexboxLayout traitsContainer;
    private FlexboxLayout unitsContainer;
    private ConstraintLayout container;
    private ProgressBar loadingIndicator;

    public TextView getMatchDate() {
        return matchDate;
    }

    public TextView getMatchDuration() {
        return matchDuration;
    }

    public MatchHistoryListContentViewHolder(@NonNull View itemView) {
        super(itemView);
        this.placement = itemView.findViewById(R.id.placement);
        this.matchDate = itemView.findViewById(R.id.match_date);
        this.matchDuration = itemView.findViewById(R.id.match_duration);
        this.status = itemView.findViewById(R.id.status);
        this.unitsContainer = itemView.findViewById(R.id.units_container);
        this.traitsContainer = itemView.findViewById(R.id.traits_container);
        this.container = itemView.findViewById(R.id.container);
        this.loadingIndicator = itemView.findViewById(R.id.loading_indicator);
    }

    public ProgressBar getLoadingIndicator() { return loadingIndicator; }
    public ConstraintLayout getContainer() {
        return container;
    }
    public TextView getStatus() {
        return status;
    }
    public FlexboxLayout getTraitsContainer() {
        return traitsContainer;
    }
    public FlexboxLayout getUnitsContainer() {
        return unitsContainer;
    }
    public TextView getPlacement() {
        return placement;
    }
}

class MatchHistoryListHeaderViewHolder extends MatchHistoryListViewHolder {
    private TextView summonerName;
    private TextView summonerRank;
    private TextView winRate;
    private ImageView summonerIcon;
    private TextView queueType;
    public MatchHistoryListHeaderViewHolder(@NonNull View itemView) {
        super(itemView);
        this.summonerName = itemView.findViewById(R.id.summoner_name);
        this.summonerRank = itemView.findViewById(R.id.summoner_rank);
        this.winRate = itemView.findViewById(R.id.win_lose_winrate);
        this.queueType = itemView.findViewById(R.id.queue_type);
        this.summonerIcon = itemView.findViewById(R.id.summoner_icon);
        this.summonerIcon.setClipToOutline(true);
    }

    public TextView getQueueType() { return queueType; }

    public ImageView getSummonerIcon() {
        return summonerIcon;
    }

    public TextView getSummonerName() {
        return summonerName;
    }

    public TextView getSummonerRank() {
        return summonerRank;
    }

    public TextView getWinRate() {
        return winRate;
    }
}

class MatchHistoryListFooterViewHolder extends MatchHistoryListViewHolder {
    public MatchHistoryListFooterViewHolder(@NonNull View itemView) {
        super(itemView);
    }
}