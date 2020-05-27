package com.uit.mindmap.mapdrawer;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatButton;
import androidx.coordinatorlayout.widget.CoordinatorLayout;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.button.MaterialButtonToggleGroup;
import com.uit.mindmap.R;
import com.uit.mindmap.maploader.NodeData;

import petrov.kristiyan.colorpicker.ColorPicker;

public class NodeCustomizer extends CoordinatorLayout {
    private NodeData data;
    private MapView mapView;
    private MaterialButtonToggleGroup textSize,connectionStyle;
    private AppCompatButton textColor, backgroundColor,outlineColor, connectionColor;
    public NodeCustomizer(Context context) {
        super(context);
        init(null);
    }

    public NodeCustomizer(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(attrs);
    }

    public NodeCustomizer(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(attrs);
    }

    private void init(@Nullable AttributeSet attrs) {
        inflate(getContext(), R.layout.node_customization_sheet,this);
        textSize=(MaterialButtonToggleGroup)findViewById(R.id.text_size);
        textColor=(AppCompatButton)findViewById(R.id.text_color);
        backgroundColor=(AppCompatButton)findViewById(R.id.background_color);
        outlineColor=(AppCompatButton)findViewById(R.id.outline_color);
        connectionColor=(AppCompatButton)findViewById(R.id.connection_color);
        connectionStyle=(MaterialButtonToggleGroup)findViewById((R.id.connection_style));

        textColor.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                final ColorPicker colorPicker = new ColorPicker((MapDrawerActivity)getContext());
                colorPicker.setOnFastChooseColorListener(new ColorPicker.OnFastChooseColorListener() {
                    @Override
                    public void setOnFastChooseColorListener(int position, int color) {
                        textColor.setBackgroundTintList(ColorStateList.valueOf(color));
                        mapView.applyTextColor(color);
                    }
                    @Override
                    public void onCancel(){
                        // put code
                    }
                })
                        .disableDefaultButtons(true)
                        .setColumns(5)
                        .show();
                colorPicker.getNegativeButton().setText(R.string.cancel);
            }
        });
        backgroundColor.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                final ColorPicker colorPicker = new ColorPicker((MapDrawerActivity)getContext());
                colorPicker.setOnFastChooseColorListener(new ColorPicker.OnFastChooseColorListener() {
                    @Override
                    public void setOnFastChooseColorListener(int position, int color) {
                        backgroundColor.setBackgroundTintList(ColorStateList.valueOf(color));
                        mapView.applyBackgroundColor(color);
                    }
                    @Override
                    public void onCancel(){
                        // put code
                    }
                })
                        .disableDefaultButtons(true)
                        .setColumns(5)
                        .show();
                colorPicker.getNegativeButton().setText(R.string.cancel);
            }
        });
        outlineColor.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                final ColorPicker colorPicker = new ColorPicker((MapDrawerActivity)getContext());
                colorPicker.setOnFastChooseColorListener(new ColorPicker.OnFastChooseColorListener() {
                    @Override
                    public void setOnFastChooseColorListener(int position, int color) {
                        outlineColor.setBackgroundTintList(ColorStateList.valueOf(color));
                        mapView.applyOutlineColor(color);
                    }
                    @Override
                    public void onCancel(){
                        // put code
                    }
                })
                        .disableDefaultButtons(true)
                        .setColumns(5)
                        .show();
                colorPicker.getNegativeButton().setText(R.string.cancel);
            }
        });
        connectionColor.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                final ColorPicker colorPicker = new ColorPicker((MapDrawerActivity)getContext());
                colorPicker.setOnFastChooseColorListener(new ColorPicker.OnFastChooseColorListener() {
                    @Override
                    public void setOnFastChooseColorListener(int position, int color) {
                        connectionColor.setBackgroundTintList(ColorStateList.valueOf(color));
                        mapView.applyConnectionColor(color);
                    }
                    @Override
                    public void onCancel(){
                        // put code
                    }
                })
                        .disableDefaultButtons(true)
                        .setColumns(5)
                        .show();
                colorPicker.getNegativeButton().setText(R.string.cancel);
            }
        });
        textSize.addOnButtonCheckedListener(new MaterialButtonToggleGroup.OnButtonCheckedListener() {
            @Override
            public void onButtonChecked(MaterialButtonToggleGroup group, int checkedId, boolean isChecked) {
                if (isChecked) {
                    mapView.applyTextSize(checkedId-1);
                }
            }
        });
        connectionStyle.addOnButtonCheckedListener(new MaterialButtonToggleGroup.OnButtonCheckedListener() {
            @Override
            public void onButtonChecked(MaterialButtonToggleGroup group, int checkedId, boolean isChecked) {
                if (isChecked) {
                    mapView.applyConnectionStyle(checkedId-4);
                }
            }
        });
    }

    public void setData(NodeData data) {
        this.data = data;
        textSize.check(data.text_size+1);
        connectionStyle.check(data.connection_style+4);
        textColor.setBackgroundTintList(ColorStateList.valueOf(data.text_color));
        backgroundColor.setBackgroundTintList(ColorStateList.valueOf(data.background_color));
        outlineColor.setBackgroundTintList(ColorStateList.valueOf(data.outline_color));
        connectionColor.setBackgroundTintList(ColorStateList.valueOf(data.connection_color));

    }

    public void setMapView(MapView mapView) {
        this.mapView = mapView;
    }
}
