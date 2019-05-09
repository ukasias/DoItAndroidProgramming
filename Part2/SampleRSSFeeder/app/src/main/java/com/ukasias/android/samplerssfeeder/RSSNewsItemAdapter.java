package com.ukasias.android.samplerssfeeder;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import java.util.ArrayList;

public class RSSNewsItemAdapter extends BaseAdapter {
    Context _context;
    ArrayList<RSSNewsItem> items;

    public RSSNewsItemAdapter(Context context) {
        _context = context;
        items = new ArrayList<RSSNewsItem>();
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
        RSSNewsItemView _view = null;
        if (view != null) {
            _view = (RSSNewsItemView) view;
        }
        else {
            _view = new RSSNewsItemView(_context);
        }

        _view.setRSSNewsItem(items.get(i));

        return _view;
    }

    public void addItem(RSSNewsItem item) {
        items.add(item);
    }

    public void clearItem() {
        items.clear();
    }
}
