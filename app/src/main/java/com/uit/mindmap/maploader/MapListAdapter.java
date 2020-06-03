package com.uit.mindmap.maploader;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.uit.mindmap.R;

public class MapListAdapter extends BaseAdapter {
    Context context;
    String[] data;
    private static LayoutInflater inflater = null;

    public MapListAdapter(Context context, String[] data){
        this.context = context;
        this.data = data;
        inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return data.length;
    }

    @Override
    public Object getItem(int position) {
        return data[position];
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View vi = convertView;
        if (vi == null)
            vi = inflater.inflate(R.layout.item_list, null);
        TextView mapName = vi.findViewById(R.id.map_name);
        TextView date= vi.findViewById(R.id.map_date);
        mapName.setText(data[position]);
        MapLoader loader=new MapLoader(context);
        date.setText(loader.mapDate(data[position]));
        ImageView thumbnail=vi.findViewById(R.id.map_thumbnail);
        thumbnail.setImageBitmap(loader.loadThumbnail(data[position]));
        return vi;
    }
}
