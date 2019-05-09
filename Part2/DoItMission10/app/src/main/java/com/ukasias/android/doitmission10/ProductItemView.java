package com.ukasias.android.doitmission10;

import android.content.Context;
import android.text.Layout;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.w3c.dom.Text;

public class ProductItemView extends LinearLayout {
    ImageView imageView;
    TextView nameTextView;
    TextView priceTextView;
    TextView commentTextView;

    public ProductItemView(Context context) {
        super(context);
        init(context);
    }

    public ProductItemView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public void init(Context context) {
        LayoutInflater inflater =
                (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.product, this, true);

        imageView = (ImageView) findViewById(R.id.image);
        nameTextView = (TextView) findViewById(R.id.name);
        priceTextView = (TextView) findViewById(R.id.price);
        commentTextView = (TextView) findViewById(R.id.comment);
    }

    public void setItem(ProductItem item) {
        if (item != null) {
            imageView.setImageResource(item.getImage());
            nameTextView.setText(item.getName());
            priceTextView.setText(String.valueOf(item.getPrice()) + " 원");
            commentTextView.setText(item.getComment());
        }
    }
}
