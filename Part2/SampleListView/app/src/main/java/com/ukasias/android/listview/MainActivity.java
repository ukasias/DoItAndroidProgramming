package com.ukasias.android.listview;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    Button addButton;
    EditText editText;

    ListView listView;
    SingerAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        addButton = (Button) findViewById(R.id.addButton);
        editText = (EditText) findViewById(R.id.editText);

        listView = (ListView) findViewById(R.id.listView);

        adapter = new SingerAdapter();

        adapter.addItem(new SingerItem
                ("소녀시대", "010-1000-1000", 20, R.drawable.singer));

        adapter.addItem(new SingerItem
                ("걸스데이", "010-2000-2000", 22, R.drawable.singer2));

        adapter.addItem(new SingerItem
                ("여자친구", "010-3000-3000", 21, R.drawable.singer3));

        adapter.addItem(new SingerItem
                ("티아라", "010-4000-4000", 24, R.drawable.singer4));

        adapter.addItem(new SingerItem
                ("AOA", "010-5000-5000", 25, R.drawable.singer5));

        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                SingerItem item = (SingerItem) adapter.getItem(position);
                Toast.makeText(getApplicationContext(),
                        "선택 : " + item.getName(),
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

    public class SingerAdapter extends BaseAdapter {
        ArrayList<SingerItem> items = new ArrayList<SingerItem>();

        public void addItem(SingerItem item) {
            items.add(item);
        }

        @Override
        public Object getItem(int i) {
            return items.get(i);
        }

        @Override
        public int getCount() {
            return items.size();
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup viewGroup) {
            SingerItemView view;
            if (convertView == null) {
                view = new SingerItemView(getApplicationContext());
            }
            else {
                view = (SingerItemView) convertView;
            }
            //view = new SingerItemView(getApplicationContext());

            SingerItem item = items.get(position);
            view.setName(item.getName());
            view.setMobile(item.getMobile());
            view.setAge(item.getAge());
            view.setImage(item.getResId());

            return view;
        }
    }
}