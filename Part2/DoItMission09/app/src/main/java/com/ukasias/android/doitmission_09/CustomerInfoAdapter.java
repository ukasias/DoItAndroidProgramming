package com.ukasias.android.doitmission_09;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import java.util.ArrayList;

public class CustomerInfoAdapter extends BaseAdapter {
    ArrayList<CustomerInfoItem> items;
    Context mContext;

    public CustomerInfoAdapter(Context context) {
        super();
        mContext = context;
        init();
    }

    public void init() {
        items = new ArrayList<CustomerInfoItem>();
    }

    public void addItem(CustomerInfoItem item) {
        items.add(item);
    }

    public void addItem(String name, String birth, String phone) {
        items.add(new CustomerInfoItem(name, birth, phone));
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
        CustomerInfoItemView mView;
        if (view != null) {
            mView = (CustomerInfoItemView) view;
        }
        else {
            mView = new CustomerInfoItemView(mContext);
        }
        mView.setItem(items.get(i));

        return mView;
    }
}
