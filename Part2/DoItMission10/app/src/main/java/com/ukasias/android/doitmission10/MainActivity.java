package com.ukasias.android.doitmission10;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {
    GridView grid;
    ProductAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        grid = (GridView) findViewById(R.id.gridView);
        adapter = new ProductAdapter(this);

        addItems();
        grid.setAdapter(adapter);

        grid.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                ProductItem item = (ProductItem) adapter.getItem(i);
                if (item != null) {
                    Toast.makeText(getApplicationContext(),
                            "상품명: " + item.getName()
                                    + ", 가격: " + item.getPrice() + " 원",
                            Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    public void addItems() {
        adapter.addItem(new ProductItem(
                R.drawable.clothes1,
                "롱코트",
                89000,
                "명절 기획 상품"
        ));
        adapter.addItem(new ProductItem(
                R.drawable.clothes2,
                "화이트 해프 자켓",
                119000,
                "신상품"));
        adapter.addItem(new ProductItem(
                R.drawable.clothes3,
                "블랙 정장 코트",
                99000,
                "이월 상품"
        ));

        adapter.addItem(new ProductItem(
                R.drawable.clothes4,
                "레이어드 코트",
                129000,
                "신상품"
        ));
    }
}
