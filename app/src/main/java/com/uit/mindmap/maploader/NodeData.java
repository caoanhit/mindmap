package com.uit.mindmap.maploader;

import android.graphics.Color;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

public class NodeData {
    public int id;
    public int parent;
    public List<Integer> children;
    public int[] pos;

    public String text_size;
    public String text_color;
    public String background_color;
    public String outline_color;
    public String connection_color;
}
