package com.ukasias.android.multimemo;

import android.annotation.TargetApi;
import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.util.Log;

import java.io.File;
import java.io.IOException;

/**
 * 음성 녹음
 */

public class VoiceRecorder implements
        MediaPlayer.OnCompletionListener, MediaPlayer.OnErrorListener {

    public static final String TAG = "VoiceRecorder";

    static final String SAMPLE_PREFIX = "recording";
    static final String SAMPLE_PATH_KEY = "sample_path";
    static final String SAMPLE_LENGTH_KEY = "sample_length";

    public static final int IDLE_STATE = 0;
    public static final int RECORDING_STATE = 1;
    public static final int PLAYING_STATE = 2;

    private int state;

    public static final int NO_ERROR = 0;
    public static final int SDCARD_ACCESS_ERROR = 1;
    public static final int INTERNAL_ERROR = 2;
    public static final int IN_CALL_RECORD_ERROR = 3;

    /**
     * Temporary storage used in case of invalid SD card
     */
    public static final String TEMP_STORAGE
            = "/data/data/com.ukasias.android.multimemo/voice/";

    public interface OnStateChangedListener {
        public void onStateChanged(int state);
        public void onError(int error);
    }

    private OnStateChangedListener stateChangedListener;

    private long sampleStart; // 녹음 또는 재생한 가장 최근의 시작 시간.
    private int sampleLength; // 현재 sample의 길이

    private File sampleFile;

    private MediaRecorder mediaRecorder;
    private MediaPlayer mediaPlayer;

    public VoiceRecorder() {
        state = IDLE_STATE;
        stateChangedListener = null;
        sampleStart = 0;
        sampleLength = 0;
        sampleFile = null;
        mediaRecorder = null;
        mediaPlayer = null;
    }

    public void saveState(Bundle recorderState) {
        recorderState.putString(SAMPLE_PATH_KEY, sampleFile.getAbsolutePath());
        recorderState.putInt(SAMPLE_LENGTH_KEY, sampleLength);
    }

    public void restoreState(Bundle recorderState) {
        String samplePath = recorderState.getString(SAMPLE_PATH_KEY);
        if (samplePath == null) {
            return;
        }

        int sampleLength = recorderState.getInt(SAMPLE_LENGTH_KEY);
        if (sampleLength == -1) {
            return;
        }

        File file = new File(samplePath);
        if (!file.exists()) {
            return;
        }

        if (sampleFile != null &&
                sampleFile.getAbsolutePath().compareTo(file.getAbsolutePath()) == 0) {
            return; // 같으면 0
        }

        delete();
        sampleFile = file;
        this.sampleLength = sampleLength;

        signalStateChanged(IDLE_STATE);
    }

    public void setOnStateChangedListener(OnStateChangedListener listener) {
        stateChangedListener = listener;
    }

    public int getState() {
        return state;
    }

    public int getSampleLength() {
        return sampleLength;
    }

    public File getSampleFile() {
        return sampleFile;
    }

    private void delete() {
        stop();

        if (sampleFile != null) {
            sampleFile.delete();
        }

        sampleFile = null;
        sampleLength = 0;

        signalStateChanged(IDLE_STATE);
    }

    public void clear() {
        stop();

        sampleLength = 0;
        signalStateChanged(IDLE_STATE);
    }

    public int progress() {
        if (state == RECORDING_STATE || state == PLAYING_STATE) {
            return (int)((System.currentTimeMillis() - sampleStart) / 1000);
        }

        return 0;
    }

    public void startRecording(int outputfileFormat, String extension, Context context) {
        stop();

        if (sampleFile == null) {
            File sampleDir = new File(BasicInfo.FOLDER_VOICE);
            if (!sampleDir.canWrite()) {
                // when sdcard is not available.
                sampleDir = new File(TEMP_STORAGE);

                if (!sampleDir.isDirectory()) {
                    print("Creating recording folder: " + sampleDir.toString());

                    sampleDir.mkdirs();
                }
            }

            try {
                sampleFile = File.createTempFile(
                        SAMPLE_PREFIX,
                        extension,
                        sampleDir);
                print("SampleFile: " + sampleFile.toString());
            }
            catch(IOException ioe) {
                setError(SDCARD_ACCESS_ERROR);
                return;
            }

            mediaRecorder = new MediaRecorder();
            mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
            mediaRecorder.setOutputFormat(outputfileFormat);
            mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
            mediaRecorder.setOutputFile(sampleFile.getAbsolutePath());

            // PREPARE
            try {
                mediaRecorder.prepare();
            }
            catch(IOException ioe) {
                setError(INTERNAL_ERROR);
                mediaRecorder.reset();
                mediaRecorder.release();
                mediaRecorder = null;
                return;
            }


            // START
            try {
                mediaRecorder.start();
            }
            catch (RuntimeException re) {
                AudioManager audioManager =
                        (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
                boolean isInCall = audioManager.getMode() == AudioManager.MODE_IN_CALL;
                if (isInCall) {
                    setError(IN_CALL_RECORD_ERROR);
                }
                else {
                    setError(INTERNAL_ERROR);
                }
                mediaRecorder.reset();
                mediaRecorder.release();
                mediaRecorder = null;
                return;
            }

            sampleStart = System.currentTimeMillis();
            setState(RECORDING_STATE);
        }
    }

    public void stopRecording() {
        if (mediaRecorder == null) {
            return;
        }

        mediaRecorder.stop();
        mediaRecorder.release();
        mediaRecorder = null;

        sampleLength = (int) (System.currentTimeMillis() - sampleStart) / 1000;

        setState(IDLE_STATE);
    }

    public void startPlayback() {
        stop();

        mediaPlayer = new MediaPlayer();

        try {
            mediaPlayer.setDataSource(sampleFile.getAbsolutePath());
            mediaPlayer.setOnCompletionListener(this);
            mediaPlayer.setOnErrorListener(this);
            mediaPlayer.prepare();
            mediaPlayer.start();
        }
        catch (IllegalStateException ise) {
            setError(INTERNAL_ERROR);
            mediaPlayer = null;
            return;
        }
        catch (IOException ioe) {
            setError(SDCARD_ACCESS_ERROR);
            mediaPlayer = null;
            return;
        }

        sampleStart = System.currentTimeMillis();
        setState(PLAYING_STATE);
    }

    public void stopPlayback() {
        if (mediaPlayer == null) {
            return;
        }

        mediaPlayer.stop();
        mediaPlayer.release();
        mediaPlayer = null;
        setState(IDLE_STATE);
    }

    public void stop() {
        stopRecording();
        stopPlayback();
    }

    @Override
    public void onCompletion(MediaPlayer mp) {
        stop();
    }

    @Override
    public boolean onError(MediaPlayer mp, int what, int extra) {
        stop();
        setError(SDCARD_ACCESS_ERROR);
        return true;
    }

    private void signalStateChanged(int state) {
        if (stateChangedListener != null) {
            stateChangedListener.onStateChanged(state);
        }
    }

    private void setError(int error) {
        if (stateChangedListener != null) {
            stateChangedListener.onError(error);
        }
    }

    private void setState(int state) {
        if (state == this.state) {
            return;
        }
        this.state = state;
        signalStateChanged(state);
    }

    private void print(String message) {
        Log.d(TAG, message + "\n");
    }
}
