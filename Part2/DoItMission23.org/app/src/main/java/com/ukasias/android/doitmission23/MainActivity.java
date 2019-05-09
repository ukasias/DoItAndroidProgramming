package com.ukasias.android.doitmission23;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {
    final static String TAG = "DoItMission23";
    final static String NUMBER = " 명";

    TextView numberText;
    ListView listView;
    EditText editText;
    Button sendButton;

    FriendAdapter adapter;


    RequestQueue requestQueue;
    final static String regId = "dgMfUqdo640:APA91bHFmX7OEX2vP29eiYs-A9X0INdp9qYcT2Ee8RwCtIe-eN1msirkHI6DK9ri3Tr7sdFEjiYUnU8QFreG1Us8-T7UeXUbulJmNvbcIiE-HtSF9VZ_D42CAm4sh_P9BZBIsCUmm6EpntWHdyw0MrUFMwCDjv5bFw";

    public interface SendResponseListener {
        public void onRequestStarted();
        public void onRequestCompleted();
        public void onRequestWithError(VolleyError error);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        numberText = findViewById(R.id.number);
        listView = findViewById(R.id.listView);
        editText = findViewById(R.id.editText);
        sendButton = findViewById(R.id.sendButton);

        adapter = new FriendAdapter(this);
        listView.setAdapter(adapter);

        Resources res = getResources();

        adapter.addItem(new FriendItem(
                res.getDrawable(R.drawable.friend01),
                "친구 #1",
                "010-1000-1000"
                ));

        adapter.addItem(new FriendItem(
                res.getDrawable(R.drawable.friend02),
                "친구 #2",
                "020-2000-2000"
        ));

        adapter.notifyDataSetChanged();

        numberText.setText(adapter.getCount() + NUMBER);
        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                send(editText.getText().toString());
            }
        });

        requestQueue = Volley.newRequestQueue(getApplicationContext());
    }

    public void send(String input) {
        JSONObject requestData = new JSONObject();

        try {
            requestData.put("priority", "high");
            JSONObject dataObj = new JSONObject();
            dataObj.put("contents", input);
            requestData.put("data", dataObj);

            JSONArray ids = new JSONArray();
            ids.put(0, regId);

            requestData.put("registration_ids", ids);
        }
        catch(JSONException jsone) {
            jsone.printStackTrace();
        }
        catch (Exception e) {
            e.printStackTrace();
        }

        sendData(requestData, new SendResponseListener() {
            @Override
            public void onRequestStarted() {
                Toast.makeText(
                        getApplicationContext(),
                        "onRequestStarted() called.",
                        Toast.LENGTH_LONG
                ).show();
            }

            @Override
            public void onRequestCompleted() {
                print("onRequestCompleted() called.");
                Toast.makeText(
                        getApplicationContext(),
                        "onRequestCompleted() called.",
                        Toast.LENGTH_LONG
                ).show();
            }

            @Override
            public void onRequestWithError(VolleyError error) {
                Toast.makeText(
                        getApplicationContext(),
                        "onRequestWithError() called.",
                        Toast.LENGTH_LONG
                ).show();
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
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<String, String>();
                headers.put(
                        "Authorization",
                        "key=AAAARiGmb9Y:APA91bG9YCyMnRZHl534ESOqgH_QiwCIDyfEhubLkQqTTH9w71o0UKiHVWO8XZT9kEY1YjgP7Sw93xfVfaEIrKThyuLUO3eYwm1JmRUej57-sbsJjWtszyJv0uK55HTBWG2C5azAWr6Ls5PIRYKg1V7CFyxMRMgWHA");
                return headers;
            }

            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                return params;
            }

            @Override
            public String getBodyContentType() {
                return "application/json";
            }
        };

        request.setShouldCache(false);
        listener.onRequestStarted();
        requestQueue.add(request);
    }

    private void print(String message) {
        Log.d(TAG, message + "\n");
    }
}
