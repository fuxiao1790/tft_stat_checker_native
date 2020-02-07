    package com.example.tft_stat_checker_native.View;

    import android.app.AlertDialog;
    import android.app.Dialog;
    import android.content.Context;
    import android.os.Bundle;
    import android.view.LayoutInflater;

    import androidx.annotation.NonNull;
    import androidx.annotation.Nullable;
    import androidx.fragment.app.DialogFragment;
    import androidx.recyclerview.widget.LinearLayoutManager;
    import androidx.recyclerview.widget.RecyclerView;

    import android.view.View;
    import android.view.ViewGroup;
    import android.widget.Button;
    import android.widget.LinearLayout;
    import android.widget.TextView;

    import com.example.tft_stat_checker_native.Controller.Config;
    import com.example.tft_stat_checker_native.Controller.ListItemOnClickListener;
    import com.example.tft_stat_checker_native.Controller.OnDialogConfirmListener;
    import com.example.tft_stat_checker_native.Modal.API;
    import com.example.tft_stat_checker_native.R;

    import java.util.ArrayList;

    public class ChangeRegionDialog extends DialogFragment {
        private OnDialogConfirmListener onDialogConfirmListener;
        private String selectedRegion = "";

        public void setDefaultHighlightedItem(String selectedRegion) {
            this.selectedRegion= selectedRegion;
        }

        public void setOnDialogConfirmListener(OnDialogConfirmListener listener) {
            this.onDialogConfirmListener = listener;
        }

        @NonNull
        @Override
        public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

            LayoutInflater inflater = requireActivity().getLayoutInflater();
            View view = inflater.inflate(R.layout.change_region_dialog, null);
            builder.setView(view);

            // setup ok button
            Button okButton = view.findViewById(R.id.ok_button);
            okButton.setOnClickListener((target) -> {
                if (this.onDialogConfirmListener != null) {
                    this.onDialogConfirmListener.onConfirm(this.selectedRegion);
                }
                dismiss();
            });

            // setup cancel button
            Button cancelButton = view.findViewById(R.id.cancel_button);
            cancelButton.setOnClickListener((target) -> dismiss());

            // setup list
            RecyclerView regionList = view.findViewById(R.id.region_list);
            regionList.hasFixedSize();
            regionList.setLayoutManager(new LinearLayoutManager(getContext()));


            // create list adapter
            ArrayList<String> regions = new ArrayList<>();
            API.getPlatforms().forEach((platform) -> regions.add(platform));
            RegionListAdapter adapter = new RegionListAdapter(regions, getContext());
            regionList.setAdapter(adapter);

            // setup on click listener
            adapter.setListItemOnClickListener((index) -> {
                this.selectedRegion = regions.get(index);
                adapter.setHighLightedItem(index);
            });

            // inital highlighted item
            adapter.setHighLightedItem(this.selectedRegion);

            return builder.create();
        }
    }

    class RegionListAdapter extends RecyclerView.Adapter<RegionListViewHolder> {
        private ArrayList<String> regions;
        private LayoutInflater inflater;
        private ListItemOnClickListener listItemOnClickListener;
        private int highLightedItem = 0;

        public void setHighLightedItem(int index) {
            int lastItem = this.highLightedItem;
            this.highLightedItem = index;

            this.notifyItemChanged(lastItem);
            this.notifyItemChanged(index);
        }

        public void setHighLightedItem(String region) {
            int lastItem = this.highLightedItem;
            int currentItem = regions.indexOf(region);
            this.highLightedItem = currentItem;

            this.notifyItemChanged(lastItem);
            this.notifyItemChanged(currentItem);
        }

        public RegionListAdapter(ArrayList<String> regions, Context ctx) {
            this.regions = regions;
            this.inflater = LayoutInflater.from(ctx);
        }

        public void setListItemOnClickListener(ListItemOnClickListener listItemOnClickListener) {
            this.listItemOnClickListener = listItemOnClickListener;
        }

        @NonNull
        @Override
        public RegionListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            RegionListViewHolder holder = new RegionListViewHolder(inflater.inflate(R.layout.region_list_item, parent, false));
            holder.setListItemOnClickListener(this.listItemOnClickListener);
            return holder;
        }

        @Override
        public void onBindViewHolder(@NonNull RegionListViewHolder holder, int position) {
            holder.getRegionText().setText(regions.get(position));
            if (position == this.highLightedItem) {
                holder.getContainer().setBackgroundResource(R.drawable.click_ripple_bg_light);
            } else {
                holder.getContainer().setBackgroundResource(R.drawable.click_ripple_bg);
            }
        }

        @Override
        public int getItemCount() {
            return regions.size();
        }
    }

    class RegionListViewHolder extends RecyclerView.ViewHolder {
        private TextView regionText;
        private LinearLayout container;
        private ListItemOnClickListener listItemOnClickListener;

        public RegionListViewHolder(@NonNull View itemView) {
            super(itemView);
            itemView.setOnClickListener((view) -> {
                if (listItemOnClickListener != null) {
                    listItemOnClickListener.onItemClicked(getLayoutPosition());
                }
            });
            this.regionText = itemView.findViewById(R.id.region_text);
            this.container = itemView.findViewById(R.id.region_list_item_container);
        }

        public LinearLayout getContainer() {
            return container;
        }

        public void setListItemOnClickListener(ListItemOnClickListener listItemOnClickListener) {
            this.listItemOnClickListener = listItemOnClickListener;
        }

        public TextView getRegionText() {
            return regionText;
        }
    }