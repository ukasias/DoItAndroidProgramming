package com.ukasias.android.samplevideoplayer;

import android.media.AudioManager;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.MediaController;
import android.widget.VideoView;

import java.net.URI;

public class MainActivity extends AppCompatActivity {
    private static final String VIDEO_URL =
            "http://sites.google.com/site/ubiaccessmobile/sample_video.mp4";

    private Button playButton;
    private Button volumeButton;

    private VideoView vView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        playButton = findViewById(R.id.playbackButton);
        volumeButton = findViewById(R.id.volumeButton);

        vView = findViewById(R.id.videoView);

        playButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                playVideo();
            }
        });
        volumeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                makeVolumeToMax();
            }
        });

        MediaController mediaController = new MediaController(this);
        vView.setMediaController(mediaController);
        vView.setVideoURI(Uri.parse(VIDEO_URL));

        vView.requestFocus();
    }

    private void playVideo() {
        if (vView != null) {
            vView.start();
            vView.seekTo(0);

        }
    }

    private void makeVolumeToMax() {
        AudioManager audioManager = (AudioManager)
                getSystemService(AUDIO_SERVICE);
        int maxVolume = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
        audioManager.setStreamVolume(AudioManager.STREAM_MUSIC,
                maxVolume, AudioManager.FLAG_SHOW_UI);
    }
}
