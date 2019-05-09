package com.ukasias.android.doitmission18;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import javax.xml.parsers.ParserConfigurationException;

public class BookItemView extends LinearLayout {
    private final String TAG = "BookItemView";

    ImageView imageView;
    TextView title;
    TextView author;
    Context _context;

    public BookItemView(Context context) {
        super(context);
        init(context);
    }

    public BookItemView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public void init(Context context) {
        print("init())");
        LayoutInflater inflater =
                (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.book_item, this, true);

        imageView = findViewById(R.id.image);
        title = findViewById(R.id.title);
        author = findViewById(R.id.author);

        _context = context;
    }

    public void setBookItem(final BookItem item) {
        if (imageView != null &&
                title != null &&
               author != null) {
            imageView.setImageDrawable(item.getImage());
            title.setText(item.getTitle());
            author.setText(item.getAuthor());
            setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View view) {
                    Toast.makeText(_context,
                            item.getContents(),
                            Toast.LENGTH_LONG).show();
                }
            });

            print("setBookItem() - inside");
        }
        else {
            print("setBookItem() - outside");
        }
    }

    private void print(String str) {
        Log.d(TAG, str + "\n");
    }
}
