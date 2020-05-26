package com.uit.mindmap.maploader;

import android.content.Context;
import android.widget.Toast;

import com.google.gson.Gson;
import com.uit.mindmap.mapdrawer.Node;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class MapLoader {
    public NodeData[] loadMap(String name){
        NodeData[] map= new NodeData[255];


        return  map;
    }
    public boolean saveMap(Context context, String fileName, NodeData[] map){
        Gson gson=new Gson();
        String save=gson.toJson(map);
        String FILENAME = "storage.json";
        try {
            FileOutputStream fos = context.openFileOutput(fileName,Context.MODE_PRIVATE);
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

        File path = new File("/storage/" + "");


        File list[] = path.listFiles();
        String[] names=new String[list.length];
        for( int i=0; i< list.length; i++)
        {
            names[i]= list[i].getName().toString();
        }
        return names;
    }
}
