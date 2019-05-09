package com.ukasias.android.doitmission_04;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.UnsupportedEncodingException;

public class MainActivity extends AppCompatActivity {
    EditText edit;
    TextView text;
    Button sendButton;
    Button closeButton;
    TextWatcher textWatcher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        edit = (EditText) findViewById(R.id.editText);
        text = (TextView) findViewById(R.id.textView);
        sendButton = (Button) findViewById(R.id.sendButton);
        closeButton = (Button) findViewById(R.id.closeButton);

        textWatcher = new TextWatcher() {
            int deleteStart = -1;
            int deleteCount = -1;

            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                byte[] byteText;

                try {
                    byteText = charSequence.toString().getBytes("KSC5601");
                    if (byteText.length > 80) {
                        deleteStart = i;
                        deleteCount = i2;
                    }
                    else {
                        deleteStart = -1;
                    }
                }
                catch(UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
                byte[] byteText;

                if (deleteStart >= 0) {
                    editable.delete(deleteStart, deleteStart + deleteCount);
                }

                try {
                    byteText = editable.toString().getBytes("KSC5601");
                    // text.setText(byteText.length + R.string.input_byte);
                    text.setText(byteText.length + " / 80 바이트");
                }
                catch(UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
            }
        };
        edit.addTextChangedListener(textWatcher);

        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getApplicationContext(),
                        edit.getText().toString(), Toast.LENGTH_LONG).show();
            }
        });

        closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }
}
