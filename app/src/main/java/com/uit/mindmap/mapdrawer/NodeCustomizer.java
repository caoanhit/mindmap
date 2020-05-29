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

import java.util.ArrayList;
import java.util.Arrays;

import petrov.kristiyan.colorpicker.ColorPicker;

public class NodeCustomizer extends CoordinatorLayout {
    private NodeData data;
    private MapView mapView;
    private MaterialButtonToggleGroup textSize,connectionStyle;
    private AppCompatButton textColor, backgroundColor,outlineColor, connectionColor;
    int[] colorList;
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

        String colors[] = getResources().getStringArray(R.array.colors);
        colorList=new int[colors.length];
        for (int i = 0; i < colors.length; i++) {
            colorList[i]=Color.parseColor(colors[i]);
        }

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
                        .setColumns(8)
                        .setColors(colorList)
                        .setColorButtonSize(30, 30)
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
                        .setColumns(8)
                        .setColors(colorList)
                        .setColorButtonSize(30, 30)
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
                        .setColumns(8)
                        .setColors(colorList)
                        .setColorButtonSize(30, 30)
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
                        .setColumns(8)
                        .setColors(colorList)
                        .setColorButtonSize(30, 30)
                        .show();
                colorPicker.getNegativeButton().setText(R.string.cancel);
            }
        });
        textSize.addOnButtonCheckedListener(new MaterialButtonToggleGroup.OnButtonCheckedListener() {
            @Override
            public void onButtonChecked(MaterialButtonToggleGroup group, int checkedId, boolean isChecked) {
                if (isChecked) {
                    switch (checkedId){
                        case R.id.text_small: mapView.applyTextSize(0); break;
                        case R.id.text_medium: mapView.applyTextSize(1); break;
                        case R.id.text_large: mapView.applyTextSize(2); break;
                    }
                }
            }
        });
        connectionStyle.addOnButtonCheckedListener(new MaterialButtonToggleGroup.OnButtonCheckedListener() {
            @Override
            public void onButtonChecked(MaterialButtonToggleGroup group, int checkedId, boolean isChecked) {
                if (isChecked) {
                    switch (checkedId){
                        case R.id.connection_style_straight: mapView.applyConnectionStyle(0); break;
                        case R.id.connection_style_dashed: mapView.applyConnectionStyle(1); break;
                        case R.id.connection_style_dotted_dash: mapView.applyConnectionStyle(2); break;
                    }
                }
            }
        });
    }

    public void setData(NodeData data) {
        this.data = data;
        switch (data.text_size){
            case 0: textSize.check(R.id.text_small); break;
            case 1: textSize.check(R.id.text_medium); break;
            case 2: textSize.check(R.id.text_large); break;
        }
        switch (data.connection_style){
            case 0: connectionStyle.check(R.id.connection_style_straight); break;
            case 1: connectionStyle.check(R.id.connection_style_dashed); break;
            case 2: connectionStyle.check(R.id.connection_style_dotted_dash); break;
        }
        textColor.setBackgroundTintList(ColorStateList.valueOf(data.text_color));
        backgroundColor.setBackgroundTintList(ColorStateList.valueOf(data.background_color));
        outlineColor.setBackgroundTintList(ColorStateList.valueOf(data.outline_color));
        connectionColor.setBackgroundTintList(ColorStateList.valueOf(data.connection_color));
    }

    public void setMapView(MapView mapView) {
        this.mapView = mapView;
    }
}
