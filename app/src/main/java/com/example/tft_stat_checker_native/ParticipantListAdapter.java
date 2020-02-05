package com.example.tft_stat_checker_native;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

public class ParticipantListAdapter extends RecyclerView.Adapter<ParticipantsViewHolder> {

    public final int LOADING = 1;
    public final int NONE = 0;
    public final int FAILED = 2;
    public final int LOADED = 3;

    private ArrayList<ParticipantData> listData;
    private ArrayList<UnitListAdapter> unitsRecyclerViewAdapters;
    private ArrayList<TraitListAdapter> traitsRecyclerViewAdapters;

    private HashMap<String, Integer> loadingStatus;
    private RequestQueue requestQueue;

    private String platform;

    public ParticipantListAdapter(@NonNull ArrayList<ParticipantData> listData, RequestQueue requestQueue, Context ctx) {
        this.listData = listData;
        this.requestQueue = requestQueue;

        this.unitsRecyclerViewAdapters = new ArrayList<>();
        this.traitsRecyclerViewAdapters = new ArrayList<>();
        this.loadingStatus = new HashMap<>();
        listData.forEach((participantData) -> {
            // construct child list adapters
            unitsRecyclerViewAdapters.add(new UnitListAdapter(ctx, participantData.getUnits()));
            traitsRecyclerViewAdapters.add(new TraitListAdapter(ctx, filterTraits(participantData.getTraits())));
            // construct loading status for summoner data / summoner ranked data
            loadingStatus.put(participantData.getPuuid(), NONE);
        });

    }

    public void setPlatform(String platform) {
        this.platform = platform;
    }

    private ArrayList<Trait> filterTraits(ArrayList<Trait> list) {
        ArrayList<Trait> re = new ArrayList<>();
        list.forEach((trait) -> {
            if (trait.getStyle() != 0) {
                re.add(trait);
            }
        });
        return re;
    }

    @NonNull
    @Override
    public ParticipantsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.participant_list_card, parent, false);
        ParticipantsViewHolder holder = new ParticipantsViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ParticipantsViewHolder holder, int position) {
        switch(this.loadingStatus.get(listData.get(position).getPuuid())) {
            case NONE: {
                showLoading(holder);
                loadingStatus.put(listData.get(position).getPuuid(), LOADING);
                loadData(position);
                break;
            }
            case LOADED: {
                showContent(holder);
                renderAdditionalContent(holder, position);
                break;
            }
            case LOADING: { showLoading(holder); break; }
            case FAILED: { showFailed(holder); break; }
        }
        renderContent(holder, position);
    }

    private void loadData(int position) {
        String puuid = listData.get(position).getPuuid();
        StringRequest req = API.getSummonerByPUUID(
                puuid,
                this.platform,
                (String res) -> {
                    try {
                        JSONObject json = new JSONObject(res);
                        SummonerData summonerData = new SummonerData(json);
                        getSummonerRankedData(summonerData, position);
                    } catch (JSONException error) {
                        Log.d("getSummonerByName", "Bad Response(Malformed JSON response): " + error.toString());
                        loadingStatus.put(puuid, FAILED);
                        notifyItemChanged(position);
                    }
                },
                (VolleyError error) -> {
                    API.onError(error);
                    loadingStatus.put(puuid, FAILED);
                    notifyItemChanged(position);
                }
        );

        requestQueue.add(req);
    }

    private void getSummonerRankedData(SummonerData summonerData, int position) {
        StringRequest req = API.getSummonerRankedData(
                summonerData.getId(),
                this.platform,
                (String res) -> {
                    try {
                        JSONArray obj = new JSONArray(res);
                        // create data obj
                        JSONObject jsonRes = obj.getJSONObject(0);
                        SummonerRankedData summonerRankedData = new SummonerRankedData(jsonRes);

                        // update data
                        listData.get(position).loadSummonerData(summonerData, summonerRankedData);

                        // update UI
                        loadingStatus.put(summonerData.getPuuid(), LOADED);
                        notifyItemChanged(position);

                    } catch(org.json.JSONException e) {
                        Log.d("getSummonerRankedData", "Bad Response(Malformed JSON response): " + e.toString());
                        loadingStatus.put(summonerData.getPuuid(), FAILED);
                        notifyItemChanged(position);
                    }
                },
                (VolleyError error) -> {
                    API.onError(error);
                    loadingStatus.put(summonerData.getPuuid(), FAILED);
                    notifyItemChanged(position);
                }
        );

        requestQueue.add(req);
    }

    private void showLoading(ParticipantsViewHolder holder) {
        holder.getLoadingIndicator().setVisibility(View.VISIBLE);

        holder.getSummonerIcon().setVisibility(View.INVISIBLE);
        holder.getSummonerName().setVisibility(View.INVISIBLE);
        holder.getSummonerRank().setVisibility(View.INVISIBLE);
        holder.getWinLoseWinRate().setVisibility(View.INVISIBLE);
    }

    private void showContent(ParticipantsViewHolder holder) {
        holder.getLoadingIndicator().setVisibility(View.INVISIBLE);

        holder.getSummonerIcon().setVisibility(View.VISIBLE);
        holder.getSummonerName().setVisibility(View.VISIBLE);
        holder.getSummonerRank().setVisibility(View.VISIBLE);
        holder.getWinLoseWinRate().setVisibility(View.VISIBLE);
    }

    private void showFailed(ParticipantsViewHolder holder) {
        holder.getLoadingIndicator().setVisibility(View.INVISIBLE);

        holder.getSummonerIcon().setVisibility(View.INVISIBLE);
        holder.getSummonerName().setVisibility(View.INVISIBLE);
        holder.getSummonerRank().setVisibility(View.INVISIBLE);
        holder.getWinLoseWinRate().setVisibility(View.INVISIBLE);
    }

    private void renderAdditionalContent(ParticipantsViewHolder holder, int position) {
        holder.getSummonerName().setText(listData.get(position).getSummonerData().getName());
        holder.getSummonerRank().setText(listData.get(position).getSummonerRankedData().getSummonerRankString());
        holder.getWinLoseWinRate().setText(listData.get(position).getSummonerRankedData().getWinLoseWinRatioString());
        Glide.with(holder.getSummonerIcon())
                .load(Config.playerIconCDN + listData.get(position).getSummonerData().getProfileIconId() + ".png")
                .diskCacheStrategy(DiskCacheStrategy.DATA)
                .into(holder.getSummonerIcon());
    }

    private void renderContent(ParticipantsViewHolder holder, int position) {
        holder.getPlacement().setText(listData.get(position).getPlacement() + "");
        holder.getUnitList().swapAdapter(unitsRecyclerViewAdapters.get(position), true);
        holder.getTraitList().swapAdapter(traitsRecyclerViewAdapters.get(position), true);
    }

    @Override public int getItemCount() { return listData.size();  }
}

