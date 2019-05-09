package com.ukasias.android.doitmission26;

import android.app.PendingIntent;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.tech.NfcA;
import android.nfc.tech.NfcF;
import android.os.Parcelable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.common.base.Charsets;
import com.google.common.primitives.Bytes;

import java.nio.charset.Charset;
import java.util.List;
import java.util.Locale;
import java.util.Random;

public class MainActivity extends AppCompatActivity {
    final static String TAG = "Mission26";

    TextView[] P;
    TextView[] B;
    TextView bText;

    final static int[] P_ids = { R.id.p1, R.id.p2, R.id.p3 };
    final static int[] B_ids = { R.id.b1, R.id.b2, R.id.b3, R.id.b4, R.id.b5, R.id.b6, R.id.b7, R.id.b8};

    Button tagButton;

    final static int RED = Color.rgb(0xee, 00, 00);
    final static int PURPLE = Color.rgb(0xcc, 0x99, 0xff);
    final static int LINE = R.drawable.back;

    private static final int TYPE_TEXT = 1;
    private static final int TYPE_URI = 2;

    private NfcAdapter adapter;
    private NdefMessage sendNdefMessage;
    private Intent targetIntent;
    private PendingIntent mPendingIntent;
    private IntentFilter[] mFilters;
    private String[][] mTechLists;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Layout 초기화
        P = new TextView[3];
        B = new TextView[8];

        for (int i = 0; i < 3; i++) {
            P[i] = findViewById(P_ids[i]);
        }
        for (int i = 0; i < 8; i++) {
            B[i] = findViewById(B_ids[i]);
        }

        bText = findViewById(R.id.b_text);

        // nfc 받기 초기 설정
        adapter = NfcAdapter.getDefaultAdapter(this);
        sendNdefMessage = null;

        targetIntent = new Intent(this, MainActivity.class);
        targetIntent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        mPendingIntent = PendingIntent.getActivity(
                this,
                0,
                targetIntent,
                0);

        IntentFilter ndef = new IntentFilter(NfcAdapter.ACTION_NDEF_DISCOVERED);
        try {
            ndef.addDataType("*/*");
        }
        catch(IntentFilter.MalformedMimeTypeException mmte) {
            mmte.printStackTrace();
        }

        mFilters = new IntentFilter[] { ndef, };
        mTechLists = new String[][] { new String[] { NfcF.class.getName()}};

