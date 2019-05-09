package com.ukasias.android.push;

import android.util.Log;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

public class MyFirebaseInstanceIDService extends FirebaseInstanceIdService {
    private final static String TAG = "MyIID";

    public MyFirebaseInstanceIDService() {
    }

    @Override
    public void onTokenRefresh() {
        Log.d(TAG, "onTokenRefresh() called.");

        String refreshedToken = FirebaseInstanceId.getInstance().getToken();
        Log.d(TAG, "RefreshedToken : " + refreshedToken);
    }
}
