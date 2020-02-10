package com.example.tft_stat_checker_native.View;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.tft_stat_checker_native.R;

public class FragmentItemViewer extends Fragment {
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_item_viewer, container, false);
    }

    public static FragmentItemViewer createInstance() {
        Bundle args = new Bundle();
        FragmentItemViewer frag = new FragmentItemViewer();
        frag.setArguments(args);
        return frag;
    }
}
