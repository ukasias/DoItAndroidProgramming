package com.ukasias.android.multimemo;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.ukasias.android.multimemo.MemoListAdapter;
import com.ukasias.android.multimemo.db.MemoDatabase;

import com.ukasias.android.multimemo.common.TitleBackgroundButton;
import com.ukasias.android.multimemo.common.TitleBitmapButton;

import java.io.File;
import java.text.ParseException;

public class MultiMemoActivity extends AppCompatActivity {
    private static final String TAG = "MultiMemoActivity";

    TitleBackgroundButton titleBackgroundButton;
    ListView memoList;
    TitleBitmapButton newMemoButton;
    TitleBitmapButton closeButton;

    private MemoListAdapter adapter;

    /**
     * 총 메모 개수
     */
    private int countMemo = 0;

    // 데이터베이스 인스턴스
    public static MemoDatabase mDatabase = null;

    /**
     * permission status
     */
    final int PERMISSION_ACQUIRED = 101;
    final int PERMISSION_NOT_ACQUIRED = 102;
    final int PERMISSION_REQUESTED = 103;


    private int permissionStatus = PERMISSION_NOT_ACQUIRED;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        if(!checkExternalStorageState()) {
            // if there's no SD Card then exit.
            return;
        }
        checkDangerousPermissions();

        titleBackgroundButton = findViewById(R.id.titleBackgroundButton);
        memoList = findViewById(R.id.memoList);
        newMemoButton = findViewById(R.id.newMemoButton);
        closeButton = findViewById(R.id.closeButton);

