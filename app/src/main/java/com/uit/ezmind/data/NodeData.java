package com.uit.ezmind.data;

import java.util.ArrayList;
import java.util.List;

public class NodeData {
    public int id;
    public int parent;
    public List<Integer> children;
    public int[] pos;
    public String text;

    public NodePreferences nodePreferences;
    public TextPreferences textPreferences;
    public LinePreferences linePreferences;

    public NodeData(){
        children=new ArrayList<>();
        pos=new int[2];
        nodePreferences=new NodePreferences();
        textPreferences=new TextPreferences();
        linePreferences=new LinePreferences();
    }

    public NodeData(NodeData data){
        id=data.id;
        parent=data.parent;
        children=new ArrayList<>(data.children);
        text=data.text;
        pos=new int[2];
        pos[0]=data.pos[0];
        pos[1]=data.pos[1];
        nodePreferences=new NodePreferences(data.nodePreferences);
        textPreferences=new TextPreferences(data.textPreferences);
        linePreferences=new LinePreferences(data.linePreferences);

    }
}
