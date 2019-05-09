package com.ukasias.android.multimemo;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.media.MediaRecorder;
import android.os.Environment;
import android.os.Handler;
import android.os.PersistableBundle;
import android.os.PowerManager;
import android.os.StatFs;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.TextView;

import com.ukasias.android.multimemo.common.TitleBitmapButton;

import java.io.File;

public class VoiceRecordingActivity extends AppCompatActivity implements VoiceRecorder.OnStateChangedListener {
    private final static String TAG = "VoiceRecordingActivity";

    private TextView textView;
    private TitleBitmapButton recordButton;
    private TitleBitmapButton cancelButton;

    boolean isStarted;
    RemainingTimeCalculator remainingTimeCalculator;

    private static final String AUDIO_3GPP = "audio/3gpp";
    private static final String AUDIO_AMR = "audio/amr";
    private static final String AUDIO_ANY = "audio/*";
    private static final String ANY_ANY = "*/*";

    private static final int BITRATE_AMR   = 5900;
    private static final int BITRATE_3GPP  = 5900;

    private static final int WARNING_INSERT_SDCARD = 1011;
    private static final int WARNING_DISK_SPACE_FULL = 1012;
    private static final int RECORDING_START = 1013;

    private static final String STATE_FILE_NAME = "soundrecorder.state";
    private static final String RECORDER_STATE_KEY = "recorder_state";
    private static final String SAMPLE_INTERRUPTED_KEY = "sample_interrupted";
    private static final String MAX_FILE_SIZE_KEY = "max_file_size";

    private static final int RECORDING_IDLE = 0;
    private static final int RECORDING_RUNNING = 1;

    String requestedType = AUDIO_3GPP;
    VoiceRecorder recorder;
    boolean sampleInterrupted;

    long maxFileSize = -1;

    PowerManager.WakeLock wakeLock;
    long recordingTime;

    String timerFormat;
    Handler handler = new Handler();
    Runnable updateTimer = new Runnable() {
        @Override
        public void run() {
            updateTimerView(textView);
        }
    };
    private int recordingState;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        print("onCreate()");

        requestWindowFeature(Window.FEATURE_NO_TITLE);

        setContentView(R.layout.activity_voice_recording);

        setBottomButtons();

