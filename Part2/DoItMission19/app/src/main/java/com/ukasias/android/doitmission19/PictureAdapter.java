package com.ukasias.android.doitmission19;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import java.util.ArrayList;

public class PictureAdapter extends BaseAdapter {
    private ArrayList<PictureItem> items;
    Context _context;

    public PictureAdapter(Context context) {
        items = new ArrayList<>();
        _context = context;
    }

    public void addItems(PictureItem item) {
        Log.d("Adapter", "addItems()");
        items.add(item);
    }

    @Override
    public int getCount() {
        return items.size();
    }

    @Override
    public Object getItem(int i) {
        return items.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        Log.d("Adapter", "getView(i: " + i + ")");
        PictureItemView itemView;
        if (view == null) {
            itemView = new PictureItemView(_context);
        }
        else {
            itemView = (PictureItemView) view;
        }
        itemView.setPictureItem(items.get(i));

        return itemView;
    }
}
