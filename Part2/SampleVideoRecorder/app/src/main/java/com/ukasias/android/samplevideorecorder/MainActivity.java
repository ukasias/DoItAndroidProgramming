package com.ukasias.android.samplevideorecorder;

import android.content.ContentValues;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {
    final static String TAG = "SampleVideoRecorder";
    final static String RECORDED_FILE = "/sdcard/videoRecorded.mp4";

    private Button recordButton;
    private Button stopRecordButton;
    private Button playButton;
    private Button stopPlayButton;

    private FrameLayout videoLayout;

    private MediaRecorder recorder;
    private MediaPlayer player;

    SurfaceHolder holder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        recordButton = findViewById(R.id.recordButton);
        stopRecordButton = findViewById(R.id.stopRecordButton);
        playButton = findViewById(R.id.playButton);
        stopPlayButton = findViewById(R.id.stopPlayButton);

        videoLayout = findViewById(R.id.videoLayout);

        recorder = null;
        player = null;

        SurfaceView surfaceView = new SurfaceView(this);
        holder = surfaceView.getHolder();

        videoLayout.addView(surfaceView);

        recordButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startRecord();
            }
        });
        stopRecordButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                stopRecord();
            }
        });
        playButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startPlay();
            }
        });
        stopPlayButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                stopPlay();
            }
        });
    }

    private void startRecord() {
        try {
            if (recorder != null) {
                recorder.stop();
                recorder.reset();
                recorder.release();
                recorder = null;
            }

            recorder = new MediaRecorder();

            recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
            recorder.setVideoSource(MediaRecorder.VideoSource.CAMERA);
            recorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
            recorder.setAudioEncoder(MediaRecorder.AudioEncoder.DEFAULT);
            recorder.setVideoEncoder(MediaRecorder.VideoEncoder.MPEG_4_SP);
            recorder.setOutputFile(RECORDED_FILE);

            recorder.setPreviewDisplay(holder.getSurface());

            recorder.prepare();
            recorder.start();

            print("녹화를 시작합니다.");
        }
        catch(Exception e) {
            print("Exception in startRecord()");
            e.printStackTrace();

            recorder.stop();
            recorder.release();
            recorder = null;
        }
    }

    private void stopRecord() {
        if (recorder == null) {
            return;
        }

        recorder.stop();
        recorder.release();
        recorder = null;

        print("녹화를 중단합니다.");

        ContentValues values = new ContentValues(10);

        values.put(MediaStore.MediaColumns.TITLE, "RecordedVideo");
        values.put(MediaStore.Audio.Media.ALBUM, "Video Album");
        values.put(MediaStore.Audio.Media.ARTIST, "Mike");
        values.put(MediaStore.Audio.Media.DISPLAY_NAME, "Recorded Video");
        values.put(MediaStore.MediaColumns.DATE_ADDED,
                System.currentTimeMillis()/1000);
        values.put(MediaStore.MediaColumns.MIME_TYPE, "video/mp4");
        values.put(MediaStore.Audio.Media.DATA, RECORDED_FILE);

        Uri videoUri = getContentResolver().insert(
                MediaStore.Video.Media.EXTERNAL_CONTENT_URI, values);
        if (videoUri == null) {
            print("Video Insert Failed.");
        }

        sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, videoUri));
    }

    private void startPlay() {
        try {
            if (player != null) {
                player.stop();
                player.reset();
                player.release();
                player = null;
            }

            player = new MediaPlayer();

            player.setDataSource(RECORDED_FILE);

            player.setDisplay(holder);

            player.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mediaPlayer) {
                    stopPlay();
                }
            });

            player.prepare();
            player.start();
            player.seekTo(0);

            print("재생을 시작합니다.");
        }
        catch(Exception e) {
            print("Exception in startRecord()");
            e.printStackTrace();

            player.stop();
            player.release();
            player = null;
        }
    }

    private void stopPlay() {
        if (player == null) {
            return;
        }

        player.stop();
        player.release();
        player = null;

        print("재생을 중단합니다.");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (recorder != null) {
            recorder.stop();
            recorder.reset();
            recorder.release();
            recorder = null;
        }

        if (player != null) {
            player.stop();
            player.reset();
            player.release();
            player = null;
        }
    }

    private void print(String str) {
        Toast.makeText(getApplicationContext(),
                TAG + " " + str + "\n",
                Toast.LENGTH_LONG).show();
    }
}
