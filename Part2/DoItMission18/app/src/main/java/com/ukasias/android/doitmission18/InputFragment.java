package com.ukasias.android.doitmission18;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

public class InputFragment extends Fragment {
    private static final String TAG = "InputFragment";

    public interface SaveBook {
        void save(String title, String author, String contents);
    }

    private SaveBook callback;

    EditText titleText;
    EditText authorText;
    EditText contentsText;
    Button saveButton;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        print("onAttach()");

        if (context instanceof SaveBook) {
            callback = (SaveBook) context;
        }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        print("onCreate()");
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable final ViewGroup container, @Nullable Bundle savedInstanceState) {
        print("onCreateView()");

        View rootView = inflater.inflate(R.layout.fragment_input,
                container, false);


        titleText = rootView.findViewById(R.id.titleText);
        authorText = rootView.findViewById(R.id.authorText);
        contentsText = rootView.findViewById(R.id.contentsText);
        saveButton = rootView.findViewById(R.id.saveButton);

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                callback.save(
                        titleText.getText().toString(),
                        authorText.getText().toString(),
                        contentsText.getText().toString()
                );
            }
        });

        return rootView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        print("onActivityCreated()");
    }

    @Override
    public void onStart() {
        super.onStart();
        print("onStart()");
        titleText.setText("");
        authorText.setText("");
        contentsText.setText("");
    }

    @Override
    public void onResume() {
        super.onResume();
        print("onResume()");
    }

    @Override
    public void onPause() {
        super.onPause();
        print("onPause()");
    }

    @Override
    public void onStop() {
        super.onStop();
        print("onStop()");
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        print("onDestroyView()");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        print("onDestroy()");
    }

    @Override
    public void onDetach() {
        super.onDetach();
        print("onDetach()");
    }

    private void print(String str) {
        Log.d(TAG, str + "\n");
    }
}
