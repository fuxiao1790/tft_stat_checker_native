package com.example.tft_stat_checker_native;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tft_stat_checker_native.Modal.TraitData;

import java.util.ArrayList;

public class TraitListAdapter extends RecyclerView.Adapter<TraitListViewHolder> {

    private ArrayList<TraitData> listData;
    private LayoutInflater itemInflater;

    int traitBGTier1 = R.drawable.trait_bg_tier1;
    int traitBGTier2 = R.drawable.trait_bg_tier2;
    int traitBGTier3 = R.drawable.trait_bg_tier3;

    public TraitListAdapter(Context ctx, ArrayList<TraitData> listData) {
        this.listData = listData;
        this.itemInflater = LayoutInflater.from(ctx);
    }

    @NonNull
    @Override
    public TraitListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new TraitListViewHolder(itemInflater.inflate(R.layout.participant_list_trait_list_item, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull TraitListViewHolder holder, int position) {
        // get drawable name from data
        String drawableName = listData.get(position).getName().toLowerCase();

        // inflate the imageview
        ImageView traitIcon = holder.getIcon();

        // get resource id
        Context ctx = traitIcon.getContext();
        int id = ctx.getResources().getIdentifier(drawableName, "drawable", ctx.getPackageName());

        traitIcon.setImageResource(id);

        // add background
        switch(listData.get(position).getStyle()) {
            case 1: { traitIcon.setBackgroundResource(this.traitBGTier1); break; }
            case 2: { traitIcon.setBackgroundResource(this.traitBGTier2); break; }
            case 3: { traitIcon.setBackgroundResource(this.traitBGTier3); break; }
            case 4: { traitIcon.setBackgroundResource(this.traitBGTier3); break; }
        }
    }

    @Override
    public int getItemCount() {
        return listData.size();
    }
}

class TraitListViewHolder extends RecyclerView.ViewHolder {
    private ImageView icon;
    public TraitListViewHolder(@NonNull View itemView) {
        super(itemView);
        this.icon = itemView.findViewById(R.id.trait_icon);
    }

    public ImageView getIcon() {
        return icon;
    }
}
