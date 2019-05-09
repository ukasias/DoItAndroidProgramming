package com.ukasias.android.multimemo;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TimePicker;

import com.ukasias.android.multimemo.common.TitleBackgroundButton;
import com.ukasias.android.multimemo.common.TitleBitmapButton;
import com.ukasias.android.multimemo.db.MemoDatabase;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;

public class MemoInsertActivity extends AppCompatActivity {
    private final String TAG = "MemoInserActivity";

    private TitleBackgroundButton titleBackgroundButton;
    private ImageView photoView;
    private TitleBitmapButton videoButton;
    private TitleBitmapButton voiceButton;
    private EditText textView;
    private ImageView handView;
    private TitleBitmapButton textButton;
    private TitleBitmapButton handButton;
    private TitleBitmapButton dateButton;
    private TitleBitmapButton timeButton;   /** NEW **/
    private TitleBitmapButton saveButton;
    private TitleBitmapButton deleteButton;
    private TitleBitmapButton cancelButton;

    /**
     * Memo Mode,   Id, Date, Text,
     *              Id_Photo/Uri_Photo,
     *              Id_Video/Uri_Video, Id_Voice/Uri_Voice,
     *              Id_Handwriting/Uri_Handwriting,
     */
    private String memoMode;
    private String memoId;
    private String memoDateTime;
    private String memoText;
    private String memoPhotoId;
    private String memoPhotoUri;
    private String memoVideoId;
    private String memoVideoUri;
    private String memoVoiceId;
    private String memoVoiceUri;
    private String memoHandwritingId;
    private String memoHandwritingUri;

    /**
     * 새 메모 일 때의 입력 여부
     */
    private boolean isPhotoCaptured;
    private boolean isVideoRecorded;
    private boolean isVoiceRecorded;
    private boolean isHandwritingMade;

    /**
     * 기존 메모 보기/수정 시의 입력 여부
     */
    private boolean isPhotoFileSaved;
    private boolean isVideoFileSaved;
    private boolean isVoiceFileSaved;
    private boolean isHandwritingFileSaved;

    /**
     * 삭제 선택시 이 flag가 켜지고, 삭제하게 됨?
     */
    private boolean isPhotoCanceled;
    private boolean isVideoCanceled;
    private boolean isVoiceCanceled;
    private boolean isHandwritingCanceled;

    /**
     * 메모 내용 중 String으로만 표현되지 않는 data
     */
    private Bitmap resultPhotoBitmap;
    private Bitmap resultHandwritingBitmap;


    /**
     * dialog 조작에 사용될 전역변수
     */
    private int checkedItem;


