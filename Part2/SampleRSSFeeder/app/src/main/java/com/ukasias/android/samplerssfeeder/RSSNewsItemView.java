package com.ukasias.android.samplerssfeeder;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class RSSNewsItemView extends LinearLayout {
    Context _context;
    ImageView icon;
    TextView title;
    TextView date;
    TextView contents;

    public RSSNewsItemView(Context context) {
        super(context);
        _context = context;
        init();
    }

    public RSSNewsItemView(Context context, AttributeSet attrs) {
        super(context, attrs);
        _context = context;
        init();
    }

    public void init() {
        if (_context == null) {
            return;
        }

        LayoutInflater inflater =
                (LayoutInflater)_context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.list_item, this, true);

        icon = (ImageView) findViewById(R.id.iconItem);
        title = (TextView) findViewById(R.id.titleItem);
        date = (TextView) findViewById(R.id.dateItem);
        contents = (TextView) findViewById(R.id.contentsItem);
    }

    public void setRSSNewsItem(RSSNewsItem newsItem) {
        final RSSNewsItem item = newsItem;
        icon.setImageDrawable(item.getIcon());
        title.setText(item.getTitle());
        date.setText(item.getDate());
        contents.setText(item.getContents());
        setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(item.getLink()));
                _context.startActivity(intent);
            }
        });
    }
}
