package com.ukasias.android.sampledatabasequery;

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
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {
    public static final String TAG = "MainActivity";
    private static String DATABASE_NAME = "null";
    private static String TABLE_NAME = "employee";
    private static int DATABASE_VERSION = 1;

    EditText editText;
    Button queryButton;
    TextView statusText;

    private SQLiteDatabase db;
    private DatabaseHelper dbHelper;
    String dbName;
    String tableName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        editText = (EditText) findViewById(R.id.editText);
        queryButton = (Button) findViewById(R.id.queryButton);
        statusText = (TextView) findViewById(R.id.status);

        queryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DATABASE_NAME = editText.getText().toString();
                boolean isOpen = openDatabase();
                if (isOpen) {
                    try {
                        Thread.sleep(1000);
                        executeRawQuery();
                        executeRawQueryParam();
                    }
                    catch(InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    private boolean openDatabase() {
        println("opening database [" + DATABASE_NAME + "]\n");

        dbHelper = new DatabaseHelper(this);
        if (dbHelper != null) {
            db = dbHelper.getWritableDatabase();
        }

        if (db == null) {
            return false;
        }

        return true;
    }

    private void executeRawQuery() {
        println("executeRawQuery() called");

        Cursor c1 = db.rawQuery("select count(*) as Total from " + TABLE_NAME,
                null);
        println("cursor count : " + c1.getCount());

        c1.moveToNext();
        println("record count : " + c1.getInt(0));

        c1.close();
    }

    private void executeRawQueryParam() {
        println("executeRawQueryParam() executed\n");

        String SQL = "select name, age, phone from " + TABLE_NAME
                + " where age > ?";
        String[] args = {"30"};

        Cursor c1 = db.rawQuery(SQL, args);
        int recordCount = c1.getCount();
        println("cursor count : " + recordCount + "\n");

        for (int i = 0; i < recordCount; i++) {
            c1.moveToNext();
            String name = c1.getString(0);
            int age = c1.getInt(1);
            String phone = c1.getString(2);

            println("Record #" + i + " : " + name + ", " + age + ", " + phone);
        }
        c1.close();
    }

    private void println(String print) {
        statusText.append(print + "\n");
    }
    private class DatabaseHelper extends SQLiteOpenHelper {
        public DatabaseHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        @Override
        public void onConfigure(SQLiteDatabase db) {
            super.onConfigure(db);
        }

        @Override
        public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            super.onDowngrade(db, oldVersion, newVersion);
        }

        @Override
        public void onOpen(SQLiteDatabase db) {
            println("opened database [" + DATABASE_NAME + "].");
        }

        @Override
        public void onCreate(SQLiteDatabase sqLiteDatabase) {
            println("creating table [" + TABLE_NAME + "].");

            try {
                String DROP_SQL = "drop table if exists " + TABLE_NAME;
                sqLiteDatabase.execSQL(DROP_SQL);
            }
            catch (Exception e) {
                Log.e(TAG, "Exception in DROP_SQL", e);
            }

            String CREATE_SQL = "create table " + TABLE_NAME + "("
                            + "_id integer PRIMARY KEY autoincrement, "
                            + "name text, "
                            + "age integer, "
                            + "phone text)";

            try {
                db.execSQL(CREATE_SQL);
            }
            catch (Exception e) {
                Log.e(TAG, "Exception in CREATE_SQL", e);
            }

            println("inserting records.");

            try {
                db.execSQL("insert into " + TABLE_NAME + "(name, age, phone) values (" +
                        "'John', 20, '010-7788-1234')");
                db.execSQL("insert into " + TABLE_NAME + "(name, age, phone) values (" +
                        "'WanKeun Cho', 37, '010-2962-6344')");
                db.execSQL("insert into " + TABLE_NAME + "(name, age, phone) values (" +
                        "'JaeKeun Cho', 39, '010-3210-6344')");
            }
            catch (Exception e) {
                Log.e(TAG, "Exception in insert SQL", e);
            }
        }

        @Override
        public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
            Log.w(TAG, "Upgrading database from version " + oldVersion +
                        " to " + newVersion + ".");
        }
    }
}
