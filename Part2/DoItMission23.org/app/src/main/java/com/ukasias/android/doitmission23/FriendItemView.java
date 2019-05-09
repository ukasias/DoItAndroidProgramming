package com.ukasias.android.doitmission23;

import android.content.Context;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class FriendItemView extends LinearLayout {
    Context context;

    ImageView imageView;
    TextView nameText;
    TextView phoneText;

    FriendItemView(Context context) {
        super(context);
        this.context = context;

        init();
    }

    private void init() {
        LayoutInflater inflater =
                (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.friend, this, true);

        imageView = findViewById(R.id.imageView);
        nameText = findViewById(R.id.name);
        phoneText = findViewById(R.id.phone);
    }

    public void setFriendItem(FriendItem item) {
        imageView.setImageDrawable(item.getPicture());
        nameText.setText(item.getName());
        phoneText.setText(item.getPhone());
    }
}
