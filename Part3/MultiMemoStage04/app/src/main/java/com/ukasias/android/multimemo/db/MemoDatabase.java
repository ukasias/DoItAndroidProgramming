package com.ukasias.android.multimemo.db;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.ukasias.android.multimemo.BasicInfo;

/**
 * 메모 데이터베이스
 */

public class MemoDatabase {
    public static final String TAG = "MemoDatabase";

    /**
     * 싱글톤 인스턴스
     */
    private static MemoDatabase database;

    /**
     * Context 객체
     */
    private Context context;

    /**
     * DatabaseHelper 객체
     */
    private DatabaseHelper dbHelper;

    /**
     * SQLiteDatabase 객체
     */
    private SQLiteDatabase db;

    /**
     * table names and version info
     */
    private static int DATABASE_VERSION = 1;

    public static String TABLE_MEMO = "MEMO";

    public static String TABLE_PHOTO = "PHOTO";
    public static String TABLE_VIDEO = "VIDEO";
    public static String TABLE_VOICE = "VOICE";
    public static String TABLE_HANDWRITING = "HANDWRITING";

    /**
     * 생성자
     * @param context
     */
    MemoDatabase(Context context) {
        this.context = context;
    }

    public static MemoDatabase getInstance(Context context) {
        if (database == null) {
            database = new MemoDatabase(context);
        }
        return database;
    }

    /**
     * 데이터베이스 열기
     */
    public boolean open() {
        println("opening database [" + BasicInfo.DATABASE_NAME + "].");

        dbHelper = new DatabaseHelper(context);
        db = dbHelper.getWritableDatabase();

        return true;
    }

    /**
     * 데이터베이스 닫기
     */
    public void close() {
        println("closing database [" + BasicInfo.DATABASE_NAME + "].");

        db.close();

        database = null;    /* singleton instance */
    }

    public Cursor rawQuery(String SQL) {
        println("rawQuery(" + SQL + ") executed.");

        Cursor c = null;
        try {
            c = db.rawQuery(SQL, null);
        }
        catch(Exception e) {
            Log.e(TAG, "Exception in rawQuery()");
        }

        return c;
    }

    public boolean execSQL(String SQL) {
        println("execSQL() executed.");

        try {
            db.execSQL(SQL);
        }
        catch(Exception e) {
            Log.e(TAG, "Exception in execSQL()");
            return false;
        }

        return true;
    }

