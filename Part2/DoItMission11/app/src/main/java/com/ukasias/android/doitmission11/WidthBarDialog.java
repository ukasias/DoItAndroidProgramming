package com.ukasias.android.doitmission11;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import static com.ukasias.android.doitmission11.ColorPaletteDialog.listener;

public class WidthBarDialog extends Activity {
    SeekBar width;
    TextView widthText;
    Button close;

    static MissionPaintBoardActivity.OnWidthSelectedListener listener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_width_bar_dialog);

        setTitle("굵기 선택");

        width = (SeekBar) findViewById(R.id.width);
        widthText = (TextView) findViewById(R.id.widthText);
        close = (Button) findViewById(R.id.close2);

        width.setMax((int) SurfaceBestPaintBoard.maxSize - 1);
        width.setProgress((int) SurfaceBestPaintBoard.mSize - 1);
        widthText.setText(String.valueOf((int) SurfaceBestPaintBoard.mSize));

        width.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                listener.onWidthSelected(i + 1);
                widthText.setText(String.valueOf(i + 1));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    public void setWidth(float w) {
        width.setProgress((int) w);
        widthText.setText(String.valueOf(w));
    }
}
