package com.ukasias.android.doitmission18;

import android.content.ContentValues;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
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
import android.widget.ListView;

public class SearchFragment extends Fragment {
    private static final String TAG = "SearchFragment";

    public interface loadBooks {
        ContentValues[] loadTable();
    }

    loadBooks callback;

    private Drawable bookImage;

    ListView listView;
    BookItemAdapter adapter;
    Context _context;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        print("onAttach()");

        if (context instanceof loadBooks) {
            callback = (loadBooks) context;
        }
        _context = context;
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

        View rootView = inflater.inflate(R.layout.fragment_search,
                container, false);

        Resources res = _context.getResources();
        bookImage = res.getDrawable(R.drawable.book);

        listView = rootView.findViewById(R.id.listView);

        adapter = new BookItemAdapter(_context);
        listView.setAdapter(adapter);

        return rootView;
    }

    public void loadList() {
        print("loadList()");
        if (callback == null) {
            print("callback is null");
        }
        else {
            print("callback is not null");
        }

        adapter.clearItems();

        ContentValues[] records = callback.loadTable();

        if (records != null) {
            int count = records.length;
            print("records.length : " + count);

            for (int i = 0; i < count; i++) {
                adapter.addBookItem(
                        new BookItem(bookImage,
                                (String) records[i].get("title"),
                                (String) records[i].get("author"),
                                (String) records[i].get("contents")));
            }
            adapter.notifyDataSetChanged();
        }
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

        /* when SearchFragment shows on display,
           read table.
         */
        loadList();
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
