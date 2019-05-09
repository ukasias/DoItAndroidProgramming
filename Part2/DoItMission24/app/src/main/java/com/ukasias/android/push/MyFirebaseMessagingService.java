package com.ukasias.android.push;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;
import java.util.GregorianCalendar;
import java.util.Map;

public class MyFirebaseMessagingService extends FirebaseMessagingService {
    final static String TAG = "MyFMessagingService";

    public MyFirebaseMessagingService() {
    }

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);

        print("onMessageReceived() called.");

        String from = remoteMessage.getFrom();
        Map<String, String> data = remoteMessage.getData();
        String contents = data.get("contents");

        popUpDialog(from, contents);
    }

    private void popUpDialog(String from, String contents) {
        print("popUpDialog() called.");

        GregorianCalendar calendar = new GregorianCalendar();
        calendar.setTimeInMillis(System.currentTimeMillis());
        SimpleDateFormat sdf = new SimpleDateFormat("MM월 dd일 HH시 mm분");

        Context context = getApplicationContext();
        Intent intent = new Intent(context, PopUpActivity.class);
        intent.putExtra("from", from);
        intent.putExtra("date", sdf.format(calendar.getTime()));
        intent.putExtra("contents", contents);


        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_SINGLE_TOP);

        context.startActivity(intent);

    }

    private void print(String message) {
        Log.d(TAG, message + "\n");
    }
}
