package com.ukasias.android.thread.looper;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class MainActivity extends AppCompatActivity {
    EditText editText1;
    EditText editText2;
    Button startButton;

    MainHandler mainHandler;
    ProcessThread thread;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        editText1 = (EditText) findViewById(R.id.editText1);
        editText2 = (EditText) findViewById(R.id.editText2);
        startButton = (Button) findViewById(R.id.button);

        mainHandler = new MainHandler();

        thread = new ProcessThread();

        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String inStr = editText1.getText().toString();
                Message msgToSend = Message.obtain();
                msgToSend.obj = inStr;

                thread.handler.sendMessage(msgToSend);
            }
        });

        thread.start();
    }

    class ProcessThread extends Thread {
        ProcessHandler handler;

        public ProcessThread() {
            handler = new ProcessHandler();
        }

        public void run() {
            Looper.prepare();
            Looper.loop();
        }
    }

    class ProcessHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            Message resultMsg = Message.obtain();
            resultMsg.obj = msg.obj + " Mike!!!";

            mainHandler.sendMessage(resultMsg);
        }
    }

    class MainHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            String str = (String) msg.obj;
            editText2.setText(str);
        }
    }


}
