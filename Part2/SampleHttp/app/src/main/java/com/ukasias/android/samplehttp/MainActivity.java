package com.ukasias.android.samplehttp;

import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class MainActivity extends AppCompatActivity {
    EditText address;
    Button request;
    TextView contents;

    Handler handler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        address = (EditText) findViewById(R.id.input01);
        request = (Button) findViewById(R.id.requestButton);
        contents = (TextView) findViewById(R.id.txtMsg);

        handler = new Handler();

        request.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ConnectionThread thread = new ConnectionThread(address.getText().toString());
                thread.start();
            }
        });
    }

    class ConnectionThread extends Thread {
        String urlStr;

        public ConnectionThread(String inStr) {
            urlStr = inStr;
        }

        @Override
        public void run() {
            try {
                final String output = request(urlStr);
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        contents.setText(output);
                    }
                });
            }
            catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


    public String request(String urlStr) {
        StringBuilder output = new StringBuilder();

        try {
            URL url = new URL(urlStr);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            if (conn != null) {
                conn.setConnectTimeout(10000);
                conn.setRequestMethod("GET");
                conn.setDoInput(true);
                conn.setDoOutput(true);

                int code = conn.getResponseCode();

                BufferedReader reader = new BufferedReader(
                        new InputStreamReader(
                                conn.getInputStream()
                        )
                );

                String line = null;

                while(true) {
                    line = reader.readLine();
                    if (line == null) {
                        break;
                    }
                    output.append(line + "\n");
                }

                reader.close();
                conn.disconnect();
            }
        }
        catch (Exception e) {
            Log.e("SamepleHTTP", "Exception in processing response", e);
            e.printStackTrace();
        }

        return output.toString();
    }
}
