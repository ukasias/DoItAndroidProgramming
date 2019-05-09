package com.ukasias.android.sampledelayed;

import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {
    TextView textView;
    Button requestButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        textView = (TextView) findViewById(R.id.textView);
        requestButton = (Button) findViewById(R.id.request);

        requestButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                request();
            }
        });
    }

    private void request() {
        String title = "원격 요청";
        String message = "데이터를 요청하시겠습니까?";
        String buttonYes = "예";
        String buttonNo = "아니오";

        AlertDialog dialog = makeRequestDiaalog(title, message,
                buttonYes, buttonNo);
        dialog.show();

        textView.setText("원격 데이터 요청 중 ...");
    }

    private AlertDialog makeRequestDiaalog(CharSequence title, CharSequence message,
                                           CharSequence buttonYes, CharSequence buttonNo) {
        AlertDialog.Builder requestDialog = new AlertDialog.Builder(this);

        requestDialog.setTitle(title);
        requestDialog.setMessage(message);
        requestDialog.setPositiveButton(buttonYes, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                for (int k = 0; k < 10; k++) {
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {}
                }

                textView.setText("원격 데이터 요청 완료");
            }
        });
        requestDialog.setNegativeButton(buttonNo, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                textView.setText("네트워크 지연 시간 시뮬레이션");
            }
        });

        return requestDialog.show();
    }
}