    /**
     * Text <-> Handwriting 전환용 Animation
     */
    private Animation translateLeftAnim;
    private Animation translateRightAnim;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_memo_insert);

        titleBackgroundButton = findViewById(R.id.titleBackgroundButton);

        initVariables();

        setMediaLayout();

        setTextHandLayout();

        setCalendar();

        setBottomButtons();

        checkIntent(getIntent());

        translateLeftAnim = AnimationUtils.loadAnimation(this, R.anim.translate_left);
        translateRightAnim = AnimationUtils.loadAnimation(this, R.anim.translate_right);

        SlidingPageAnimationListener listener = new SlidingPageAnimationListener();

        translateLeftAnim.setAnimationListener(listener);
        translateRightAnim.setAnimationListener(listener);
    }

    private class SlidingPageAnimationListener implements Animation.AnimationListener {

        @Override
        public void onAnimationStart(Animation animation) {
            print("onAnimationStart()");
        }

        @Override
        public void onAnimationEnd(Animation animation) {
            print("onAnimationEnd()");
        }

        @Override
        public void onAnimationRepeat(Animation animation) {
            print("onAnimationRepeat()");
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        print("onNewIntent() called.");

        checkIntent(intent);
    }

    private void initVariables() {
        isPhotoCaptured = false;
        isVideoRecorded = false;
        isVoiceRecorded = false;
        isHandwritingMade = false;

        isPhotoFileSaved = false;
        isVideoFileSaved = false;
        isVoiceFileSaved = false;
        isHandwritingFileSaved = false;

        isPhotoCanceled = false;
        isVideoCanceled = false;
        isVoiceCanceled = false;
        isHandwritingCanceled = false;

        checkedItem = -1;
    }

    private void setMediaLayout() {
        photoView = findViewById(R.id.photoView);
        photoView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isPhotoCaptured || isPhotoFileSaved) {
                    showDialog(BasicInfo.CONTENT_PHOTO_EX);
                }
                else {
                    showDialog(BasicInfo.CONTENT_PHOTO);
                }
            }
        });


        videoButton = findViewById(R.id.videoButton);
        videoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isVideoRecorded || isVideoFileSaved) {
                    showDialog(BasicInfo.CONTENT_VIDEO_EX);
                }
                else {
                    showDialog(BasicInfo.CONTENT_VIDEO);
                }
            }
        });

        voiceButton = findViewById(R.id.voiceButton);
        voiceButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isVoiceRecorded || isVoiceFileSaved) {
                    showDialog(BasicInfo.CONTENT_VOICE_EX);
                }
                else {
                    showDialog(BasicInfo.CONTENT_VOICE);
                }
            }
        });
    }

    private void setTextHandLayout() {
        textView = findViewById(R.id.textView);
        handView = findViewById(R.id.handView);
        textButton = findViewById(R.id.textButton);
        handButton = findViewById(R.id.handButton);

        textButton.setSelected(true);
        handButton.setSelected(false);
        textView.setVisibility(View.VISIBLE);
        handView.setVisibility(View.GONE);

        handView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showHandwritingMakingActivity();
            }
        });

        textButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!textButton.isSelected()) {
                    textView.setVisibility(View.VISIBLE);
                    handView.setVisibility(View.GONE);  /* View.INVISIBLE이 아니네? */
                    textView.startAnimation(translateLeftAnim);

                    textButton.setSelected(true);
                    handButton.setSelected(false);
                }
            }
        });

        handButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!handButton.isSelected()) {
                    textView.setVisibility(View.GONE);
                    handView.setVisibility(View.VISIBLE);
                    handView.startAnimation(translateLeftAnim);

                    textButton.setSelected(false);
                    handButton.setSelected(true);
                }
            }
        });
    }

    private void setCalendar() {

        dateButton = findViewById(R.id.dateButton);
        dateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String mDateStr = dateButton.getText().toString();
                Calendar calendar = Calendar.getInstance();
                Date date = new Date();

                try {
                    if (BasicInfo.language.equals("ko")) {
                        print("ko: " + mDateStr);
                        date = BasicInfo.dateKorFormat.parse(mDateStr);
                    }
                    else {
                        print("HELLO: " + mDateStr);
                        date = BasicInfo.dateFormat.parse(mDateStr);
                    }
                }
                catch(ParseException e) {
                    Log.d(TAG, "ParseException in setCalendar().");
                }
                catch(Exception e) {
                    Log.d(TAG, "Exception in setCalendar().");
                }

                print("date: " + date);
                calendar.setTime(date);

                new DatePickerDialog(
                        MemoInsertActivity.this,
                        datePickerListener,
                        calendar.get(Calendar.YEAR),
                        calendar.get(Calendar.MONTH),
                        calendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });


        timeButton = findViewById(R.id.timeButton);
        timeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String timeStr = timeButton.getText().toString();
                String hour = timeStr.substring(0, 2);
                String min = timeStr.substring(4, 6);
                print("time: " + hour + ":" + min);

                new TimePickerDialog(
                        MemoInsertActivity.this,
                        timePickerListener,
                        Integer.valueOf(hour),
                        Integer.valueOf(min),
                        true).show();
            }
        });
    }

    private void setBottomButtons() {
        saveButton = findViewById(R.id.saveButton);
        deleteButton = findViewById(R.id.deleteButton);
        cancelButton = findViewById(R.id.cancelButton);

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean isParsed = parseDateText();
                if (isParsed) {
                    if (memoMode.equals(BasicInfo.MEMO_MODE_INSERT)) {
                        saveMemo();
                    }
                    else if (memoMode.equals(BasicInfo.MEMO_MODE_MODIFY) ||
                             memoMode.equals(BasicInfo.MEMO_MODE_VIEW)) {
                        modifyMemo();
                    }
                }
            }
        });

        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialog(BasicInfo.CONFIRM_DELETE);
            }
        });

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void checkIntent(Intent intent) {

        if (intent == null) {
            print("checkIntent(intent: null)");
            return;
        }
        print("checkIntent(intent: not null)");

        try {
            memoMode = intent.getStringExtra(BasicInfo.KEY_MEMO_MODE);
            print("memoMode: " + memoMode);
            if (memoMode.equals(BasicInfo.MEMO_MODE_MODIFY) ||
                    memoMode.equals(BasicInfo.MEMO_MODE_VIEW)) {
                print("modify or view");
                titleBackgroundButton.setText("메모 보기");
                saveButton.setText("수정");

                processIntent(intent);

                deleteButton.setVisibility(View.VISIBLE);
            }
            else {
                print("insert");
                titleBackgroundButton.setText("새 메모");
                saveButton.setText("저장");

                processIntent(null);

                deleteButton.setVisibility(View.GONE);
            }
        }
        catch(Exception e) {
            print("checkIntent() exception: ");
            e.printStackTrace();
        }
    }

    private void processIntent(Intent intent) {

        if (intent != null) {
            print("processIntent(intent) called.");
            memoId = intent.getStringExtra(BasicInfo.KEY_MEMO_ID);

            memoDateTime = intent.getStringExtra(BasicInfo.KEY_MEMO_DATE);
            memoText = intent.getStringExtra(BasicInfo.KEY_MEMO_TEXT);

            memoPhotoId = intent.getStringExtra(BasicInfo.KEY_ID_PHOTO);
            memoPhotoUri = intent.getStringExtra(BasicInfo.KEY_URI_PHOTO);

            memoVideoId = intent.getStringExtra(BasicInfo.KEY_ID_VIDEO);
            memoVideoUri = intent.getStringExtra(BasicInfo.KEY_URI_VIDEO);

            memoVoiceId = intent.getStringExtra(BasicInfo.KEY_ID_VOICE);
            memoVoiceUri = intent.getStringExtra(BasicInfo.KEY_URI_VOICE);

            memoHandwritingId = intent.getStringExtra(BasicInfo.KEY_ID_HANDWRITING);
            memoHandwritingUri = intent.getStringExtra(BasicInfo.KEY_URI_HANDWRITING);
        }
        else {
            memoId = "-1";

            memoDateTime = BasicInfo.dateTimeFormat.format(new Date());
            memoText = "";

            memoPhotoId = "-1";
            memoPhotoUri = "";

            memoVideoId = "-1";
            memoVideoUri = "";

            memoVoiceId = "-1";
            memoVoiceUri = "";

            memoHandwritingId = "-1";
            memoHandwritingUri = "";
        }

        setInitMemo();
    }

    private void setInitMemo() {
        print("setInitMemo(): " + memoDateTime);

        try {
            dateButton.setText(BasicInfo.dateKorFormat.format(
                    BasicInfo.dateFormat.parse(
                            memoDateTime.substring(0, 10))));
        }
        catch(ParseException pe) {
            print("ParseException on dateButton");
        }

        try {
            timeButton.setText(BasicInfo.timeKorFormat.format(
                    BasicInfo.timeFormat.parse(
                            memoDateTime.substring(11, 19))));
        }
        catch(ParseException pe) {
            print("ParseException on timeButton");
        }

        textView.setText(memoText);

        if (memoPhotoId.equals("") || memoPhotoId.equals("-1")) {
            /* default person picture */
            photoView.setImageResource(R.drawable.person_add);
        }
        else {
            isPhotoFileSaved = true;
            photoView.setImageURI(Uri.parse(BasicInfo.FOLDER_PHOTO + memoPhotoUri));
        }

        if (!memoHandwritingId.equals("") && !memoHandwritingId.equals("-1")) {
            isHandwritingFileSaved = true;
            Bitmap resultBitmap = BitmapFactory.decodeFile(
                    BasicInfo.FOLDER_HANDWRITING + memoHandwritingUri);
            handView.setImageBitmap(resultBitmap);
        }

        if (!memoVideoId.equals("") && !memoVideoId.equals("-1")) {
            isVideoFileSaved = true;
            videoButton.setCompoundDrawablesWithIntrinsicBounds(
                    null,
                    getResources().getDrawable(R.drawable.icon_video),
                    null, null);
        }
        else {
            videoButton.setCompoundDrawablesWithIntrinsicBounds(
                    null,
                    getResources().getDrawable(R.drawable.icon_video_empty),
                    null, null);
        }

        if (!memoVoiceId.equals("") && !memoVoiceId.equals("-1")) {
            isVoiceFileSaved = true;
            voiceButton.setCompoundDrawablesWithIntrinsicBounds(
                    null,
                    getResources().getDrawable(R.drawable.icon_voice),
                    null, null);
        }
        else {
            voiceButton.setCompoundDrawablesWithIntrinsicBounds(
                    null,
                    getResources().getDrawable(R.drawable.icon_voice_empty),
                    null, null);
        }
    }

    private DatePickerDialog.OnDateSetListener datePickerListener = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
            String str_year = String.valueOf(year);
            String str_month = String.valueOf(month + 1);
            String str_day = String.valueOf(dayOfMonth);

            if (month + 1 < 10) {
                str_month = "0" + str_month;
            }
            if (dayOfMonth < 10) {
                str_day = "0" + str_day;
            }

            if (BasicInfo.language.equals("ko")) {
                dateButton.setText(str_year + "년 " + str_month + "월 " + str_day + "일");
            } else {
                dateButton.setText(str_year + "-" + str_month + "-" + str_day);
            }

            print("DatePickerDialog - onDateSet() 01 memoDateTime: " + memoDateTime);
            memoDateTime =
                    str_year + "-" + str_month + "-" + str_day + " "
                    + memoDateTime.substring(11, 19);
            print("DatePickerDialog - onDateSet() 02 memoDateTime: " + memoDateTime);
        }
    };

    private TimePickerDialog.OnTimeSetListener timePickerListener = new TimePickerDialog.OnTimeSetListener() {
        @Override
        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
            String str_hour = String.valueOf(hourOfDay);
            if (hourOfDay < 10) {
                str_hour = "0" + str_hour;
            }

            String str_min = String.valueOf(minute);
            if (minute < 10) {
                str_min = "0" + str_min;
            }

            if (BasicInfo.language.equals("ko")) {
                timeButton.setText(str_hour + "시 " + str_min + "분");
            }
            else {
                timeButton.setText(str_hour + ":" + str_min);
            }

            print("TimePickerDialog - onTimeSet() 01 memoDateTime: " + memoDateTime);
            memoDateTime = memoDateTime.substring(0, 11)
                    + str_hour + ":" + str_min + ":00";
            print("TimePickerDialog - onTimeSet() 02 memoDateTime: " + memoDateTime);
        }
    };

    private void showPhotoCaptureActivity() {
        print("showPhotoCaptureActivity() called.");

        Intent intent = new Intent(getApplicationContext(), PhotoCaptureActivity.class);
        startActivityForResult(intent, BasicInfo.REQ_PHOTO_CAPTURE_ACTIVITY);
    }

    private void showPhotoSelectionActivity() {
        print("showPhotoSelectionActivity() called.");

        Intent intent = new Intent(getApplicationContext(), PhotoSelectionActivity.class);
        startActivityForResult(intent, BasicInfo.REQ_PHOTO_SELECTION_ACTIVITY);
    }

    private void showHandwritingMakingActivity() {
        print("showHandwritingMakingActivity() called.");

        Intent intent = new Intent(getApplicationContext(), HandwritingMakingActivity.class);
        startActivityForResult(intent, BasicInfo.REQ_HANDWRITING_MAKING_ACTIVITY);
    }

    private void showVideoRecordingActivity() {
        Intent intent = new Intent(getApplicationContext(),
                VideoRecordingActivity.class);
        startActivityForResult(intent, BasicInfo.REQ_VIDEO_RECORDING_ACTIVITY);
    }

    private void showVideoSelectionActivity() {
        Intent intent = new Intent(getApplicationContext(),
                VideoSelectionActivity.class);
        startActivityForResult(intent, BasicInfo.REQ_VIDEO_SELECTION_ACTIVITY);
    }

    private void showVideoPlayingActivity() {
        Intent intent = new Intent(getApplicationContext(),
                VideoPlayActivity.class);
        intent.putExtra(BasicInfo.KEY_URI_VIDEO,
                    BasicInfo.FOLDER_VIDEO + memoVideoUri);

        startActivity(intent);
    }

    private void showVoiceRecordingActivity() {
        Intent intent = new Intent(getApplicationContext(),
                VoiceRecordingActivity.class);
        startActivityForResult(intent, BasicInfo.REQ_VOICE_RECORDING_ACTIVITY);
    }

    private void showVoicePlayingActivity() {
        Intent intent = new Intent(getApplicationContext(),
                VoicePlayActivity.class);
        intent.putExtra(BasicInfo.KEY_URI_VOICE,
                BasicInfo.FOLDER_VOICE + memoVoiceUri);
        startActivity(intent);
    }

    /**
     * 새로 사진을 찍은 경우에만 동작한다.
     * : 캡쳐된 사진을 photo 폴더에 복사한 후, PHOTO table에 정보 추가.
     *   photo 이름은 현재 시간 값을 이용.
     */
    private String insertPhoto() {
        String photoName = null;
        if (!isPhotoCaptured) { // 사진을 새로 찍은 경우는 물론, 앨범에 사진을 선택한 경우에도,
            return null;        // isPhotoCaptured는 true가 된다.
        }

        try {
            /*if (memoMode != null && memoMode.equals(BasicInfo.MEMO_MODE_MODIFY)) {
                /* MEMO_MODE_MODIFY : 메모 수정 모드인 경우, 기존 photo를 지운다. */
            if (isPhotoFileSaved) {
                print("Previous photo is newly created for modify mode.");

                String SQL = "delete from " + MemoDatabase.TABLE_PHOTO +
                             " where _ID = '" + memoPhotoId + "'";
                print("SQL: " + SQL);
                if (MultiMemoActivity.mDatabase != null) {
                    /* DB에서는 uri를 지운다. */
                    MultiMemoActivity.mDatabase.execSQL(SQL);

                    File previousFile =
                            new File(BasicInfo.FOLDER_PHOTO + memoPhotoUri);
                    if (previousFile != null && previousFile.exists()) {
                        previousFile.delete();
                    }
                }
            }

            File photoFolder = new File(BasicInfo.FOLDER_PHOTO);

            if (!photoFolder.isDirectory()) {
                print("creating photo folder : " + photoFolder);
                photoFolder.mkdirs();
            }

            // 현재 시각으로 사진 이름을 정하고, photo folder에 photo file 생성
            photoName = createFilename();

            FileOutputStream outstream = new FileOutputStream(BasicInfo.FOLDER_PHOTO + photoName);
            resultPhotoBitmap.compress(Bitmap.CompressFormat.PNG, 100, outstream);
            outstream.close();

            // db photo table에 uri 정보 넣음
            print("isCaptured: " + isPhotoCaptured);

            String SQL = "insert into " + MemoDatabase.TABLE_PHOTO +
                         " (URI) values (" + "'" + photoName + "')";
            print("SQL: " + SQL);
            if (MultiMemoActivity.mDatabase != null) {
                MultiMemoActivity.mDatabase.execSQL(SQL);
            }
        }
        catch(IOException ioe) {
            print("insertPhoto() IOException");
            ioe.printStackTrace();
        }
        catch(Exception e) {
            print("insertPhoto() Exception");
            e.printStackTrace();
        }

        return photoName;
    }

    /**
     * handwriting file이 만들어진 경우에만 동작한다.
     * 그려진 그림 파일을 handwriting 폴더에 복사한 후, HANDWRITING table에 정보 추가
     * handwriting 이름은 현재 시간 값을 이용.
     */
    private String insertHandwriting() {
        print("insertHandwriting");

        String handwritingFilename = null;
        if (isHandwritingMade == false) {
            return null;
        }

        try {
            print("isHandwritingFileSaved: " + isHandwritingFileSaved);
            // 기존 파일 저장된 경우 DB에서 uri 삭제, 폴더에서 파일 삭제,
            if (isHandwritingFileSaved) {
            //if (memoMode != null && memoMode.equals(BasicInfo.MEMO_MODE_MODIFY)) {
                print("previous handwriting is newly created for modify mode");

                String SQL = "delete from " + MemoDatabase.TABLE_HANDWRITING +
                        " where _id = '" + memoId + "'";
                print("SQL: " + SQL);
                if (MultiMemoActivity.mDatabase != null) {
                    MultiMemoActivity.mDatabase.execSQL(SQL);
                }

                File prevFile = new File(
                        BasicInfo.FOLDER_HANDWRITING + memoHandwritingUri);
                if (prevFile.exists()) {
                    prevFile.delete();
                }
            }

            File handwritingFolder = new File(BasicInfo.FOLDER_HANDWRITING);

            // 폴더 없을 경우 생성.
            if (!handwritingFolder.isDirectory()) {
                print("creating handwriting folder " + handwritingFolder.toString());

                handwritingFolder.mkdirs();
            }

            // handwriting 파일 저장
            handwritingFilename = createFilename();

            FileOutputStream outputStream = new FileOutputStream(
                    BasicInfo.FOLDER_HANDWRITING + handwritingFilename);
            resultHandwritingBitmap.compress(
                    Bitmap.CompressFormat.PNG, 100, outputStream);
            outputStream.close();

            // Handwriting DB에 넣음
            if (handwritingFilename != null) {
                print("Inserting handwriting file into Handwriting Database");

                String SQL = "insert into " + MemoDatabase.TABLE_HANDWRITING +
                            "(URI) values('" + handwritingFilename + "')";
                print("SQL: " + SQL);
                if (MultiMemoActivity.mDatabase != null) {
                    MultiMemoActivity.mDatabase.execSQL(SQL);
                }
            }
        }
        catch(IOException ioe) {
            print("IOException in insertHandwriting()");
        }
        catch(Exception ioe) {
            print("Exception in insertHandwriting()");
        }

        return handwritingFilename;
    }

    /**
     * video file이 만들어진 경우에만 동작한다.
     * 영상 파일을 video 폴더에 복사한 후, video table에 정보 추가
     * video 이름은 현재 시간 값을 이용.
     */
    private String insertVideo() {
        String videoName = null;

        print("isVideoRecorded: " + isVideoRecorded);
        if (!isVideoRecorded) { // video가 새로 저장되지 않은 경우는 return.
            return null;
        }

        try {
            /*if (memoMode != null && memoMode.equals(BasicInfo.MEMO_MODE_MODIFY)) {
                /* MEMO_MODE_MODIFY : 메모 수정 모드인 경우, 기존 photo를 지운다. */
            if (isVideoFileSaved) {
                print("Previous video is newly created for modify mode.");

                String SQL = "delete from " + MemoDatabase.TABLE_VIDEO +
                        " where _ID = '" + memoVideoId + "'";
                print("SQL: " + SQL);
                if (MultiMemoActivity.mDatabase != null) {
                    /* DB에서는 uri를 지운다. */
                    MultiMemoActivity.mDatabase.execSQL(SQL);

                }

                File previousFile =
                        new File(BasicInfo.FOLDER_VIDEO + memoVideoUri);
                if (previousFile != null && previousFile.exists()) {
                    previousFile.delete();
                }
            }

            File videoFolder = new File(BasicInfo.FOLDER_VIDEO);

            if (!videoFolder.isDirectory()) {
                print("creating video folder : " + videoFolder);
                videoFolder.mkdirs();
            }

            // 현재 시각으로 사진 이름을 정하고, photo folder에 photo file 생성
            videoName = createFilename();

            File tempFile = new File(BasicInfo.FOLDER_VIDEO + "recorded");
            tempFile.renameTo(new File(BasicInfo.FOLDER_VIDEO + videoName));

            if (videoName != null) {
                // db photo table에 uri 정보 넣음
                print("isVideoRecorded: " + isVideoRecorded);

                String SQL = "insert into " + MemoDatabase.TABLE_VIDEO +
                        " (URI) values (" + "'" + videoName + "')";

                print("SQL: " + SQL);
                if (MultiMemoActivity.mDatabase != null) {
                    MultiMemoActivity.mDatabase.execSQL(SQL);
                }
            }
        }
        catch(Exception e) {
            print("insertVideo() Exception");
            e.printStackTrace();
        }

        return videoName;
    }

    private String insertVoice() {
       	String voiceName = null;
    	Log.d(TAG, "isVoiceRecorded            : " +isVoiceRecorded);
    	if (isVoiceRecorded) {
	    	//if (memoMode != null && (memoMode.equals(BasicInfo.MEMO_MODE_MODIFY) || memoMode.equals(BasicInfo.MEMO_MODE_VIEW))) {
            if (isVoiceFileSaved) {
				Log.d(TAG, "previous voice is newly created for modify mode.");

				String SQL = "delete from " + MemoDatabase.TABLE_VOICE +
				" where _ID = '" + memoVoiceId + "'";
		    	Log.d(TAG, "SQL : " + SQL);
		    	if (MultiMemoActivity.mDatabase != null) {
		    		MultiMemoActivity.mDatabase.execSQL(SQL);
		    	}

				File previousFile = new File(BasicInfo.FOLDER_VOICE + memoVoiceUri);
				if (previousFile.exists()) {
					previousFile.delete();
				}
			}


			File voiceFolder = new File(BasicInfo.FOLDER_VOICE);

			//폴더가 없다면 폴더를 생성한다.
			if(!voiceFolder.isDirectory()){
				Log.d(TAG, "creating voice folder : " + voiceFolder);
				voiceFolder.mkdirs();
			}

			voiceName = createFilename();

			File tempFile = new File(BasicInfo.FOLDER_VOICE + "recorded");
			tempFile.renameTo(new File(BasicInfo.FOLDER_VOICE + voiceName));

			if (voiceName != null) {
				Log.d(TAG, "isVoiceRecorded            : " +isVoiceRecorded);

				// INSERT PICTURE INFO
				String SQL = "insert into " + MemoDatabase.TABLE_VOICE
                        + "(URI) values(" + "'" + voiceName + "')";
				if (MultiMemoActivity.mDatabase != null) {
					MultiMemoActivity.mDatabase.execSQL(SQL);
				}
			}
    	}

    	return voiceName;
    }


    private String createFilename() {
        Date curDate = new Date();
        String curDateStr = String.valueOf(curDate.getTime());

        return curDateStr;
    }

    @Override
    protected Dialog onCreateDialog(int id) {
        print("onCreateDialog(id: " + id + ") called.");
        AlertDialog.Builder builder = null;

        switch(id) {
            case BasicInfo.CONFIRM_TEXT_INPUT:
                builder = new AlertDialog.Builder(this);
                builder.setTitle("메모");
                builder.setMessage("텍스트를 입력하세요.");
                builder.setPositiveButton("확인",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        });
                break;

            case BasicInfo.CONTENT_PHOTO:
                builder = new AlertDialog.Builder(this);

                builder.setTitle("선택하세요");
                checkedItem = 0;
                builder.setSingleChoiceItems(
                        R.array.array_photo,
                        checkedItem,
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                checkedItem = which;
                            }
                        }
                );

                builder.setPositiveButton("선택", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (checkedItem == 0) {
                            showPhotoCaptureActivity();
                        }
                        else if (checkedItem == 1) {
                            showPhotoSelectionActivity();
                        }
                    }
                });

                builder.setNegativeButton("취소", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        print("Negative button clicked.");
                    }
                });

                break;
            case BasicInfo.CONTENT_PHOTO_EX:
                builder = new AlertDialog.Builder(this);

                checkedItem = 0;
                builder.setTitle("선택하세요.");
                builder.setSingleChoiceItems(
                        R.array.array_photo_ex,
                        checkedItem,
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                checkedItem = which;
                            }
                        }
                );

                builder.setPositiveButton(
                        "선택",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                switch(checkedItem) {
                                    case 0:
                                        showPhotoCaptureActivity();
                                        break;
                                    case 1:
                                        showPhotoSelectionActivity();
                                        break;
                                    case 2:
                                        isPhotoCaptured = false;
                                        isPhotoCanceled = true;

                                        photoView.setImageResource(R.drawable.person_add);
                                        break;
                                }
                            }
                        }
                );

                builder.setNegativeButton(
                        "취소",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                finish();
                            }
                        }
                );

                break;

            case BasicInfo.CONTENT_VIDEO:
                builder = new AlertDialog.Builder(this);
                builder.setTitle(R.string.selection_title);
                checkedItem = 0;
                builder.setSingleChoiceItems(
                        R.array.array_video, checkedItem,
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                print("which button: " + which);
                                checkedItem = which;
                            }
                        });
                builder.setPositiveButton(
                        R.string.selection_button,
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                print("which: " + which + ", checkedItem: " + checkedItem);

                                if (checkedItem == 0) {
                                    showVideoRecordingActivity();
                                }
                                else if (checkedItem == 1) {
                                    showVideoSelectionActivity();
                                }
                            }
                        });
                builder.setNegativeButton(R.string.cancel_button,
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        });
                break;

            case BasicInfo.CONTENT_VIDEO_EX:
                builder = new AlertDialog.Builder(this);
                builder.setTitle(R.string.selection_title);
                checkedItem = 0;
                builder.setSingleChoiceItems(
                        R.array.array_video_ex, checkedItem,
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                print("which button: " + which);
                                checkedItem = which;
                            }
                        });
                builder.setPositiveButton(
                        R.string.selection_button,
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                print("which: " + which + ", checkedItem: " + checkedItem);

                                if (checkedItem == 0) {
                                    showVideoPlayingActivity();
                                }
                                else if (checkedItem == 1) {
                                    showVideoRecordingActivity();
                                }
                                else if (checkedItem == 2) {
                                    showVideoSelectionActivity();
                                }
                                else if (checkedItem == 3) {
                                    isVideoCanceled = true;
                                    isVideoRecorded = false;

                                    videoButton.setCompoundDrawablesWithIntrinsicBounds(
                                            null,
                                            getResources().getDrawable(R.drawable.icon_video_empty),
                                            null,
                                            null);
                                }
                            }
                        });
                builder.setNegativeButton(R.string.cancel_button,
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        });
                break;

            case BasicInfo.CONTENT_VOICE:
                builder = new AlertDialog.Builder(this);
                builder.setTitle(R.string.selection_title);
                checkedItem = 0;
                builder.setSingleChoiceItems(
                        R.array.array_voice, checkedItem,
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                print("which button: " + which);
                                checkedItem = which;
                            }
                        });
                builder.setPositiveButton(
                        R.string.selection_button,
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                print("Selected Index: " + which);

                                if (checkedItem == 0) {
                                    showVoiceRecordingActivity();
                                }
                            }
                        });
                builder.setNegativeButton(R.string.cancel_button,
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        });
                break;

            case BasicInfo.CONTENT_VOICE_EX:
                builder = new AlertDialog.Builder(this);
                builder.setTitle(R.string.selection_title);
                checkedItem = 0;
                builder.setSingleChoiceItems(
                        R.array.array_voice_ex, checkedItem,
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                print("which button: " + which);
                                checkedItem = which;
                            }
                        });
                builder.setPositiveButton(
                        R.string.selection_button,
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                print("Selected Index: " + which);

                                if (checkedItem == 0) {
                                    showVoicePlayingActivity();
                                }
                                else if (checkedItem == 1) {
                                    showVoiceRecordingActivity();
                                }
                                else if (checkedItem == 2) {
                                    isVoiceCanceled = true;
                                    isVoiceRecorded = false;

                                    voiceButton.setCompoundDrawablesWithIntrinsicBounds(
                                            null,
                                            getResources().getDrawable(R.drawable.icon_voice_empty),
                                            null,
                                            null);
                                }
                            }
                        });
                builder.setNegativeButton(R.string.cancel_button,
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        });
                break;


            case BasicInfo.CONFIRM_DELETE:
                builder = new AlertDialog.Builder(this);
                builder.setTitle("메모");
                builder.setMessage("메모를 삭제하시겠습니까?");
                builder.setPositiveButton("예",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                deleteMemo();
                            }
                        });
                builder.setNegativeButton("아니오",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dismissDialog(BasicInfo.CONFIRM_DELETE);
                            }
                        });
                break;
        }

        return builder.create();
    }

    /**
     * 메모와 시간 체크
     */
    private boolean parseDateText() {
        String testStr = dateButton.getText().toString() + " "
                        + timeButton.getText().toString();

        try {
            testStr = BasicInfo.dateTimeFormat.format(
                        BasicInfo.dateTimeKorFormat.parse(testStr));

            if (testStr.equals(memoDateTime)) {
                print("parseDateText() - memoDateTime is CORRECT...");
            }
            else {
                print("parseDateText() - memoDateTime is WRONG...");
            }
        }
        catch(ParseException pe) {
            print("ParseException in parseDateText()");
        }

        memoText = textView.getText().toString();

        if (memoText.trim().length() < 1) {
            showDialog(BasicInfo.CONFIRM_TEXT_INPUT);
            return false;
        }

        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        print("onActivityResult(requestCode: "
                + requestCode + ", resultCode: " + resultCode + ") called."
                + "RESULT_OK: " + RESULT_OK + ", RESULT_CANCELED: " + RESULT_CANCELED);
        super.onActivityResult(requestCode, resultCode, data);

        switch(requestCode) {
            case BasicInfo.REQ_PHOTO_CAPTURE_ACTIVITY:
                print("BasicInfo.REQ_PHOTO_CAPTURE_ACTIVITY");
                if (resultCode == RESULT_OK) {
                    boolean isPhotoExists = checkCapturedPhotoFile();
                    if (isPhotoExists) {
                        print("image file exists: " + BasicInfo.FOLDER_PHOTO + "captured");
                        resultPhotoBitmap = BitmapFactory.decodeFile(
                                BasicInfo.FOLDER_PHOTO + "captured");

                        photoView.setImageBitmap(resultPhotoBitmap);
                        isPhotoCaptured = true;
                        photoView.invalidate();
                    }
                    else {
                        print("image file doesn't exist." +
                            BasicInfo.FOLDER_PHOTO + "captured");
                    }
                }
                break;
            case BasicInfo.REQ_PHOTO_SELECTION_ACTIVITY:
                print("BasicInfo.REQ_PHOTO_SELECTION_ACTIVITY");
                if (resultCode == RESULT_OK) {
                    Uri photoUri = data.getParcelableExtra(BasicInfo.KEY_URI_PHOTO);
                    try {
                        BitmapFactory.Options options =
                                new BitmapFactory.Options();
                        options.inSampleSize = 8;
                        resultPhotoBitmap = BitmapFactory.decodeStream(
                                getContentResolver().openInputStream(
                                        photoUri),
                                null,
                                options);

                        photoView.setImageBitmap(resultPhotoBitmap);
                        isPhotoCaptured = true;

                        photoView.invalidate();
                    }
                    catch(Exception e) {
                        e.printStackTrace();
                    }
                }
                break;
            case BasicInfo.REQ_HANDWRITING_MAKING_ACTIVITY:
                print("BasicInfo.REQ_HANDWRITING_MAKING_ACTIVITY");
                if (resultCode == RESULT_OK) {
                    boolean isHandwritingExists = checkHandwritingFile();
                    if (isHandwritingExists) {
                        resultHandwritingBitmap =
                                BitmapFactory.decodeFile(
                                        BasicInfo.FOLDER_HANDWRITING + HandwritingMakingActivity.handwritingFilename);
                        isHandwritingMade = true;

                        handView.setImageBitmap(resultHandwritingBitmap);
                    }
                }
                break;

            case BasicInfo.REQ_VIDEO_RECORDING_ACTIVITY:
                print("BasicInfo.REQ_VIDEO_RECORDING_ACTIVITY");
                if (resultCode == RESULT_OK) {
                    boolean isVideoExists = checkRecordedVideoFile();
                    if (isVideoExists) {
                        isVideoRecorded = true;
                        videoButton.setCompoundDrawablesWithIntrinsicBounds(
                                null,
                                getResources().getDrawable(R.drawable.icon_video),
                                null, null);
                    }
                }
                break;

            case BasicInfo.REQ_VIDEO_SELECTION_ACTIVITY:
                print("BasicInfo.REQ_VIDEO_SELECTION_ACTIVITY");

                String [] proj = { MediaStore.Video.Media.DATA };
                Cursor cursor = null;

                if (resultCode == RESULT_OK) {
                    try {
                        String uriStr = data.getStringExtra(BasicInfo.KEY_URI_VIDEO);
                        Uri uri = Uri.parse(BasicInfo.URI_MEDIA_FORMAT + uriStr);

                        print("Uri: " + uri.toString());
                        // content://media/external/video/media/165799

                        cursor = getContentResolver().query(uri, proj,
                                null, null, null);
                        int column_index =
                                cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DATA);
                        cursor.moveToFirst();

                        File fromFile = new File(cursor.getString(column_index));
                        cursor.close();
                        print("fromFile: " + fromFile.getAbsolutePath());
                        // /storage/emulated/0/Download/20170905_194903.mp4

                        File folder = new File(BasicInfo.FOLDER_VIDEO);
                        if (folder.isDirectory() == false) {
                            folder.mkdirs();
                        }
                        File toFile = new File(BasicInfo.FOLDER_VIDEO + "recorded");
                        copy(fromFile, toFile);

                        isVideoRecorded = true;

                        videoButton.setCompoundDrawablesWithIntrinsicBounds(
                                null,
                                getResources().getDrawable(R.drawable.icon_video),
                                null, null);
                    }
                    catch(Exception e) {
                        print("IOException in 0002");
                        e.printStackTrace();
                    }
                    finally {
                        if (cursor != null) {
                            cursor.close();
                        }
                    }
                }
                break;

            case BasicInfo.REQ_VOICE_RECORDING_ACTIVITY:
                print("BasicInfo.REQ_VOICE_RECORDING_ACTIVITY");
                if (resultCode == RESULT_OK) {
                    boolean isVoiceExists = checkRecordedVoiceFile();
                    if (isVoiceExists) {
                        isVoiceRecorded = true;
                        voiceButton.setCompoundDrawablesWithIntrinsicBounds(
                                null,
                                getResources().getDrawable(R.drawable.icon_voice),
                                null, null);
                    }
                }
                break;
        }
    }

    private void copy(File src, File dst) throws IOException {
        InputStream in = new FileInputStream(src);
        try {
            OutputStream out = new FileOutputStream(dst);
            try {
                byte[] buf = new byte[1024];
                int len;
                while((len = in.read(buf)) > 0) {
                    out.write(buf, 0, len);
                }
            } finally {
                out.close();
            }
        }
        finally {
            in.close();
        }
    }

    private boolean checkCapturedPhotoFile() {
        File file = new File(BasicInfo.FOLDER_PHOTO + "captured");
        if (file.exists()) {
            return true;
        }
        return false;
    }

    private boolean checkHandwritingFile() {
        File file = new File(
                BasicInfo.FOLDER_HANDWRITING +
                        HandwritingMakingActivity.handwritingFilename);
        if (file.exists()) {
            return true;
        }
        return false;
    }

    private boolean checkRecordedVideoFile() {
        File file = new File(BasicInfo.FOLDER_VIDEO + "recorded");
        if (file.exists()) {
            return true;
        }
        else {
            return false;
        }
    }

    private boolean checkRecordedVoiceFile() {
        File file = new File(BasicInfo.FOLDER_VOICE + "recorded");
        if (file.exists()) {
            return true;
        }
        else {
            return false;
        }
    }

    private void saveMemo() {
        print("saveMemo()" + memoDateTime);

        String SQL = null;

        int photoId = -1;
        String photoFilename = insertPhoto();

        /** PHOTO: 새로 캡쳐된 사진이 있는 경우 insertPhoto()에서 해당 사진을 photo folder에 현재 시간을
         * 이름으로 해서 넣었고, DB의 photo table에도 넣었다. 여기서는 해당 uri의 id를 찾는다. */
        if (photoFilename != null) {
            SQL = "select _ID from " + MemoDatabase.TABLE_PHOTO +
                    " where URI = '" + photoFilename + "'";
            print("SQL: " + SQL);

            if (MultiMemoActivity.mDatabase != null) {
                Cursor cursor = MultiMemoActivity.mDatabase.rawQuery(SQL);
                if (cursor.moveToNext()) {
                    photoId = cursor.getInt(0);
                }
                cursor.close();
            }
        }

        String handwritingFilename = insertHandwriting();
        int handwritingId = -1;

        if (handwritingFilename != null) {
            SQL = "select _ID from " + MemoDatabase.TABLE_HANDWRITING +
                    " where URI = '" + handwritingFilename + "'";
            print("SQL: " + SQL);
            if (MultiMemoActivity.mDatabase != null) {
                Cursor cursor = MultiMemoActivity.mDatabase.rawQuery(SQL);
                if (cursor.moveToNext()) {
                    handwritingId = cursor.getInt(0);
                }
                cursor.close();
            }
        }

        int videoId = -1;
        String videoFilename = insertVideo();

        if (videoFilename != null) {
            SQL = "select _ID from " + MemoDatabase.TABLE_VIDEO +
                    " where URI = '" + videoFilename + "'";
            print("SQL: " + SQL);

            if (MultiMemoActivity.mDatabase != null) {
                Cursor cursor = MultiMemoActivity.mDatabase.rawQuery(SQL);
                if (cursor.moveToNext()) {
                    videoId = cursor.getInt(0);
                }
                cursor.close();
            }
        }

        int voiceId = -1;
        String voiceFilename = insertVideo();

        if (voiceFilename != null) {
            SQL = "select _ID from " + MemoDatabase.TABLE_VOICE +
                    " where URI = '" + voiceFilename + "'";
            print("SQL: " + SQL);

            if (MultiMemoActivity.mDatabase != null) {
                Cursor cursor = MultiMemoActivity.mDatabase.rawQuery(SQL);
                if (cursor.moveToNext()) {
                    voiceId = cursor.getInt(0);
                }
                cursor.close();
            }
        }

        SQL = "insert into " + MemoDatabase.TABLE_MEMO +
                "(INPUT_DATE, CONTENTS_TEXT, ID_PHOTO, ID_VIDEO, ID_VOICE, ID_HANDWRITING) values(" +
                "DATETIME('" + memoDateTime + "'), " +
                "'" + memoText +"', " +
                "'" + photoId + "', " +
                "'" + videoId + "', " +
                "'" + voiceId + "', " +
                "'" + handwritingId + "')";
        print("SQL: " + SQL);

        if (MultiMemoActivity.mDatabase != null) {
            MultiMemoActivity.mDatabase.execSQL(SQL);
        }

        Intent intent = getIntent();
        setResult(RESULT_OK, intent);
        finish();
    }

    private void modifyMemo() {
        print("modifyMemo()");

        Intent intent = getIntent();
        String photoFilename = insertPhoto();
        int photoId = -1;

        String SQL;

        /****** PHOTO 처리 ********************************************************/
        /* insertPhoto()에서 table에 넣었고, 여기서는 해당 uri의 id를 찾는다. */

        /**
         * isPhotoCaptured | isPhotoFileSaved | isPhotoCanceled
         *      false               false           false       ->   처리필요없음
         *      false               false           true        ->     불가능
         *      false               true            false       ->   처리필요없음
         *      false               true            true        -> 1. 기존 photo 삭제
         *      true                false           false       -> 2. 새 photo 저장 필요.
         *      true                false           true        -> 불가능 처리함(cancel이면 capture는 false)
         *      true                true            false       -> 3. 기존 photo 삭제 후 새 photo 저장 필요
         *      true                true            true        -> 불가능 처리함(cancel이면 capture는 false)
         *
         *      가능한 경우인 1,2,3만 처리
         *      2,3은 위의 insertPhoto()에서 처리됨.
         */

        if (photoFilename != null) {
            /**
             * 2,3에 해당하는 경우로 DB 처리.
             * 기존에 저장된 photo가 없고, 새로 캡쳐되거나 앨범에서 선택된 사진이 있을 때에 해당한다.
             * 해당 사진의 uri를 찾는다.
             */

            SQL = "select _ID from " + MemoDatabase.TABLE_PHOTO +
                    " where URI = '" + photoFilename + "'";
            print("SQL: " + SQL);

            if (MultiMemoActivity.mDatabase != null) {
                Cursor cursor = MultiMemoActivity.mDatabase.rawQuery(SQL);
                if (cursor.moveToNext()) {
                    photoId = cursor.getInt(0);
                }
                cursor.close();
            }
            print("photoId: " + photoId);

            // memo id에 해당하는 record의 photo id 값을 업데이트 한다. : -1 -> 유효 id.
            SQL = "update " + MemoDatabase.TABLE_MEMO +
                    " set " + "ID_PHOTO = '" + photoId + "'" +
                    " where _id = '" + memoId + "'";
            print("SQL: " + SQL);
            if (MultiMemoActivity.mDatabase != null) {
                //MultiMemoActivity.mDatabase.rawQuery(SQL);
                MultiMemoActivity.mDatabase.execSQL(SQL);
            }
            memoPhotoId = String.valueOf(photoId);  // photo id 갱신
        }
        /* photoFilename이 비어 있으므로, 이전에 저장했던 파일을 지운 경우이다. */
        else if (isPhotoFileSaved && isPhotoCanceled) {
            /**
             * 위의 1의 경우를 여기서 처리함.
             */
            print("isPhotoFileSaved: " + isPhotoFileSaved + ", isPhotoCanceled: " + isPhotoCanceled);

            /* photo table의 해당 record를 삭제한다. */
            SQL = "delete from " + MemoDatabase.TABLE_PHOTO +
                    " where _ID = '" + memoPhotoId + "'";
            print("SQL: " + SQL);
            if (MultiMemoActivity.mDatabase != null) {
                MultiMemoActivity.mDatabase.execSQL(SQL);
            }

            photoId = -1;
            /* memo table의 해당 record의 photo id 값을 -1로 바꾼다. */
            SQL = "update " + MemoDatabase.TABLE_MEMO +
                    " set " +
                    " ID_PHOTO = '" + photoId + "'" +
                    " where _ID = '" + memoId + "'";
            print("SQL: " + SQL);
            if (MultiMemoActivity.mDatabase != null) {
                MultiMemoActivity.mDatabase.execSQL(SQL);
            }

            memoPhotoId = String.valueOf(photoId);
        }

        /****** Handwriting 처리 *****************************************************/
        String handwritingFilename = insertHandwriting();
        int handwritingId = -1;

        if (handwritingFilename != null) {
            /**
             * 2,3에 해당하는 경우로 DB 처리.
             * 기존에 저장된 handwriting이 없고, 새로 그려진 경우에 해당
             * 해당 사진의 uri를 찾는다.
             */

            SQL = "select _ID from " + MemoDatabase.TABLE_HANDWRITING +
                    " where URI = '" + handwritingFilename + "'";
            print("SQL: " + SQL);

            if (MultiMemoActivity.mDatabase != null) {
                Cursor cursor = MultiMemoActivity.mDatabase.rawQuery(SQL);
                if (cursor.moveToNext()) {
                    handwritingId = cursor.getInt(0);
                }
                cursor.close();
            }
            print("handwritingId: " + handwritingId);

            // memo id에 해당하는 record의 photo id 값을 업데이트 한다. : -1 -> 유효 id.
            SQL = "update " + MemoDatabase.TABLE_MEMO +
                    " set " + "ID_HANDWRITING = '" + handwritingId + "'" +
                    " where _id = '" + memoId + "'";
            print("SQL: " + SQL);
            if (MultiMemoActivity.mDatabase != null) {
                MultiMemoActivity.mDatabase.execSQL(SQL);
            }
            memoHandwritingId = String.valueOf(handwritingId);
        }
        /* handwritingFilename이 비어 있으므로, 이전에 저장했던 파일을 지운 경우이다. */
        else if (isHandwritingFileSaved && isHandwritingCanceled) {
            /**
             * 위의 1의 경우를 여기서 처리함.
             */
            print("isHandwritingFileSaved: " + isHandwritingFileSaved +
                    ", isHandwritingoCanceled: " + isHandwritingCanceled);

            /* handwriting table의 해당 record를 삭제한다. */
            SQL = "delete from " + MemoDatabase.TABLE_HANDWRITING +
                    " where _ID = '" + memoHandwritingId + "'";
            print("SQL: " + SQL);
            if (MultiMemoActivity.mDatabase != null) {
                MultiMemoActivity.mDatabase.execSQL(SQL);
            }

            handwritingId = -1;
            /* memo table의 해당 record의 photo id 값을 -1로 바꾼다. */
            SQL = "update " + MemoDatabase.TABLE_MEMO +
                    " set " +
                    " ID_HANDWRITING = '" + handwritingId + "'" +
                    " where _ID = '" + memoId + "'";
            print("SQL: " + SQL);
            if (MultiMemoActivity.mDatabase != null) {
                MultiMemoActivity.mDatabase.execSQL(SQL);
            }

            memoHandwritingId = String.valueOf(handwritingId);
        }


        /****** Video 처리 *****************************************************/
        String videoFilename = insertVideo();
        int videoId = -1;

        if (videoFilename != null) {
            /**
             * 2,3에 해당하는 경우로 DB 처리.
             * 기존에 저장된 video가 없고, 새로 그려진 경우에 해당
             * 해당 사진의 uri를 찾는다.
             */

            SQL = "select _ID from " + MemoDatabase.TABLE_VIDEO +
                    " where URI = '" + videoFilename + "'";
            print("SQL: " + SQL);

            if (MultiMemoActivity.mDatabase != null) {
                Cursor cursor = MultiMemoActivity.mDatabase.rawQuery(SQL);
                if (cursor.moveToNext()) {
                    videoId = cursor.getInt(0);
                }
                cursor.close();
            }
            print("videoId: " + videoId);

            // memo id에 해당하는 record의 video id 값을 업데이트 한다. : -1 -> 유효 id.
            SQL = "update " + MemoDatabase.TABLE_MEMO +
                    " set " + "ID_VIDEO = '" + videoId + "'" +
                    " where _id = '" + memoId + "'";
            print("SQL: " + SQL);
            if (MultiMemoActivity.mDatabase != null) {
                MultiMemoActivity.mDatabase.execSQL(SQL);
            }
            memoVideoId = String.valueOf(videoId);
        }
        /* videoFilename이 비어 있으므로, 이전에 저장했던 파일을 지운 경우이다. */
        else if (isVideoFileSaved && isVideoCanceled) {
            /**
             * 위의 1의 경우를 여기서 처리함.
             */
            print("isVideoFileSaved: " + isVideoFileSaved +
                    ", isVideoCanceled: " + isVideoCanceled);

            /* video table의 해당 record를 삭제한다. */
            SQL = "delete from " + MemoDatabase.TABLE_VIDEO +
                    " where _ID = '" + memoVideoId + "'";
            print("SQL: " + SQL);
            if (MultiMemoActivity.mDatabase != null) {
                MultiMemoActivity.mDatabase.execSQL(SQL);
            }

            videoId = -1;
            /* memo table의 해당 record의 video id 값을 -1로 바꾼다. */
            SQL = "update " + MemoDatabase.TABLE_MEMO +
                    " set " +
                    " ID_VIDEO = '" + videoId + "'" +
                    " where _ID = '" + memoId + "'";
            print("SQL: " + SQL);
            if (MultiMemoActivity.mDatabase != null) {
                MultiMemoActivity.mDatabase.execSQL(SQL);
            }

            memoVideoId = String.valueOf(videoId);
        }


        /****** Voice 처리 *****************************************************/
        String voiceFilename = insertVoice();
        int voiceId = -1;

        if (voiceFilename != null) {
            /**
             * 2,3에 해당하는 경우로 DB 처리.
             * 기존에 저장된 voice가 없고, 새로 그려진 경우에 해당
             * 해당 사진의 uri를 찾는다.
             */

            SQL = "select _ID from " + MemoDatabase.TABLE_VOICE +
                    " where URI = '" + voiceFilename + "'";
            print("SQL: " + SQL);

            if (MultiMemoActivity.mDatabase != null) {
                Cursor cursor = MultiMemoActivity.mDatabase.rawQuery(SQL);
                if (cursor.moveToNext()) {
                    voiceId = cursor.getInt(0);
                }
                cursor.close();
            }
            print("voiceId: " + voiceId);

            // memo id에 해당하는 record의 voice id 값을 업데이트 한다. : -1 -> 유효 id.
            SQL = "update " + MemoDatabase.TABLE_MEMO +
                    " set " + "ID_VOICE = '" + voiceId + "'" +
                    " where _id = '" + memoId + "'";
            print("SQL: " + SQL);
            if (MultiMemoActivity.mDatabase != null) {
                MultiMemoActivity.mDatabase.execSQL(SQL);
            }
            memoVoiceId = String.valueOf(voiceId);
        }
        /* voiceFilename이 비어 있으므로, 이전에 저장했던 파일을 지운 경우이다. */
        else if (isVoiceFileSaved && isVoiceCanceled) {
            /**
             * 위의 1의 경우를 여기서 처리함.
             */
            print("isVoiceFileSaved: " + isVoiceFileSaved +
                    ", isVoiceCanceled: " + isVoiceCanceled);

            /* voice table의 해당 record를 삭제한다. */
            SQL = "delete from " + MemoDatabase.TABLE_VOICE +
                    " where _ID = '" + memoVoiceId + "'";
            print("SQL: " + SQL);
            if (MultiMemoActivity.mDatabase != null) {
                MultiMemoActivity.mDatabase.execSQL(SQL);
            }

            voiceId = -1;
            /* memo table의 해당 record의 voice id 값을 -1로 바꾼다. */
            SQL = "update " + MemoDatabase.TABLE_MEMO +
                    " set " +
                    " ID_VOICE = '" + voiceId + "'" +
                    " where _ID = '" + memoId + "'";
            print("SQL: " + SQL);
            if (MultiMemoActivity.mDatabase != null) {
                MultiMemoActivity.mDatabase.execSQL(SQL);
            }

            memoVoiceId = String.valueOf(voiceId);
        }

        /****** MEMO 처리 : INPUT_DATE, TEXT ***********************************************/

        print("modifyMemo(): memoDateTime: " + memoDateTime);

        /* Memo table의 INPUT_DATE, TEXT는 그냥 update */
        SQL = "update " + MemoDatabase.TABLE_MEMO +
                " set " +
                " INPUT_DATE = DATETIME('" + memoDateTime + "'), " +
                " CONTENTS_TEXT = '" + memoText + "'" +
                " where _ID = '" + memoId + "'";
        print("SQL: " + SQL);

        if (MultiMemoActivity.mDatabase != null) {
            MultiMemoActivity.mDatabase.execSQL(SQL);
        }

        /*
        intent.putExtra(BasicInfo.KEY_MEMO_TEXT, memoText);
        intent.putExtra(BasicInfo.KEY_MEMO_DATE, memoDate);

        intent.putExtra(BasicInfo.KEY_ID_PHOTO, memoPhotoId);
        intent.putExtra(BasicInfo.KEY_ID_VIDEO, memoVideoId);
        intent.putExtra(BasicInfo.KEY_ID_VOICE, memoVoiceId);
        intent.putExtra(BasicInfo.KEY_ID_HANDWRITING, memoHandwritingId);

        intent.putExtra(BasicInfo.KEY_URI_PHOTO, memoPhotoUri);
        intent.putExtra(BasicInfo.KEY_URI_VIDEO, memoVideoUri);
        intent.putExtra(BasicInfo.KEY_URI_VOICE, memoVoiceUri);
        intent.putExtra(BasicInfo.KEY_URI_HANDWRITING, memoHandwritingUri);
        */

        setResult(RESULT_OK, intent);
        finish();
    }

    private void deleteMemo() {
        // photo
        print("deleting previous photo record and files: "
                + memoPhotoId + ", " + memoPhotoUri);

        String SQL = "delete from " + MemoDatabase.TABLE_PHOTO
                    + " where _id = '" + memoPhotoId + "'";
        print("SQL: " + SQL);
        if (MultiMemoActivity.mDatabase != null) {
            MultiMemoActivity.mDatabase.execSQL(SQL);
        }
        File photoFile = new File(BasicInfo.FOLDER_PHOTO + memoPhotoUri);
        if (photoFile.exists()) {
            photoFile.delete();
        }

        // handwriting
        print("deleting previous handwriting record and files: "
                + memoPhotoId + ", " + memoPhotoUri);

        SQL = "delete from " + MemoDatabase.TABLE_HANDWRITING
                + " where _id = '" + memoHandwritingId + "'";
        print("SQL: " + SQL);
        if (MultiMemoActivity.mDatabase != null) {
            MultiMemoActivity.mDatabase.execSQL(SQL);
        }
        File handwritingFile = new File(BasicInfo.FOLDER_PHOTO + memoHandwritingUri);
        if (handwritingFile.exists()) {
            handwritingFile.delete();
        }


        // video
        print("deleting previous video record and files: "
                + memoVideoId + ", " + memoVideoUri);

        SQL = "delete from " + MemoDatabase.TABLE_VIDEO
                + " where _id = '" + memoVideoId + "'";
        print("SQL: " + SQL);
        if (MultiMemoActivity.mDatabase != null) {
            MultiMemoActivity.mDatabase.execSQL(SQL);
        }
        File videoFile = new File(BasicInfo.FOLDER_VIDEO + memoVideoUri);
        if (videoFile.exists()) {
            videoFile.delete();
        }

        // voice
        print("deleting previous voice record and files: "
                + memoVoiceId + ", " + memoVoiceUri);

        SQL = "delete from " + MemoDatabase.TABLE_VOICE
                + " where _id = '" + memoVoiceId + "'";
        print("SQL: " + SQL);
        if (MultiMemoActivity.mDatabase != null) {
            MultiMemoActivity.mDatabase.execSQL(SQL);
        }
        File voiceFile = new File(BasicInfo.FOLDER_VOICE + memoVoiceUri);
        if (voiceFile.exists()) {
            voiceFile.delete();
        }


        // memo
        print("deleting previous memo record: " + memoId);
        SQL = "delete from " + MemoDatabase.TABLE_MEMO + " where _id = '"
                + memoId + "'";
        print("SQL: " + SQL);
        if (MultiMemoActivity.mDatabase != null) {
            MultiMemoActivity.mDatabase.execSQL(SQL);
        }

        setResult(RESULT_OK);

        finish();
    }

    private void print(String message) {
        Log.d(TAG, message + "\n");
    }

}
