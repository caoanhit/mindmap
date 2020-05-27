package com.uit.mindmap.maploader;

import android.Manifest;
import android.content.Context;
import android.os.Environment;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;

import com.google.gson.Gson;
import com.uit.mindmap.mapdrawer.Node;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MapLoader {
    public NodeData[] loadMap(String name){
        NodeData[] map= new NodeData[255];


        return  map;
    }
    public boolean saveMap(Context context, String fileName, NodeData[] map){
        Gson gson=new Gson();
        String save=gson.toJson(map);
        File f = new File(Environment.getExternalStorageDirectory().getPath()+"/Mindmap/saves"+fileName+".map");

        try {
            FileOutputStream fos = new FileOutputStream(f);
            if (save != null) {
                fos.write(save.getBytes());
            }
            fos.close();
            return true;
        } catch (FileNotFoundException fileNotFound) {
            return false;
        } catch (IOException ioException) {
            return false;
        }
    }
    public String[] getSavedMapsName(){

        File f = new File(Environment.getExternalStorageDirectory().getPath()+"/Mindmap/saves");
        if (!f.exists()) {
            f.mkdirs();
        }
        List<String> list;
        File files[] = f.listFiles();
        if(files!=null) {
            list=new ArrayList<>();
        }
        else return null;
        for(int i=0; i<files.length; i++)
        {
            File file = files[i];
            /*It's assumed that all file in the path are in supported type*/
            String filePath = file.getPath();
            if(filePath.endsWith(".map")) // Condition to check .jpg file extension
                list.add(filePath);
        }
        String[] a=new String[list.size()];
        list.toArray(a);
        return a;
    }
}
