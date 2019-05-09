package com.ukasias.android.multimemo;

import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.CursorAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.ukasias.android.multimemo.common.TitleBitmapButton;

import java.io.File;

public class VideoSelectionActivity extends AppCompatActivity {
    private static final String TAG = "VideoSelectionActivity";

    private TextView videoText;
    private ListView listView;
    private TitleBitmapButton okButton;
    private TitleBitmapButton cancelButton;

    private String videoUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_video_selection);

        videoText = findViewById(R.id.videoText);
        listView = findViewById(R.id.listView);
        okButton = findViewById(R.id.okButton);
        cancelButton = findViewById(R.id.cancelButton);

        setBottomButtons();

        print("Gallery Data Is Loading..");

        Cursor c = getContentResolver().query(
                MediaStore.Video.Media.EXTERNAL_CONTENT_URI,
                null, null, null,null);

        print("c.count(): " + c.getCount());

        final VideoCursorAdapter adapter = new VideoCursorAdapter(this, c);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                try {
                    Uri uri = ContentUris.withAppendedId(
                            MediaStore.Video.Media.EXTERNAL_CONTENT_URI,
                            id);
                    videoUri = uri.getPath();

                    String str = ((TextView) view).getText().toString();
                    videoText.setText(str);
                    videoText.setSelected(true);
                }
                catch (Exception e) {
                    Log.d(TAG, e.toString());
                }
            }
        });
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);

        if (hasFocus) {
            sendBroadcast(new Intent(
                    Intent.ACTION_MEDIA_SCANNER_SCAN_FILE,
                    Uri.parse(Environment.getExternalStorageDirectory().getAbsolutePath())
            ));
        }
    }

    class VideoCursorAdapter extends CursorAdapter {
        public VideoCursorAdapter(Context context, Cursor cursor) {
            super(context, cursor);
        }

        @Override
        public View newView(Context context, Cursor cursor, ViewGroup parent) {
            TextView videoTitleText = new TextView(context);

            videoTitleText.setTextColor(Color.BLACK);
            videoTitleText.setLayoutParams(new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    200));
            videoTitleText.setGravity(View.TEXT_ALIGNMENT_GRAVITY);
            videoTitleText.setPadding(20, 20, 20, 20);
            videoTitleText.setTextSize(20.0f);

            return videoTitleText;
        }

        @Override
        public void bindView(View view, Context context, Cursor cursor) {
            TextView videoTitleText = (TextView) view;

            long id = cursor.getLong(
                    cursor.getColumnIndexOrThrow(MediaStore.Video.Media._ID));
            String str = cursor.getString(
                    cursor.getColumnIndexOrThrow(MediaStore.Video.Media.TITLE));
            Uri uri = ContentUris.withAppendedId(
                    MediaStore.Video.Media.EXTERNAL_CONTENT_URI, id);
            print(" id -> " + id + ", uri -> " + uri + ", str: " + str);
            /* id -> 165799, uri -> content://media/external/video/media/165799, str: 20170905_194903 */

            try {
                videoTitleText.setText(str);
            }
            catch (Exception e) {}
        }
    }

    private void setBottomButtons() {
        okButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showParentActivity();
            }
        });

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    /**
     * 부모로 돌아가기
     */
    private void showParentActivity() {
        Intent intent = getIntent();
        if (videoUri != null) {
            intent.putExtra(BasicInfo.KEY_URI_VIDEO, videoUri);

            setResult(RESULT_OK, intent);
        }

        finish();
    }

    private void print(String message) {
        Log.d(TAG, message + "\n");
    }
}
