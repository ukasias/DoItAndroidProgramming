package com.ukasias.android.push;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;
import com.google.firebase.messaging.RemoteMessage;

public class MyFirebaseInstanceIDService extends FirebaseInstanceIdService {
    final static String TAG = "MyFInstanceIDService";

    public MyFirebaseInstanceIDService() {
    }

    @Override
    public void onTokenRefresh() {
        super.onTokenRefresh();

        print("onTokenRefresh() called.");

        String regId = FirebaseInstanceId.getInstance().getToken();
        print("regId : " + regId);
    }



    private void print(String message) {
        Log.d(TAG, message + "\n");
    }
}
