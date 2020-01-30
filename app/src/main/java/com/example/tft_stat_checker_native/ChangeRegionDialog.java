package com.example.tft_stat_checker_native;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyboardShortcutGroup;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class ChangeRegionDialog extends Dialog {

    private ArrayList<String> platforms;
    private String selectedPlatform;

    public ChangeRegionDialog(@NonNull Context context, String selectedPlatform) {
        super(context);
        Config.init();
        this.platforms = new ArrayList<>();
        Config.getPlatforms().forEach((String key, String value) -> {
            this.platforms.add(key);
        });
        this.selectedPlatform = selectedPlatform;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.change_region_dialog);

        iniRecyclerView();
    }

    private void iniRecyclerView() {
        RecyclerView platformList = findViewById(R.id.region_list);
        PlatFormListAdapter adapter = new PlatFormListAdapter(getContext(), this.platforms);
        adapter.setOnRegionSelectListener((int index) -> selectedPlatform = platforms.get(index));
        platformList.setLayoutManager(new LinearLayoutManager(getContext()));
        platformList.setAdapter(adapter);
        platformList.hasFixedSize();
    }

    public String getSelectedPlatform() { return selectedPlatform; }

    @Override public void onProvideKeyboardShortcuts(List<KeyboardShortcutGroup> data, @Nullable Menu menu, int deviceId) { }
    @Override public void onPointerCaptureChanged(boolean hasCapture) { }
}

class PlatFormListAdapter extends RecyclerView.Adapter<PlatformViewHolder> {
    private ArrayList<String> platformList;
    private LayoutInflater layoutInflater;
    private OnRegionSelectListener onRegionSelectListener;

    public PlatFormListAdapter(Context ctx, ArrayList<String> platformList) {
        this.layoutInflater = LayoutInflater.from(ctx);
        this.platformList = platformList;
    }

    public void setOnRegionSelectListener(OnRegionSelectListener onRegionSelectListener) {
        this.onRegionSelectListener = onRegionSelectListener;
    }

    @NonNull
    @Override
    public PlatformViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new PlatformViewHolder(layoutInflater.inflate(R.layout.region_list_item, parent, false), onRegionSelectListener);
    }

    @Override
    public void onBindViewHolder(@NonNull PlatformViewHolder holder, int position) {
        holder.getRegion().setText(platformList.get(position));
    }

    @Override
    public int getItemCount() {
        return platformList.size();
    }
}

class PlatformViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
    private TextView region;
    private OnRegionSelectListener onRegionSelectListener;

    public PlatformViewHolder(@NonNull View itemView, OnRegionSelectListener listener) {
        super(itemView);
        this.region = itemView.findViewById(R.id.region_text);
        this.onRegionSelectListener = listener;
    }

    public void setOnRegionSelectListener(OnRegionSelectListener onRegionSelectListener) { this.onRegionSelectListener = onRegionSelectListener; }

    public TextView getRegion() { return region; }

    @Override
    public void onClick(View view) {
        Log.d("on click ?", "hello ??");
        onRegionSelectListener.onSelect(getAdapterPosition());
    }
}

interface OnRegionSelectListener {
    void onSelect(int index);
}