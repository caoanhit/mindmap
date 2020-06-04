package com.uit.mindmap.maploader;

import android.graphics.Color;
import android.view.ViewGroup;
import android.widget.TextView;

import com.uit.mindmap.R;

import java.util.ArrayList;
import java.util.List;

import petrov.kristiyan.colorpicker.ColorPicker;

public class NodeData {
    public int id;
    public int parent;
    public List<Integer> children;
    public int[] pos;

    public String text="Root node";
    public int textSize=1;
    public int textColor=Color.DKGRAY;
    public int fillColor=Color.WHITE;
    public int outlineColor= Color.GRAY;
    public int lineStyle=0;
    public int lineColor= Color.BLACK;
    public int arrow=0;

    public NodeData(){
        children=new ArrayList<>();
    }

    public NodeData(NodeData data){
        id=data.id;
        parent=data.parent;
        children=new ArrayList<>();
        for(Integer i:data.children) this.children.add(i);
        pos=new int[2];
        pos[0]=data.pos[0];
        pos[1]=data.pos[1];
        text=data.text;
        textSize=data.textSize;
        textColor=data.textColor;
        fillColor=data.fillColor;
        outlineColor=data.outlineColor;
        lineStyle=data.lineStyle;
        lineColor=data.lineColor;
        arrow=data.arrow;
    }
}
