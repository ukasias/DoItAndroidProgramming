package com.ukasias.android.sampleandroidsocket;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class MainActivity extends AppCompatActivity {
    Button button;
    EditText edit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        button = (Button) findViewById(R.id.button);
        edit = (EditText) findViewById(R.id.edit);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String addr = edit.getText().toString().trim();

                ConnectThread thread = new ConnectThread(addr);
                thread.start();
            }
        });
    }

    class ConnectThread extends Thread {
        String hostname;

        public ConnectThread(String addr) {
            hostname = addr;
        }

        public void run() {
            try {
                int port = 5001;
                Socket sock = new Socket(hostname, port);

                ObjectOutputStream outstream =
                        new ObjectOutputStream(sock.getOutputStream());
                outstream.writeObject("Hello AndroidTown on Android");
                outstream.flush();

                ObjectInputStream instream =
                        new ObjectInputStream(sock.getInputStream());
                String obj = (String) instream.readObject();

                Log.d("MainActivity", "서버에서 받은 메시지 : " + obj);

                sock.close();
            }
            catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
