package com.ukasias.android.doitmission19;

import android.Manifest;
import android.content.ContentProvider;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.MediaStore;
import android.provider.UserDictionary;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Gallery;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.net.URI;
import java.text.DateFormat;
import java.text.FieldPosition;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class MainActivity extends AppCompatActivity {
    final static String TAG = "DoItMission19";
    final private int REQUEST_ALBUM_PICTURES = 1001;

    TextView textView;
    ListView listView;

    PictureAdapter adapter;

    Boolean go_on;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        textView = findViewById(R.id.textView);
        listView = findViewById(R.id.listView);

        adapter = new PictureAdapter(this);
        listView.setAdapter(adapter);

        go_on = true;

        checkPermissions();
        if (go_on) loadPictures();
    }

    private void loadPictures() {
        // 참고: http://shygiants.github.io/android/2016/01/13/contentresolver.html

        String[] mProjection = {
                MediaStore.Images.Media.DISPLAY_NAME,
                MediaStore.Images.Media.DATE_TAKEN,
                MediaStore.Images.Thumbnails.DATA };

        Cursor _cursor;
        /* this 4 parameters' query() works with at least the API level 26.
           5 parameters' query() available since API level 1.
           6 parameters' query() available since API level 16.
        _cursor = getContentResolver().query(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                mProjection,
                null,
                null);
         */
        _cursor = getContentResolver().query(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                mProjection,
                null,
                null,
                null);

        ArrayList<Uri> pictures = new ArrayList<Uri>();
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy년 MM월 dd일");
        int count;

        if (_cursor != null) {
            count = _cursor.getCount();
            textView.setText(String.valueOf(count) + " 개");

            for (int i = 0; i < count; i++) {
                _cursor.moveToNext();
                calendar.setTimeInMillis(Long.parseLong(_cursor.getString(1)));

                String name = _cursor.getString(0);
                String date = simpleDateFormat.format(calendar.getTime());
                Uri pictureUri = Uri.parse(_cursor.getString(2));
                PictureItem item = new PictureItem(pictureUri, name, date);
                adapter.addItems(item);
                adapter.notifyDataSetChanged();
                print(name + ", " + date + ", " + pictureUri.toString());

                calendar.setTimeInMillis(System.currentTimeMillis());
                print("현재 시간: " + simpleDateFormat.format(calendar.getTime()));
            }
        }
    }

	private void checkPermissions() {
        String[] permissions = {
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.CAMERA
        };

        int permissionCheck = PackageManager.PERMISSION_GRANTED;
        for (int i = 0; i < permissions.length; i++) {
            permissionCheck = ContextCompat.checkSelfPermission(this,
                    permissions[i]);
            if (permissionCheck == PackageManager.PERMISSION_DENIED) {
                break;
            }
        }

        if (permissionCheck == PackageManager.PERMISSION_GRANTED) {
            toast("권한 있음");
        }
        else {
            toast("권한 없음");

            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    permissions[0])) {
                toast("권한 설명 필요함");
            }
            else {
                ActivityCompat.requestPermissions(this,
                        permissions,
                        1);
            }
        }
	}

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if (requestCode == 1) {
            for(int i = 0 ; i < permissions.length; i++) {
                if (grantResults[i] == PackageManager.PERMISSION_GRANTED) {
                    toast(permissions[i] + " 권한이 승인됨");
                }
                else {
                    toast(permissions[i] + " 권한이 승인되지 않음");
                    go_on = false;
                }
            }
        }
    }

    private void print(String message) {
        Log.d(TAG, message + "\n");
    }

    private void toast(String message) {
        Toast.makeText(
                getApplicationContext(),
                message,
                Toast.LENGTH_LONG).show();
    }
}
