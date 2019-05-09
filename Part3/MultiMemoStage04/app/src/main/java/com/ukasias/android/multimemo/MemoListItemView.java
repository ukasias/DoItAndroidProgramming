package com.ukasias.android.multimemo;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class MemoListItemView extends LinearLayout {
    private static final String TAG = "MemoListItemView";
    private Context context;

    private ImageView itemPhoto;
    private TextView itemDateTime;
    private TextView itemText;
    private ImageView itemVideoState;
    private ImageView itemVoiceState;
    private ImageView itemHandWriting;

    private String videoUri;
    private String voiceUri;

    private Bitmap bitmap;

    MemoListItemView(Context context) {
        super(context);
        this.context = context;

        init();
    }

    private void init() {
        LayoutInflater inflater =
                (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.memo_listitem, this, true);

        itemPhoto = findViewById(R.id.itemPhoto);
        itemDateTime = findViewById(R.id.itemDate);
        itemText = findViewById(R.id.itemText);
        itemVideoState = findViewById(R.id.itemVideoState);
        itemVoiceState = findViewById(R.id.itemVoiceState);
        itemHandWriting = findViewById(R.id.itemHandWriting);

        itemVideoState.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (videoUri != null
                        && videoUri.trim().length() > 0
                        && !videoUri.equals("-1")) {
                    showVideoPlayingActivity();
                }
                else {
                    toast("재생할 동영상이 없습니다.");
                }
            }
        });

        itemVoiceState.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (voiceUri != null
                        && voiceUri.trim().length() > 0
                        && !voiceUri.equals("-1")) {
                    showVoicePlayingActivity();
                }
                else {
                    toast("재생할 음성이 없습니다.");
                }
            }
        });
    }

    // date, text, photo, handwriting 설정
    public void setContents(int index, String data) {
        switch(index) {
            case 0: // dateTime
                itemDateTime.setText(data);
                break;
            case 1: // text
                itemText.setText(data);
                break;
            case 2: // photo
                if (data == null || data.equals("-1") || data.equals("")) {
                    itemPhoto.setImageResource(R.drawable.person);
                }
                else {
                    if (bitmap != null) {
                        bitmap.recycle();
                    }
                    BitmapFactory.Options options = new BitmapFactory.Options();
                    options.inSampleSize = 8;

                    bitmap = BitmapFactory.decodeFile(
                            BasicInfo.FOLDER_PHOTO + data,
                            options);
                    itemPhoto.setImageBitmap(bitmap);
                }
                break;
            case 3:	// handwriting
                if (data == null || data.equals("-1") || data.equals("")) {
                    itemHandWriting.setImageBitmap(null);
                }
                else {
                    itemHandWriting.setImageURI(
                            Uri.parse(BasicInfo.FOLDER_HANDWRITING + data));
                }
                break;
            default:
                throw new IllegalArgumentException();
        }
    }

    public void setMediaState(String videoUri, String voiceUri) {
        this.videoUri = videoUri;
        this.voiceUri = voiceUri;

        if (videoUri == null || videoUri.trim().length() < 1 || videoUri.equals("-1")) {
            itemVideoState.setImageResource(R.drawable.icon_video_empty);
        }
        else {
            itemVideoState.setImageResource(R.drawable.icon_video);
        }

        if (voiceUri == null || voiceUri.trim().length() < 1 || voiceUri.equals("-1")) {
            itemVoiceState.setImageResource(R.drawable.icon_voice_empty);
        }
        else {
            itemVoiceState.setImageResource(R.drawable.icon_voice);
        }
    }

    private void showVideoPlayingActivity() {
        Intent intent = new Intent(context, VideoPlayActivity.class);
        if (BasicInfo.isAbsoluteVideoPath(videoUri)) {
            intent.putExtra(BasicInfo.KEY_URI_VIDEO,
                    BasicInfo.FOLDER_VIDEO + videoUri);
        }
        else {
            intent.putExtra(BasicInfo.KEY_URI_VIDEO,
                    videoUri);
        }

        context.startActivity(intent);
    }

    private void showVoicePlayingActivity() {
        Intent intent = new Intent(context, VoicePlayActivity.class);
        intent.putExtra(BasicInfo.KEY_URI_VOICE,
                BasicInfo.FOLDER_VOICE + voiceUri);
        context.startActivity(intent);
    }


    private void print(String message) {
        Log.d(TAG, message + "\n");
    }

    private void toast(String message) {
        Toast.makeText(context, message, Toast.LENGTH_LONG).show();
    }
}
