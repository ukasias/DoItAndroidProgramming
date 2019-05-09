package com.ukasias.android.sampledatabase;

import android.database.sqlite.SQLiteDatabase;
import android.icu.text.AlphabeticIndex;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {
    Button databaseButton;
    Button tableButton;
    EditText dataBaseText;
    EditText tableText;
    TextView statusText;

    SQLiteDatabase db;
    String dbName;
    String tableName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        databaseButton = (Button) findViewById(R.id.makeDBButton);
        tableButton = (Button) findViewById(R.id.makeTableButton);
        dataBaseText = (EditText) findViewById(R.id.dbText);
        tableText = (EditText) findViewById(R.id.tableText);
        statusText = (TextView) findViewById(R.id.status);

        databaseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                createDatabase(dataBaseText.getText().toString());
            }
        });

        tableButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                createTable(tableText.getText().toString());

                RecordThread thread = new RecordThread();
                thread.start();
            }
        });
    }

    class RecordThread extends Thread {
        @Override
        public void run() {
            try {
                Thread.sleep(2000);

                int count = insertRecord();
                println(count + " records inserted.");
            }
            catch(InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private void println(String print) {
        statusText.append(print + "\n");
    }

    private void createDatabase(String name) {
        println("creating database [" + name + "].");

        db = openOrCreateDatabase(name,
                MODE_PRIVATE, null);

        dbName = name;
    }

    private void createTable(String name) {
        println("creating table [" + name + "]");

        db.execSQL("create table " + name + "("
                + "_id integer PRIMARY KEY autoincrement,"
                + "name text, "
                + "age integer, "
                + "phone text);");
        tableName = name;
    }

    private int insertRecord() {
        println("inserting records.");

        int count = 3;

        db.execSQL("insert into " + tableName + "(name, age, phone) values (" +
                "'John', 20, '010-7788-1234')");
        db.execSQL("insert into " + tableName + "(name, age, phone) values (" +
                "'WanKeun Cho', 37, '010-2962-6344')");
        db.execSQL("insert into " + tableName + "(name, age, phone) values (" +
                "'JaeKeun Cho', 39, '010-3210-6344')");

        return count;
    }
}
