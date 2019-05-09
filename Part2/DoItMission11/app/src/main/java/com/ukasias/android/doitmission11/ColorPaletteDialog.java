package com.ukasias.android.doitmission11;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.LinearLayout;

import static com.ukasias.android.doitmission11.ColorPaletteDialog.listener;

public class ColorPaletteDialog extends Activity {
    GridView grid;
    Button close;
    ColorDataAdapter adapter;

    static MissionPaintBoardActivity.OnColorSelectedListener listener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_color_palette_dialog);

        setTitle("색상 선택");

        grid = (GridView) findViewById(R.id.grid);
        close = (Button) findViewById(R.id.close);

        grid.setColumnWidth(4);
        grid.setBackgroundColor(Color.GRAY);
        grid.setVerticalSpacing(4);
        grid.setHorizontalSpacing(4);

        adapter = new ColorDataAdapter(this);
        grid.setNumColumns(adapter.getColumnCount());
        grid.setAdapter(adapter);

        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }
}

class ColorDataAdapter extends BaseAdapter {
    private final String TAG = "ColorDataAdapter";

    public static final int [] colors = new int[] {
            0xff000000, 0xff00007f, 0xff0000ff, 0xff007f00, 0xff007f7f, 0xff00ff00, 0xff00ff7f,
            0xff00ffff, 0xff7f007f, 0xff7f00ff, 0xff7f7f00, 0xff7f7f7f, 0xffff0000, 0xffff007f,
            0xffff00ff, 0xffff7f00, 0xffff7f7f, 0xffff7fff, 0xffffff00, 0xffffff7f, 0xffffffff
    };

    Context _context;
    int rowCount;
    int columnCount;

    public ColorDataAdapter(Context context) {
        super();
        _context = context;
        rowCount = 3;
        columnCount = 7;
    }

    @Override
    public int getCount() {
        return colors.length;
    }

    @Override
    public Object getItem(int i) {
        if (i >= 0 && i < colors.length) {
            return colors[i];
        }
        return null;
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    public int getColumnCount() {
        return columnCount;
    }

    @Override
    public View getView(final int position, View view, ViewGroup viewGroup) {
        int rowIndex = position / columnCount;
        int columnIndex = position % columnCount;
        Log.d(TAG, "getView() - (" + rowIndex + ", " + columnIndex + ")");

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT);
        final Button button = new Button(_context);
        button.setBackgroundColor(colors[position]);
        button.setLayoutParams(params);
        button.setText("");
        button.setTag(colors[position]);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (listener != null) {
                    listener.onColorSelected(((Integer)view.getTag()).intValue());
                    ((ColorPaletteDialog)_context).finish();
                }
            }
        });

        return button;
    }
}
