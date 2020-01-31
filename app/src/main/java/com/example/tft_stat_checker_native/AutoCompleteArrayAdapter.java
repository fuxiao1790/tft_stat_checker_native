package com.example.tft_stat_checker_native;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;

public class AutoCompleteArrayAdapter extends ArrayAdapter {
    private ArrayList<String> listData;
    private LayoutInflater layoutInflater;

    public AutoCompleteArrayAdapter(@NonNull Context context, ArrayList<String> listData) {
        super(context, 0);
        this.listData = listData;
        this.layoutInflater = LayoutInflater.from(context);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        TextView textView = (TextView) layoutInflater.inflate(R.layout.auto_complete_item, parent, false);
        textView.setText(listData.get(position));
        return textView;
    }

    public void addItem(String s) {
        listData.add(0, s);
    }
}