    private class DatabaseHelper extends SQLiteOpenHelper {
        public DatabaseHelper(Context context) {
            super(
                    context,
                    BasicInfo.DATABASE_NAME,
                    null,
                    DATABASE_VERSION);
            println("BasicInfo.DATABASE_NAME: " + BasicInfo.DATABASE_NAME);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            println("creating database [" + BasicInfo.DATABASE_NAME + "].");

            /**
             * create table MEMO
             */
            println("creating table [" + TABLE_MEMO + "].");

            String DROP_SQL = "drop table if exists " + TABLE_MEMO;
            try {
                db.execSQL(DROP_SQL);
            } catch(Exception e) {
                Log.e(TAG, "Exception in drop table " + TABLE_MEMO, e);
            }

            String CREATE_SQL = "create table " + TABLE_MEMO + "(" +
                    "_id INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT, " +
                    "INPUT_DATE TIMESTAMP DEFAULT CURRENT_TIMESTAMP, " +
                    "CONTENTS_TEXT TEXT DEFAULT '', " +
                    "ID_PHOTO INTEGER, " +
                    "ID_VIDEO INTEGER, " +
                    "ID_VOICE INTEGER, " +
                    "ID_HANDWRITING INTEGER, " +
                    "CREATE_DATE TIMESTAMP DEFAULT CURRENT_TIMESTAMP " +
                    ")";
            try {
                db.execSQL(CREATE_SQL);
            } catch(Exception e) {
                Log.e(TAG, "Exception in create table " + TABLE_MEMO, e);
            }

            /**
             * create table PHOTO
             */
            println("creating table [" + TABLE_PHOTO + "].");

            DROP_SQL = "drop table if exists " + TABLE_PHOTO;
            try {
                db.execSQL(DROP_SQL);
            } catch(Exception e) {
                Log.e(TAG, "Exception in drop table " + TABLE_PHOTO, e);
            }

            CREATE_SQL = "create table " + TABLE_PHOTO + "(" +
                    "_id INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT, " +
                    "URI TEXT, " +
                    "CREATE_DATE TIMESTAMP DEFAULT CURRENT_TIMESTAMP " +
                    ")";
            try {
                db.execSQL(CREATE_SQL);
            } catch(Exception e) {
                Log.e(TAG, "Exception in create table " + TABLE_PHOTO, e);
            }

            // create index table : 테이블 검색 속도를 빠르게 하기 위한 인덱스 테이블
            //                                          index name                 table name
            String CREATE_INDEX_SQL = "create index " + TABLE_PHOTO + "_IDX ON " + TABLE_PHOTO
                    + "("
                    + "URI"
                    + ")";
            try {
                db.execSQL(CREATE_INDEX_SQL);
            } catch(Exception e) {
                Log.e(TAG, "Exception in creating index table " + TABLE_PHOTO);
            }

            /**
             * create table VIDEO
             */
            println("creating table [" + TABLE_VIDEO + "].");

            DROP_SQL = "drop table if exists " + TABLE_VIDEO;
            try {
                db.execSQL(DROP_SQL);
            } catch(Exception e) {
                Log.e(TAG, "Exception in drop table " + TABLE_VIDEO, e);
            }

            CREATE_SQL = "create table " + TABLE_VIDEO + "(" +
                    "_id INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT, " +
                    "URI TEXT, " +
                    "CREATE_DATE TIMESTAMP DEFAULT CURRENT_TIMESTAMP " +
                    ")";
            try {
                db.execSQL(CREATE_SQL);
            } catch(Exception e) {
                Log.e(TAG, "Exception in create table " + TABLE_VIDEO, e);
            }

            // create index table : 테이블 검색 속도를 빠르게 하기 위한 인덱스 테이블
            //                                    index name                 table name
            CREATE_INDEX_SQL = "create index " + TABLE_VIDEO + "_IDX ON " + TABLE_VIDEO
                    + "("
                    + "URI"
                    + ")";
            try {
                db.execSQL(CREATE_INDEX_SQL);
            } catch(Exception e) {
                Log.e(TAG, "Exception in creating index table " + TABLE_VIDEO);
            }

            /**
             * create table VOICE
             */
            println("creating table [" + TABLE_VOICE + "].");

            DROP_SQL = "drop table if exists " + TABLE_VOICE;
            try {
                db.execSQL(DROP_SQL);
            } catch(Exception e) {
                Log.e(TAG, "Exception in drop table " + TABLE_VOICE, e);
            }

            CREATE_SQL = "create table " + TABLE_VOICE + "(" +
                    "_id INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT, " +
                    "URI TEXT, " +
                    "CREATE_DATE TIMESTAMP DEFAULT CURRENT_TIMESTAMP " +
                    ")";
            try {
                db.execSQL(CREATE_SQL);
            } catch(Exception e) {
                Log.e(TAG, "Exception in create table " + TABLE_VOICE, e);
            }

            // create index table : 테이블 검색 속도를 빠르게 하기 위한 인덱스 테이블
            //                                    index name                 table name
            CREATE_INDEX_SQL = "create index " + TABLE_VOICE + "_IDX ON " + TABLE_VOICE
                    + "("
                    + "URI"
                    + ")";
            try {
                db.execSQL(CREATE_INDEX_SQL);
            } catch(Exception e) {
                Log.e(TAG, "Exception in creating index table " + TABLE_VOICE);
            }

            /**
             * create table HANDWRITING
             */
            println("creating table [" + TABLE_HANDWRITING + "].");

            DROP_SQL = "drop table if exists " + TABLE_HANDWRITING;
            try {
                db.execSQL(DROP_SQL);
            } catch(Exception e) {
                Log.e(TAG, "Exception in drop table " + TABLE_HANDWRITING, e);
            }

            CREATE_SQL = "create table " + TABLE_HANDWRITING + "(" +
                    "_id INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT, " +
                    "URI TEXT, " +
                    "CREATE_DATE TIMESTAMP DEFAULT CURRENT_TIMESTAMP " +
                    ")";
            try {
                db.execSQL(CREATE_SQL);
            } catch(Exception e) {
                Log.e(TAG, "Exception in create table " + TABLE_HANDWRITING, e);
            }

            // create index table : 테이블 검색 속도를 빠르게 하기 위한 인덱스 테이블
            //                                    index name                      table name
            CREATE_INDEX_SQL = "create index " + TABLE_HANDWRITING + "_IDX ON " + TABLE_HANDWRITING
                    + "("
                    + "URI"
                    + ")";
            try {
                db.execSQL(CREATE_INDEX_SQL);
            } catch(Exception e) {
                Log.e(TAG, "Exception in creating index table " + TABLE_HANDWRITING);
            }

        }

        @Override
        public void onOpen(SQLiteDatabase db) {
            println("opened database [" + BasicInfo.DATABASE_NAME + "].");
        }

        @Override
        public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
            println("Upgrading database from version " + oldVersion + " to " + newVersion + ".");
        }
    }

    private void println(String message) {
        Log.d(TAG, message + "\n");
    }
}
