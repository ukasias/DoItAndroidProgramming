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
public class Fragment1 extends Fragment {
    @Override
    public void onStart() {
        super.onStart();
        Log.d("Fragment 1", "onStart()");
    }

    @Override
    public void onResume() {
        super.onResume();
        super.onStart();
        Log.d("Fragment 1", "onResume()");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        Log.d("Fragment 1", "onCreateView()");
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.fragment1,
                container, false);
        return rootView;
    }

}
