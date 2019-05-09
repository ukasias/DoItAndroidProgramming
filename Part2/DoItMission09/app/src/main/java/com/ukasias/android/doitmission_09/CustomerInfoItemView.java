package com.ukasias.android.doitmission_09;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class CustomerInfoItemView extends LinearLayout {
    ImageView imageView;
    TextView nameText;
    TextView birthText;
    TextView phoneText;

    Context mContext;

    public CustomerInfoItemView(Context context) {
        super(context);
        mContext = context;
        init(context);
    }

    public CustomerInfoItemView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        init(context);
    }

    public void init(Context context) {
        inflate(context, R.layout.customer, this);

        imageView = (ImageView) findViewById(R.id.imageView);
        nameText = (TextView) findViewById(R.id.name);
        birthText = (TextView) findViewById(R.id.birth);
        phoneText = (TextView) findViewById(R.id.phone);
    }

    public void setItem(CustomerInfoItem item) {
        imageView.setImageResource(item.getIcon());
        nameText.setText(item.getName());
        birthText.setText(item.getBirth());
        phoneText.setText(item.getPhoneNumber());
    }
}
