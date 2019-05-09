package com.ukasias.android.multimemo;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import java.util.ArrayList;
import java.util.List;

public class MemoListAdapter extends BaseAdapter {

    private Context mContext;

    private List<MemoListItem> mItems;

    MemoListAdapter(Context context) {
        mContext = context;
        mItems = new ArrayList<MemoListItem>();
    }

    public void clear() {
        mItems.clear();
    }

    public void addItem(MemoListItem item) {
        mItems.add(item);
    }

    public void setListItems(List<MemoListItem> list) {
        mItems = list;
    }

    public boolean areAllItemsSelectable() {
        return false;
    }

    public boolean isSelectable(int position) {
        try {
            return mItems.get(position).getSelectable();
        }
        catch(IndexOutOfBoundsException ex) {
            return false;
        }
    }

    @Override
    public int getCount() {
        return mItems.size();
    }

    @Override
    public Object getItem(int i) {
        return mItems.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        MemoListItemView itemView;
        if (view == null) {
            itemView = new MemoListItemView(mContext);
        }
        else {
            itemView = (MemoListItemView) view;
        }

        itemView.setContents(0, ((String) mItems.get(i).getData(0)));
        itemView.setContents(1, ((String) mItems.get(i).getData(1)));
        itemView.setContents(2, ((String) mItems.get(i).getData(3)));
        itemView.setContents(3, ((String) mItems.get(i).getData(9)));

        itemView.setMediaState(
                mItems.get(i).getData(5), mItems.get(i).getData(7));

        return itemView;
    }
}