        // 버튼 눌렀을 때의 nfc tag 작성 처리
        tagButton = findViewById(R.id.tagButton);
        tagButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendNdefMessage = createTagMessage(pickLocation(), TYPE_TEXT);
                //adapter.enableForegroundNdefPush(, sendNdefMessage);
                NdefMessage[] msgs = new NdefMessage[1];
                msgs[0] = sendNdefMessage;
                targetIntent.putExtra(NfcAdapter.EXTRA_NDEF_MESSAGES, msgs);
                startActivity(targetIntent);
            }
        });

        // NFC Tag 전달 시의 처리
        Intent passedIntent = getIntent();
        if (passedIntent != null) {
            String action = passedIntent.getAction();
            if (NfcAdapter.ACTION_TAG_DISCOVERED.equals(action)) {
                processTag(passedIntent);
            }
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        print("onNewIntent() called.");
        super.onNewIntent(intent);

        if (intent != null) {
            processTag(intent);
        }
    }

    private String pickLocation() {
        String location="";
        Random random = new Random();
        int v;

        v = random.nextInt(3);
        location += "P" + String.valueOf(v + 1) + "-";

        v = random.nextInt(8);
        if (v / 4 > 0) {
            location += "B1-";
        }
        else {
            location += "B2-";
        }

        location += String.valueOf(v + 21);

        return location;
    }

    private int getPIndex(String message) {
        StringBuilder builder = new StringBuilder(message);
        builder.delete(0, 1);
        builder.delete(1, 7);
        return (Integer.parseInt(builder.toString()) - 1);
    }

    private int getBIndex(String message) {
        StringBuilder builder = new StringBuilder(message);
        builder.delete(0, 6);
        return (Integer.parseInt(builder.toString()) - 21);
    }

    private String getBInfo(int bIndex) {
        if (bIndex / 4 == 0) {
            return "B2";
        }
        else {
            return "B1";
        }
    }

    private void toast(String message) {
        Toast.makeText(
                getApplicationContext(),
                message,
                Toast.LENGTH_LONG).show();
    }

    private void print(String message) {
        Log.d(TAG, message + "\n");
    }

    private NdefMessage createTagMessage(String message, int type) {
        NdefRecord[] records = new NdefRecord[1];

        print("createTagMessage() called. : " + message);

        if (type == TYPE_TEXT) {
            records[0] = createTextRecord(message, Locale.KOREAN, true);
        }
        else {
            records[0] = createUriRecord(message.getBytes());
        }

        NdefMessage ndefMessage = new NdefMessage(records);

        return ndefMessage;
    }

    private NdefRecord createTextRecord(String message, Locale locale, boolean useUtf_8) {
        final byte[] langBytes = locale.getLanguage().getBytes(Charsets.US_ASCII);
        int langSize = langBytes.length;
        Charset utfEncoding = useUtf_8? Charsets.UTF_8 : Charset.forName("UTF-16");
        final byte[] dataBytes = message.getBytes(utfEncoding);
        final int utfBit = useUtf_8? 0 : (1 << 7);
        final char status = (char) (utfBit + langSize);

        final byte[] data = Bytes.concat(new byte[] {(byte) status}, langBytes, dataBytes);

        byte[] sample = new byte[0];
        print("sample : " + sample.toString());

        return new NdefRecord(
                NdefRecord.TNF_WELL_KNOWN,
                NdefRecord.RTD_TEXT,
                new byte[0],
                data);
    }

    private NdefRecord createUriRecord(byte[] data) {
        return new NdefRecord(
                NdefRecord.TNF_ABSOLUTE_URI,
                NdefRecord.RTD_URI,
                new byte[0],
                data);
    }

    @Override
    protected void onResume() {
        print("onResume() called.");
        super.onResume();

        if (adapter != null) {
            adapter.enableForegroundDispatch(
                    this,
                    mPendingIntent,
                    mFilters,
                    mTechLists);

            if  (sendNdefMessage != null) {
                adapter.enableForegroundNdefPush(this, sendNdefMessage);
            }
        }
    }

    @Override
    protected void onPause() {
        print("onPause() called.");
        super.onPause();

        if (adapter != null) {
            adapter.disableForegroundDispatch(
                    this);

            adapter.disableForegroundNdefPush(this);
        }
    }

    private void processTag(Intent passedIntent) {
        print("processTag() called.");

        Parcelable[] rawMsgs =(Parcelable[])
                passedIntent.getParcelableArrayExtra(
                        NfcAdapter.EXTRA_NDEF_MESSAGES);
        if (rawMsgs == null) {
            print("NDEF is null.");
            return;
        }

        NdefMessage[] msgs;
        msgs = new NdefMessage[rawMsgs.length];
        for (int i = 0; i < rawMsgs.length; i++) {
            msgs[i] = (NdefMessage) rawMsgs[i];
            showTag(msgs[i]);
        }
    }

    private void showTag(NdefMessage message) {
        print("showTag() called.");
        List<ParsedRecord> records = NdefMessageParser.parse(message);
        final int size = records.size();

        print("showTag() : size = " + size);
        if (size <= 0) {
            return;
        }

        ParsedRecord record = records.get(0);
        if (record.getType() != ParsedRecord.TYPE_TEXT) {
            return;
        }

        String tagString = ((TextRecord) record).getText();
        checkLocation(tagString);
    }

    private void checkLocation(String message) {
        int pindex = getPIndex(message);
        int bindex = getBIndex(message);
        String b_text = getBInfo(bindex);

        for (int i = 0; i < 3; i++) {
            if (i == pindex) {
                P[i].setBackgroundColor(RED);
            }
            else {
                P[i].setBackgroundColor(PURPLE);
            }
        }

        for (int i = 0; i < 8; i++) {
            if (i == bindex) {
                B[i].setBackgroundColor(RED);
            }
            else {
                B[i].setBackgroundResource(LINE);
            }
        }

        bText.setText(b_text);
    }
}
