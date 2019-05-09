package com.ukasias.android.doitmission18;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Toast;

import java.util.ArrayList;

public class BookItemAdapter extends BaseAdapter {
    private final String TAG = "BookItemAdapter";

    Context _context;
    ArrayList<BookItem> items;

    public BookItemAdapter(Context context) {
        super();

        _context = context;
        items = new ArrayList<BookItem>();
    }

    public void addBookItem(BookItem item) {
        items.add(item);
        print("addBookItem() - " + items.size());
    }

    public void clearItems() {
        items.clear();
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
        print("getView(" + i + ")");

        BookItemView biView = new BookItemView(_context);
        biView.setBookItem(items.get(i));

        return biView;
    }

    void print(String str) {
        Log.d(TAG, str + "\n");
    }
}
