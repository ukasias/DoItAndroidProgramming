    package com.ukasias.android.push;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.firebase.iid.FirebaseInstanceId;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

    public class MainActivity extends AppCompatActivity {
    private final static String TAG = "SamplePush";

    TextView receivedText;
    EditText sendText;
    Button sendButton;
    TextView logText;

    String regId;

    RequestQueue queue;

    public interface SendResponseListener {
        public void onRequestStarted();
        public void onRequestCompleted();
        public void onRequestWithError(VolleyError error);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        receivedText = findViewById(R.id.receivedText);
        sendText = findViewById(R.id.sendText);
        sendButton = findViewById(R.id.sendButton);
        logText = findViewById(R.id.logText);

        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String inputMessage = sendText.getText().toString();
                send(inputMessage);
            }
        });

        queue = Volley.newRequestQueue(getApplicationContext());

        getRegistrationId();
    }

    public void getRegistrationId() {
        print("getRegistration() called.");

        regId = FirebaseInstanceId.getInstance().getToken();
        print("regId : " + regId);

        sendText.setText(regId);
    }

    public void send(String inputMessage) {
        JSONObject requestData = new JSONObject();

        try {
            requestData.put("priority", "high");
            JSONObject dataObj = new JSONObject();
            dataObj.put("contents", inputMessage);
            requestData.put("data", dataObj);

            JSONArray idArray = new JSONArray();
            idArray.put(0, regId);
            requestData.put("registration_ids", idArray);
        }
        catch(JSONException e) {
            e.printStackTrace();
        }
        catch(Exception e) {
            e.printStackTrace();
        }

        sendData(requestData, new SendResponseListener() {
            @Override
            public void onRequestStarted() {
                print("onRequestStarted() called.");
            }

            @Override
            public void onRequestCompleted() {
                print("onRequestCompleted() called.");
            }

            @Override
            public void onRequestWithError(VolleyError error) {
                print("onRequestWithError() called.");
            }
        });
    }

    public void sendData(JSONObject requestData, final SendResponseListener listener) {
        JsonObjectRequest request = new JsonObjectRequest(
                Request.Method.POST,
                "https://fcm.googleapis.com/fcm/send",
                requestData,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        listener.onRequestCompleted();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        listener.onRequestWithError(error);
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();

                return params;
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<String, String>();

                headers.put("Authorization",
                        "key=AAAAdRGT_bI:APA91bHoHGO0uB6JgVyBhX67PI4X_tQUgewdjtwW1o25UtN6jJ0emaWllCCTcE2Dev0HahkvVW7HHPB_umoL-TdUTBbUfVCDJxIzf4Fd1L_52pnzTpJx0MTg-2yI-t4FkziCrX8NkS4sobWZS-VDQpXo9KxE-NBLNA");

                return headers;
            }

            @Override
            public String getBodyContentType() {
                return "application/json";
            }
        };

        request.setShouldCache(false);
        listener.onRequestStarted();
        queue.add(request);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        print("onNewIntent() called.");

        if (intent != null) {
            processIntent(intent);
        }

        super.onNewIntent(intent);
    }

    private void processIntent(Intent intent) {
        String from = intent.getStringExtra("from");
        if (from == null) {
            print("from is null.");
            return;
        }

        String contents = intent.getStringExtra("contents");
        print("DATA : " + from + ", " + contents);

        receivedText.setText("[" + from + "]로부터 수신한 데이터 : " + contents);
    }

    public void print(String message) {
        Log.d(TAG, message + "\n");
        logText.append(message + "\n");
    }
}
