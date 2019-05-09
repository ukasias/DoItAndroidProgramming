package com.ukasias.android.samplecapture;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.hardware.Camera;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.Toast;

import java.io.IOException;

public class MainActivity extends AppCompatActivity {
    final static String TAG = "SampleCapture";
    final static String PICTURE_FILE = "picture_01";

    private Button takePictureButton;
    private FrameLayout frameLayout;
    private CameraSurfaceView surface;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        takePictureButton = findViewById(R.id.takePictureButton);
        frameLayout = findViewById(R.id.frameLayout);

        surface = new CameraSurfaceView(this);
        frameLayout.addView(surface);

        takePictureButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                surface.capture(new Camera.PictureCallback() {
                    @Override
                    public void onPictureTaken(byte[] bytes, Camera camera) {
                        try {
                            Bitmap bitmap = BitmapFactory.decodeByteArray(
                                    bytes,
                                    0,
                                    bytes.length);
                            String outUriStr = MediaStore.Images.Media.insertImage(
                                    getContentResolver(),
                                    bitmap,
                                    "Captured Image",
                                    "Captured Image using Camera");

                            print("outUriStr : " + outUriStr);

                            if (outUriStr == null) {
                                print("Image Insert Failed.");
                                return;
                            }
                            else {
                                Uri outUri = Uri.parse(outUriStr);
                                sendBroadcast(
                                        new Intent(
                                                Intent.ACTION_MEDIA_SCANNER_SCAN_FILE,
                                                outUri));

                                toast("카메라로 찍은 사진을 앨범에 저장했습니다.");
                                camera.startPreview();
                            }
                        }
                        catch (Exception e) {
                            print("onPictureTake() exception");
                            e.printStackTrace();
                        }
                    }
                });
            }
        });
    }


    class CameraSurfaceView extends SurfaceView implements SurfaceHolder.Callback {

        private SurfaceHolder holder;
        private Camera camera;

        CameraSurfaceView(Context context) {
            super(context);

            holder = getHolder();
            holder.addCallback(this);
            holder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        }

        @Override
        public void surfaceCreated(SurfaceHolder surfaceHolder) {
            print("surfaceCreated() 실행");
            camera = Camera.open();

            try {
                camera.setPreviewDisplay(holder);
            }
            catch(IOException ioe) {
                print("surfaceCreated() - setPreviewDisplay() exception");
                ioe.printStackTrace();
            }
        }

        @Override
        public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i1, int i2) {
            print("surfaceChanged(holder, i: " + i + ", i1: " + i1 + ", i2: " + i2 + ") 실행");
            camera.startPreview();
        }

        @Override
        public void surfaceDestroyed(SurfaceHolder surfaceHolder) {
            print("surfaceDestroyed() 실행");

            camera.stopPreview();
            camera.release();
            camera = null;
        }

        public boolean capture(Camera.PictureCallback handler) {
            if (camera != null) {
                camera.takePicture(null, null, handler);
                return true;
            }
            else {
                return false;
            }
        }
    }

    private void print(String message) {
        Log.d(TAG, message + "\n");
    }

    private void toast(String message) {
        Toast.makeText(getApplicationContext(),
                message,
                Toast.LENGTH_LONG).show();
    }
}
