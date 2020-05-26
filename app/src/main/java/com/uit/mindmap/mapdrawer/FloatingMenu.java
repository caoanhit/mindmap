package com.uit.mindmap.mapdrawer;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageButton;

import androidx.annotation.Nullable;

import com.uit.mindmap.R;

import java.util.List;

public class FloatingMenu extends FrameLayout {
    List<Integer> targets;
    private ImageButton newNode, edit, customize,delete;
    private MapView map;

    public FloatingMenu(Context context) {
        super(context);
        init(null);
    }

    public FloatingMenu(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(attrs);
    }

    public FloatingMenu(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(attrs);
    }

    public FloatingMenu(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(attrs);
    }

    private void init(@Nullable AttributeSet attrs) {
        inflate(getContext(), R.layout.floating_menu, this);
        newNode=findViewById(R.id.new_node);
        edit=findViewById(R.id.edit);
        customize=findViewById(R.id.customize);
        delete=findViewById(R.id.delete);
        newNode.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                map.addNode();
            }
        });
        edit.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                map.editText();

            }
        });
        delete.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                map.removeNode();
            }
        });
    }
    public void assignMap(MapView map){
        this.map=map;
    }
}
