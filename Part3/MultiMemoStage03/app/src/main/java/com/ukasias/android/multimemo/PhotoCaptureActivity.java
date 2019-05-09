package com.ukasias.android.multimemo;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.hardware.Camera;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.ukasias.android.multimemo.common.TitleBitmapButton;

import java.io.File;
import java.io.FileOutputStream;

public class PhotoCaptureActivity extends AppCompatActivity {
    private static final String TAG = "PhotoCaptureActivity";

    private FrameLayout frame;
    private TitleBitmapButton captureButton;

    private CameraSurfaceView cameraView;

    /**
     * 버튼을 눌러 캡쳐 진행 중인 상태에서 버튼이 또 눌릴 때의 무시 처리를 위한 변수
     */
    private boolean processing;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        print("onCreate() called.");

        // 상태 바와 타이틀 설정
        final Window win = getWindow();
        win.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                     WindowManager.LayoutParams.FLAG_FULLSCREEN);
        boolean requested =
                requestWindowFeature(Window.FEATURE_NO_TITLE);
        print("requestWindowFeature(Window.FEATURE_NO_TITLE): " + requested);

        setContentView(R.layout.activity_photo_capture);

        cameraView = new CameraSurfaceView(getApplicationContext());
        frame = findViewById(R.id.frame);
        frame.addView(cameraView);

        processing = false;
        setCaptureButton();
    }

    private void setCaptureButton() {
        print("setCaptureButton() called.");

        captureButton = findViewById(R.id.capture_button);
        captureButton.setBackgroundBitmap(
                R.drawable.btn_camera_capture_normal,
                R.drawable.btn_camera_capture_click);
        captureButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!processing) {
                    processing = true;
                    print("onClick() executed.");
                    cameraView.capture(new CameraPictureCallback());
                }
            }
        });
    }

    class CameraPictureCallback implements Camera.PictureCallback {
        private static final String TAG = "CameraPictureCallback";

        @Override
        public void onPictureTaken(byte[] data, Camera camera) {
            Log.d(TAG, "onPictureTaken() called.");

            //int bitmapWidth = 480;
            //int bitmapHeight = 360;

            Bitmap capturedBitmap = BitmapFactory.decodeByteArray(data, 0, data.length);

            print("picture size: " + capturedBitmap.getWidth() + " x " + capturedBitmap.getHeight());

            //Bitmap scaledBitmap = Bitmap.createScaledBitmap(capturedBitmap, bitmapWidth, bitmapHeight, false);
            Bitmap resultBitmap = null;

            Matrix matrix = new Matrix();
            matrix.setRotate(0);

            //resultBitmap = Bitmap.createBitmap(scaledBitmap, 0, 0, bitmapWidth, bitmapHeight,matrix, true);
            resultBitmap =
                    Bitmap.createBitmap(
                            capturedBitmap, 0, 0,
                            capturedBitmap.getWidth(), capturedBitmap.getHeight(),
                            matrix, true);

            try {
                File photoFolder = new File(BasicInfo.FOLDER_PHOTO);
                if (!photoFolder.exists()) {
                    print("creatring photo folder : " + photoFolder);
                    photoFolder.mkdirs();
                }

                String photoName = "captured";
                File file = new File(BasicInfo.FOLDER_PHOTO + photoName);
                if (file.exists()) {
                    file.delete();
                }

                FileOutputStream outstream =
                        new FileOutputStream(BasicInfo.FOLDER_PHOTO + photoName);
                resultBitmap.compress(Bitmap.CompressFormat.PNG, 100, outstream);
                outstream.close();
            }
            catch(Exception e) {
                Log.e(TAG, "Error in writing captured image.", e);
                showDialog(BasicInfo.IMAGE_CANNOT_BE_STORED);
            }

            showParentActivity();

            processing = false;
        }
    }

    private void showParentActivity() {
        setResult(RESULT_OK);

        finish();
    }

    @Override
    protected Dialog onCreateDialog(int id) {
        print("onCreateDialog() called.");

        switch(id) {
            case BasicInfo.IMAGE_CANNOT_BE_STORED:
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("사진을 저장할 수 없습니다. SD 카드 상태를 확인하세요.");
                builder.setPositiveButton("확인",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        });
                return builder.create();
        }
        return null;
    }

    private void print(String message) {
        Log.d(TAG, message + "\n");
    }

    private void toast(String message) {
        Toast.makeText(
                getApplicationContext(),
                message,
                Toast.LENGTH_LONG
        ).show();
    }
}
