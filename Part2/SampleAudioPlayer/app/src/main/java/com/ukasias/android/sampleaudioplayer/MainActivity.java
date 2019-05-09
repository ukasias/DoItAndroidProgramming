package com.ukasias.android.sampleaudioplayer;

import android.media.MediaPlayer;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.io.IOException;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";

    Button startButton;
    Button pauseButton;
    Button restartButton;

    static final String AUDIO_URL =
            "http://sites.google.com/site/ubiaccessmobile/sample_audio.amr";
    private MediaPlayer mediaPlayer;
    private int playbackPosition;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        startButton = findViewById(R.id.start);
        pauseButton = findViewById(R.id.pause);
        restartButton = findViewById(R.id.restart);

        mediaPlayer = null;
        playbackPosition = 0;

        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startAudio();
            }
        });

        pauseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pauseAudio();
            }
        });

        restartButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                restartAudio();
            }
        });
    }

    private void startAudio() {
        killMediaPlayer();

        mediaPlayer = new MediaPlayer();
        if (mediaPlayer != null) {
            try {
                mediaPlayer.setDataSource(AUDIO_URL);
                mediaPlayer.prepare();
                mediaPlayer.start();

                print("음악 파일 재생 시작됨");
            }
            catch (IOException ioe) {
                ioe.printStackTrace();
            }
        }
    }

    private void pauseAudio() {
        if (mediaPlayer != null && mediaPlayer.isPlaying()) {
            mediaPlayer.pause();
            playbackPosition = mediaPlayer.getCurrentPosition();

            print("음악 파일 재생 중지됨");
        }
    }

    private void restartAudio() {
        if (mediaPlayer != null &&
                !mediaPlayer.isPlaying()) {
            mediaPlayer.start();
            mediaPlayer.seekTo(playbackPosition);
        }
    }

    private void killMediaPlayer() {
        if (mediaPlayer != null) {
            try {
                mediaPlayer.release();
            }
            catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void print(String str) {
        Toast.makeText(getApplicationContext(),
                str,
                Toast.LENGTH_LONG).show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        killMediaPlayer();
    }
}
