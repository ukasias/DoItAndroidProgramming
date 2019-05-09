package com.ukasias.android.doitmission19;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class PictureItemView extends LinearLayout {
    private ImageView thumbnail;
    private TextView name;
    private TextView date;

    public PictureItemView(Context context) {
        super(context);
        init(context);
    }

    private void init(Context context) {
        LayoutInflater inflater =
                (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.picture_item, this, true);

        thumbnail = findViewById(R.id.imageView);
        name = findViewById(R.id.name);
        date = findViewById(R.id.date);
    }

    public void setPictureItem(PictureItem item) {
        if (thumbnail != null) {
            thumbnail.setImageURI(item.getThumbnail());
            name.setText(item.getName());
            date.setText(item.getDate());
        }
    }
}
