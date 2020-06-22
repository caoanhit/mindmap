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
    int sortOption;
    private static LayoutInflater inflater = null;

    public MapListAdapter(Context context, List<MapData> data, int layout) {
        this.context = context;
        this.data = data;
        this.layout = layout;
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
        LinearLayout.LayoutParams params;
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
                int width = (int) context.getResources().getDimension(R.dimen.button_size) * 4;
                int height = (int) context.getResources().getDimension(R.dimen.button_size);
                int vheight = (int) context.getResources().getDimension(R.dimen.thumbnail_list_height);
                final PopupWindow popupWindow = new PopupWindow(popup, width, height, true);
                popupWindow.setBackgroundDrawable(context.getDrawable(R.drawable.rounded_flat));
                popupWindow.setElevation(context.getResources().getDimension(R.dimen.elevation));
                popup.findViewById(R.id.btn_rename_map).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (popupWindow.isShowing()) popupWindow.dismiss();
                        LayoutInflater li = LayoutInflater.from(context);
                        View customDialogView = li.inflate(R.layout.edit_text_dialog, null);
                        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
                        TextView t = customDialogView.findViewById(R.id.tv_dialog);
                        t.setText(context.getResources().getString(R.string.rename_map));
                        alertDialogBuilder.setView(customDialogView);
                        final EditText etName = (EditText) customDialogView.findViewById(R.id.name);
                        etName.setText(data.get(position).name);
                        etName.selectAll();
                        alertDialogBuilder.setCancelable(true).setPositiveButton("OK", null)
                                .setNegativeButton("Cancel", null);
                        final AlertDialog alertDialog = alertDialogBuilder.create();
                        alertDialog.setOnShowListener(new DialogInterface.OnShowListener() {
                            @Override
                            public void onShow(DialogInterface dialog) {
                                Button ok = alertDialog.getButton(AlertDialog.BUTTON_POSITIVE);
                                ok.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        String s = etName.getText().toString();
                                        if (loader.mapExist(s)) {
                                            Toast.makeText(context, "Map name already exists", Toast.LENGTH_SHORT).show();
                                        } else {
                                            if (loader.renameMap(data.get(position).name, etName.getText().toString())) {
                                                data.get(position).name = s;
                                                mapName.setText(etName.getText().toString());
                                                sortlist(sortOption);
                                            } else
                                                Toast.makeText(context, "Error: can not rename map", Toast.LENGTH_SHORT).show();
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
                        if (popupWindow.isShowing()) popupWindow.dismiss();
                        LayoutInflater li = LayoutInflater.from(context);
                        View customDialogView = li.inflate(R.layout.edit_text_dialog, null);
                        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
                        TextView t = customDialogView.findViewById(R.id.tv_dialog);
                        t.setText(context.getResources().getString(R.string.copy_map));
                        alertDialogBuilder.setView(customDialogView);
                        final EditText etName = (EditText) customDialogView.findViewById(R.id.name);
                        etName.setText(data.get(position).name + " (copy)");
                        etName.selectAll();
                        alertDialogBuilder.setCancelable(true).setPositiveButton("OK", null)
                                .setNegativeButton("Cancel", null);
                        final AlertDialog alertDialog = alertDialogBuilder.create();
                        alertDialog.setOnShowListener(new DialogInterface.OnShowListener() {
                            @Override
                            public void onShow(DialogInterface dialog) {
                                Button ok = alertDialog.getButton(AlertDialog.BUTTON_POSITIVE);
                                ok.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        String s = etName.getText().toString();
                                        if (loader.mapExist(s)) {
                                            Toast.makeText(context, "Map name already exists", Toast.LENGTH_SHORT).show();
                                        } else {
                                            if (loader.copyMap(data.get(position).name, s)) {
                                                data.add(loader.loadMapData(s));
                                                sortlist(sortOption);
                                            } else {
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
                        if (popupWindow.isShowing()) popupWindow.dismiss();
                        if (!loader.deleteMap(data.get(position).name)) {
                            Toast.makeText(context, "Error: can not delete map", Toast.LENGTH_SHORT).show();
                        } else {
                            data.remove(position);
                            if (data.size() == 0) {
                                Log.i("List", "empty");
                                ((MapManagerActivity) context).setEmpty();
                            }
                            notifyDataSetChanged();
                        }
                    }
                });

                popup.findViewById(R.id.btn_share_map).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        String filePath = Environment.getExternalStorageDirectory().getPath() + MapLoader.SAVE_PATH + data.get(position).name + MapLoader.SAVE_EXTENSION;
                        Intent intentShareFile = new Intent(Intent.ACTION_SEND);
                        File fileWithinMyDir = new File(filePath);

                        if (fileWithinMyDir.exists()) {
                            Uri uri = FileProvider.getUriForFile(
                                    context,
                                    "com.uit.ezmind.provider",
                                    fileWithinMyDir);
                            if (uri != null) {
                                // Grant temporary read permission to the content URI


                                intentShareFile.setType("text/plain");
                                intentShareFile.putExtra(Intent.EXTRA_STREAM, uri);

                                intentShareFile.addFlags(
                                        Intent.FLAG_GRANT_READ_URI_PERMISSION);

                                intentShareFile.putExtra(Intent.EXTRA_SUBJECT,
                                        "Sharing Map...");

                                context.startActivity(Intent.createChooser(intentShareFile, "Share Map"));
                            }
                        }
                    }
                });
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
