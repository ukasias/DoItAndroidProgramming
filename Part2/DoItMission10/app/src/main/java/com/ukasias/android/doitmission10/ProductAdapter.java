package com.ukasias.android.doitmission10;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import java.util.ArrayList;

public class ProductAdapter extends BaseAdapter {
    ArrayList<ProductItem> items;
    Context mContext;

    public ProductAdapter(Context context) {
        items = new ArrayList<ProductItem>();
        mContext = context;
    }

    public void addItem(ProductItem item) {
        if (items != null) {
            items.add(item);
        }
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        ProductItemView curView;
        ProductItem item = items.get(i);

        if (view == null) {
            curView = new ProductItemView(mContext);
        }
        else {
            curView = (ProductItemView) view;
        }

        if (item != null) {
            curView.setItem(item);
        }

        return curView;
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
}