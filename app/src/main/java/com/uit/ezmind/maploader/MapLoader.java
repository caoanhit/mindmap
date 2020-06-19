package com.uit.ezmind.maploader;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.gson.Gson;
import com.uit.ezmind.data.MapData;
import com.uit.ezmind.data.NodeData;

import org.json.JSONArray;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.List;

public class MapLoader {
    public static final int maxNodeAmount=255;
    Context context;

    public MapLoader(Context context) {
        this.context = context;
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

    public NodeData[] loadMap(String fileName) {
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)
            ActivityCompat.requestPermissions((Activity) context, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
        NodeData[] map = new NodeData[maxNodeAmount];
        File fl = new File(Environment.getExternalStorageDirectory().getPath() + "/Mindmap/saves/" + fileName + ".map");
        try {
            FileInputStream fin = new FileInputStream(fl);
            String ret = convertStreamToString(fin);
            fin.close();
            JSONArray arr = new JSONArray(ret);
            Gson gson = new Gson();
            for (int i = 0; i < maxNodeAmount; i++) {
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

    public void saveThumbnail(String fileName, Bitmap bmp) {
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)
            ActivityCompat.requestPermissions((Activity) context, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
        File f;
        if (isExternalStorageWritable()) {
            f = new File(Environment.getExternalStorageDirectory().getPath() + "/Mindmap/.thumbnail/" + fileName + ".thb");
        } else
            f = new File(Environment.getDataDirectory() + "/Mindmap/.thumbnail/" + fileName + ".thb");
        try (FileOutputStream out = new FileOutputStream(f)) {

            bmp.compress(Bitmap.CompressFormat.PNG, 100, out);
        } catch (IOException e) {
            e.printStackTrace();
            Log.i("thumbnail", e.getMessage());
        }
        bmp.recycle();
    }

    public Bitmap loadThumbnail(String fileName) {
//        if (ContextCompat.checkSelfPermission(context, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)
//            ActivityCompat.requestPermissions((Activity) context, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
        File f;
        if (isExternalStorageWritable()) {
            f = new File(Environment.getExternalStorageDirectory().getPath() + "/Mindmap/.thumbnail/" + fileName + ".thb");
        } else
            f = new File(Environment.getDataDirectory() + "/Mindmap/.thumbnail/" + fileName + ".thb");
        if (f.exists()) {
            Bitmap b = BitmapFactory.decodeFile(f.getAbsolutePath());
            return b;
        }
        return null;
    }

    public boolean renameMap(String fileName, String newName) {
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)
            ActivityCompat.requestPermissions((Activity) context, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
        File f, t, fNew, tNew;
        if (isExternalStorageWritable()) {
            f = new File(Environment.getExternalStorageDirectory().getPath() + "/Mindmap/saves/" + fileName + ".map");
            t = new File(Environment.getExternalStorageDirectory().getPath() + "/Mindmap/.thumbnail/" + fileName + ".thb");
            fNew = new File(Environment.getExternalStorageDirectory().getPath() + "/Mindmap/saves/" + newName + ".map");
            tNew = new File(Environment.getExternalStorageDirectory().getPath() + "/Mindmap/.thumbnail/" + newName + ".thb");

        } else {
            f = new File(Environment.getDataDirectory() + "/Mindmap/saves/" + fileName + ".map");
            t = new File(Environment.getDataDirectory() + "/Mindmap/.thumbnail/" + fileName + ".thb");
            fNew = new File(Environment.getDataDirectory() + "/Mindmap/saves/" + newName + ".map");
            tNew = new File(Environment.getDataDirectory() + "/Mindmap/.thumbnail/" + newName + ".thb");
        }
        if (f.exists() && f.renameTo(fNew)) {
            if (t.exists() && t.renameTo(tNew))
            {

            }
            else return false;
            return true;
        }
        return false;
    }

    public boolean copyMap(String fileName, String newName) {
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)
            ActivityCompat.requestPermissions((Activity) context, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
        File f, t, fNew, tNew;
        if (isExternalStorageWritable()) {
            f = new File(Environment.getExternalStorageDirectory().getPath() + "/Mindmap/saves/" + fileName + ".map");
            t = new File(Environment.getExternalStorageDirectory().getPath() + "/Mindmap/.thumbnail/" + fileName + ".thb");
            fNew = new File(Environment.getExternalStorageDirectory().getPath() + "/Mindmap/saves/" + newName + ".map");
            tNew = new File(Environment.getExternalStorageDirectory().getPath() + "/Mindmap/.thumbnail/" + newName + ".thb");

        } else {
            f = new File(Environment.getDataDirectory() + "/Mindmap/saves/" + fileName + ".map");
            t = new File(Environment.getDataDirectory() + "/Mindmap/.thumbnail/" + fileName + ".thb");
            fNew = new File(Environment.getDataDirectory() + "/Mindmap/saves/" + newName + ".map");
            tNew = new File(Environment.getDataDirectory() + "/Mindmap/.thumbnail/" + newName + ".thb");
        }
        if (f.exists()) {
            try {
                copy(f, fNew);
            } catch (Exception e) {
                return false;
            }
            if (t.exists()) {
                try {
                    copy(t, tNew);
                } catch (Exception e) {
                }
            }
            return true;
        }
        return false;
    }

    public boolean deleteMap(String fileName) {
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)
            ActivityCompat.requestPermissions((Activity) context, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
        File f, t;
        if (isExternalStorageWritable()) {
            f = new File(Environment.getExternalStorageDirectory().getPath() + "/Mindmap/saves/" + fileName + ".map");
            t = new File(Environment.getExternalStorageDirectory().getPath() + "/Mindmap/.thumbnail/" + fileName + ".thb");
        } else {
            f = new File(Environment.getDataDirectory() + "/Mindmap/saves/" + fileName + ".map");
            t = new File(Environment.getDataDirectory() + "/Mindmap/.thumbnail/" + fileName + ".thb");
        }
        if (f.exists()) {
            if (f.delete()) {
                if (t.exists()) {
                    t.delete();
                }
                return true;
            }
        }
        return false;
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
        if (!t.exists()) {
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

//    public String mapDate(String fileName) {
//        if (ContextCompat.checkSelfPermission(context, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)
//            ActivityCompat.requestPermissions((Activity) context, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
//        File f;
//        if (isExternalStorageWritable()) {
//            f = new File(Environment.getExternalStorageDirectory().getPath() + "/Mindmap/saves/" + fileName + ".map");
//        } else f = new File(Environment.getDataDirectory() + "/Mindmap/saves/" + fileName + ".map");
//        DateFormat dateFormat = DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.MEDIUM);
//
//        if (f.exists()) return dateFormat.format(f.lastModified());
//        return "";
//    }
    public long loadDate(String fileName){
//        if (ContextCompat.checkSelfPermission(context, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)
//            ActivityCompat.requestPermissions((Activity) context, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
        File f;
        if (isExternalStorageWritable()) {
            f = new File(Environment.getExternalStorageDirectory().getPath() + "/Mindmap/saves/" + fileName + ".map");
        } else f = new File(Environment.getDataDirectory() + "/Mindmap/saves/" + fileName + ".map");

        if (f.exists()) return f.lastModified();
        return 0;
    }
    public List<MapData> loadMapList(){
        String[] names=getSavedMapsName();
        if (names==null||names.length==0) return null;
        List<MapData> data= new ArrayList<>();
        for (int i=0; i<  names.length; i++) {
            data.add(new MapData(names[i],loadThumbnail(names[i]),loadDate(names[i])));
        }
        return data;
    }
    public MapData loadMapData(String fileName){
        return new MapData(fileName, loadThumbnail(fileName),loadDate(fileName));
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

    private boolean isExternalStorageWritable() {
        if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {
            return true;
        }
        return false;
    }

    private void copy(File src, File dst) throws IOException {
        FileInputStream inStream = new FileInputStream(src);
        FileOutputStream outStream = new FileOutputStream(dst);
        FileChannel inChannel = inStream.getChannel();
        FileChannel outChannel = outStream.getChannel();
        inChannel.transferTo(0, inChannel.size(), outChannel);
        inStream.close();
        outStream.close();
    }
}
