package com.uit.mindmap.widgets;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.uit.mindmap.R;
import com.uit.mindmap.data.MapData;
import com.uit.mindmap.maploader.MapLoader;
import com.uit.mindmap.maploader.MapManagerActivity;

import java.util.List;

public class MapListAdapter extends BaseAdapter {
    Context context;
    List<MapData> data;
    boolean isInSelectMode;
    private static LayoutInflater inflater = null;

    public MapListAdapter(Context context, List<MapData> data){
        this.context = context;
        this.data = data;
        inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
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
            vi = inflater.inflate(R.layout.item_list, null);

        final TextView mapName = vi.findViewById(R.id.map_name);
        final TextView date= vi.findViewById(R.id.map_date);
        final ImageButton btn= vi.findViewById(R.id.map_option);

        if (isInSelectMode) btn.setVisibility(View.GONE);
        else btn.setVisibility(View.VISIBLE);

        mapName.setText(data.get(position).name);
        final MapLoader loader=new MapLoader(context);
        date.setText(data.get(position).getDate());
        ImageView thumbnail=vi.findViewById(R.id.map_thumbnail);
        thumbnail.setImageBitmap(data.get(position).thumbnail);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LayoutInflater inflater=(LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                final View popup= inflater.inflate(R.layout.map_options,null);
                final PopupWindow popupWindow=new PopupWindow(popup,LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT,true);
                popupWindow.setBackgroundDrawable(context.getDrawable(R.drawable.rounded_flat));
                popupWindow.setElevation(context.getResources().getDimension(R.dimen.elevation));
                popup.findViewById(R.id.btn_rename_map).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(popupWindow.isShowing()) popupWindow.dismiss();
                        LayoutInflater li = LayoutInflater.from(context);
                        View customDialogView = li.inflate(R.layout.edit_text_dialog, null);
                        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
                        TextView t=customDialogView.findViewById(R.id.tv_dialog);
                        t.setText(context.getResources().getString(R.string.rename_map));
                        alertDialogBuilder.setView(customDialogView);
                        final EditText etName = (EditText) customDialogView.findViewById(R.id.name);
                        etName.setText(data.get(position).name);
                        etName.selectAll();
                        alertDialogBuilder.setCancelable(true).setPositiveButton("OK",null)
                        .setNegativeButton("Cancel",null);
                        final AlertDialog alertDialog = alertDialogBuilder.create();
                        alertDialog.setOnShowListener(new DialogInterface.OnShowListener() {
                            @Override
                            public void onShow(DialogInterface dialog) {
                                Button ok=alertDialog.getButton(AlertDialog.BUTTON_POSITIVE);
                                ok.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        String s=etName.getText().toString();
                                        if(loader.mapExist(s)){
                                            Toast.makeText(context, "Map name already exists", Toast.LENGTH_SHORT).show();
                                        }
                                        else {
                                            if(loader.renameMap(data.get(position).name, etName.getText().toString())) {
                                                data.get(position).name=s;
                                                mapName.setText(etName.getText().toString());
                                                notifyDataSetChanged();
                                            }
                                            else Toast.makeText(context, "Error: can not rename map", Toast.LENGTH_SHORT).show();
                                            alertDialog.dismiss();
                                        }
                                    }
                                });
                            }
                        });
                        alertDialog.show();
                    }
                });
                popup.findViewById(R.id.btn_duplicate_map).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(popupWindow.isShowing()) popupWindow.dismiss();
                        LayoutInflater li = LayoutInflater.from(context);
                        View customDialogView = li.inflate(R.layout.edit_text_dialog, null);
                        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
                        TextView t=customDialogView.findViewById(R.id.tv_dialog);
                        t.setText(context.getResources().getString(R.string.copy_map));
                        alertDialogBuilder.setView(customDialogView);
                        final EditText etName = (EditText) customDialogView.findViewById(R.id.name);
                        etName.setText(data.get(position).name+ " (copy)");
                        etName.selectAll();
                        alertDialogBuilder.setCancelable(true).setPositiveButton("OK",null)
                                .setNegativeButton("Cancel",null);
                        final AlertDialog alertDialog = alertDialogBuilder.create();
                        alertDialog.setOnShowListener(new DialogInterface.OnShowListener() {
                            @Override
                            public void onShow(DialogInterface dialog) {
                                Button ok=alertDialog.getButton(AlertDialog.BUTTON_POSITIVE);
                                ok.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        String s=etName.getText().toString();
                                        if(loader.mapExist(s)){
                                            Toast.makeText(context, "Map name already exists", Toast.LENGTH_SHORT).show();
                                        }
                                        else {
                                            if(loader.copyMap(data.get(position).name,s)) {
                                                data.add(loader.loadMapData(s));
                                                notifyDataSetChanged();
                                            }
                                            else {
                                                Toast.makeText(context, "Error: can not copy map", Toast.LENGTH_SHORT).show();
                                            }
                                            alertDialog.dismiss();
                                        }
                                    }
                                });
                            }
                        });
                        alertDialog.show();
                    }
                });
                popup.findViewById(R.id.btn_delete_map).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(popupWindow.isShowing()) popupWindow.dismiss();
                        if(!loader.deleteMap(data.get(position).name)){
                            Toast.makeText(context, "Error: can not delete map", Toast.LENGTH_SHORT).show();
                        }
                        else{
                            data.remove(position);
                            if(data.size()==0) {
                                Log.i("List", "empty" );
                                ((MapManagerActivity)context).setEmpty();
                            }
                            notifyDataSetChanged();
                        }
                    }
                });
                popupWindow.showAsDropDown(v,-v.getWidth(),-v.getHeight());
            }
        });

        return vi;
    }
    public void setSelectMode(boolean isInSelectMode){
        this.isInSelectMode=isInSelectMode;
        notifyDataSetChanged();
    }
}
