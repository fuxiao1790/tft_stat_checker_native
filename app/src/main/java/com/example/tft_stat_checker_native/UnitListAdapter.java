package com.example.tft_stat_checker_native;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class UnitListAdapter extends RecyclerView.Adapter<UnitViewHolder> {
    private ArrayList<Unit> listData;
    private LayoutInflater itemInflater;

    private Drawable unitBorderRarity0;
    private Drawable unitBorderRarity1;
    private Drawable unitBorderRarity2;
    private Drawable unitBorderRarity3;
    private Drawable unitBorderRarity4;
    private Drawable unitBorderRarity5;

    private Drawable unitTier1;
    private Drawable unitTier2;
    private Drawable unitTier3;

    public UnitListAdapter(Context ctx, ArrayList<Unit> listData) {
        this.listData = listData;
        this.itemInflater = LayoutInflater.from(ctx);

        this.unitBorderRarity0 = ContextCompat.getDrawable(ctx, R.drawable.unit_border_rarity0);
        this.unitBorderRarity1 = ContextCompat.getDrawable(ctx, R.drawable.unit_border_rarity1);
        this.unitBorderRarity2 = ContextCompat.getDrawable(ctx, R.drawable.unit_border_rarity2);
        this.unitBorderRarity3 = ContextCompat.getDrawable(ctx, R.drawable.unit_border_rarity3);
        this.unitBorderRarity4 = ContextCompat.getDrawable(ctx, R.drawable.unit_border_rarity4);
        this.unitBorderRarity5 = ContextCompat.getDrawable(ctx, R.drawable.unit_border_rarity5);

        this.unitTier1 = ContextCompat.getDrawable(ctx, R.drawable.unit_tier1);
        this.unitTier2 = ContextCompat.getDrawable(ctx, R.drawable.unit_tier2);
        this.unitTier3 = ContextCompat.getDrawable(ctx, R.drawable.unit_tier3);
    }

    @NonNull
    @Override
    public UnitViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new UnitViewHolder(itemInflater.inflate(R.layout.participant_list_unit_list_item, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull UnitViewHolder holder, int position) {
        // get drawable name from data
        String drawableName = listData.get(position).getCharacterID().toLowerCase();

        // inflate the imageview
        ImageView unitIcon = holder.getUnitIcon();

        // get resource id
        Context ctx = unitIcon.getContext();
        int id = ctx.getResources().getIdentifier(drawableName, "drawable", ctx.getPackageName());

        unitIcon.setImageResource(id);
        unitIcon.setClipToOutline(true);

        // background resource = corner
        // foreground resource = bourder + dots

        unitIcon.setBackgroundResource(R.drawable.unit_corner);

        Drawable border = this.unitBorderRarity0;
        // add border
        switch (listData.get(position).getRarity()) {
            case 0: { border = this.unitBorderRarity0; break; }
            case 1: { border = this.unitBorderRarity1; break; }
            case 2: { border = this.unitBorderRarity2; break; }
            case 3: { border = this.unitBorderRarity3; break; }
            case 4: { border = this.unitBorderRarity4; break; }
            case 5: { border = this.unitBorderRarity5; break; }
        }

        Drawable tier = this.unitTier1;
        // add tier icons
        switch (listData.get(position).getTier()) {
            case 1: { tier = this.unitTier1; break; }
            case 2: { tier = this.unitTier2; break; }
            case 3: { tier = this.unitTier3; break; }
        }

        LayerDrawable combined = new LayerDrawable(new Drawable[]{border, tier});

        unitIcon.setForeground(combined);

        for (int i = listData.get(position).getItems().size(); i < holder.getItemIcons().size(); i++) {
            holder.getItemIcons().get(i).setVisibility(View.INVISIBLE);
        }

        for (int i = 0; i < listData.get(position).getItems().size(); i++) {
            int itemID = listData.get(position).getItems().get(i).getId();
            int drawableID = ctx.getResources().getIdentifier("item_" + itemID, "drawable", ctx.getPackageName());
            holder.getItemIcons().get(i).setImageResource(drawableID);
        }
    }

    @Override
    public int getItemCount() {
        return listData.size();
    }
}

class UnitViewHolder extends RecyclerView.ViewHolder {
    private ImageView unitIcon;
    private ArrayList<ImageView> itemIcons;
    public UnitViewHolder(@NonNull View itemView) {
        super(itemView);
        this.unitIcon = itemView.findViewById(R.id.unit_icon);
        this.itemIcons = new ArrayList<>();

        ImageView icon1 = itemView.findViewById(R.id.item_1);
        icon1.setClipToOutline(true);
        itemIcons.add(icon1);

        ImageView icon2 = itemView.findViewById(R.id.item_2);
        icon2.setClipToOutline(true);
        itemIcons.add(icon2);

        ImageView icon3 = itemView.findViewById(R.id.item_3);
        icon3.setClipToOutline(true);
        itemIcons.add(icon3);
    }

    public ImageView getUnitIcon() {
        return unitIcon;
    }

    public ArrayList<ImageView> getItemIcons() {
        return itemIcons;
    }
}
