package com.uit.mindmap.maploader;

import android.Manifest;
import android.content.Context;
import android.os.Environment;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

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
        File f;
        if (isExternalStorageWritable()) {
            f = new File(Environment.getExternalStorageDirectory().getPath() + "/Mindmap/saves/" + fileName + ".map");
        }
        else f= new File(Environment.getDataDirectory()+"/Mindmap/saves/"+fileName+".map");
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
        File f;
        if (isExternalStorageWritable()) {
            f = new File(Environment.getExternalStorageDirectory().getPath() + "/Mindmap/saves");
        }
        else f= new File(Environment.getDataDirectory()+"/Mindmap/saves");
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
            String filePath = file.getPath();
            if(filePath.endsWith(".map")) {
                String filename=filePath.substring(filePath.lastIndexOf("/")+1);
                filename=filename.substring(0,filename.length()-4);
                list.add(filename);
            }
        }
        String[] a=new String[list.size()];
        list.toArray(a);
        return a;
    }
    private  boolean isExternalStorageWritable(){
        if(Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {
            return true;
        }
        return false;
    }
    public boolean mapExist(String fileName){
        File f;
        if (isExternalStorageWritable()) {
            f = new File(Environment.getExternalStorageDirectory().getPath() + "/Mindmap/saves/" + fileName + ".map");
        }
        else f= new File(Environment.getDataDirectory()+"/Mindmap/saves/"+fileName+".map");
        if (f.exists()) return true;
        return false;
    }
}
