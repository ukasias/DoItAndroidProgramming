package com.ukasias.android.doitmission_06;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class ProductsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_products);

        Intent passedIntent = getIntent();
        processIntent(passedIntent);

        Button button1 = (Button) findViewById(R.id.button8);
        button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.putExtra("name", getString(R.string.products));
                setResult(RESULT_OK, intent);

                finish();
            }
        });

        Button button2 = (Button) findViewById(R.id.button9);
        button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                intent.putExtra("name", getString(R.string.products));
                startActivity(intent);

                setResult(RESULT_CANCELED, intent);

                finish();
            }
        });
    }

    private void processIntent(Intent intent) {
        String from;
        if ((from = intent.getStringExtra("name")) != null) {
            Toast.makeText(getApplicationContext(),
                    "From : " + from, Toast.LENGTH_LONG).show();
        }
    }
}
