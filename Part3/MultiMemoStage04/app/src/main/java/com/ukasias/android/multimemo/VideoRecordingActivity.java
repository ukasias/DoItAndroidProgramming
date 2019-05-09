package com.ukasias.android.multimemo;

import android.media.MediaRecorder;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.ukasias.android.multimemo.common.TitleBitmapButton;

import java.io.File;
import java.io.IOException;

public class VideoRecordingActivity extends AppCompatActivity {
    private final static String TAG = "VideoRecordingActivity";

    private FrameLayout frameLayout;
    private TitleBitmapButton recordButton;

    private MediaRecorder mediaRecorder;
    CameraSurfaceView cameraSurfaceView;
    boolean isStarted;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        /**
         * 다음 코드는 setContentView()보다 앞서 실행되어야 한다.
         * Activity 창에서 title 지우기.  ==> NOT WORKING!!!!!!!!!!!!!!!!!!!!!
         */

        final Window win = this.getWindow();
        win.setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        requestWindowFeature(Window.FEATURE_NO_TITLE);

        setContentView(R.layout.activity_video_recording);

        frameLayout = findViewById(R.id.frameLayout);
        recordButton = findViewById(R.id.recordButton);

        mediaRecorder = new MediaRecorder();
        cameraSurfaceView = new CameraSurfaceView(getApplicationContext());
        frameLayout.addView(cameraSurfaceView);

        isStarted = false;

        setRecordButton();
    }

    private void setRecordButton() {
        recordButton.setCompoundDrawablesWithIntrinsicBounds(
                0,
                R.drawable.btn_voice_record,
                0,
                0);
        recordButton.setText(R.string.video_recording_start_title);

        recordButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isStarted == false) {
                    cameraSurfaceView.stopPreview();
                    prepareVideoRecording();

                    mediaRecorder.start();
                    isStarted = true;
                    recordButton.setCompoundDrawablesWithIntrinsicBounds(
                            0,
                            R.drawable.btn_voice_stop,
                            0,
                            0);
                    recordButton.setText(R.string.video_recording_stop_title);
                }
                else {
                    mediaRecorder.stop();
                    mediaRecorder.release();
                    mediaRecorder = null;
                    isStarted = false;
                    cameraSurfaceView.startPreview();

                    setResult(RESULT_OK);

                    recordButton.setCompoundDrawablesWithIntrinsicBounds(
                            0,
                            R.drawable.btn_voice_record,
                            0,
                            0);
                    recordButton.setText(R.string.video_recording_start_title);
                }
            }
        });
    }

    private void prepareVideoRecording() {
        checkVideoFolder();

        String videoName = BasicInfo.FOLDER_VIDEO + "recorded";

        if (mediaRecorder == null) {
            mediaRecorder = new MediaRecorder();
        }
        else {
            mediaRecorder.reset();
        }

        mediaRecorder.setVideoSource(MediaRecorder.VideoSource.CAMERA);
        mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
        mediaRecorder.setVideoEncoder(MediaRecorder.VideoEncoder.DEFAULT);
        mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.DEFAULT);

        mediaRecorder.setVideoFrameRate(30);
        mediaRecorder.setOutputFile(videoName);

        mediaRecorder.setPreviewDisplay(cameraSurfaceView.getSurface());

        try {
            mediaRecorder.prepare();
        }
        catch(IllegalStateException ise) {
            toast("IllegalStateException");
        }
        catch(IOException ioe) {
            toast("IOException");
        }
    }

    private void checkVideoFolder() {
        File videoFolder = new File(BasicInfo.FOLDER_VIDEO);
        if (!videoFolder.isDirectory()) {
            print("creating video folder : " + videoFolder);

            videoFolder.mkdirs();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (mediaRecorder != null) {
            mediaRecorder.release();
            mediaRecorder = null;
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (isStarted) {
                toast(getResources().getString(R.string.video_recording_message));
            }
            else {
                finish();
            }
            return true;
        }
        return false;
    }

    private void print(String message) {
        Log.d(TAG, message + "\n");
    }

    private void toast(String message) {
        Toast.makeText(VideoRecordingActivity.this,
                message, Toast.LENGTH_LONG).show();
    }
}
