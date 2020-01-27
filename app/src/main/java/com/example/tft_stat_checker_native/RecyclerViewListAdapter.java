package com.example.tft_stat_checker_native;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
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
import com.google.android.flexbox.FlexboxLayout;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;

public class RecyclerViewListAdapter extends RecyclerView.Adapter<RecyclerViewListViewHolder> {

    public static final int LOADING = 1;
    public static final int NONE = 0;
    public static final int LOADED = 2;
    public static final int FAILED = 3;

    private ArrayList<MatchData> data;
    private boolean moreToLoad;
    private ListHeaderData listHeaderData;
    private RequestQueue requestQueue;

    // stores all cards loading state
    private HashMap<String, Integer> loadingState;

    // stores all imageviews inflated for all unit icons in a card
    // matchID => ArrayList
    private HashMap<String, ArrayList<ImageView>> unitIcons;

    // stores all imageviews inflated for all trait icons in a card
    // matchID => ArrayList
    private HashMap<String, ArrayList<ImageView>> traitIcons;

    // on click listener
    private OnCardClickListener onCardClickListener;

    public RecyclerViewListAdapter(RequestQueue queue) {
        this.data = new ArrayList<>();
        this.data.add(new MatchData(0, new ArrayList<>(), new ArrayList<>(), MatchData.TYPE_HEADER, "00000000"));
        this.data.add(new MatchData(0, new ArrayList<>(), new ArrayList<>(), MatchData.TYPE_FOOTER, "00000000"));
        this.moreToLoad = true;
        this.loadingState = new HashMap<>();
        this.requestQueue = queue;
        this.unitIcons = new HashMap<>();
        this.traitIcons = new HashMap<>();
    }

    public void setListHeaderData(ListHeaderData listHeaderData) {
        this.listHeaderData = listHeaderData;
        notifyItemChanged(0);
    }

    public void appendItem (MatchData item) {
        // append to list data
        int insertIndex = data.size();
        data.add(insertIndex - 1, item);

        // update loading state
        loadingState.put(item.getId(), RecyclerViewListAdapter.NONE);

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
            loadingState.put(list.get(i).getId(), RecyclerViewListAdapter.NONE);
        }

