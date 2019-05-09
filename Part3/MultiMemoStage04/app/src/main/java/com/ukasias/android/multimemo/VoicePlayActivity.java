package com.ukasias.android.multimemo;

import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.ukasias.android.multimemo.common.TitleBitmapButton;

import org.w3c.dom.Text;

import java.io.IOException;

public class VoicePlayActivity extends AppCompatActivity {
    private final static String TAG = "VoicePlayActivity";

    TitleBitmapButton playStopButton;
    TitleBitmapButton playCloseButton;

    TextView playTimeText;
    TextView totalTimeText;

    ProgressBar progressBar;

    MediaPlayer player;
    VoiceRecordingActivity.RemainingTimeCalculator remainingTimeCalculator;

    int time;
    boolean isPlaying;
    boolean isHolding;
    int curTime;    // play 중 멈춘 경우, 이 때의 시간을 임시 보관함.

    String voicePath;
    Handler handler;
    Runnable updateTimer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_voice_play);
        setTitle(R.string.audio_play_title);

        init();

        setUIs();

        getVoicePath();

        /* Activity 시작되면 재생 시작함 */
        isPlaying = true;
        isHolding = false;
        curTime = 0;
        playStopButton.setBackgroundBitmap(
                R.drawable.btn_voice_pause,
                R.drawable.btn_voice_pause);

        startPlayback(voicePath);
    }

    private void init() {
        player = null;
        handler = new Handler();
        updateTimer = new Runnable() {
            @Override
            public void run() {
                if (isPlaying && (time <= player.getDuration() / 1000)) {
                    if (time > 0) {
                        // progress bar의 표시를 1 늘림
                        progressBar.incrementProgressBy(1);
                    }
                    // text 표시를 업데이트함.
                    updateTimerView();
                }
                else {
                    // 실행 중이 아닌 경우, 이미 실행 완료된 경우이다.
                    progressBar.setProgress(progressBar.getMax());
                    if (progressBar.getProgress() == progressBar.getMax()) {
                        isPlaying = false;
                        isHolding = false;

                        stop();
                    }
                }
            }
        };

        player = new MediaPlayer();
        int duration = player.getDuration();
    }

    private void setUIs() {
        playStopButton = findViewById(R.id.playStopButton);
        playStopButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isPlaying && !isHolding) { /* 재생 중일 때 -> 재생을 멈춘다 (hold) */
                    isHolding = true;
                    isPlaying = false;

                    player.pause();
                    playStopButton.setBackgroundBitmap(
                            R.drawable.btn_voice_play,
                            R.drawable.btn_voice_play);
                    curTime = progressBar.getProgress();
                    handler.removeCallbacks(updateTimer);
                }
                else if(isHolding) {  /* 재생 중간에 멈춰 있을 때 -> 다시 재생한다. */
                    isHolding = false;
                    isPlaying = true;

                    player.start();
                    playStopButton.setBackgroundBitmap(
                            R.drawable.btn_voice_pause,
                            R.drawable.btn_voice_pause);
                    progressBar.setProgress(curTime);
                    handler.post(updateTimer);
                }
                else {  /* 재생 완료 혹은 재생 시작 전일 때: isPlaying/isHolding 둘 다 false
                            -> 재생한다. */
                    isPlaying = true;

                    progressBar.setProgress(0);
                    startPlayback(voicePath);
                }
            }
        });

        playCloseButton = findViewById(R.id.playCloseButton);
        playCloseButton.setText(R.string.close_button);
        playCloseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stop();
                finish();
            }
        });

        playTimeText = findViewById(R.id.playTimeText);
        totalTimeText = findViewById(R.id.totalTimeText);

        progressBar = findViewById(R.id.progressBar);
        progressBar.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                int action = event.getAction();

                switch(action) {
                    case MotionEvent.ACTION_UP:
                        v.performClick();
                        break;
                    case MotionEvent.ACTION_DOWN:
                        handleProgressBar(event);
                        break;
                }
                return true;
            }
        });
    }

    private void handleProgressBar(MotionEvent event) {
        print("handProgressBar(): (x, y) - (" + event.getX() + ", " + event.getY() + ")");

        // calc %
        int progressWidth = progressBar.getWidth();
        float currentX = event.getX();
        float currentOffset = (currentX / (float) progressWidth); // UI내에서의 offset (0 ~ 1.0)

        if (currentOffset > 0.0f && currentOffset < 1.0f) {
            if (player != null) {
                /**
                 * getDuration()에서 넘어오는 값은 1초는 1000이다.
                 */
                float offsetFloat = ((float)player.getDuration()) * currentOffset;

                if (isPlaying) {
                    stop();
                    time = 0;
                    player = new MediaPlayer();
                    try {
                        player.setDataSource(voicePath);
                        player.prepare();
                        progressBar.setMax(player.getDuration()/1000);
                        progressBar.setProgress(new Float(offsetFloat/1000.0f).intValue());
                        player.seekTo(new Float(offsetFloat).intValue());
                        time = new Float(offsetFloat/1000.0).intValue();
                        player.start();
                        playStopButton.setBackgroundBitmap(
                                R.drawable.btn_voice_pause, R.drawable.btn_voice_pause);

                        handler.post(updateTimer);
                    }
                    catch(IllegalArgumentException e) {
                        print("IllegalArgumentException in ProgressBar OnTouch()");
                        player = null;
                    }
                    catch(IOException ioe) {
                        print("IOException in ProgressBar OnTouch()");
                        player = null;
                    }
                }
            }
        }
    }

    private void getVoicePath() {
        Intent intent = getIntent();
        voicePath = intent.getStringExtra(BasicInfo.KEY_URI_VOICE);
    }


    public void startPlayback(String path) {
        stop();
        time = 0;

        player = new MediaPlayer();

        try {
            player.setDataSource(path);
            player.prepare();

            int duration = player.getDuration()/1000;
            progressBar.setMax(duration);  /* 지속 시간을 max로 잡음 */
            String timeFormat = "%02d:%02d";
            String timeStr = String.format(timeFormat, duration / 60, duration % 60);
            totalTimeText.setText(timeStr);

            player.start();
            playStopButton.setBackgroundBitmap(
                    R.drawable.btn_voice_pause,
                    R.drawable.btn_voice_pause);
        }
        catch(IllegalArgumentException iae) {
            print("IllegalArgumentException occured in startPlayback()");
            return;
        }
        catch(IOException ioe) {
            print("IOException occured in startPlayback()");
            return;
        }

        handler.post(updateTimer);  // 시작j
    }

    public void stopPlayback() {
        if (player == null) {
            return;
        }

        player.stop();
        player.release();
        player = null;
        playStopButton.setBackgroundBitmap(
                R.drawable.btn_voice_play,
                R.drawable.btn_voice_play);
    }

    public void stop() {
        stopPlayback();

        progressBar.setProgress(0);
        handler.removeCallbacks(updateTimer);
        playTimeText.setText("00:00");
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        stop();
        finish();
    }

    @Override
    protected void onPause() {
        super.onPause();
        handler.removeCallbacks(updateTimer);
    }

    /**
     * handler는 handler의 post(), postDelayed() method를 통해 Runnable 객체를 받아 실행해주는 역할
     *
     * 전달되는 Runnable 객체는 실행 후, handler에 다시 postDelayed()를 실행함으로써, 일정 시간 후,
     * 다시 실행되도록 함.
     */

    private void updateTimerView() {
        String timerFormat = "%02d:%02d";
        String timeStr = String.format(timerFormat, time / 60, time % 60);
        playTimeText.setText(timeStr);

        time++;

        handler.postDelayed(updateTimer, 1000);
    }

    private void print(String message) {
        Log.d(TAG, message + "\n");
    }
}
