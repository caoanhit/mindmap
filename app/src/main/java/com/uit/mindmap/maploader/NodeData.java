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

    public int text_size;
    public int text_color;
    public int background_color;
    public int outline_color;
    public int connection_style;
    public int connection_color;
}
