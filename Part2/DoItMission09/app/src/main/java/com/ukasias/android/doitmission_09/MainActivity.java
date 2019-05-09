package com.ukasias.android.doitmission_09;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.List;

public class MainActivity extends AppCompatActivity {
    TextView numText;
    EditText nameText;
    EditText birthText;
    EditText phoneText;
    Button addButton;
    ListView listView;

    CustomerInfoAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        numText = (TextView) findViewById(R.id.num);
        nameText = (EditText) findViewById(R.id.name);
        birthText = (EditText) findViewById(R.id.birth);
        phoneText = (EditText) findViewById(R.id.phone);
        addButton = (Button) findViewById(R.id.add);
        listView = (ListView) findViewById(R.id.listView);

        adapter = new CustomerInfoAdapter(this);
        listView.setAdapter(adapter);

        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                adapter.addItem(
                        nameText.getText().toString(),
                        birthText.getText().toString(),
                        phoneText.getText().toString());
                adapter.notifyDataSetChanged();
                numText.setText(String.valueOf(adapter.getCount()) + "명");
            }
        });
    }
}
