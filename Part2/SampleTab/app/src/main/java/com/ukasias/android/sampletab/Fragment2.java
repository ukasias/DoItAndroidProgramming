package com.ukasias.android.sampletab;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


/**
 * A simple {@link Fragment} subclass.
 */
public class Fragment2 extends Fragment {
    @Override
    public void onStart() {
        super.onStart();
        Log.d("Fragment 2", "onStart()");
    }

    @Override
    public void onResume() {
        super.onResume();
        super.onStart();
        Log.d("Fragment 2", "onResume()");
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        Log.d("Fragment 2", "onCreateView()");
        return inflater.inflate(R.layout.fragment2, container, false);
    }

}
