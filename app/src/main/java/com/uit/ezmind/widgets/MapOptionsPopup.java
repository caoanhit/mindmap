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
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.Toast;

import androidx.core.content.FileProvider;

import com.uit.ezmind.R;
import com.uit.ezmind.data.MapData;
import com.uit.ezmind.maploader.MapLoader;
import com.uit.ezmind.maploader.MapManagerActivity;

import java.io.File;
import java.util.List;

public class MapOptionsPopup extends PopupWindow {
    int position;
    MapListAdapter adapter;


    public MapOptionsPopup(final Context context, int width, int height, boolean focusable, final MapListAdapter adapter){
        this.adapter=adapter;
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View popup = inflater.inflate(R.layout.map_options, null);
        setContentView(popup);
        setWidth(width);
        setHeight(height);
        setFocusable(focusable);
        final MapLoader loader=new MapLoader(context);
        popup.findViewById(R.id.btn_rename_map).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TextDialog textDialog = new TextDialog(context);
                textDialog.setDialogTitle(R.string.rename_map)
                        .setDialogText(adapter.data.get(position).name)
                        .setOnOKClicked(new TextDialog.OnOKClicked() {
                            @Override
                            public void OnClick(String text, AlertDialog alertDialog) {
                                if (loader.mapExist(text)) {
                                    Toast.makeText(context, "Map name already exists", Toast.LENGTH_SHORT).show();
                                } else {
                                    if (loader.renameMap(adapter.data.get(position).name, text)) {
                                        adapter.data.get(position).name = text;
                                        adapter.sortlist(adapter.sortOption);
                                    } else
                                        Toast.makeText(context, "Error: can not rename map", Toast.LENGTH_SHORT).show();
                                    alertDialog.dismiss();
                                    adapter.notifyDataSetChanged();
                                }
                            }
                        }).show();
            }
        });
        popup.findViewById(R.id.btn_duplicate_map).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (MapOptionsPopup.this.isShowing()) MapOptionsPopup.this.dismiss();
                TextDialog textDialog = new TextDialog(context);
                textDialog.setDialogTitle(R.string.copy_map)
                        .setDialogText(adapter.data.get(position).name + " (copy)")
                        .setOnOKClicked(new TextDialog.OnOKClicked() {
                            @Override
                            public void OnClick(String text, AlertDialog alertDialog) {
                                if (loader.mapExist(text)) {
                                    Toast.makeText(context, "Map name already exists", Toast.LENGTH_SHORT).show();
                                } else {
                                    if (loader.copyMap(adapter.data.get(position).name, text)) {
                                        adapter.data.add(loader.loadMapData(text));
                                        adapter.sortlist(adapter.sortOption);
                                    } else {
                                        Toast.makeText(context, "Error: can not copy map", Toast.LENGTH_SHORT).show();
                                    }
                                    alertDialog.dismiss();
                                }
                            }
                        }).show();
            }
        });
        popup.findViewById(R.id.btn_delete_map).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (context.getSharedPreferences("MyPrefs",Context.MODE_PRIVATE).getBoolean("confirmDelete",true)) {
                    AlertDialog.Builder dialog = new AlertDialog.Builder(context);
                    dialog.setMessage("Do you want to delete map?");
                    dialog.setIcon(R.mipmap.ic_launcher);
                    dialog.setPositiveButton(R.string.no, null);
                    dialog.setNegativeButton(R.string.yes, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            if (MapOptionsPopup.this.isShowing()) MapOptionsPopup.this.dismiss();
                            if (!loader.deleteMap(adapter.data.get(position).name)) {
                                Toast.makeText(context, "Error: can not delete map", Toast.LENGTH_SHORT).show();
                            } else {
                                adapter.data.remove(position);
                                if (adapter.data.size() == 0) {
                                    Log.i("List", "empty");
                                    ((MapManagerActivity) context).setEmpty();
                                }
                                adapter.notifyDataSetChanged();
                            }
                        }
                    });
                    dialog.show();
                }
                else {
                    if (MapOptionsPopup.this.isShowing()) MapOptionsPopup.this.dismiss();
                    if (!loader.deleteMap(adapter.data.get(position).name)) {
                        Toast.makeText(context, "Error: can not delete map", Toast.LENGTH_SHORT).show();
                    } else {
                        adapter.data.remove(position);
                        if (adapter.data.size() == 0) {
                            Log.i("List", "empty");
                            ((MapManagerActivity) context).setEmpty();
                        }
                        adapter.notifyDataSetChanged();
                    }
                }
            }
        });

        popup.findViewById(R.id.btn_share_map).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String filePath = Environment.getExternalStorageDirectory().getPath() + MapLoader.SAVE_PATH + adapter.data.get(position).name + MapLoader.SAVE_EXTENSION;
                Intent intentShareFile = new Intent(Intent.ACTION_SEND);
                File fileWithinMyDir = new File(filePath);

                if (fileWithinMyDir.exists()) {
                    Uri uri = FileProvider.getUriForFile(
                            context,
                            "com.uit.ezmind.provider",
                            fileWithinMyDir);
                    if (uri != null) {
                        // Grant temporary read permission to the content URI


                        intentShareFile.setType("application/pdf");
                        intentShareFile.putExtra(Intent.EXTRA_STREAM, uri);

                        intentShareFile.addFlags(
                                Intent.FLAG_GRANT_READ_URI_PERMISSION);

                        intentShareFile.putExtra(Intent.EXTRA_SUBJECT,
                                adapter.data.get(position).name + MapLoader.SAVE_EXTENSION);

                        context.startActivity(Intent.createChooser(intentShareFile, "Share Map"));
                    }
                }
            }
        });
    }

    public MapOptionsPopup setPosition(int position) {
        this.position = position;
        return this;
    }
}
