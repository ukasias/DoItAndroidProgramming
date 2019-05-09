package com.ukasias.android.doitmission_07;

import android.app.DatePickerDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Toast;

import java.text.FieldPosition;
import java.text.NumberFormat;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class MainActivity extends AppCompatActivity {
    EditText name;
    EditText age;
    Button birthButton;
    Button saveButton;
    Date birthday;
    SimpleDateFormat sdFormat;
    DatePickerDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        name = (EditText) findViewById(R.id.editText1);
        age = (EditText) findViewById(R.id.editText2);
        birthButton = (Button) findViewById(R.id.button3);
        saveButton = (Button) findViewById(R.id.saveButton);

        birthday = new Date();
        sdFormat = new SimpleDateFormat("yyyy년 MM월 dd일");
        birthButton.setText(sdFormat.format(birthday));
        dialog = new DatePickerDialog(this,
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker datePicker, int i, int i1, int i2) {
                        birthday.setYear(datePicker.getYear() - 1900);
                        birthday.setMonth(datePicker.getMonth());
                        birthday.setDate(datePicker.getDayOfMonth());
                        birthButton.setText(sdFormat.format(birthday));
                    }
                }, birthday.getYear() + 1900, birthday.getMonth(), birthday.getDate());

        birthButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.updateDate(
                        birthday.getYear() + 1900,
                        birthday.getMonth(),
                        birthday.getDate());
                dialog.show();
            }
        });

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getApplicationContext(),
                        "이름 : " + name.getText().toString() + "\n" +
                                "나이 : " + age.getText().toString() + "\n" +
                                "생년월일 : " + sdFormat.format(birthday).toString(),
                        Toast.LENGTH_LONG).show();
            }
        });
    }
}
