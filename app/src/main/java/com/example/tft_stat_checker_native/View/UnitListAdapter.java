package com.example.tft_stat_checker_native.View;

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

import com.example.tft_stat_checker_native.Modal.UnitData;
import com.example.tft_stat_checker_native.R;
import com.google.android.flexbox.FlexboxLayout;

import java.util.ArrayList;

public class UnitListAdapter extends RecyclerView.Adapter<UnitViewHolder> {

    private ArrayList<UnitData> listData;
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

    public UnitListAdapter(Context ctx, ArrayList<UnitData> listData) {
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

        ImageView unitIcon = holder.getUnitIcon();

        // get resource id
        Context ctx = unitIcon.getContext();
        int id = ctx.getResources().getIdentifier(drawableName, "drawable", ctx.getPackageName());
        unitIcon.setImageResource(id);

        // background resource = corner
        // foreground resource = border + tier
        unitIcon.setBackgroundResource(R.drawable.unit_corner);

        // add border
        Drawable border = this.unitBorderRarity0;
        switch (listData.get(position).getRarity()) {
            case 0: { border = this.unitBorderRarity0; break; }
            case 1: { border = this.unitBorderRarity1; break; }
            case 2: { border = this.unitBorderRarity2; break; }
            case 3: { border = this.unitBorderRarity3; break; }
            case 4: { border = this.unitBorderRarity4; break; }
            case 5: { border = this.unitBorderRarity5; break; }
        }

        // add tier icons
        Drawable tier = this.unitTier1;
        switch (listData.get(position).getTier()) {
            case 1: { tier = this.unitTier1; break; }
            case 2: { tier = this.unitTier2; break; }
            case 3: { tier = this.unitTier3; break; }
        }
        LayerDrawable combined = new LayerDrawable(new Drawable[]{border, tier});
        unitIcon.setForeground(combined);

        listData.get(position).getItems().forEach((item) -> {
            FlexboxLayout container = holder.getItemIconContainer();
            ImageView itemIcon = (ImageView) itemInflater.inflate(R.layout.unit_list_unit_item, container, false);

            int itemID = item.getId();
            int drawableID = ctx.getResources().getIdentifier("item_" + itemID, "drawable", ctx.getPackageName());
            itemIcon.setImageResource(drawableID);
            itemIcon.setClipToOutline(true);

            container.addView(itemIcon);
        });
    }

    @Override
    public void onViewRecycled(@NonNull UnitViewHolder holder) {
        if (holder != null) {
            holder.getItemIconContainer().removeAllViews();
        }
    }

    @Override
    public int getItemCount() {
        return listData.size();
    }
}

class UnitViewHolder extends RecyclerView.ViewHolder {

    private ImageView unitIcon;
    private FlexboxLayout itemIconContainer;

    public UnitViewHolder(@NonNull View itemView) {
        super(itemView);
        this.unitIcon = itemView.findViewById(R.id.unit_icon);
        this.itemIconContainer = itemView.findViewById(R.id.item_icon_container);
        this.unitIcon.setClipToOutline(true);
    }

    public ImageView getUnitIcon() {
        return unitIcon;
    }

    public FlexboxLayout getItemIconContainer() {
        return itemIconContainer;
    }
}
