package com.example.a15puzzle;

import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;

import java.util.ArrayList;

public class PuzzleAdapter extends BaseAdapter {
    private ArrayList<Button> buttons;
    private int width, height;

    public PuzzleAdapter(ArrayList<Button> buttons, int width, int height) {
        this.buttons = buttons;
        this. width = width;
        this.height = height;
    }

    @Override
    public int getCount() {
        return buttons.size();
    }

    @Override
    public Object getItem(int position) {
        return (Object) buttons.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Button button;

        button = convertView == null ? this.buttons.get(position) : (Button) convertView;

        android.widget.AbsListView.LayoutParams params = new android.widget.AbsListView.LayoutParams(this.width,this.height);
        button.setLayoutParams(params);

        return button;
    }
}
