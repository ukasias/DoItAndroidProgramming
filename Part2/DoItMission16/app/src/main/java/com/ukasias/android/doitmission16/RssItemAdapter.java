package com.ukasias.android.doitmission16;

import android.content.Context;
import android.telephony.RadioAccessSpecifier;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.BaseAdapter;

import java.util.ArrayList;

public class RssItemAdapter extends BaseAdapter {
    ArrayList<RssItem> items;
    Context _context;
    WebView _webView;

    public RssItemAdapter(Context context, WebView webView) {
        _context = context;
        _webView = webView;
        items = new ArrayList<RssItem>();
    }

    @Override
    public int getCount() {
        return items.size();
    }

    @Override
    public Object getItem(int i) {
        if (i >= 0 && i < items.size()) {
            return items.get(i);
        }
        return null;
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        RssItemView _view;
        if (i < 0 || i >= items.size()) {
            return null;
        }

        if (view == null) {
            _view = new RssItemView(_context);
        }
        else {
            _view = (RssItemView) view;
        }

        final RssItem item = items.get(i);
        _view.setRssItem(item);
        _view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                _webView.loadUrl(item.getLink());
            }
        });

        return _view;
    }

    public void addItem(RssItem item) {
        items.add(item);
    }
}
