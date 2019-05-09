package com.ukasias.android.doitmission11;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ukasias.android.doitmission11.R;

public class MissionPaintBoardActivity extends AppCompatActivity {
    private final String TAG = "MissionPaintBoard";

    Button colorButton;
    Button penButton;
    Button eraserButton;
    Button undoButton;

    Button colorBox;
    TextView size;

    RadioGroup radio;
    RadioButton butt;
    RadioButton round;
    RadioButton square;

    SurfaceBestPaintBoard paintBoard;

    int mColor;
    float mSize;
    Paint.Cap mCap;

    interface OnColorSelectedListener {
        void onColorSelected(int color);
    }

    interface OnWidthSelectedListener {
        void onWidthSelected(float width);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mission_paint_board);

        colorButton = (Button) findViewById(R.id.colorPalette);
        penButton = (Button) findViewById(R.id.penPalette);
        eraserButton = (Button) findViewById(R.id.eraser);
        undoButton = (Button) findViewById(R.id.undo);

        colorBox = (Button) findViewById(R.id.color);
        size = (TextView) findViewById(R.id.size);

        radio = (RadioGroup) findViewById(R.id.radio);
        butt = (RadioButton) findViewById(R.id.butt);
        round = (RadioButton) findViewById(R.id.round);
        square = (RadioButton) findViewById(R.id.square);

        paintBoard = (SurfaceBestPaintBoard) findViewById(R.id.paintBoard);

        mColor = SurfaceBestPaintBoard.defaultColor;
        mSize = SurfaceBestPaintBoard.defaultSize;
        mCap = SurfaceBestPaintBoard.defaultCap;

        radio.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                switch(radioGroup.getCheckedRadioButtonId()) {
                    case R.id.butt:
                        mCap = Paint.Cap.BUTT;
                        break;
                    case R.id.round:
                        mCap = Paint.Cap.ROUND;
                        break;
                    case R.id.square:
                        mCap = Paint.Cap.SQUARE;
                        break;
                }
                paintBoard.updatePaintProperty(mColor, mSize, mCap);
            }
        });

        colorBox.setBackgroundColor(mColor);
        size.setText(new String("Size: " + String.valueOf(mSize)));

        switch(mCap) {
            case BUTT:
                butt.setChecked(true);
                break;
            case ROUND:
                round.setChecked(true);
                break;
            case SQUARE:
                square.setChecked(true);
                break;
        }

        colorButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ColorPaletteDialog.listener = new OnColorSelectedListener() {
                    @Override
                    public void onColorSelected(int color) {
                        mColor = color;
                        paintBoard.updatePaintProperty(mColor, mSize, mCap);
                        colorBox.setBackgroundColor(mColor);
                        size.setText("Size: " + String.valueOf(mSize));
                    }
                };

                Intent intent = new Intent(getApplicationContext(), ColorPaletteDialog.class);
                startActivity(intent);
            }
        });

        penButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                WidthBarDialog.listener = new OnWidthSelectedListener() {
                    @Override
                    public void onWidthSelected(float width) {
                        mSize = width;
                        paintBoard.updatePaintProperty(mColor, mSize, mCap);
                        size.setText(new String("Size: " + String.valueOf(mSize)));
                    }
                };

                Intent intent = new Intent(getApplicationContext(),
                        WidthBarDialog.class);
                startActivity(intent);
            }
        });
    }
}
