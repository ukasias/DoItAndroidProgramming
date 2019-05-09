package com.ukasias.android.samplenfctagmaker;

import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.tech.Ndef;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;

import com.google.common.base.Charsets;
import com.google.common.primitives.Bytes;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {
    public static final int TYPE_TEXT = 1;
    public static final int TYPE_URI = 2;

    Button writeButton;
    EditText writeText;

    RadioButton textButton;
    RadioButton uriButton;

    TextView byteText;
    TextView tagText;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        writeButton = findViewById(R.id.writeButton);
        writeText = findViewById(R.id.writeText);

        textButton = findViewById(R.id.textButton);
        uriButton = findViewById(R.id.uriButton);

        byteText = findViewById(R.id.byteText);
        tagText = findViewById(R.id.tagText);


        writeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String message = writeText.getText().toString();
                int type = textButton.isChecked()? TYPE_TEXT : TYPE_URI;
                NdefMessage mMessage = createTagMessage(message, type);
                byte[] messageBytes = mMessage.toByteArray();
                String hexStr = bytesToHex0x(messageBytes);
                byteText.setText(hexStr);
                showTag(mMessage);
            }
        });
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

    private String bytesToHex0x(byte[] s) {
        String str = "";
        for (byte a : s) {
            str += String.format("0x%02x ", a);
        }

        return str;
    }

    private void showTag(NdefMessage message) {
        List<ParsedRecord> records = NdefMessageParser.parse(message);
        tagText.setText("");

        for (ParsedRecord record : records) {
            String recordStr = "";

            if  (record.getType() == ParsedRecord.TYPE_TEXT) {
                recordStr = "TEXT: " + ((TextRecord) record).getText() + "\n";
            }
            else {
                recordStr = "URI: " + ((UriRecord) record).getUri().toString() + "\n";
            }
            tagText.append(recordStr);
            tagText.invalidate();
        }
    }
}
