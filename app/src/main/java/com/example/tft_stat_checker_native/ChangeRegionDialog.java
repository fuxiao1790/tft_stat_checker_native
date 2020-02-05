    package com.example.tft_stat_checker_native;

    import android.app.AlertDialog;
    import android.app.Dialog;
    import android.content.Context;
    import android.os.Bundle;
    import android.util.Log;
    import android.view.LayoutInflater;

    import androidx.annotation.NonNull;
    import androidx.annotation.Nullable;
    import androidx.fragment.app.DialogFragment;
    import androidx.recyclerview.widget.LinearLayoutManager;
    import androidx.recyclerview.widget.RecyclerView;

    import android.view.View;
    import android.view.ViewGroup;
    import android.widget.Button;
    import android.widget.TextView;

    import java.util.ArrayList;

    public class ChangeRegionDialog extends DialogFragment {
        private OnDialogConfirmListener onDialogConfirmListener;

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

            Button okButton = view.findViewById(R.id.ok_button);
            okButton.setOnClickListener((target) -> {
                if (this.onDialogConfirmListener != null) {
                    this.onDialogConfirmListener.onConfirm("");
                }
                dismiss();
            });

            Button cancelButton = view.findViewById(R.id.cancel_button);
            cancelButton.setOnClickListener((target) -> {
                dismiss();
            });

            RecyclerView regionList = view.findViewById(R.id.region_list);
            regionList.hasFixedSize();
            regionList.setLayoutManager(new LinearLayoutManager(getContext()));

            ArrayList<String> regions = new ArrayList<>();
            Config.init();
            Config.getPlatforms().forEach((key, value) -> {
                regions.add(key);
            });
            RegionListAdapter adapter = new RegionListAdapter(regions, getContext());
            regionList.setAdapter(adapter);

            return builder.create();
        }
    }

    interface OnDialogConfirmListener {
        void onConfirm(String region);
    }

    class RegionListAdapter extends RecyclerView.Adapter<RegionListViewHolder> {
        private ArrayList<String> regions;
        private LayoutInflater inflater;

        public RegionListAdapter(ArrayList<String> regions, Context ctx) {
            this.regions = regions;
            this.inflater = LayoutInflater.from(ctx);
        }

        @NonNull
        @Override
        public RegionListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new RegionListViewHolder(inflater.inflate(R.layout.region_list_item, parent, false));
        }

        @Override
        public void onBindViewHolder(@NonNull RegionListViewHolder holder, int position) {
            holder.getRegionText().setText(regions.get(position));
        }

        @Override
        public int getItemCount() {
            return regions.size();
        }
    }

    class RegionListViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private TextView regionText;
        public RegionListViewHolder(@NonNull View itemView) {
            super(itemView);
            this.regionText = itemView.findViewById(R.id.region_text);
        }

        public TextView getRegionText() {
            return regionText;
        }

        @Override
        public void onClick(View view) {
            Log.d("REGION LIST ITEM ON CLICK", getLayoutPosition() + "");
        }
    }


