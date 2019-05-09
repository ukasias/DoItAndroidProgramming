package com.ukasias.android.multimemo;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;
import android.widget.MediaController;
import android.widget.VideoView;

public class VideoPlayActivity extends AppCompatActivity {
    private static final String TAG = "VideoPlayActivity";

    private VideoView videoView;
    private String videoUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        final Window win = getWindow();
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        win.setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_video_play);

        setVideoView();

        setMediaController();
    }

    private void setVideoView() {
        videoView = findViewById(R.id.videoView);

        Intent intent = getIntent();
        videoUri = intent.getStringExtra(BasicInfo.KEY_URI_VIDEO);
        videoView.setVideoPath(videoUri);
    }

    private void setMediaController() {
        MediaController mediaController = new MediaController(
                VideoPlayActivity.this);

        videoView.setMediaController(mediaController);
        videoView.start();
    }
}
