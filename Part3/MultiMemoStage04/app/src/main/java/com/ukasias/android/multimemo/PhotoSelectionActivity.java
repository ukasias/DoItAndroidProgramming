package com.ukasias.android.multimemo;

import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.content.res.TypedArray;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.CursorAdapter;
import android.widget.Gallery;
import android.widget.ImageView;
import android.widget.TextView;

import com.ukasias.android.multimemo.common.TitleBitmapButton;

public class PhotoSelectionActivity extends AppCompatActivity {
    public static final String TAG = "PhotoSelectionActivity";

    /**
     * UI components
     */
    private CoverFlow photoGallery;
    private TextView textView;
    private ImageView selectedPhoto;
    private TitleBitmapButton okButton;
    private TitleBitmapButton cancelButton;

    /**
     * For Coverflow spacing
     */
    private static int spacing = -45;

    /**
     * About selected photo
     */
    private Uri selectedPhotoUri;
    private Bitmap selectedPhotoBitmap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo_selection);

        init();
    }

    private void init() {
        selectedPhotoUri = null;
        selectedPhotoBitmap = null;

        setBottomButtons();

        setSelectPhotoLayout();
    }

    private void showParentActivity() {
        /* 이 Activity를 띄울 때 사용된 intent */
        Intent intent = getIntent();

        /* 이 intent에 선택된 URI 정보를 담아 부모에게 넘긴다. */
        if (selectedPhotoUri != null && selectedPhotoBitmap != null) {
            intent.putExtra(BasicInfo.KEY_URI_PHOTO, selectedPhotoUri);
            setResult(RESULT_OK, intent);
        }
        else {
            setResult(RESULT_CANCELED, intent);
        }

        finish();
    }

    private void setBottomButtons() {
        okButton = findViewById(R.id.loading_okBtn);
        okButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showParentActivity();
            }
        });

        cancelButton = findViewById(R.id.loading_cancelBtn);
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void setSelectPhotoLayout() {
        photoGallery = findViewById(R.id.loading_gallery);
        textView = findViewById(R.id.loading_selectPhotoText);
        selectedPhoto = findViewById(R.id.loading_selectedPhoto);
        selectedPhoto.setVisibility(View.VISIBLE);

        /* 외부 저장소의 사진 가져옴 */
        Cursor cursor = getContentResolver().query(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                null, null, null,
                MediaStore.Images.Media.DATE_TAKEN + " DESC");

        PhotoCursorAdapter adapter =
                new PhotoCursorAdapter(this, cursor);

        /**
         * CoverFlow -> Gallery -> AbsSpinner
         * AbssSpinner : void setAdapter() 선언/정의 되어있음.
         */

        photoGallery.setAdapter(adapter);
        photoGallery.setSpacing(spacing);
        print("Count of gallery images: " + photoGallery.getCount());
        if (adapter.getCount() > 0) {
            photoGallery.setSelection(0, true);
        }
        //photoGallery.setSelection(2, true); // -> 왜 position은 2
        photoGallery.setAnimationDuration(3000);
        photoGallery.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                try {
                    Uri uri =
                            ContentUris.withAppendedId(
                                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                                    id);    //개별 이미지에 대한 Uri 생성

                    selectedPhotoUri = uri;
                    BitmapFactory.Options options =
                            new BitmapFactory.Options();
                    options.inSampleSize = 1;

                    selectedPhotoBitmap =
                            BitmapFactory.decodeStream(
                                    getContentResolver().openInputStream(uri),
                                    null,
                                    options);
                    print("Selected Image URI from Album: " + selectedPhotoUri);

                    textView.setVisibility(View.GONE);
                    selectedPhoto.setImageBitmap(selectedPhotoBitmap);
                    selectedPhoto.setVisibility(View.VISIBLE);
                }
                catch(Exception e) {
                    print(e.toString());
                }
            }
        });

    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        print("onWindowFocusChanged(hasFocus: " + hasFocus);
        print("Environment.getExternalStorageDirectory().getAbsolutePath(): " +
                Environment.getExternalStorageDirectory().getAbsolutePath() + ")");

        super.onWindowFocusChanged(hasFocus);

        if (hasFocus) {
            MediaScannerConnection.scanFile(
                    this,
                    new String[]{Environment.getExternalStorageDirectory().getAbsolutePath()},
                    null,
                    new MediaScannerConnection.OnScanCompletedListener() {
                        @Override
                        public void onScanCompleted(String path, Uri uri) {
                            print("Media scan completed...");
                        }
                    });
        }
    }

    private void print(String message) {
        Log.d(TAG, message + "\n");
    }

    class PhotoCursorAdapter extends CursorAdapter {

        private int galleryItemBackground;

        /**
         * 참고: https://m.blog.naver.com/PostView.nhn?blogId=horajjan&logNo=220329510302&proxyReferer=https%3A%2F%2Fwww.google.co.kr%2F
         */

        public PhotoCursorAdapter(Context context, Cursor c) {
            super(context, c);

            /**
             * TypedArray: Resource로부터 읽어들인 array를 담는 container.
             * Resources.Theme.obtainStyledAttributes(AttributeSet, int[], int, int)나
             * Resources.obtainAttributes(AttributeSet, int[])으로 읽어온 array를 담는 container.
             *
             * drawable list의 array를 위해 사용됨,
             * TypedArray is used for an array of drawables listing.
             *
             */

            /**
             * 참고
             * http://www.geeks.gallery/how-to-list-images-from-array-xml-in-android/
             */


            TypedArray array = obtainStyledAttributes(R.styleable.Gallery1);
            galleryItemBackground =
                    array.getResourceId(
                            R.styleable.Gallery1_android_galleryItemBackground,
                            0);
            array.recycle();
        }

        /**
         * 새 View를 만들어 return 하는 역할. (cursor는 현재의 item으로 position 이동 되어 있음)
         */
        @Override
        public View newView(Context context, Cursor cursor, ViewGroup parent) {
            /*
             * 여기서는 ImageView를 만들어 return.
             */

            ImageView v = new ImageView(context);
            v.setLayoutParams(new Gallery.LayoutParams(660, 480));
            v.setBackgroundResource(galleryItemBackground);

            return v;
        }

        /**
         * 화면에 출력할 View에 ContentProvider로부터 받아온 cursor의 내용물을 연결하는 역할.
         * newView()에서 view를 만들고, 여기서 내용물을 채운다.
         */
        @Override
        public void bindView(View view, Context context, Cursor cursor) {
            ImageView img = (ImageView) view;

            /* cursor에서 id 값을 읽어옴 */
            long id =
                    cursor.getLong(
                            cursor.getColumnIndexOrThrow(MediaStore.Images.Media._ID));

            /* id로 content uri를 찾아냄 */
            Uri uri = ContentUris.withAppendedId(
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                    id);
            print("id: " + id + ", uri: " + uri);

            try {
                /* uri로 file stream을 읽어 bitmap 만들고, 설정 */
                BitmapFactory.Options options = new BitmapFactory.Options();
                options.inSampleSize = 8;
                Bitmap bm = BitmapFactory.decodeStream(
                        getContentResolver().openInputStream(uri),
                        null,
                        options);
                img.setImageBitmap(bm);
            }
            catch(Exception e) {}
        }

        private void print(String message) {
            Log.d(TAG, message + "\n");
        }
    }
}
