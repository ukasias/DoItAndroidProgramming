package com.ukasias.android.doitmission17;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import java.util.concurrent.ThreadPoolExecutor;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "DoItMission-17";

    private final String DATABASE_NAME = "booksInfo";
    private final String DATABASE_TABLE_NAME = "books";
    private final int DATABASE_VERSION = 1;

    EditText titleText;
    EditText authorText;
    EditText contentsText;
    Button saveButton;

    SQLiteDatabase db;
    BooksInfoOpenHelper helper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        titleText = findViewById(R.id.titleText);
        authorText = findViewById(R.id.authorText);
        contentsText = findViewById(R.id.contentsText);
        saveButton = findViewById(R.id.saveButton);

        helper = new BooksInfoOpenHelper(this);
        db = helper.getWritableDatabase();

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                save();
            }
        });

        openOrCreateTable();
        load();
    }

    public void openOrCreateTable() {
        String[] tableName = new String[] { DATABASE_TABLE_NAME, };

        Cursor cursor1 = db.rawQuery("select count(*) from sqlite_master where name=?",
                tableName);

        if (cursor1.getCount() > 0) {
            cursor1.moveToNext();
            if (cursor1.getInt(0) == 1) {
                Log.d(TAG, DATABASE_TABLE_NAME + " table exists");
                return;
            } else {
                Log.d(TAG, DATABASE_TABLE_NAME + " table doesn't exist");
            }
        }
        cursor1.close();

        String CREATE_SQL = "create table " + DATABASE_TABLE_NAME + "("
                + "_id integer PRIMARY KEY autoincrement, "
                + "title text, "
                + "author text, "
                + "contents text)";

        try {
            db.execSQL(CREATE_SQL);
            Log.d(TAG, DATABASE_TABLE_NAME + "table is created");
        }
        catch(Exception e) {
            e.printStackTrace();
        }
    }

    public void load() {

        Cursor cursor = db.rawQuery
                ("select title, author, contents from " + DATABASE_TABLE_NAME, null);

        if (cursor.getCount() > 0) {
            cursor.moveToNext();
            titleText.setText(cursor.getString(0));
            authorText.setText(cursor.getString(1));
            contentsText.setText(cursor.getString(2));
            print("load() - getCount() : " + cursor.getCount() + " data exists");
        }
        else {
            print("load() - getCount() : " + cursor.getCount() + " data doesn't exist");
        }
        cursor.close();
    }

    public void save() {
        Cursor cursor = db.rawQuery("select * from " + DATABASE_TABLE_NAME, null);

        String[] id = new String[] { "1" };
        ContentValues recordValues = new ContentValues();
        recordValues.put("title", titleText.getText().toString());
        recordValues.put("author", authorText.getText().toString());
        recordValues.put("contents", contentsText.getText().toString());

        if (cursor.getCount() > 0) {
            int result = db.update(DATABASE_TABLE_NAME,
                    recordValues, "_id = ?", id);

            if (result == 1) { print("save() - data exists, data updated."); }
            else { print("save() - data exists, data failed to be updated."); }
        }
        else {
            int result = (int) db.insert(DATABASE_TABLE_NAME, null,
                    recordValues);

            if (result == 1) {
                print("save() - data doesn't exist, data saved.");
            } else {
                print("save() - data doesn't exist, data failed to be saved.");
            }
        }

        cursor.close();
    }

    class BooksInfoOpenHelper extends SQLiteOpenHelper {

        public BooksInfoOpenHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        /*
         * When there's no SQLiteDatabase named 'DATABASE_NAME',
         * onCreate() is called. if there is. this is not called.
         */
        @Override
        public void onCreate(SQLiteDatabase sqLiteDatabase) {
            print("onCreate() - " + DATABASE_NAME + " be created.");
        }

        @Override
        public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
            print("onUpgrade(oldVersion: " + oldVersion + ", " + newVersion + ")");
        }

        @Override
        public void onOpen(SQLiteDatabase sqLiteDatabase) {
            print("onOpen(sqLiteDatabase: " + sqLiteDatabase.getPath());
        }
    }

    private void print(String str) {
        Log.d(TAG, str + "\n");
    }
}
