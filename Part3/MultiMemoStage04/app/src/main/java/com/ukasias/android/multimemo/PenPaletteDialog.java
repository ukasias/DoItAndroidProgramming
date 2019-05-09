package com.ukasias.android.multimemo;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.ukasias.android.multimemo.common.TitleBitmapButton;

public class PenPaletteDialog extends Activity {
    private static final String TAG = "PenPaletteDialog";

    /**
     * UI Widgets
     */
    private TextView textView;
    private GridView gridView;
    private TitleBitmapButton closeButton;

    /**
     * Listener, ColorDataAdapter
     */
    public static OnPenSelectedListener listener;
    private PenDataAdapter adapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_handwriting_dialog);

        textView = findViewById(R.id.textView);
        gridView = findViewById(R.id.gridView);
        closeButton = findViewById(R.id.closeButton);

        textView.setText("펜 굵기 선택");

        //gridView.setColumnWidth(12);    /* column width size */
        gridView.setBackgroundColor(Color.GRAY);
        //gridView.setHorizontalSpacing(4);
        //gridView.setVerticalSpacing(4);

        adapter = new PenDataAdapter(this);
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

class PenDataAdapter extends BaseAdapter {
    private static final String TAG = "PenDataAdapter";

    private static final int[] pens = new int[] {
            1, 2, 3, 4, 5,
            6, 7, 8, 9, 10,
            11, 13, 15, 17, 20
    };

    private Context mContext;
    private int rowCount;
    private int columnCount;


    public PenDataAdapter(Context context) {
        mContext = context;

        rowCount = 3;
        columnCount = 5;
    }

    @Override
    public int getCount() {
        return pens.length;
    }

    public int getColumnCount() {
        return columnCount;
    }

    public int getRowCount() {
        return rowCount;
    }

    @Override
    public Object getItem(int position) {
        return pens[position];
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        int row = position / columnCount;
        int column = position % columnCount;
        int pen = pens[position];
        print("getView(row: " + row + ", column: " + column + ", parent: (" +
                parent.getWidth() + ", " + parent.getHeight() + ")) called.");

        GridView.LayoutParams params = new GridView.LayoutParams(
                GridView.LayoutParams.MATCH_PARENT,
                GridView.LayoutParams.MATCH_PARENT);

        ImageView item = new ImageView(mContext);
        int imageWidth = parent.getWidth() / columnCount;
        int imageHeight = parent.getHeight() / rowCount;

        if (imageWidth <= 0 || imageHeight <= 0) {
            return item;
        }
        Bitmap penBitmap = Bitmap.createBitmap(
                imageWidth, imageHeight, Bitmap.Config.ARGB_8888);
        Canvas penCanvas = new Canvas();
        penCanvas.setBitmap(penBitmap);
        Paint penPaint = new Paint();

        // 배경 칠하기
        penPaint.setColor(Color.WHITE);
        penCanvas.drawRect(
                new Rect(0, 0, imageWidth, imageHeight),
                penPaint);

        // 테두리 선 긋기
        imageWidth -= 1;
        imageHeight -= 1;
        penPaint.setColor(Color.GRAY);
        penPaint.setStrokeWidth(1);
        penCanvas.drawLine(0, 0, 0, imageHeight, penPaint);
        penCanvas.drawLine(imageWidth, imageHeight, 0, imageHeight, penPaint);
        penCanvas.drawLine(imageWidth, imageHeight, imageWidth, 0, penPaint);
        penCanvas.drawLine(0, 0, imageWidth, 0, penPaint);

        // 현재 두께에 따라 가운데에 줄 긋기
        penPaint.setColor(Color.BLACK);
        penPaint.setStrokeWidth(pen);
        penCanvas.drawLine(
                0, (imageHeight+1-pen)/2,
                imageWidth, (imageHeight+1-pen)/2, penPaint);

        item.setTag(pen);  // 어떻게 쓰이나? : 자신의 pens 굵기 정보를 저장
        item.setLayoutParams(params);
        item.setImageBitmap(penBitmap);
        item.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (PenPaletteDialog.listener != null) {
                    print("onPenSelected(" + ((Integer) v.getTag()).intValue() + ")");
                    PenPaletteDialog.listener.onPenSelected(
                            ((Integer) v.getTag()).intValue());
                }
                // PenPaletteDialog를 종료 시킴.
                ((PenPaletteDialog) mContext).finish();
            }
        });

        return item;
    }

    private void print(String message) {
        Log.d(TAG, message + "\n");
    }
}