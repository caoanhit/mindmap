package com.uit.mindmap.maploader;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Environment;
import android.util.Log;
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
    public MapLoader(Context context){
        this.context=context;
    }
    public NodeData[] loadMap(String fileName){
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.READ_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED)
            ActivityCompat.requestPermissions((Activity) context, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE,Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
        NodeData[] map = new NodeData[255];
        File fl = new File(Environment.getExternalStorageDirectory().getPath() + "/Mindmap/saves/" + fileName + ".map");
        try {
            FileInputStream fin = new FileInputStream(fl);
            String ret = convertStreamToString(fin);
            fin.close();
            JSONArray arr=new JSONArray(ret);
            Gson gson = new Gson();
            for(int i=0; i<255;i++){
                try {
                    if(arr.getJSONObject(i)!=null)
                        map[i]=gson.fromJson(String.valueOf(arr.getJSONObject(i)),NodeData.class);
                }
                catch (Exception e){
                }
            }
        } catch (FileNotFoundException fileNotFound) {
            Log.i("load","File not found");
            return null;
        } catch (IOException ioException) {
            Log.i("load",ioException.getMessage());
            return null;
        } catch (Exception e) {
            Log.i("error",e.getMessage());
            return null;
        }
        return map;
    }

    public boolean saveMap(String fileName, NodeData[] map) {
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.READ_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED)
            ActivityCompat.requestPermissions((Activity) context, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE,Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
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
            return true;
        } catch (FileNotFoundException fileNotFound) {
            Log.i("save","File not found");
            return false;
        } catch (IOException ioException) {
            Log.i("save",ioException.getMessage());
            return false;
        }
    }

    public String[] getSavedMapsName() {
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.READ_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED)
            ActivityCompat.requestPermissions((Activity) context, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE,Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
        File f;
        if (isExternalStorageWritable()) {
            f = new File(Environment.getExternalStorageDirectory().getPath() + "/Mindmap/saves");
        } else f = new File(Environment.getDataDirectory() + "/Mindmap/saves");
        if (!f.exists()) {
            f.mkdirs();
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
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.READ_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED)
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
}