        // update list
        notifyItemRangeInserted(insertIndex - 1, list.size());
    }

    public void removeAllItems () {
        for (int i = data.size() - 1; i > -1; i--) {
            if (data.get(i).getType() != MatchData.TYPE_HEADER && data.get(i).getType() != MatchData.TYPE_FOOTER) {
                data.remove(i);
            }
        }
        notifyDataSetChanged();
    }

    public void setMoreToLoad(boolean moreToLoad) {
        this.moreToLoad = moreToLoad;
    }

    public void setOnCardClickListener(OnCardClickListener onCardClickListener) {
        this.onCardClickListener = onCardClickListener;
    }

    @Override
    public RecyclerViewListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        switch (viewType) {
            case MatchData.TYPE_HEADER: {
                RecyclerViewHeaderViewHolder viewHolder = new RecyclerViewHeaderViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.match_history_list_card_header, parent, false));
                return viewHolder;
            }
            case MatchData.TYPE_FOOTER: {
                RecyclerViewFooterViewHolder viewHolder = new RecyclerViewFooterViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.match_history_list_card_footer, parent, false));
                return viewHolder;
            }
            default: {
                RecyclerViewContentViewHolder viewHolder = new RecyclerViewContentViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.match_history_list_card, parent, false));
                viewHolder.setOnCardClickListener((int position) -> {
                    if (this.onCardClickListener != null) {
                        onCardClickListener.onClick(position);
                    }
                });
                return viewHolder;
            }
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerViewListViewHolder holder, int position) {
        // update view based on view type
        if (holder instanceof RecyclerViewContentViewHolder) {

            // if current item has data render item normally
            // else fetch data and update state and notify recylcerview to update
            switch (loadingState.get(data.get(position).getId())) {
                case RecyclerViewListAdapter.LOADED: {
                    renderContent(holder, position);
                    showContent((RecyclerViewContentViewHolder) holder);
                    break;
                }
                case RecyclerViewListAdapter.NONE: {
                    loadCardData(position);
                    showLoading((RecyclerViewContentViewHolder) holder);
                    break;
                }
                case RecyclerViewListAdapter.LOADING: {
                    showLoading((RecyclerViewContentViewHolder) holder);
                    break;
                }
                case RecyclerViewListAdapter.FAILED: {
                    hideContent((RecyclerViewContentViewHolder) holder);
                    break;
                }
                default: {
                    break;
                }
            }
        } else if (holder instanceof RecyclerViewHeaderViewHolder && listHeaderData != null) {
            // render header here
            RecyclerViewHeaderViewHolder temp = (RecyclerViewHeaderViewHolder) holder;
            temp.getSummonerName().setText(listHeaderData.getSummonerNameString());
            temp.getSummonerRank().setText(listHeaderData.getSummonerRankString());
            temp.getWinRate().setText(listHeaderData.getWinLoseWinRatioString());
            temp.getSummonerIcon().setClipToOutline(true);
            Glide.with(temp.getSummonerIcon())
                    .load(Config.playerIconCDN + listHeaderData.getSummonerData().getProfileIconId() + ".png")
                    .diskCacheStrategy(DiskCacheStrategy.DATA)
                    .into(temp.getSummonerIcon());
        }
    }

    private void showLoading(RecyclerViewContentViewHolder holder) {
        holder.getStatus().setVisibility(View.VISIBLE);
        holder.getStatus().setText("LOADING");

        holder.getId().setVisibility(View.INVISIBLE);
        holder.getMatchDuration().setVisibility(View.INVISIBLE);
        holder.getMatchDate().setVisibility(View.INVISIBLE);
        holder.getIconContainer().setVisibility(View.INVISIBLE);
        holder.getPlacement().setVisibility(View.INVISIBLE);
        holder.getUnitsContainer().setVisibility(View.INVISIBLE);
        holder.getTraitsContainer().setVisibility(View.INVISIBLE);
    }
    private void showContent(RecyclerViewContentViewHolder holder) {
        holder.getStatus().setVisibility(View.GONE);
        holder.getStatus().setText("");

        holder.getId().setVisibility(View.VISIBLE);
        holder.getMatchDuration().setVisibility(View.VISIBLE);
        holder.getMatchDate().setVisibility(View.VISIBLE);
        holder.getIconContainer().setVisibility(View.VISIBLE);
        holder.getPlacement().setVisibility(View.VISIBLE);
        holder.getUnitsContainer().setVisibility(View.VISIBLE);
        holder.getTraitsContainer().setVisibility(View.VISIBLE);
    }

    private void hideContent(RecyclerViewContentViewHolder holder) {
        holder.getStatus().setVisibility(View.VISIBLE);
        holder.getStatus().setText("FAILED");

        holder.getId().setVisibility(View.INVISIBLE);
        holder.getMatchDuration().setVisibility(View.INVISIBLE);
        holder.getMatchDate().setVisibility(View.INVISIBLE);
        holder.getIconContainer().setVisibility(View.INVISIBLE);
        holder.getPlacement().setVisibility(View.INVISIBLE);
        holder.getUnitsContainer().setVisibility(View.INVISIBLE);
        holder.getTraitsContainer().setVisibility(View.INVISIBLE);
    }

    private void renderContent(@NonNull RecyclerViewListViewHolder holder, int position) {
        RecyclerViewContentViewHolder temp = (RecyclerViewContentViewHolder) holder;

        temp.getPlacement().setText(data.get(position).getPlacement() + "");
        if (data.get(position).getPlacement() > 4) {
            temp.getPlacement().setBackgroundColor(ContextCompat.getColor(temp.getPlacement().getContext(), R.color.placementLost));
        } else {
            temp.getPlacement().setBackgroundColor(ContextCompat.getColor(temp.getPlacement().getContext(), R.color.placementWin));
        }

        temp.getMatchDate().setText(data.get(position).getGameDateTime() + "");
        temp.getMatchDuration().setText(data.get(position).getGameLength() + "");

        temp.getId().setText(data.get(position).getId());

        // reuse stored imageviews if possible
        if (unitIcons.containsKey(data.get(position).getId())) {
            ArrayList<ImageView> icons = unitIcons.get(data.get(position).getId());
            for (int i = 0; i < icons.size(); i++) {
                if (icons.get(i).getParent() != null) {
                    ((FlexboxLayout) icons.get(i).getParent()).removeView(icons.get(i));
                }
                temp.getUnitsContainer().addView(icons.get(i));
            }
        } else {
            ArrayList<ImageView> icons = new ArrayList<>();
            for (int i = 0; i < data.get(position).getUnits().size(); i++) {

                // get drawable name from data
                String drawableName = data.get(position).getUnits().get(i).getCharacterID().toLowerCase();

                // inflate the imageview
                ImageView unitIcon = (ImageView) LayoutInflater
                        .from(temp.getUnitsContainer().getContext())
                        .inflate(
                                R.layout.match_history_list_card_unit_icon,
                                ((RecyclerViewContentViewHolder) holder).getUnitsContainer(),
                                false
                        );

                // get resource id
                Context ctx = unitIcon.getContext();
                int id = ctx.getResources().getIdentifier(drawableName, "drawable", ctx.getPackageName());

                unitIcon.setImageResource(id);
                unitIcon.setClipToOutline(true);

                // add border
                switch (data.get(position).getUnits().get(i).getRarity()) {
                    case 0: { unitIcon.setBackgroundResource(R.drawable.unit_border_rarity0); break; }
                    case 1: { unitIcon.setBackgroundResource(R.drawable.unit_border_rarity1); break; }
                    case 2: { unitIcon.setBackgroundResource(R.drawable.unit_border_rarity2); break; }
                    case 3: { unitIcon.setBackgroundResource(R.drawable.unit_border_rarity3); break; }
                    case 4: { unitIcon.setBackgroundResource(R.drawable.unit_border_rarity4); break; }
                    case 5: { unitIcon.setBackgroundResource(R.drawable.unit_border_rarity5); break; }
                }

                // add tier icons
                switch (data.get(position).getUnits().get(i).getTier()) {
                    case 1: { unitIcon.setForeground(ContextCompat.getDrawable(ctx, R.drawable.unit_tier1)); break; }
                    case 2: { unitIcon.setForeground(ContextCompat.getDrawable(ctx, R.drawable.unit_tier2)); break; }
                    case 3: { unitIcon.setForeground(ContextCompat.getDrawable(ctx, R.drawable.unit_tier3)); break; }
                }

                icons.add(unitIcon);
                temp.getUnitsContainer().addView(unitIcon);
            }
            unitIcons.put(data.get(position).getId(), icons);
        }

        // reuse stored imageviews if possible
        if (traitIcons.containsKey(data.get(position).getId())) {
            ArrayList<ImageView> icons = traitIcons.get(data.get(position).getId());
            for (int i = 0; i < icons.size(); i++) {
                if (icons.get(i).getParent() != null) {
                    ((FlexboxLayout) icons.get(i).getParent()).removeView(icons.get(i));
                }
                temp.getTraitsContainer().addView(icons.get(i));
            }
        } else {
            ArrayList<ImageView> icons = new ArrayList<>();
            for (int i = 0; i < data.get(position).getTraits().size(); i++) {
                if (data.get(position).getTraits().get(i).getStyle() != 0) {

                    // get drawable name from data
                    String drawableName = data.get(position).getTraits().get(i).getName().toLowerCase();

                    // inflate the imageview
                    ImageView traitIcon = (ImageView) LayoutInflater
                            .from(temp.getTraitsContainer().getContext())
                            .inflate(
                                    R.layout.match_history_list_card_trait_icon,
                                    ((RecyclerViewContentViewHolder) holder).getTraitsContainer(),
                                    false
                            );

                    // get resource id
                    Context ctx = traitIcon.getContext();
                    int id = ctx.getResources().getIdentifier(drawableName, "drawable", ctx.getPackageName());

                    traitIcon.setImageResource(id);

                    // add background
                    switch(data.get(position).getTraits().get(i).getStyle()) {
                        case 1: { traitIcon.setBackgroundResource(R.drawable.trait_bg_tier1); break; }
                        case 2: { traitIcon.setBackgroundResource(R.drawable.trait_bg_tier2); break; }
                        case 3: { traitIcon.setBackgroundResource(R.drawable.trait_bg_tier3); break; }
                    }

                    icons.add(traitIcon);
                    temp.getTraitsContainer().addView(traitIcon);
                }
            }
            traitIcons.put(data.get(position).getId(), icons);
        }
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
                "NA",
                (String res) -> {
                    Log.d("getMatchResultByMatchID", res);
                    try {
                        // load data into item
                        JSONObject jsonRes = new JSONObject(res);
                        data.get(position).loadData(jsonRes, listHeaderData.getSummonerData().getPuuid());

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
    public void onViewRecycled(@NonNull RecyclerViewListViewHolder holder) {
        if (holder instanceof RecyclerViewContentViewHolder) {
            if (((RecyclerViewContentViewHolder) holder).getTraitsContainer() != null) {
                ((RecyclerViewContentViewHolder) holder).getTraitsContainer().removeAllViews();
            }
            if (((RecyclerViewContentViewHolder) holder).getUnitsContainer() != null) {
                ((RecyclerViewContentViewHolder) holder).getUnitsContainer().removeAllViews();
            }
        }
    }

    @Override
    public int getItemCount() {
        if (data.size() < 3) {
            if (listHeaderData == null) {
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

class RecyclerViewListViewHolder extends RecyclerView.ViewHolder {
    public RecyclerViewListViewHolder(@NonNull View itemView) {
        super(itemView);
    }
}

class RecyclerViewContentViewHolder extends RecyclerViewListViewHolder implements View.OnClickListener{
    private TextView placement;
    private TextView id;
    private TextView matchDate;
    private TextView matchDuration;
    private TextView status;
    private LinearLayout iconContainer;
    private FlexboxLayout unitsContainer;
    private FlexboxLayout traitsContainer;
    private ConstraintLayout container;
    private OnCardClickListener onCardClickListener;

    public TextView getMatchDate() {
        return matchDate;
    }

    public TextView getMatchDuration() {
        return matchDuration;
    }

    public RecyclerViewContentViewHolder(@NonNull View itemView) {
        super(itemView);
        this.placement = itemView.findViewById(R.id.placement);
        this.matchDate = itemView.findViewById(R.id.match_date);
        this.matchDuration = itemView.findViewById(R.id.match_duration);
        this.status = itemView.findViewById(R.id.status);
        this.unitsContainer = itemView.findViewById(R.id.units_container);
        this.traitsContainer = itemView.findViewById(R.id.traits_container);
        this.iconContainer = itemView.findViewById(R.id.icon_container);
        this.id = itemView.findViewById(R.id.id);
        this.container = itemView.findViewById(R.id.container);
        this.onCardClickListener = onCardClickListener;
        itemView.setOnClickListener(this);
    }

    public void setOnCardClickListener(OnCardClickListener onCardClickListener) {
        this.onCardClickListener = onCardClickListener;
    }

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

    public TextView getId() {
        return id;
    }

    public LinearLayout getIconContainer() {
        return iconContainer;
    }

    @Override
    public void onClick(View view) {
        if (this.onCardClickListener != null) {
            onCardClickListener.onClick(getAdapterPosition());
        }
    }
}

class RecyclerViewHeaderViewHolder extends RecyclerViewListViewHolder {
    private TextView summonerName;
    private TextView summonerRank;
    private TextView winRate;
    private ImageView summonerIcon;
    public RecyclerViewHeaderViewHolder(@NonNull View itemView) {
        super(itemView);
        this.summonerName = itemView.findViewById(R.id.summoner_name);
        this.summonerRank = itemView.findViewById(R.id.summoner_rank);
        this.winRate = itemView.findViewById(R.id.win_lose_winrate);
        this.summonerIcon = itemView.findViewById(R.id.summoner_icon);
    }

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

class RecyclerViewFooterViewHolder extends RecyclerViewListViewHolder {
    public RecyclerViewFooterViewHolder(@NonNull View itemView) {
        super(itemView);
    }
}

class ListHeaderData {
    private SummonerData summonerData;
    private SummonerRankedData summonerRankedData;

    public SummonerData getSummonerData() {
        return summonerData;
    }

    public SummonerRankedData getSummonerRankedData() {
        return summonerRankedData;
    }

    public ListHeaderData(SummonerData summonerData, SummonerRankedData summonerRankedData) {
        this.summonerData = summonerData;
        this.summonerRankedData = summonerRankedData;
    }

    public String getSummonerNameString() {
        return summonerData.getName();
    }

    public String getSummonerRankString() {
        return summonerRankedData.getTier() + " " + summonerRankedData.getRank();
    }

    public String getWinLoseWinRatioString() {
        float winRatio = 100f * summonerRankedData.getWins() * 1f / ( summonerRankedData.getWins() * 1f + summonerRankedData.getLosses() * 1f );
        DecimalFormat format = new DecimalFormat(".00");
        return summonerRankedData.getWins() + "W  " + summonerRankedData.getLosses() + "L  Win Ratio: " + format.format(winRatio) + "%";
    }
}

interface OnCardClickListener {
    void onClick(int position);
}