        adapter = new MemoListAdapter(this);
        memoList.setAdapter(adapter);
        memoList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int posision, long l) {
                viewMemo(posision);
            }
        });

        newMemoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                print("newMemoButton clicked.");

                Intent intent = new Intent(getApplicationContext(), MemoInsertActivity.class);
                intent.putExtra(BasicInfo.KEY_MEMO_MODE, BasicInfo.MEMO_MODE_INSERT);

                startActivityForResult(intent, BasicInfo.REQ_INSERT_ACTIVITY);
            }
        });

        closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                print("closeButton clicked.");
                System.exit(0);//finish();
            }
        });
    }

    private boolean checkExternalStorageState() {
        print("checkExternalStorageState() - " + Environment.getExternalStorageState());

        // SD Card Checking
        if (!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            toast("SD 카드가 없습니다. SD카드를 넣은 후 다시 실행하십시오.");
            print("SD 카드가 없습니다. SD카드를 넣은 후 다시 실행하십시오.");
            return false;
        }
        else print("SD 카드가 있습니다.");

        // "/storage/emulated/0"
        String externalPath = Environment.getExternalStorageDirectory().getAbsolutePath();
        print("File.separator: " + File.separator); // '/'
        print("File.pathSeparator: " + File.pathSeparator); // ':'

        if (!BasicInfo.ExternalPathChecked && externalPath != null) {
            BasicInfo.ExternalPath = externalPath + File.separator;
            print("ExternalPath: " + BasicInfo.ExternalPath);

            BasicInfo.FOLDER_PHOTO = BasicInfo.ExternalPath + BasicInfo.FOLDER_PHOTO;
            BasicInfo.FOLDER_VIDEO = BasicInfo.ExternalPath + BasicInfo.FOLDER_VIDEO;
            BasicInfo.FOLDER_VOICE = BasicInfo.ExternalPath + BasicInfo.FOLDER_VOICE;
            BasicInfo.FOLDER_HANDWRITING
                                   = BasicInfo.ExternalPath + BasicInfo.FOLDER_HANDWRITING;

            BasicInfo.DATABASE_NAME = BasicInfo.ExternalPath + BasicInfo.DATABASE_NAME;

            BasicInfo.ExternalPathChecked = true;
        }

        return true;
    }

    private void checkDangerousPermissions() {
        print("checkDangerousPermissions() called.");
        String[] permissions = {
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.CAMERA
        };
                                    /* 0 */
        int permissionCheck = PackageManager.PERMISSION_GRANTED;
        for (String permission : permissions) {
            permissionCheck =
                    ContextCompat.checkSelfPermission(this, permission);
            if (permissionCheck == PackageManager.PERMISSION_DENIED) {
                break;
            }
        }

        if (permissionCheck == PackageManager.PERMISSION_GRANTED) {
            permissionStatus = PERMISSION_ACQUIRED;
        }
        else {
            boolean shouldShow = false;
            for (String permisstion : permissions) {
                shouldShow = ActivityCompat.shouldShowRequestPermissionRationale(
                        this, permisstion);
                if (shouldShow == true) break;
            }
            if (shouldShow) {
                permissionStatus = PERMISSION_NOT_ACQUIRED;
            }
            else {
                permissionStatus = PERMISSION_REQUESTED;
                ActivityCompat.requestPermissions(
                        this,
                        permissions,
                        1);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(
            int requestCode,
            @NonNull String[] permissions,
            @NonNull int[] grantResults) {

        print("onRequestPermissionsResult() called.(requestCode: " + requestCode + ")");
        if (requestCode == 1) {
            int length = permissions.length;
            for (int i = 0; i < length; i++) {
                if (grantResults[i] == PackageManager.PERMISSION_GRANTED) {
                    print(permissions[i] + "권한이 승인됨.");
                }
                else {
                    print(permissions[i] + "권한이 승인되지 않음.");
                }
            }

            if (grantResults[0] == PackageManager.PERMISSION_GRANTED &&
                    grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                permissionStatus = PERMISSION_ACQUIRED;
            }
            else {
                permissionStatus = PERMISSION_NOT_ACQUIRED;
            }
            handlePermission();
        }
    }

    @Override
    protected void onStart() {
        print("onStart() called.");
        super.onStart();
        if (permissionStatus != PERMISSION_REQUESTED)
            handlePermission();
    }

    private void handlePermission() {
        switch(permissionStatus) {
            case PERMISSION_ACQUIRED:
                initDatabase();
                break;
            case PERMISSION_NOT_ACQUIRED:
                disableUI();
                break;
            case PERMISSION_REQUESTED:
                /* nothing */
                break;
        }
    }

    private void initDatabase() {
        print("initDatabase()");
        openDatabase();
        loadMemoListData();
    }

    private void disableUI() {
        memoList.setEnabled(false);
        newMemoButton.setEnabled(false);
    }

    private void print(String message) {
        Log.d(TAG, message + "\n");
    }

    private void toast(String message) {
        Toast.makeText(
                getApplicationContext(),
                message,
                Toast.LENGTH_LONG
        ).show();
    }

    /**
     * 데이터베이스 열기 (없을 경우 만든다)
     */
    private void openDatabase() {
        print("openDatabase() called.");

        if (mDatabase != null) {
            mDatabase.close();
            mDatabase = null;
        }

        mDatabase = MemoDatabase.getInstance(this);
        boolean isOpen = mDatabase.open();
        if (isOpen) {
            Log.d(TAG, "Memo database is open.");
        }
        else {
            Log.d(TAG, "Memo database is not open.");
        }
    }

    /**
     * 메모 리스트 로딩
     */
    private int loadMemoListData() {
        String SQL =
                "select _id, INPUT_DATE, CONTENTS_TEXT, ID_PHOTO, " +
                "ID_VIDEO, ID_VOICE, ID_HANDWRITING from " + MemoDatabase.TABLE_MEMO +
                " order by INPUT_DATE desc";

        print("SQL: " + SQL);
        int recordCount = -1;
        Cursor cursor = null;

        if (mDatabase != null) {
            cursor = MultiMemoActivity.mDatabase.rawQuery(SQL);

            recordCount = cursor.getCount();
            print("recordCount: " + recordCount);
            adapter.clear();

            for (int i = 0; i < recordCount; i++) {
                cursor.moveToNext();

                String id = cursor.getString(0);
                String date = cursor.getString(1);
                if (date != null) {
                    print("loadMemoListData() - date: " + date);
                    /* date: 2018-08-16 00:00:00 */
                    if (date.length() > 10) {
                        date = date.substring(0, 10);
                    }
                    try {
                        date = BasicInfo.dateKorFormat.format(
                                BasicInfo.dateFormat.parse(date));
                    }
                    catch(ParseException pe) {
                        Log.e(TAG, "ParseException in parsing date", pe);
                    }
                }
                /*
                if (date != null && date.length() > 10) {
                    date = date.substring(0, 10);
                }
                */

                String text = cursor.getString(2);

                String id_photo = cursor.getString(3);
                String uri_photo = getPhotoUriStr(id_photo);
                String id_video = cursor.getString(4);
                String uri_video = getVideoUriStr(id_video);
                String id_voice = cursor.getString(5);
                String uri_voice = getVoiceUriStr(id_voice);
                String id_handwriting = cursor.getString(6);
                String uri_handwriting = getHandWritingUriStr(id_handwriting);

                print("id: " + id);
                print("date: " + date);
                print("text: " + text);
                print("id_photo: " + id_photo + ", uri_photo: " + uri_photo);
                print("id_video: " + id_video + ", uri_video: " + uri_video);
                print("id_voice: " + id_voice + ", uri_voice: " + uri_voice);
                print("id_handwriting: " + id_handwriting + ", uri_handwriting: " + uri_handwriting);

                adapter.addItem(new MemoListItem(id, date, text,
                        id_photo, uri_photo,
                        id_video, uri_video,
                        id_voice, uri_voice,
                        id_handwriting, uri_handwriting));
            }
            cursor.close();

            adapter.notifyDataSetChanged();
        }
        return recordCount;
    }

    private String getPhotoUriStr(String id_photo) {
        String photoUriStr = "";

        if (!id_photo.equals(null) && !id_photo.equals("-1")) {
            String SQL =
                    "select URI from " + MemoDatabase.TABLE_PHOTO +
                            " where _id = " + id_photo + "";
            Cursor resCursor = mDatabase.rawQuery(SQL);
            if (resCursor.getCount() == 1) {
                resCursor.moveToNext();
                photoUriStr = resCursor.getString(0);
                resCursor.close();
            }
        }
        return photoUriStr;
    }

    private String getVideoUriStr(String id_video) {
        return "NOT YET IMPLEMENTED";
    }

    private String getVoiceUriStr(String id_voice) {
        return "NOT YET IMPLEMENTED";
    }

    private String getHandWritingUriStr(String id_handwriting) {
        return "NOT YET IMPLEMENTED";
    }

    private void viewMemo(int position) {
        print("viewMemo(" + position + ") called.");

        MemoListItem item = (MemoListItem) adapter.getItem(position);

        Intent intent = new Intent(getApplicationContext(), MemoInsertActivity.class);

        intent.putExtra(BasicInfo.KEY_MEMO_MODE, BasicInfo.MEMO_MODE_VIEW);
        intent.putExtra(BasicInfo.KEY_MEMO_ID, item.getId());

        intent.putExtra(BasicInfo.KEY_MEMO_DATE, item.getData(0));
        intent.putExtra(BasicInfo.KEY_MEMO_TEXT, item.getData(1));

        intent.putExtra(BasicInfo.KEY_ID_PHOTO, item.getData(2));
        intent.putExtra(BasicInfo.KEY_URI_PHOTO, item.getData(3));

        intent.putExtra(BasicInfo.KEY_ID_VIDEO, item.getData(4));
        intent.putExtra(BasicInfo.KEY_URI_VIDEO, item.getData(5));

        intent.putExtra(BasicInfo.KEY_ID_VOICE, item.getData(6));
        intent.putExtra(BasicInfo.KEY_URI_VOICE, item.getData(7));

        intent.putExtra(BasicInfo.KEY_ID_HANDWRITING, item.getData(8));
        intent.putExtra(BasicInfo.KEY_URI_HANDWRITING, item.getData(9));

        print("id: " + item.getId());
        print("date: " + item.getData(0));
        print("text: " + item.getData(1));
        print("photo id: " + item.getData(2));
        print("photo uri: " + item.getData(3));


        startActivityForResult(intent, BasicInfo.REQ_VIEW_ACTIVITY);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        print("onActivityResult(requestCode: " + requestCode +
                ", resultCode: " + resultCode + ")");

        switch (requestCode) {
            case BasicInfo.REQ_INSERT_ACTIVITY:
                if (resultCode == RESULT_OK) {
                    print("REQ_INSERT_ACTIVITY");
                    //loadMemoListData();
                    // => onStart() -> handlePermission() -> initDatabase() -> loadMemoListData()
                }
                break;

            case BasicInfo.REQ_VIEW_ACTIVITY:
                if (resultCode == RESULT_OK) {
                    print("BasicInfo.REQ_VIEW_ACTIVITY");
                    //loadMemoListData();
                    // => onStart() -> handlePermission() -> initDatabase() -> loadMemoListData()
                }
                break;
        }
    }

}