        init(savedInstanceState);
    }

    private void setBottomButtons() {
        print("setBottomButtons()");

        isStarted = false;
        recordButton = findViewById(R.id.recordButton);
        cancelButton = findViewById(R.id.cancelButton);

        recordButton.setCompoundDrawablesWithIntrinsicBounds(
                0,
                R.drawable.btn_voice_record,
                0, 0);
        recordButton.setText(R.string.audio_recording_start_title);
        recordButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isStarted) {
                    stopVoiceRecording();
                    isStarted = false;
                }
                else {
                    startVoiceRecording();
                    isStarted = true;
                }
            }
        });

        cancelButton.setCompoundDrawablesWithIntrinsicBounds(0,
                R.drawable.cancel_btn, 0 , 0);
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void init(Bundle icycle) {
        print("init()");

        textView = findViewById(R.id.textView);
        textView.setText("00:00");

        sampleInterrupted = false;

        recorder = new VoiceRecorder();
        recorder.setOnStateChangedListener(this);

        remainingTimeCalculator = new RemainingTimeCalculator();
        PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
        wakeLock = pm.newWakeLock(PowerManager.SCREEN_DIM_WAKE_LOCK, "MediGalaxy");

        if (icycle != null) {
            Bundle recorderState = icycle.getBundle(RECORDER_STATE_KEY);
            if (recorderState != null) {
                recorder.restoreState(recorderState);
                sampleInterrupted =
                        recorderState.getBoolean(SAMPLE_INTERRUPTED_KEY, false);
                maxFileSize = recorderState.getLong(MAX_FILE_SIZE_KEY, -1);
            }
        }

        timerFormat = "%02d:%02d";

        updateUi(textView);
    }

    private void updateUi(TextView textView) {
        updateTimerView(textView);
    }

    private void updateTimerView(TextView textView) {
        Resources res = getResources();
        int state = recorder.getState();

        boolean ongoing =
                (state == VoiceRecorder.RECORDING_STATE ||
                 state == VoiceRecorder.PLAYING_STATE);
        long time = ongoing? recorder.progress() : recorder.getSampleLength();

        String timeStr = String.format(timerFormat, time/60, time%60);

        textView.setText(timeStr);
        recordingTime = time;

        if (state == VoiceRecorder.PLAYING_STATE) {
        } else if (state == VoiceRecorder.RECORDING_STATE) {
            updateTimeRemaining();
        }

        if (ongoing) {
            handler.postDelayed(updateTimer, 1000);
        }
    }

    private void updateTimeRemaining() {
        long t = remainingTimeCalculator.timeRemaining();

        if (t <= 0) {
            sampleInterrupted = true;

            recorder.stop();
            return;
        }

        String timeStr ="";
        timeStr = String.format("%d seconds remained..", t);

        textView.setText(timeStr);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        print("onConfigurationChanged()");

        super.onConfigurationChanged(newConfig);
        updateUi(textView);
    }

    @Override
    public void onSaveInstanceState(Bundle outState, PersistableBundle outPersistentState) {
        print("onSaveInstanceState()");

        super.onSaveInstanceState(outState, outPersistentState);

        if (recorder.getSampleLength() == 0) {
            return;
        }

        Bundle recorderState = new Bundle();

        recorder.saveState(recorderState);
        recorderState.putBoolean(SAMPLE_INTERRUPTED_KEY, sampleInterrupted);
        recorderState.putLong(MAX_FILE_SIZE_KEY, maxFileSize);

        outState.putBundle(RECORDER_STATE_KEY, recorderState);
    }

    @Override
    public void onStateChanged(int state) {
        print("onStateChanged(state: " + state);
        if (state == VoiceRecorder.PLAYING_STATE ||
                state == VoiceRecorder.RECORDING_STATE) {
            sampleInterrupted = false;
        }

        if (state == VoiceRecorder.RECORDING_STATE) {
            wakeLock.acquire();
        }
        else {
            if (wakeLock.isHeld()) {
                wakeLock.release();
            }
        }
        updateUi(textView);
    }

    @Override
    public void onError(int error) {
        String message = null;

        switch (error) {
            case VoiceRecorder.SDCARD_ACCESS_ERROR:
                message = "SD 접근 오류입니다.";
                break;
            case VoiceRecorder.IN_CALL_RECORD_ERROR:
            case VoiceRecorder.INTERNAL_ERROR:
                message = "내부 오류입니다.";
                break;
        }

        if (message != null) {
            new AlertDialog.Builder(this)
                    .setTitle(R.string.audio_recording_title)
                    .setMessage(message)
                    .setPositiveButton("확인", null)
                    .setCancelable(false)
                    .show();
        }
    }


    private void startVoiceRecording() {
        voiceRecordingStart();

        recordButton.setText(R.string.video_recording_stop_title);
        recordButton.setCompoundDrawablesWithIntrinsicBounds(0,
                R.drawable.btn_voice_stop, 0, 0);
        textView.setText("00:00");
    }

    private void voiceRecordingStart() {
        remainingTimeCalculator.reset();

        if (!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            sampleInterrupted = true;
            showDialog(WARNING_INSERT_SDCARD);

            stopAudioPlayback();

            if (AUDIO_AMR.equals(requestedType)) {
                remainingTimeCalculator.setBitRate(BITRATE_AMR);
                recorder.startRecording(
                        MediaRecorder.OutputFormat.RAW_AMR,
                        ".amr",
                        this);
            }
            else if (AUDIO_3GPP.equals(requestedType)) {
                remainingTimeCalculator.setBitRate(BITRATE_3GPP);
                recorder.startRecording(
                        MediaRecorder.OutputFormat.THREE_GPP,
                        ".3gpp", this);
            }
            else {
                throw new IllegalArgumentException("Invalid output file type requested..");
            }

            if (maxFileSize != -1) {
                remainingTimeCalculator.setFileSizeLimit(
                        recorder.getSampleFile(),
                        maxFileSize);
            }
        }
        else if (!remainingTimeCalculator.isDiskSpaceAvailable()) {
            sampleInterrupted = true;
            showDialog(WARNING_DISK_SPACE_FULL);
        }
        else {
            stopAudioPlayback();

            if (AUDIO_AMR.equals(requestedType)) {
                remainingTimeCalculator.setBitRate(BITRATE_AMR);
                recorder.startRecording(
                        MediaRecorder.OutputFormat.RAW_AMR,
                        ".amr",
                        this);
            }
            else if (AUDIO_3GPP.equals(requestedType)) {
                remainingTimeCalculator.setBitRate(BITRATE_3GPP);
                recorder.startRecording(
                        MediaRecorder.OutputFormat.THREE_GPP,
                        ".3gpp", this);
            }
            else {
                throw new IllegalArgumentException("Invalid output file type requested..");
            }

            if (maxFileSize != -1) {
                remainingTimeCalculator.setFileSizeLimit(
                        recorder.getSampleFile(),
                        maxFileSize);
            }
        }

        recordingState = RECORDING_RUNNING;
    }

    private void stopVoiceRecording() {
        recorder.stop();

        recordButton.setText(R.string.audio_recording_start_title);
        recordButton.setCompoundDrawablesWithIntrinsicBounds(0,
                R.drawable.btn_voice_record, 0 , 0);

        File tempFile = recorder.getSampleFile();
        saveRecording(tempFile);

        recordingState = RECORDING_IDLE;
    }

    private void saveRecording(File tempFile) {
        checkVoiceFolder();

        String voiceName = "recorded";

        try {
            tempFile.renameTo(new File(BasicInfo.FOLDER_VOICE + voiceName));

            setResult(RESULT_OK);
        }
        catch (Exception ex) {
            print("Exception in storing recording...");
        }

        if (voiceName != null) {
            print("Recording file saved to : " + voiceName);
        }
    }

    private void checkVoiceFolder() {
        File voiceFolder = new File(BasicInfo.FOLDER_VOICE);
        if (!voiceFolder.isDirectory()) {
            print("creating voice folder: " + voiceFolder);

            voiceFolder.mkdirs();
        }
    }

    private void stopAudioPlayback() {
        Intent i = new Intent("com.android.music.musicservicecommand");
        i.putExtra("command", "pause");

        sendBroadcast(i);
    }

    private void print(String message) {
        Log.d(TAG, message + "\n");
    }

    class RemainingTimeCalculator {
        public static final int UNKNOWN_LIMIT = 0;
        public static final int FILE_SIZE_LIMIT = 1;
        public static final int DISK_SPACE_LIMIT = 2;

        private int currentLowerLimit;

        private File sdcardDirectory;
        private File recordingFile;

        private long maxBytes;

        //  초당 bytes 수
        private int bytesPerSecond;

        // free block이 마지막으로 바뀌었을 때의 시간
        private long blocksChangedTime;

        // 위의 시간에 이용 가능한 block의 수
        private long lastBlocks;

        // 파일 크기가 마지막으로 바뀌었을 때의 시간
        private long fileSizeChangedTime;

        // 마지막으로 파일 크기가 바뀌었을 때의 파일 크기
        private long lastFileSize;

        RemainingTimeCalculator() {
            currentLowerLimit = UNKNOWN_LIMIT;

            sdcardDirectory = Environment.getExternalStorageDirectory();
        }

        public void setFileSizeLimit(File file, long maxBytes) {
            recordingFile = file;
            this.maxBytes = maxBytes;
        }

        public int getCurrentLowerLimit() {
            return currentLowerLimit;
        }

        public void reset() {
            currentLowerLimit = UNKNOWN_LIMIT;
            blocksChangedTime = -1;
            fileSizeChangedTime = -1;
        }

        /**
         * 초당 bitrate 설정
         */
        public void setBitRate(int bitRate) {
            bytesPerSecond = (bitRate / 8);
        }

        /**
         * 디렉토리에 한 block 이상 존재하면 true.
         */
        public boolean isDiskSpaceAvailable() {
            StatFs statFs = new StatFs(sdcardDirectory.getAbsolutePath());

            return statFs.getAvailableBlocks() > 1;
        }

        /**
         * 얼마나 더 오래 녹음할 수 있는지를 계산한다.
         */
        public long timeRemaining() {
            StatFs fs = new StatFs(sdcardDirectory.getAbsolutePath());
            long blocks = fs.getAvailableBlocks();
            long blockSize = fs.getBlockSize();
            long now = System.currentTimeMillis();

            if (blocksChangedTime == -1 || blocks != lastBlocks) {
                blocksChangedTime = now;
                lastBlocks = blocks;
            }

            /**
             * 아래 계산은 항상 한 개의 free block을 남겨둔다. 우리가 쓰고 있는 block의 free space는
             * 포함되지 않기 때문이다. 우리가 파일을 닫고, flush할 때 마지막 block이 여전히 뜯어먹힐텐데, disk
             * 용량을 벗어나지는 않을 것이다.
             */
            long result = lastBlocks * blockSize / bytesPerSecond; /* 초 */
            result -= (now - blocksChangedTime) / 1000;

            if (recordingFile == null) {
                currentLowerLimit = DISK_SPACE_LIMIT;

                return result;
            }

            /**
             * 녹음 파일이 설정되어 있을 때는 위의 단순 free block에 의한 시간 계산 만이 아니고, 다음의
             * 추정치를 추가 계산한다.
             * maxBytes까지 도달할 때까지의 시간 계산을 추가한다. 둘 중 더 작은 값을 return 한다.
             */

            recordingFile = new File(recordingFile.getAbsolutePath());
            long fileSize = recordingFile.length();
            if (fileSizeChangedTime == -1 || fileSize != lastFileSize) {
                fileSizeChangedTime = now;
                lastFileSize = fileSize;
            }

            long result2 = (maxBytes - fileSize) / bytesPerSecond;
            result2 -= (now - fileSizeChangedTime) / 1000;
            result2 -= 1;

            /**
             * 더 작은 쪽에 대한 limit으로 정한다.
             */
            currentLowerLimit = result < result2?
                    DISK_SPACE_LIMIT : FILE_SIZE_LIMIT;

            return Math.min(result, result2);
        }
    }
}
