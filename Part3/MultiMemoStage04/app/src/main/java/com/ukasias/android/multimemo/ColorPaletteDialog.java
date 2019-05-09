package com.ukasias.android.multimemo;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.TextView;

import com.ukasias.android.multimemo.common.TitleBitmapButton;

public class ColorPaletteDialog extends Activity {
    private static final String TAG = "ColorPaletteDialog";

    /**
     * UI Widgets
     */
    private TextView textView;
    private GridView gridView;
    private TitleBitmapButton closeButton;

    /**
     * Listener, ColorDataAdapter
     */
    public static OnColorSelectedListener listener;
    private ColorDataAdapter adapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_handwriting_dialog);

        textView = findViewById(R.id.textView);
        gridView = findViewById(R.id.gridView);
        closeButton = findViewById(R.id.closeButton);

        textView.setText("색상 선택");

        //gridView.setColumnWidth(12);    /* column width size */
        gridView.setBackgroundColor(Color.GRAY);
        //gridView.setHorizontalSpacing(4);
        //gridView.setVerticalSpacing(4);

        adapter = new ColorDataAdapter(this);
        gridView.setAdapter(adapter);
        gridView.setNumColumns(adapter.getColumnCount());

        closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
}

class ColorDataAdapter extends BaseAdapter {
    private static final String TAG = "ColorDataAdapter";

    private static final int[] colors = new int[] {
            0xff000000, 0xff00007f, 0xff0000ff, 0xff007f00, 0xff007f7f, 0xff00ff00, 0xff00ff7f,
            0xff00ffff, 0xff7f007f, 0xff7f00ff, 0xff7f7f00, 0xff7f7f7f, 0xffff0000, 0xffff007f,
            0xffff00ff, 0xffff7f00, 0xffff7f7f, 0xffff7fff, 0xffffff00, 0xffffff7f, 0xffffffff
    };

    private Context mContext;
    private int rowCount;
    private int columnCount;


    public ColorDataAdapter(Context context) {
        mContext = context;

        rowCount = 3;
        columnCount = 7;
    }

    @Override
    public int getCount() {
        return colors.length;
    }

    public int getColumnCount() {
        return columnCount;
    }

    public int getRowCount() {
        return rowCount;
    }

    @Override
    public Object getItem(int position) {
        return colors[position];
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        int row = position / columnCount;
        int column = position % columnCount;
        print("getView(row: " + row + ", column: " + column + ") called.");

        View item = new View(mContext);
        item.setBackgroundColor(colors[position]);
        item.setTag(colors[position]);  // 어떻게 쓰이나? : 자신의 색상 정보를 가짐
        if (parent.getWidth() > 0 && parent.getHeight() > 0) {
            ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(
                    parent.getWidth() / columnCount,
                    parent.getHeight() / rowCount);
            item.setLayoutParams(params);
        }

        item.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ColorPaletteDialog.listener != null) {
                    ColorPaletteDialog.listener.onColorSelected(
                            ((Integer) v.getTag()).intValue());
                }
                // ColorPaletteDialog를 종료 시킴.
                ((ColorPaletteDialog) mContext).finish();
            }
        });

        return item;
    }

    private void print(String message) {
        Log.d(TAG, message + "\n");
    }
}