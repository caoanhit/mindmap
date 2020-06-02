package com.uit.mindmap.maploader;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.uit.mindmap.mapdrawer.Node;

import org.json.JSONArray;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

public class MapLoader {
    Context context;

    public MapLoader(Context context) {
        this.context = context;
    }

    public NodeData[] loadMap(String fileName) {
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)
            ActivityCompat.requestPermissions((Activity) context, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
        NodeData[] map = new NodeData[255];
        File fl = new File(Environment.getExternalStorageDirectory().getPath() + "/Mindmap/saves/" + fileName + ".map");
        try {
            FileInputStream fin = new FileInputStream(fl);
            String ret = convertStreamToString(fin);
            fin.close();
            JSONArray arr = new JSONArray(ret);
            Gson gson = new Gson();
            for (int i = 0; i < 255; i++) {
                try {
                    if (arr.getJSONObject(i) != null)
                        map[i] = gson.fromJson(String.valueOf(arr.getJSONObject(i)), NodeData.class);
                } catch (Exception e) {
                }
            }
        } catch (Exception e) {
            Toast.makeText(context, "Error: Cannot load map", Toast.LENGTH_SHORT).show();
            return null;
        }
        return map;
    }

    public boolean saveMap(String fileName, NodeData[] map) {
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)
            ActivityCompat.requestPermissions((Activity) context, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
        Gson gson = new Gson();
        String save = gson.toJson(map);
        File f;
        if (isExternalStorageWritable()) {
            f = new File(Environment.getExternalStorageDirectory().getPath() + "/Mindmap/saves/" + fileName + ".map");
        } else f = new File(Environment.getDataDirectory() + "/Mindmap/saves/" + fileName + ".map");
        try {
            FileOutputStream fos = new FileOutputStream(f);
            if (save != null) {
                fos.write(save.getBytes());
            }
            fos.close();
            Toast.makeText(context, "Map saved to \"" + fileName + "\"", Toast.LENGTH_SHORT).show();
            return true;
        } catch (IOException ioException) {
            Log.i("save", ioException.getMessage());
            Toast.makeText(context, "Error: Cannot save map", Toast.LENGTH_SHORT).show();
            return false;
        }
    }

    public String[] getSavedMapsName() {
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)
            ActivityCompat.requestPermissions((Activity) context, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
        File f;
        if (isExternalStorageWritable()) {
            f = new File(Environment.getExternalStorageDirectory().getPath() + "/Mindmap/saves");
        } else f = new File(Environment.getDataDirectory() + "/Mindmap/saves");
        File t;
        if (isExternalStorageWritable()) {
            t = new File(Environment.getExternalStorageDirectory().getPath() + "/Mindmap/.thumbnail");
        } else t = new File(Environment.getDataDirectory() + "/Mindmap/.thumbnail");
        if (!f.exists()) {
            f.mkdirs();
        }
        if (!t.exists()){
            t.mkdir();
        }
        List<String> list;
        File files[] = f.listFiles();
        if (files != null) {
            list = new ArrayList<>();
        } else return null;
        for (int i = 0; i < files.length; i++) {
            File file = files[i];
            String filePath = file.getPath();
            if (filePath.endsWith(".map")) {
                String filename = filePath.substring(filePath.lastIndexOf("/") + 1);
                filename = filename.substring(0, filename.length() - 4);
                list.add(filename);
            }
        }
        String[] a = new String[list.size()];
        list.toArray(a);
        return a;
    }

    private boolean isExternalStorageWritable() {
        if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {
            return true;
        }
        return false;
    }

    public boolean mapExist(String fileName) {
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)
            ActivityCompat.requestPermissions((Activity) context, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
        File f;
        if (isExternalStorageWritable()) {
            f = new File(Environment.getExternalStorageDirectory().getPath() + "/Mindmap/saves/" + fileName + ".map");
        } else f = new File(Environment.getDataDirectory() + "/Mindmap/saves/" + fileName + ".map");

        if (f.exists()) return true;
        return false;
    }

    public static String convertStreamToString(InputStream is) throws Exception {
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        StringBuilder sb = new StringBuilder();
        String line = null;
        while ((line = reader.readLine()) != null) {
            sb.append(line).append("\n");
        }
        reader.close();
        return sb.toString();
    }

    public void saveThumbnail(String fileName, Bitmap bmp) {
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)
            ActivityCompat.requestPermissions((Activity) context, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
        File f;
        if (isExternalStorageWritable()) {
            f = new File(Environment.getExternalStorageDirectory().getPath() + "/Mindmap/.thumbnail/" + fileName + ".thb");
        } else f = new File(Environment.getDataDirectory() + "/Mindmap/.thumbnail/" + fileName + ".thb");
        try (FileOutputStream out = new FileOutputStream(f)) {

            bmp.compress(Bitmap.CompressFormat.PNG, 100, out);
        } catch (IOException e) {
            e.printStackTrace();
            Log.i("thumbnail",e.getMessage());
        }
    }

    public boolean deleteMap(String fileName){
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)
            ActivityCompat.requestPermissions((Activity) context, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
        File f;
        if (isExternalStorageWritable()) {
            f = new File(Environment.getExternalStorageDirectory().getPath() + "/Mindmap/saves/" + fileName + ".map");
        } else f = new File(Environment.getDataDirectory() + "/Mindmap/saves/" + fileName + ".map");
        if (f.exists()){
            if(f.delete()){
                return true;
            }
        }
        return false;
    }
    public Bitmap loadThumbnail(String fileName){
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)
            ActivityCompat.requestPermissions((Activity) context, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
        File f;
        if (isExternalStorageWritable()) {
            f = new File(Environment.getExternalStorageDirectory().getPath() + "/Mindmap/.thumbnail/" + fileName + ".thb");
        } else f = new File(Environment.getDataDirectory() + "/Mindmap/.thumbnail/" + fileName + ".thb");
        if(f.exists()){
            Bitmap b= BitmapFactory.decodeFile(f.getAbsolutePath());
            return b;
        }
        return null;
    }
}
