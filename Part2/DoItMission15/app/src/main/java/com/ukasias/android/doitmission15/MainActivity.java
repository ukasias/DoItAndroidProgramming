package com.ukasias.android.doitmission15;

import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class MainActivity extends AppCompatActivity {
    private String TAG = "DoItMission15";
    private String googlenews = "https://news.google.com/?taa=1&hl=ko&gl=KR&ceid=KR:ko";
    private String magandacafe = "http://www.magandacafe.com";

    EditText addressText;
    Button requestButton;

    TextView originalText;
    WebView webView;

    Handler handler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        addressText = (EditText) findViewById(R.id.addressText);
        requestButton = (Button) findViewById(R.id.requestButton);
        originalText = (TextView) findViewById(R.id.originalText);
        webView = (WebView) findViewById(R.id.webView);

        addressText.setText(magandacafe);

        requestButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                WebThread thread = new WebThread(addressText.getText().toString());
                thread.start();
            }
        });

        handler = new Handler();
    }

    class UpdateSuccessRunnable implements Runnable {
        String _contents;
        public UpdateSuccessRunnable(String result) {
            _contents = result;

        }

        @Override
        public void run() {
            originalText.setText(_contents);
            webView.loadData(_contents, "text/html", "utf-8");
        }
    }

    class UpdateFailRunnable implements Runnable {
        int code;

        public UpdateFailRunnable(int resCode) {
            code = resCode;
        }

        @Override
        public void run() {
            Toast.makeText(getApplicationContext(),
                    "Failed: " + code,
                    Toast.LENGTH_LONG).show();
        }
    }

    class WebThread extends Thread {
        String urlStr;

        public WebThread(String urlString) {
            urlStr = urlString;
        }

        @Override
        public void run() {
            try {
                URL url = new URL(urlStr);
                HttpURLConnection conn =
                        (HttpURLConnection) url.openConnection();

                conn.setConnectTimeout(10000);
                conn.setDoInput(true);
                conn.setDoOutput(true);

                conn.setRequestMethod("POST");

                int resCode = conn.getResponseCode();
                if (resCode == HttpURLConnection.HTTP_OK) {
                    StringBuilder builder = new StringBuilder();
                    BufferedReader reader = new BufferedReader(
                            new InputStreamReader(conn.getInputStream()));
                    String line = null;

                    while(true) {
                        line = reader.readLine();
                        if (line == null) {
                            break;
                        }
                        builder.append(line + "\n");
                    }
                    reader.close();
                    conn.disconnect();

                    handler.post(new UpdateSuccessRunnable(builder.toString()));
                }
                else {
                    handler.post(new UpdateFailRunnable(resCode));
                }
            }
            catch(MalformedURLException e) {
                e.printStackTrace();
            }
            catch(IOException e) {
                e.printStackTrace();
            }
            catch(Exception e) {
                e.printStackTrace();
            }
        }
    }
}
