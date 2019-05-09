package com.ukasias.android.push;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.ukasias.android.push.FriendItem;
import com.ukasias.android.push.FriendItemView;

import java.util.ArrayList;

public class FriendAdapter extends BaseAdapter {
    Context context;
    ArrayList<FriendItem> items;

    FriendAdapter(Context context) {
        this.context = context;
        items = new ArrayList<FriendItem>();
    }

    public void addItem(FriendItem item) {
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
        FriendItemView itemView;

        if (view == null) itemView = new FriendItemView(context);
        else itemView = (FriendItemView) view;

        itemView.setFriendItem(items.get(i));

        return itemView;
    }
}
