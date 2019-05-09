package com.ukasias.android.multimemo;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.ukasias.android.multimemo.common.TitleBackgroundButton;
import com.ukasias.android.multimemo.common.TitleBitmapButton;
import com.ukasias.android.multimemo.db.MemoDatabase;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class MemoInsertActivity extends AppCompatActivity {
    private final String TAG = "MemoInserActivity";

    private TitleBackgroundButton titleBackgroundButton;
    private ImageView photoView;
    private TitleBitmapButton videoButton;
    private TitleBitmapButton voiceButton;
    private EditText textView;
    private View handView;
    private TitleBitmapButton textButton;
    private TitleBitmapButton handButton;
    private TitleBitmapButton dateButton;
    private TitleBitmapButton saveButton;
    private TitleBitmapButton cancelButton;

    /**
     * Memo Mode,   Id, Date, Text,
     *              Id_Photo/Uri_Photo,
     *              Id_Video/Uri_Video, Id_Voice/Uri_Voice,
     *              Id_Handwriting/Uri_Handwriting,
     */
    private String memoMode;
    private String memoId;
    private String memoDate;
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
        videoButton = findViewById(R.id.videoButton);
        voiceButton = findViewById(R.id.voiceButton);

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

        textButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!textButton.isSelected()) {
                    textView.setVisibility(View.VISIBLE);
                    handView.setVisibility(View.GONE);  /* View.INVISIBLE이 아니네? */
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
    }

    private void setBottomButtons() {
        saveButton = findViewById(R.id.saveButton);
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
            }
            else {
                print("insert");
                titleBackgroundButton.setText("새 메모");
                saveButton.setText("저장");

                processIntent(null);
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

            memoDate = intent.getStringExtra(BasicInfo.KEY_MEMO_DATE);
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

            memoDate = BasicInfo.dateKorFormat.format(new Date());
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
        print("setInitMemo(): " + memoDate);

        dateButton.setText(memoDate);
        textView.setText(memoText);

        if (memoPhotoId.equals("") || memoPhotoId.equals("-1")) {
            /* default person picture */
            photoView.setImageResource(R.drawable.person_add);
        }
        else {
            isPhotoFileSaved = true;
            photoView.setImageURI(Uri.parse(BasicInfo.FOLDER_PHOTO + memoPhotoUri));
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
                memoDate = dateButton.getText().toString();
            } else {
                dateButton.setText(str_year + "-" + str_month + "-" + str_day);
                memoDate = dateButton.getText().toString();
            }
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
        }

        return builder.create();
    }

    private boolean parseDateText() {
        /*
        String insertDateStr = dateButton.getText().toString();
        try {
            Date insertDate = BasicInfo.dateKorFormat.parse(insertDateStr);
            memoDate = BasicInfo.dateFormat.format(insertDate);
        }
        catch(ParseException pe) {
            print("Exception in parsing date: " + insertDateStr);
            pe.printStackTrace();
            return false;
        }*/

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
        }
    }

    private boolean checkCapturedPhotoFile() {
        File file = new File(BasicInfo.FOLDER_PHOTO + "captured");
        if (file.exists()) {
            return true;
        }
        return false;
    }

    private void saveMemo() {
        print("saveMemo()");

        String photoFilename = insertPhoto();
        int photoId = -1;

        String SQL = null;

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

        print("saveMemo() 01: memoDate: " + memoDate);
        try {
            memoDate =
                    BasicInfo.dateFormat.format(
                            BasicInfo.dateKorFormat.parse(memoDate));
        }
        catch(ParseException pe) {
            Log.e(TAG, "ParseException in parsing memoDate", pe);
        }
        print("saveMemo() 02: memoDate: " + memoDate);

        SQL = "insert into " + MemoDatabase.TABLE_MEMO +
                "(INPUT_DATE, CONTENTS_TEXT, ID_PHOTO, ID_VIDEO, ID_VOICE, ID_HANDWRITING) values(" +
                "DATETIME('" + memoDate + "'), " +
                "'" + memoText +"', " +
                "'" + photoId + "', " +
                "'" + "" + "', " +
                "'" + "" + "', " +
                "'" + "" + "')";
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

        String SQL = null;

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

        /****** MEMO 처리 : INPUT_DATE, TEXT ***********************************************/

        print("modifyMemo() 01: memoDate: " + memoDate);
        try {
            memoDate =
                    BasicInfo.dateFormat.format(
                            BasicInfo.dateKorFormat.parse(memoDate));
        }
        catch(ParseException pe) {
            Log.e(TAG, "ParseException in parsing memoDate", pe);
        }
        print("modifyMemo() 02: memoDate: " + memoDate);

        /* Memo table의 INPUT_DATE, TEXT는 그냥 update */
        SQL = "update " + MemoDatabase.TABLE_MEMO +
                " set " +
                " INPUT_DATE = DATETIME('" + memoDate + "'), " +
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

    private void print(String message) {
        Log.d(TAG, message + "\n");
    }
}
