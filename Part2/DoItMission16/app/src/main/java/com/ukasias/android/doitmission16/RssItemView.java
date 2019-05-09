package com.ukasias.android.doitmission16;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class RssItemView extends LinearLayout {
    ImageView iconView;
    TextView titleText;
    TextView descriptionText;

    Context _context;

    public RssItemView(Context context) {
        super(context);
        _context = context;
        init();
    }

    public RssItemView(Context context, AttributeSet attrs) {
        super(context, attrs);
        _context = context;
        init();
    }

    public void init() {
        LayoutInflater inflater =
                (LayoutInflater) _context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.rss, this, true);

        iconView = (ImageView) findViewById(R.id.iconView);
        titleText = (TextView) findViewById(R.id.titleText);
        descriptionText = (TextView) findViewById(R.id.descriptionText);
    }

    public void setRssItem(RssItem item) {
        if (item != null) {
            iconView.setImageDrawable(item.getIcon());
            titleText.setText(item.getTitle());
            descriptionText.setText(item.getContents());
        }
    }
}
