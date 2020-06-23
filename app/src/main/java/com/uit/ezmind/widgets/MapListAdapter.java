package com.uit.ezmind.widgets;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.content.FileProvider;

import com.uit.ezmind.R;
import com.uit.ezmind.data.MapData;
import com.uit.ezmind.maploader.MapLoader;
import com.uit.ezmind.maploader.MapManagerActivity;

import java.io.File;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class MapListAdapter extends BaseAdapter {
    Context context;
    List<MapData> data;
    boolean isInSelectMode;
    int layout;
    public int sortOption;
    private static LayoutInflater inflater = null;
    MapOptionsPopup popupWindow;

    public MapListAdapter(Context context, List<MapData> data, int layout) {
        this.context = context;
        this.data = data;
        this.layout = layout;
        inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        int width = (int) context.getResources().getDimension(R.dimen.button_size) * 4;
        int height = (int) context.getResources().getDimension(R.dimen.button_size);
        popupWindow = new MapOptionsPopup(context,width, height, true, MapListAdapter.this);
        popupWindow.setBackgroundDrawable(context.getDrawable(R.drawable.rounded_flat));
        popupWindow.setElevation(context.getResources().getDimension(R.dimen.elevation));
    }

    @Override
    public int getCount() {
        return data.size();
    }

    @Override
    public Object getItem(int position) {
        return data.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        View vi = convertView;
        if (vi == null)
            vi = inflater.inflate(R.layout.item_card, null);

        final TextView mapName = vi.findViewById(R.id.map_name);
        final TextView date = vi.findViewById(R.id.map_date);
        final ImageButton btn = vi.findViewById(R.id.map_option);

        if (isInSelectMode) btn.setVisibility(View.GONE);
        else btn.setVisibility(View.VISIBLE);

        mapName.setText(data.get(position).name);
        final MapLoader loader = new MapLoader(context);
        date.setText(data.get(position).getDate());
        ImageView thumbnail = vi.findViewById(R.id.map_thumbnail);
        int backgroundColor = context.getSharedPreferences("MyPrefs", Context.MODE_PRIVATE).getInt("background_color", 0);
        if (backgroundColor != 0)
            thumbnail.setBackgroundColor(backgroundColor);

        thumbnail.setImageBitmap(data.get(position).thumbnail);
        LinearLayout card = vi.findViewById(R.id.card_view);
        final LinearLayout.LayoutParams params;
        switch (layout) {
            case 0:
                card.setOrientation(LinearLayout.HORIZONTAL);
                params = (LinearLayout.LayoutParams) thumbnail.getLayoutParams();
                params.width = (int) context.getResources().getDimension(R.dimen.thumbnail_list_width);
                params.height = LinearLayout.LayoutParams.MATCH_PARENT;
                thumbnail.setLayoutParams(params);
                break;
            case 1:
                card.setOrientation(LinearLayout.VERTICAL);
                params = (LinearLayout.LayoutParams) thumbnail.getLayoutParams();
                params.width = LinearLayout.LayoutParams.MATCH_PARENT;
                params.height = LinearLayout.LayoutParams.WRAP_CONTENT;
                thumbnail.setLayoutParams(params);
                break;
        }

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                final View popup = inflater.inflate(R.layout.map_options, null);
                int vheight = (int) context.getResources().getDimension(R.dimen.thumbnail_list_height);
                int width = (int) context.getResources().getDimension(R.dimen.button_size) * 4;
                int height = (int) context.getResources().getDimension(R.dimen.button_size);
                popupWindow = new MapOptionsPopup(context,width, height, true, MapListAdapter.this);
                popupWindow.setBackgroundDrawable(context.getDrawable(R.drawable.rounded_flat));
                popupWindow.setElevation(context.getResources().getDimension(R.dimen.elevation));
                popupWindow.setPosition(position);
                popupWindow.showAsDropDown(v, -width + v.getWidth(), -vheight / 2 - height / 2);
            }
        });

        return vi;
    }

    public void setSelectMode(boolean isInSelectMode) {
        this.isInSelectMode = isInSelectMode;
        notifyDataSetChanged();
    }

    public void changeLayout(int layout) {
        this.layout = layout;
        ;
    }

    public void setData(List<MapData> data) {
        this.data = data;
        notifyDataSetChanged();
    }

    public void sortlist(final int option) {
        sortOption = option;
        Collections.sort(data, new Comparator<MapData>() {
            @Override
            public int compare(MapData o1, MapData o2) {
                switch (option) {
                    case 0:
                        return o1.name.compareTo(o2.name);
                    case 1:
                        return o2.name.compareTo(o1.name);
                    case 2:
                        return (o1.date > o2.date) ? -1 : 1;
                    case 3:
                        return (o1.date > o2.date) ? 1 : -1;
                }
                return o1.name.compareTo(o2.name);
            }
        });
        notifyDataSetChanged();
    }

    public int getLayout() {
        return layout;
    }
}
