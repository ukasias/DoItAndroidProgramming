package com.ukasias.android.multimemo;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.ukasias.android.multimemo.common.TitleBackgroundButton;
import com.ukasias.android.multimemo.common.TitleBitmapButton;

public class MultiMemoActivity extends AppCompatActivity {
    private static final String TAG = "MultiMemoActivity";

    TitleBackgroundButton titleBackgroundButton;
    ListView memoList;
    TitleBitmapButton newMemoButton;
    TitleBitmapButton closeButton;

    private MemoListAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        titleBackgroundButton = findViewById(R.id.titleBackgroundButton);
        memoList = findViewById(R.id.memoList);
        newMemoButton = findViewById(R.id.newMemoButton);
        closeButton = findViewById(R.id.closeButton);

        adapter = new MemoListAdapter(this);
        memoList.setAdapter(adapter);
        memoList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                viewMemo(i);
            }
        });

        newMemoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                print("newMemoButton clicked.");
            }
        });

        closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                print("closeButton clicked.");
                finish();
            }
        });

        loadMemoListData();
    }

    private void print(String message) {
        Log.d(TAG, message + "\n");
    }

    private void toast(String message) {
        Toast.makeText(
                getApplicationContext(),
                message,
                Toast.LENGTH_LONG
        ).show();
    }

    private void loadMemoListData() {
        MemoListItem aItem = new MemoListItem("1",
                "2018-08-10 10:20",
                "집에 가서 치맥을 먹자",
                null, null,
                null, null,
                null, null,
                null, null);

        adapter.addItem(aItem);
        adapter.notifyDataSetChanged();
    }

    private void viewMemo(int position) {}
}
