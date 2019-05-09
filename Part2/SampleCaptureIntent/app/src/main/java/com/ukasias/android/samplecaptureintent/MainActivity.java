package com.ukasias.android.samplecaptureintent;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.File;

public class MainActivity extends AppCompatActivity {
    public static final int REQUEST_IMAGE_CAPTURE = 1001;

    Button takePictureButton;
    ImageView imageView;

    private File file;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        takePictureButton = findViewById(R.id.takePictureButton);
        imageView = findViewById(R.id.imageView);

        takePictureButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                intent.putExtra(MediaStore.EXTRA_OUTPUT,
                        Uri.fromFile(file));
                if (intent.resolveActivity(getPackageManager()) != null) {
                    startActivityForResult(
                            intent, REQUEST_IMAGE_CAPTURE);
                }
            }
        });

        file = createFile();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_IMAGE_CAPTURE &&
                resultCode == RESULT_OK) {
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inSampleSize = 8;
            Bitmap bitmap = BitmapFactory.decodeFile(file.getAbsolutePath(), options);

            imageView.setImageBitmap(bitmap);

            Toast.makeText(getApplicationContext(),
                    "사진 찍기 성공",
                    Toast.LENGTH_LONG).show();
        }
        else {
            Toast.makeText(getApplicationContext(),
                    "사진 찍기 실패",
                    Toast.LENGTH_LONG).show();
        }
    }

    private File createFile() {
        String imageFileName = "test.jpg";
        File storageDir = Environment.getExternalStorageDirectory();
        File curFile = new File(storageDir, imageFileName);

        return curFile;
    }
}
