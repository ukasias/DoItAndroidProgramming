package com.ukasias.android.samplenfc;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.PendingIntent;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.tech.NfcF;
import android.os.Parcelable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.common.base.Charsets;
import com.google.common.primitives.Bytes;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {
    private final String TAG = "MainActivity";

    public static final int REQ_CODE_PUSH = 1001;
    public static final int SHOW_PUSH_CONFIRM = 2001;

    private Button mBroadcastButton;
    private TextView mTextView;

    private NfcAdapter mAdapter;
    private PendingIntent mPendingIntent;
    private IntentFilter[] mFilters;
    private String[][] mTechList;

    public static final int TYPE_TEXT = 1;
    public static final int TYPE_URI = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mAdapter = NfcAdapter.getDefaultAdapter(this);
        setContentView(R.layout.activity_main);

        mBroadcastButton = findViewById(R.id.broadcastButton);
        mTextView = findViewById(R.id.textView);

        mBroadcastButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int type = TYPE_TEXT;
                String msg = "Hello Android!";

                // 메모리에 태그 정보 생성
                NdefMessage mMessage = createTagMessage(msg, type);
                NdefMessage[] msgs = new NdefMessage[1];
                msgs[0] = mMessage;

                // 가상으로 인텐트 전달
                Intent intent = new Intent(NfcAdapter.ACTION_TAG_DISCOVERED);
                intent.putExtra(NfcAdapter.EXTRA_NDEF_MESSAGES, msgs);
                startActivity(intent);
            }
        });

        print("onCreate() called.");

        if (mAdapter == null) {
            mTextView.setText("사용하기 전에 NFC를 활성화하세요.");
        }
        else {
            mTextView.setText("NFC 태그를 스캔하세요.");
        }

        Intent targetIntent = new Intent(this, MainActivity.class);
        targetIntent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        mPendingIntent =
                PendingIntent.getActivity(
                        this,
                        0,
                        targetIntent,
                        0);

        IntentFilter ndef = new IntentFilter(NfcAdapter.ACTION_NDEF_DISCOVERED);

        try {
            ndef.addDataType("*/*");
        } catch(IntentFilter.MalformedMimeTypeException mmte) {
            throw new RuntimeException("fail", mmte);
        }

        mFilters = new IntentFilter[] { ndef };
        mTechList = new String[][] { new String[] { NfcF.class.getName() }};

        Intent passedIntent = getIntent();
        if (passedIntent != null) {
            String action = passedIntent.getAction();
            if (NfcAdapter.ACTION_TAG_DISCOVERED.equals(action)) {
                processTag(passedIntent);
            }
        }
    }

    @Override
    protected void onNewIntent(Intent passedIntent) {
        super.onNewIntent(passedIntent);

        print("onNewIntent() called.");
        if (passedIntent != null) {
            processTag(passedIntent);
        }
    }

    private void processTag(Intent passedIntent) {
        print("processTag() called.");

        Parcelable[] rawMsgs =
                passedIntent.getParcelableArrayExtra(NfcAdapter.EXTRA_NDEF_MESSAGES);

        if (rawMsgs == null) {
            print("NDEF is null.");
            return;
        }

        mTextView.setText(rawMsgs.length + "개 태그 스캔됨.");

        NdefMessage[] msgs;
        if (rawMsgs != null) {
            msgs = new NdefMessage[rawMsgs.length];
            int size = rawMsgs.length;
            for (int i = 0; i < size; i++) {
                msgs[i] = (NdefMessage) rawMsgs[i];
                showTag(msgs[i]);
            }
        }

        showDialog(SHOW_PUSH_CONFIRM);
    }

    private void showTag(NdefMessage ndefMessage) {
        List<ParsedRecord> records = NdefMessageParser.parse(ndefMessage);
        mTextView.setText("");

        for (ParsedRecord record : records) {
            String recordStr = "";

            if  (record.getType() == ParsedRecord.TYPE_TEXT) {
                recordStr = "TEXT: " + ((TextRecord) record).getText() + "\n";
            }
            else {
                recordStr = "URI: " + ((UriRecord) record).getUri().toString() + "\n";
            }
            mTextView.append(recordStr);
            mTextView.invalidate();
        }

    }

    @Override
    protected Dialog onCreateDialog(int id) {
        AlertDialog.Builder builder;

        switch(id) {
            case SHOW_PUSH_CONFIRM:
                builder = new AlertDialog.Builder(this);
                builder.setTitle("푸쉬 액티비티");
                builder.setMessage("푸쉬 액티비티를 띄우시겠습니까?");
                builder.setPositiveButton("예",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                Intent newIntent = new Intent(getApplicationContext(), NFCPushActivity.class);
                                startActivityForResult(newIntent, REQ_CODE_PUSH);
                            }
                        });
                builder.setNegativeButton("아니오",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {

                            }
                        });
                return builder.create();
        }

        return null;
    }

    @Override
    protected void onPause() {
        super.onPause();

        if (mAdapter != null) {
            mAdapter.disableForegroundDispatch(this);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (mAdapter != null) {
            mAdapter.enableForegroundDispatch(this, mPendingIntent, mFilters, mTechList);
        }
    }

    private void print(String message) {
        Log.d(TAG, message + "\n");
    }

    private NdefMessage createTagMessage(String msg, int type) {
        NdefRecord[] records = new NdefRecord[1];
        if (type == TYPE_TEXT) {
            records[0] = createTextRecord(msg, Locale.KOREAN, true);
        } else if (type == TYPE_URI) {
            records[0] = createUriRecord(msg.getBytes());
        }

        return new NdefMessage(records);
    }

    private NdefRecord createTextRecord(String text, Locale locale,boolean encodeInUtf8) {
        final byte[] langBytes = locale.getLanguage().getBytes(StandardCharsets.US_ASCII);
        final Charset encoding =
                encodeInUtf8? Charsets.UTF_8 :
                        Charset.forName("UTF-16");
        final byte[] textBytes = text.getBytes(encoding);
        final int utfbit = encodeInUtf8? 0 : (1 << 7);
        final char status = (char) (utfbit + langBytes.length);
        final byte[] data = Bytes.concat(
                new byte[] {(byte) status},
                langBytes,
                textBytes);

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


}
