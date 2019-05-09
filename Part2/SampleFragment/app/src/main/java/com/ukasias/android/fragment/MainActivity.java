package com.ukasias.android.fragment;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class MainActivity extends AppCompatActivity {
    MainFragment mainFragment;
    MenuFragment menuFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        /*
        mainFragment = (MainFragment) getSupportFragmentManager()
                .findFragmentById(R.id.mainFragment);
                */
        mainFragment = new MainFragment();
        menuFragment = new MenuFragment();
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.container, mainFragment).commit();
    }

    public void onFragmentChanged(int index) {
        switch (index) {
            case 0:
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.container, menuFragment).commit();
                break;
            case 1:
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.container, mainFragment).commit();
                break;
            default:
                break;
        }
    }
}
