package com.ukasias.android.tutorial.graphic;

import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class GoodPaintBoardActivity extends AppCompatActivity {
    Button colorButton;
    Button penButton;
    Button eraserButton;
    Button undoButton;

    Button colorBox;
    TextView size;

    SurfaceBestPaintBoard paintBoard;

    int mColor;
    float mSize;

    interface OnColorSelectedListener {
        void onColorSelected(int color);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_good_paint_board);

        colorButton = (Button) findViewById(R.id.colorPalette);
        penButton = (Button) findViewById(R.id.penPalette);
        eraserButton = (Button) findViewById(R.id.eraser);
        undoButton = (Button) findViewById(R.id.undo);

        colorBox = (Button) findViewById(R.id.color);
        size = (TextView) findViewById(R.id.size);

        paintBoard = (SurfaceBestPaintBoard) findViewById(R.id.paintBoard);

        colorButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ColorPaletteDialog.listener = new OnColorSelectedListener() {
                    @Override
                    public void onColorSelected(int color) {
                        mColor = color;
                        paintBoard.updatePaintProperty(mColor, mSize);
                        colorBox.setBackgroundColor(mColor);
                        size.setText("Size: " + String.valueOf(mSize));
                    }
                };

                Intent intent = new Intent(getApplicationContext(),
                                        ColorPaletteDialog.class);
                startActivity(intent);
            }
        });

        mColor = SurfaceBestPaintBoard.defaultColor;
        mSize = SurfaceBestPaintBoard.defaultSize;

        colorBox.setBackgroundColor(mColor);
        size.setText(new String("Size: " + String.valueOf(mSize)));
    }
}
