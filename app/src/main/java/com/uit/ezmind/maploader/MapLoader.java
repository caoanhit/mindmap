package com.uit.ezmind.maploader;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
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
    public static final String SAVE_PATH = "/EzMind/saves/";
    public static final String THUMBNAIL_PATH = "/EzMind/.thumbnail/";
    public static final String SAVE_EXTENSION = ".ezmind";
    public static final String THUMBNAIL_EXTENSION = ".thb";
    public static final int maxNodeAmount = 255;
    Context context;

    public MapLoader(Context context) {
        this.context = context;
    }


    public boolean saveMap(String fileName, NodeData[] map) {
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)
            ActivityCompat.requestPermissions((Activity) context, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
        Gson gson = new Gson();
        String save = gson.toJson(map);
        File f = new File(Environment.getExternalStorageDirectory().getPath() + SAVE_PATH + fileName + SAVE_EXTENSION);
        try {
            FileOutputStream fos = new FileOutputStream(f);
            if (save != null) {
                fos.write(save.getBytes());
            }
            fos.close();
            Toast.makeText(context, "Map saved to \"" + fileName + "\"", Toast.LENGTH_SHORT).show();
            return true;
        } catch (IOException ioException) {
            Toast.makeText(context, "Error: Cannot save map", Toast.LENGTH_SHORT).show();
            return false;
        }
    }

    public NodeData[] loadMap(String fileName) {
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)
            ActivityCompat.requestPermissions((Activity) context, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
        NodeData[] map = new NodeData[maxNodeAmount];
        File f = new File(Environment.getExternalStorageDirectory().getPath() + SAVE_PATH + fileName + SAVE_EXTENSION);
        try {
            FileInputStream fin = new FileInputStream(f);
            String ret = convertStreamToString(fin);
            fin.close();
            JSONArray arr = new JSONArray(ret);
            Gson gson = new Gson();
            for (int i = 0; i < maxNodeAmount; i++) {
                try {
                    if (arr.getJSONObject(i) != null)
                        map[i] = gson.fromJson(String.valueOf(arr.getJSONObject(i)), NodeData.class);
                } catch (Exception ignored) {
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
        File f = new File(Environment.getExternalStorageDirectory().getPath() + THUMBNAIL_PATH + fileName + THUMBNAIL_EXTENSION);
        try (FileOutputStream out = new FileOutputStream(f)) {
            bmp.compress(Bitmap.CompressFormat.PNG, 100, out);
        } catch (IOException e) {
            e.printStackTrace();
        }
        bmp.recycle();
    }

    public Bitmap loadThumbnail(String fileName) {
        File f = new File(Environment.getExternalStorageDirectory().getPath() + THUMBNAIL_PATH + fileName + THUMBNAIL_EXTENSION);
        if (f.exists()) {
            return BitmapFactory.decodeFile(f.getAbsolutePath());
        }
        return null;
    }

    public boolean renameMap(String fileName, String newName) {
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)
            ActivityCompat.requestPermissions((Activity) context, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
        File f, t, fNew, tNew;
        f = new File(Environment.getExternalStorageDirectory().getPath() + SAVE_PATH + fileName + SAVE_EXTENSION);
        t = new File(Environment.getExternalStorageDirectory().getPath() + THUMBNAIL_PATH + fileName + THUMBNAIL_EXTENSION);
        fNew = new File(Environment.getExternalStorageDirectory().getPath() + SAVE_PATH + newName + SAVE_EXTENSION);
        tNew = new File(Environment.getExternalStorageDirectory().getPath() + THUMBNAIL_PATH + newName + THUMBNAIL_EXTENSION);
        if (f.exists() && f.renameTo(fNew)) {
            return t.exists() && t.renameTo(tNew);
        }
        return false;
    }

    public boolean copyMap(String fileName, String newName) {
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)
            ActivityCompat.requestPermissions((Activity) context, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
        File f, t, fNew, tNew;
        f = new File(Environment.getExternalStorageDirectory().getPath() + SAVE_PATH + fileName + SAVE_EXTENSION);
        t = new File(Environment.getExternalStorageDirectory().getPath() + THUMBNAIL_PATH + fileName + THUMBNAIL_EXTENSION);
        fNew = new File(Environment.getExternalStorageDirectory().getPath() + SAVE_PATH + newName + SAVE_EXTENSION);
        tNew = new File(Environment.getExternalStorageDirectory().getPath() + THUMBNAIL_PATH + newName + THUMBNAIL_EXTENSION);

        if (f.exists()) {
            try {
                copy(f, fNew);
            } catch (Exception e) {
                return false;
            }
            if (t.exists()) {
                try {
                    copy(t, tNew);
                } catch (Exception ignored) {
                }
            }
            return true;
        }
        return false;
    }

    public boolean deleteMap(String fileName) {
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)
            ActivityCompat.requestPermissions((Activity) context, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
        File f = new File(Environment.getExternalStorageDirectory().getPath() + SAVE_PATH + fileName + SAVE_EXTENSION);
        File t = new File(Environment.getExternalStorageDirectory().getPath() + THUMBNAIL_PATH + fileName + THUMBNAIL_EXTENSION);
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
        File f = new File(Environment.getExternalStorageDirectory().getPath() + SAVE_PATH);
        File t = new File(Environment.getExternalStorageDirectory().getPath() + THUMBNAIL_PATH);
        if (!f.exists()) {
            f.mkdirs();
        }
        if (!t.exists()) {
            t.mkdir();
        }
        List<String> list;
        File[] files = f.listFiles();
        if (files != null) {
            list = new ArrayList<>();
        } else return null;
        for (File file : files) {
            String filePath = file.getPath();
            if (filePath.endsWith(SAVE_EXTENSION)) {
                String filename = filePath.substring(filePath.lastIndexOf("/") + 1);
                filename = filename.substring(0, filename.length() - SAVE_EXTENSION.length());
                list.add(filename);
            }
        }
        String[] a = new String[list.size()];
        list.toArray(a);
        return a;
    }

    public long loadDate(String fileName) {
        File f;
        f = new File(Environment.getExternalStorageDirectory().getPath() + SAVE_PATH + fileName + SAVE_EXTENSION);

        if (f.exists()) return f.lastModified();
        return 0;
    }

    public List<MapData> loadMapList() {
        String[] names = getSavedMapsName();
        if (names == null || names.length == 0) return null;
        List<MapData> data = new ArrayList<>();
        for (String name : names) {
            data.add(new MapData(name, loadThumbnail(name), loadDate(name)));
        }
        return data;
    }

    public MapData loadMapData(String fileName) {
        return new MapData(fileName, loadThumbnail(fileName), loadDate(fileName));
    }

    public boolean mapExist(String fileName) {
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)
            ActivityCompat.requestPermissions((Activity) context, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
        File f;

        f = new File(Environment.getExternalStorageDirectory().getPath() + SAVE_PATH + fileName + SAVE_EXTENSION);


        return f.exists();
    }

    public static String convertStreamToString(InputStream is) throws Exception {
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        StringBuilder sb = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            sb.append(line).append("\n");
        }
        reader.close();
        return sb.toString();
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

    public boolean importMap(String path) {
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)
            ActivityCompat.requestPermissions((Activity) context, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
        File f, fNew;
        f = new File(path);
        String fileName = f.getName();
        fNew = new File(Environment.getExternalStorageDirectory().getPath() + SAVE_PATH + fileName);

        if (f.exists()) {
            try {
                copy(f, fNew);
            } catch (Exception e) {
                return false;
            }
            return true;
        }
        return false;
    }
}