class ParticipantsViewHolder extends RecyclerView.ViewHolder {
    private TextView summonerName;
    private TextView summonerRank;
    private TextView placement;
    private TextView winLoseWinRate;
    private ImageView summonerIcon;
    private ProgressBar loadingIndicator;
    private RecyclerView unitList;
    private RecyclerView traitList;

    public ParticipantsViewHolder(@NonNull View itemView) {
        super(itemView);
        this.summonerName = itemView.findViewById(R.id.summoner_name);
        this.summonerRank = itemView.findViewById(R.id.summoner_rank);
        this.placement = itemView.findViewById(R.id.placement);
        this.summonerIcon = itemView.findViewById(R.id.summoner_icon);
        this.summonerIcon.setClipToOutline(true);
        this.winLoseWinRate = itemView.findViewById(R.id.win_lose_winrate);
        this.loadingIndicator = itemView.findViewById(R.id.loading_indicator);

        // set up recycler views
        // list adapters will be added in onBindViewHolder
        this.unitList = itemView.findViewById(R.id.unit_list);
        unitList.setLayoutManager(new LinearLayoutManager(itemView.getContext(), LinearLayoutManager.HORIZONTAL, false));
        unitList.hasFixedSize();

        this.traitList = itemView.findViewById(R.id.trait_list);
        traitList.setLayoutManager(new LinearLayoutManager(itemView.getContext(), LinearLayoutManager.HORIZONTAL, false));
        traitList.hasFixedSize();
    }

    public RecyclerView getTraitList() { return traitList; }
    public RecyclerView getUnitList() { return unitList; }
    public ProgressBar getLoadingIndicator() { return loadingIndicator; }
    public TextView getWinLoseWinRate() { return winLoseWinRate; }
    public ImageView getSummonerIcon() { return summonerIcon; }
    public TextView getSummonerName() { return summonerName; }
    public TextView getPlacement() { return placement; }
    public TextView getSummonerRank() { return summonerRank; }
}
