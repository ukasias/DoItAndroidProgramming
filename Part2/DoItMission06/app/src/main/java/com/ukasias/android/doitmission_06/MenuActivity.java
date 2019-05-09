package com.ukasias.android.doitmission_06;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class MenuActivity extends AppCompatActivity {
    public static final int REQUEST_CODE_CUSTOMERS = 101;
    public static final int REQUEST_CODE_SALES = 102;
    public static final int REQUEST_CODE_PRODUCTS = 103;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        Button button1 = (Button) findViewById(R.id.button1);
        button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), CustomersActivity.class);
                intent.putExtra("name", getString(R.string.menu));
                startActivityForResult(intent, REQUEST_CODE_CUSTOMERS);
            }
        });


        Button button2 = (Button) findViewById(R.id.button2);
        button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), SalesActivity.class);
                intent.putExtra("name", getString(R.string.menu));
                startActivityForResult(intent, REQUEST_CODE_SALES);
            }
        });


        Button button3 = (Button) findViewById(R.id.button3);
        button3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), ProductsActivity.class);
                intent.putExtra("name", getString(R.string.menu));
                startActivityForResult(intent, REQUEST_CODE_PRODUCTS);
            }
        });

        Intent passedIntent = getIntent();
        processIntent(passedIntent);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            if (requestCode == REQUEST_CODE_CUSTOMERS ||
                    requestCode == REQUEST_CODE_PRODUCTS |
                    requestCode == REQUEST_CODE_SALES) {
                processIntent(data);
            }
        }
        else if(resultCode == RESULT_CANCELED) {
            finish();
        }
    }

    private void processIntent(Intent intent) {
        String from;

        if ((from = intent.getStringExtra("name")) != null) {
            Toast.makeText(getApplicationContext(),
                    "From : " + from, Toast.LENGTH_LONG).show();
        }
    }
}
