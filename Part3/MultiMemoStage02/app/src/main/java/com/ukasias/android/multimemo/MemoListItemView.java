package com.ukasias.android.multimemo;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class MemoListItemView extends LinearLayout {
    private static final String TAG = "MemoListItemView";
    private Context context;

    private ImageView itemPhoto;
    private TextView itemDate;
    private TextView itemText;
    private ImageView itemVideoState;
    private ImageView itemVoiceState;
    private ImageView itemHandWriting;

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
        itemDate = findViewById(R.id.itemDate);
        itemText = findViewById(R.id.itemText);
        itemVideoState = findViewById(R.id.itemVideoState);
        itemVoiceState = findViewById(R.id.itemVoiceState);
        itemHandWriting = findViewById(R.id.itemHandWriting);
    }

    // date, text, photo, handwriting 설정
    public void setContents(int index, String data) {
        switch(index) {
            case 0: // date
                itemDate.setText(data);
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
            case 3:
                if (data == null || data.equals("-1") || data.equals("")) {
                    itemHandWriting.setImageBitmap(null);
                }
                else {
                    itemHandWriting.setImageURI(
                            Uri.parse(BasicInfo.FOLDER_PHOTO + data));
                }
                break;
            default:
                break;
        }
    }

    public void setMediaState(Object video, Object voice) {
        if (video == null) {
            itemVideoState.setImageResource(R.drawable.icon_video_empty);
        }
        else {
            itemVideoState.setImageResource(R.drawable.icon_video);
        }

        if (voice == null) {
            itemVoiceState.setImageResource(R.drawable.icon_voice_empty);
        }
        else {
            itemVoiceState.setImageResource(R.drawable.icon_voice);
        }
    }

    private void print(String message) {
        Log.d(TAG, message + "\n");
    }
}
