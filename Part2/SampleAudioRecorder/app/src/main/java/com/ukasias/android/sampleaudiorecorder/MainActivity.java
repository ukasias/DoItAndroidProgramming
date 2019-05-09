package com.ukasias.android.sampleaudiorecorder;

import android.content.ContentValues;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

public class MainActivity extends AppCompatActivity {

    private final String TAG = "SampleAudioRecorder";
    private final String RECORDED_FILE = "/sdcard/recorded.mp4";

    private Button recordButton;
    private Button stopRecordButton;
    private Button playButton;
    private Button stopPlayButton;

    private MediaPlayer player;
    private MediaRecorder recorder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        recordButton = findViewById(R.id.recordButton);
        stopRecordButton = findViewById(R.id.stopRecordButton);
        playButton = findViewById(R.id.playButton);
        stopPlayButton = findViewById(R.id.stopPlayButton);

        recordButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                recordAudio();
            }
        });
        stopRecordButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                stopRecordAudio();
            }
        });
        playButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                playAudio();
            }
        });
        stopPlayButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                stopPlayAudio();
            }
        });
    }

    private void recordAudio() {
        if (recorder != null) {
            recorder.stop();
            recorder.release();
            recorder = null;
        }

        recorder = new MediaRecorder();

        recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        recorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
        recorder.setAudioEncoder(MediaRecorder.AudioEncoder.DEFAULT);
        recorder.setOutputFile(RECORDED_FILE);

        try {
            recorder.prepare();
            recorder.start();
            print("녹음을 시작합니다.");
        }
        catch(IOException e) {
            e.printStackTrace();
        }
    }
    private void stopRecordAudio() {
        if (recorder == null) {
            return;
        }

        ContentValues values = new ContentValues();
        values.put(MediaStore.MediaColumns.TITLE, "Recorded");
        values.put(MediaStore.Audio.Media.ALBUM, "Audio Album");
        values.put(MediaStore.Audio.Media.ARTIST, "Mike");
        values.put(MediaStore.Audio.Media.DISPLAY_NAME, "Recorded Audio");
        values.put(MediaStore.Audio.Media.IS_RINGTONE, 1);
        values.put(MediaStore.Audio.Media.IS_MUSIC, 1);
        values.put(MediaStore.MediaColumns.DATE_ADDED,
                System.currentTimeMillis() / 1000);
        values.put(MediaStore.MediaColumns.MIME_TYPE, "audio/mp4");
        values.put(MediaStore.Audio.Media.DATA, RECORDED_FILE);

        Uri audioUri = getContentResolver().insert(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, values);

        if (audioUri == null) {
            Log.d(TAG, "stopRecordAudio() : audio insert failed.");
            print("녹음을 중단합니다. 오디오를 Media Store에 저장하지 못헀습니다.");
        }
        else {
            Log.d(TAG, "stopRecordAudio() : audio insert succeeded.");
            print("녹음을 중단합니다. 오디오를 Media Store에 저장하였습니다.");
        }

        /*
        Log.d(TAG, "Try to find a file");
        File file = new File(RECORDED_FILE);
        Log.d(TAG, "FILE: " + file.getAbsolutePath() + " found.");
        if (file.exists()) {
            Log.d(TAG, RECORDED_FILE + " file exists");
            // set to Readable MODE_WORLD_READABLE
            file.setReadable(true, false);
            file.setWritable(true, false);
        }
        else {
            Log.d(TAG, RECORDED_FILE + " file doesn't exist");
        }
        */

        recorder.stop();
        recorder.release();
        recorder = null;
    }
    private void playAudio() {
        if (player != null) {
            player.stop();
            player.release();
            player = null;
        }

        try {
            player = new MediaPlayer();
            player.setDataSource(RECORDED_FILE);
            player.prepare();
            player.start();
            player.seekTo(0);
            print("오디오를 재생합니다.");
        }
        catch(IOException ioe) {
            ioe.printStackTrace();
        }
        catch(Exception e) {
            e.printStackTrace();
        }
    }
    private void stopPlayAudio() {
        if (player == null) {
            return;
        }

        player.stop();
        player.release();
        player = null;
    }

    private void print(String message) {
        Toast.makeText(getApplicationContext(),
                message,
                Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (recorder != null) {
            recorder.stop();
            recorder.release();
            recorder = null;
        }

        if (player != null) {
            player.stop();
            player.release();
            player = null;
        }
    }
}

