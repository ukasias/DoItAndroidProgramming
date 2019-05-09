package com.ukasias.android.doitmission18;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity
        implements InputFragment.SaveBook, SearchFragment.loadBooks {
    private static final String TAG = "DoItMission18_Main";

    private final String DATABASE_NAME = "booksInfo";
    private final String DATABASE_TABLE_NAME = "books";
    private int DATABASE_VERSION = 1;

    Button inputButton;
    Button searchButton;

    SQLiteDatabase db;
    BooksInfoOpenHelper helper;

    InputFragment inputFragment;
    SearchFragment searchFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        inputButton = findViewById(R.id.inputButton);
        searchButton = findViewById(R.id.searchButton);

        helper = new BooksInfoOpenHelper(this);
        db = helper.getWritableDatabase();

        if (helper == null) {
            print("helper is null");
        }
        else {
            print("helper is not null");
        }

        if (db == null) {
            print("db is null");
        }
        else {
            print("db is not null");
        }

        inputFragment = new InputFragment();
        searchFragment = new SearchFragment();

        inputButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "Hello this is inputButton");
                getSupportFragmentManager().beginTransaction().replace(R.id.container,
                        inputFragment).commit();
            }
        });

        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "Hello this is searchButton");
                getSupportFragmentManager().beginTransaction().replace(R.id.container,
                        searchFragment).commit();
            }
        });

                
		getSupportFragmentManager().beginTransaction().replace(R.id.container,
                        searchFragment).commit();

        openOrCreateTable();
    }

    public void openOrCreateTable() {
        String[] tableName = new String[] { DATABASE_TABLE_NAME, };

        try {
            if (db == null) {
                Log.d(TAG, "openOrCreateTable() - Database null");
                return;
            }
            else {
                Log.d(TAG, "openOrCreateTable() - Database not null");
            }
            Cursor cursor1 = db.rawQuery("select count(*) from sqlite_master where name=?",
                    tableName);

            if (cursor1.getCount() > 0) {
                cursor1.moveToNext();
                if (cursor1.getInt(0) == 1) {
                    Log.d(TAG, "openOrCreateTable() - " + DATABASE_TABLE_NAME + " table exists");
                    return;
                } else {
                    Log.d(TAG, "openOrCreateTable() - " + DATABASE_TABLE_NAME + " table doesn't exist");
                }
            }
            cursor1.close();
        }
        catch(Exception e) {
            e.printStackTrace();
            return;
        }

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

    @Override
    public ContentValues[] loadTable() {
        try {
            if (db == null) {
                Log.d(TAG, "loadTable() - Database null");
                return null;
            }

            Cursor cursor = db.rawQuery("select title, author, contents from " + DATABASE_TABLE_NAME, null);
            int count = cursor != null ? cursor.getCount() : 0;
            ContentValues[] values = count > 0 ? new ContentValues[count] : null;

            for (int i = 0; i < count; i++) {
                cursor.moveToNext();
                values[i] = new ContentValues();
                values[i].put("title", cursor.getString(0));
                values[i].put("author", cursor.getString(1));
                values[i].put("contents", cursor.getString(2));
            }
            print("loadTable() - " + count + " items loaded.");

            if (cursor != null) {
                cursor.close();
            }
            return values;
        }
        catch(Exception e) {
            print("loadTable() - exception occured.");
            e.printStackTrace();
        }

        return null;
    }

    @Override
    public void save(String title, String author, String contents) {
        ContentValues recordValues = new ContentValues();
        recordValues.put("title", title);
        recordValues.put("author", author);
        recordValues.put("contents", contents);

        int result = db == null? -1 : (int) db.insert(DATABASE_TABLE_NAME, null,
                    recordValues);

        print("save() - result:" + result);
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
