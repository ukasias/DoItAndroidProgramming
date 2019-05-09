package com.ukasias.android.multimemo;

import android.content.Context;
import android.hardware.Camera;
import android.util.Log;
import android.view.Surface;
import android.view.SurfaceView;
import android.view.SurfaceHolder;
import android.widget.Toast;

import java.io.IOException;

public class CameraSurfaceView extends SurfaceView
        implements SurfaceHolder.Callback {
    private static final String TAG = "CameraSurfaceView";

    private Context mContext;

    private SurfaceHolder holder;
    private Camera camera;

    public CameraSurfaceView(Context context) {
        super(context);
        mContext = context;

        holder = getHolder();
        holder.addCallback(this);
        holder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        openCamera();
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        camera.startPreview();
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        stopPreview();
    }

    public Surface getSurface() {
        return holder.getSurface();
    }

    public boolean capture(Camera.PictureCallback jpegHandler) {
        toast("Picture captured...");

        if (camera != null) {
            camera.takePicture(null, null,
                    jpegHandler);
            return true;
        }
        else {
            return false;
        }
    }

    private void openCamera() {
        camera = Camera.open();
        camera.setDisplayOrientation(0);

        try {
            camera.setPreviewDisplay(holder);
        }
        catch(IOException ioe) {
            Log.e(TAG, "Failed to set camera preview - IOException");
        }
        catch(Exception e) {
            Log.e(TAG, "Failed to set camera preview - Exception");
        }
    }

    public void stopPreview() {
        camera.stopPreview();
        camera.release();
        camera = null;
    }

    public void startPreview() {
        openCamera();
        camera.startPreview();
    }

    private void print(String message) {
        Log.d(TAG, message + "\n");
    }

    private void toast(String message) {
        Toast.makeText(mContext, message, Toast.LENGTH_LONG).show();
    }
}
