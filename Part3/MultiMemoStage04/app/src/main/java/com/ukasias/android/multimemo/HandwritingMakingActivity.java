package com.ukasias.android.multimemo;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.ukasias.android.multimemo.common.TitleBitmapButton;

import java.io.File;
import java.io.FileOutputStream;

public class HandwritingMakingActivity extends AppCompatActivity {
    private static final String TAG = "HandwritingMActivity";

    public static final String handwritingFilename = "made";

    /**
     * UI Widgets
     */
    private HandwritingView handwritingView;
    private TitleBitmapButton saveButton;

    private TitleBitmapButton colorButton;
    private TitleBitmapButton penButton;
    private TitleBitmapButton eraserButton;
    private TitleBitmapButton undoButton;

    private ImageView displayColor;
    private TextView displayPen;

    /**
     * Setting Variables
     */

    private static final int DEFAULT_COLOR = Color.BLACK;
    private static final int DEFAULT_PENSIZE = 10;
    private static final int DEFAULT_ERASESIZE = 20;

    private int mColor;
    private int mSize;
    private int mPrevColor;
    private int mPrevSize;
    private boolean mEraserSelected;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_handwriting_making);

        handwritingView = findViewById(R.id.handwritingView);

        setTopLayout();

        setBottomLayout();

        init();
    }

    private void init() {
        mColor = DEFAULT_COLOR;
        mSize = DEFAULT_PENSIZE;
        mEraserSelected = false;
    }

    private void setTopLayout() {
        print("setTopLayout() called.");

        colorButton = findViewById(R.id.colorButton);
        penButton = findViewById(R.id.penButton);
        eraserButton = findViewById(R.id.eraserButton);
        undoButton = findViewById(R.id.undoButton);

        displayColor = findViewById(R.id.displayColor);
        displayPen = findViewById(R.id.displayPen);

        ColorPaletteDialog.listener = new OnColorSelectedListener() {
            @Override
            public void onColorSelected(int color) {
                mColor = color;
                handwritingView.updatePaintProperty(mColor, mSize);
                displayPaintProperty();
            }
        };
        colorButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(
                        getApplicationContext(),
                        ColorPaletteDialog.class);
                startActivity(intent);
            }
        });

        PenPaletteDialog.listener = new OnPenSelectedListener() {
            @Override
            public void onPenSelected(int pen) {
                mSize = pen;
                handwritingView.updatePaintProperty(mColor, mSize);
                displayPaintProperty();
            }
        };
        penButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(
                        getApplicationContext(),
                        PenPaletteDialog.class);
                startActivity(intent);
            }
        });

        eraserButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mEraserSelected = !mEraserSelected;

                if (mEraserSelected) {
                    colorButton.setEnabled(false);
                    penButton.setEnabled(false);
                    undoButton.setEnabled(false);

                    colorButton.invalidate();
                    penButton.invalidate();
                    undoButton.invalidate();

                    mPrevColor = mColor;
                    mPrevSize = mSize;

                    mColor = Color.WHITE;
                    mPrevSize = DEFAULT_ERASESIZE;

                    handwritingView.updatePaintProperty(mColor, mSize);
                    displayPaintProperty();
                }
                else {
                    colorButton.setEnabled(true);
                    penButton.setEnabled(true);
                    undoButton.setEnabled(true);

                    colorButton.invalidate();
                    penButton.invalidate();
                    undoButton.invalidate();

                    mColor = mPrevColor;
                    mSize = mPrevSize;

                    handwritingView.updatePaintProperty(mColor, mSize);
                    displayPaintProperty();
                }
            }
        });

        undoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                print("undo button clicked");

                handwritingView.undo();
            }
        });

        // HERE
    }

    private void setBottomLayout() {
        print("setBottomLayout() called.");
        saveButton = findViewById(R.id.saveButton);

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveHandwritingMaking();
            }
        });
    }

    private void displayPaintProperty() {
        displayColor.setBackgroundColor(mColor);
        displayPen.setText("펜 두꼐:" + mSize);
    }

    private void saveHandwritingMaking() {
        try {
            checkHandwritingFolder();

            File file = new File(
                    BasicInfo.FOLDER_HANDWRITING + handwritingFilename);
            if (file.exists()) {
                file.delete();
            }

            FileOutputStream outputStream =
                    new FileOutputStream(BasicInfo.FOLDER_HANDWRITING + handwritingFilename);
            boolean saved = handwritingView.Save(outputStream);
            outputStream.close();

            if (saved) {
                print(BasicInfo.FOLDER_HANDWRITING + handwritingFilename + " saved");
            }
            else {
                print(BasicInfo.FOLDER_HANDWRITING + handwritingFilename + " failed to save");
            }
        }
        catch(Exception e) {
            e.printStackTrace();
        }

        setResult(RESULT_OK);
        finish();
    }

    private void checkHandwritingFolder() {
        File handwritingFolder = new File(BasicInfo.FOLDER_HANDWRITING);
        if (handwritingFolder.isDirectory() == false) {
            print(BasicInfo.FOLDER_HANDWRITING + " folder created.");

            handwritingFolder.mkdirs();
        }
    }

    private void print(String message) {
        Log.d(TAG, message + "\n");
    }
}
