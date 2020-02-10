package com.example.tft_stat_checker_native.View;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.tft_stat_checker_native.R;

public class FragmentUnitViewer extends Fragment {
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_unit_viewer, container, false);
    }

    public static FragmentUnitViewer createInstance() {
        Bundle args = new Bundle();
        FragmentUnitViewer frag = new FragmentUnitViewer();
        frag.setArguments(args);
        return frag;
    }
}
