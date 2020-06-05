package com.uit.mindmap.mapdrawer;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatButton;
import androidx.coordinatorlayout.widget.CoordinatorLayout;

import com.google.android.material.button.MaterialButtonToggleGroup;
import com.uit.mindmap.R;
import com.uit.mindmap.maploader.NodeData;
import com.uit.mindmap.widgets.ColorPickerButton;

import petrov.kristiyan.colorpicker.ColorPicker;

public class NodeCustomizer extends CoordinatorLayout {
    private NodeData data;
    private MapView mapView;
    private MaterialButtonToggleGroup textSize, connectionStyle, arrow;
    private ColorPickerButton btnTextColor, btnFillColor, btnOutlineColor, btnLineColor;
    private int[] colorList;
    boolean evenDisabled = false;

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
        inflate(getContext(), R.layout.node_customization_sheet, this);
        findViewsByIds();
        btnTextColor.setOnColorPickedListener(new ColorPickerButton.OnColorPickedListener() {
            @Override
            public void onPick(int color) {
                btnTextColor.setBackgroundTintList(ColorStateList.valueOf(color));
                mapView.setTextColor(color);
            }
        });
        btnTextColor.setOnColorPickedListener(new ColorPickerButton.OnColorPickedListener() {
            @Override
            public void onPick(int color) {
                btnTextColor.setBackgroundTintList(ColorStateList.valueOf(color));
                mapView.setTextColor(color);
            }
        });
        btnTextColor.setOnColorPickedListener(new ColorPickerButton.OnColorPickedListener() {
            @Override
            public void onPick(int color) {
                btnTextColor.setBackgroundTintList(ColorStateList.valueOf(color));
                mapView.setTextColor(color);
            }
        });
        btnTextColor.setOnColorPickedListener(new ColorPickerButton.OnColorPickedListener() {
            @Override
            public void onPick(int color) {
                btnTextColor.setBackgroundTintList(ColorStateList.valueOf(color));
                mapView.setTextColor(color);
            }
        });
        btnFillColor.setOnColorPickedListener(new ColorPickerButton.OnColorPickedListener() {
            @Override
            public void onPick(int color) {
                btnFillColor.setBackgroundTintList(ColorStateList.valueOf(color));
                mapView.setFillColor(color);
            }
        });
        btnOutlineColor.setOnColorPickedListener(new ColorPickerButton.OnColorPickedListener() {
            @Override
            public void onPick(int color) {
                btnOutlineColor.setBackgroundTintList(ColorStateList.valueOf(color));
                mapView.setOutlineColor(color);
            }
        });
        btnLineColor.setOnColorPickedListener(new ColorPickerButton.OnColorPickedListener() {
            @Override
            public void onPick(int color) {
                btnLineColor.setBackgroundTintList(ColorStateList.valueOf(color));
                mapView.setLineColor(color);
            }
        });
        textSize.addOnButtonCheckedListener(new MaterialButtonToggleGroup.OnButtonCheckedListener() {
            @Override
            public void onButtonChecked(MaterialButtonToggleGroup group, int checkedId, boolean isChecked) {
                if (isChecked && !evenDisabled) {
                    switch (checkedId) {
                        case R.id.text_small:
                            mapView.setTextSize(0);
                            break;
                        case R.id.text_medium:
                            mapView.setTextSize(1);
                            break;
                        case R.id.text_large:
                            mapView.setTextSize(2);
                            break;
                    }
                }
            }
        });
        connectionStyle.addOnButtonCheckedListener(new MaterialButtonToggleGroup.OnButtonCheckedListener() {
            @Override
            public void onButtonChecked(MaterialButtonToggleGroup group, int checkedId, boolean isChecked) {
                if (isChecked && !evenDisabled) {
                    switch (checkedId) {
                        case R.id.line_style_straight:
                            mapView.setLineStyle(0);
                            break;
                        case R.id.line_style_dashed:
                            mapView.setLineStyle(1);
                            break;
                        case R.id.connection_style_dotted_dash:
                            mapView.setLineStyle(2);
                            break;
                    }
                }
            }
        });
        arrow.addOnButtonCheckedListener(new MaterialButtonToggleGroup.OnButtonCheckedListener() {
            @Override
            public void onButtonChecked(MaterialButtonToggleGroup group, int checkedId, boolean isChecked) {
                if(!evenDisabled) {
                    switch (checkedId) {
                        case R.id.arrow_back:
                            if (isChecked) mapView.setArrow(data.arrow + 1);
                            else mapView.setArrow(data.arrow - 1);
                            break;
                        case R.id.arrow_forward:
                            if (isChecked) mapView.setArrow(data.arrow + 2);
                            else mapView.setArrow(data.arrow - 2);
                            break;
                    }
                }
            }
        });
    }

    private void findViewsByIds(){
        textSize = findViewById(R.id.text_size);
        btnTextColor = findViewById(R.id.btn_text_color);
        btnFillColor = findViewById(R.id.btn_fill_color);
        btnOutlineColor = findViewById(R.id.btn_outline_color);
        btnLineColor = findViewById(R.id.btn_line_color);
        connectionStyle = findViewById((R.id.line_style));
        arrow= findViewById(R.id.arrow);
    }
    public void setData(NodeData data) {
        evenDisabled=true;
        this.data = data;
        switch (data.textSize) {
            case 0:
                textSize.check(R.id.text_small);
                break;
            case 1:
                textSize.check(R.id.text_medium);
                break;
            case 2:
                textSize.check(R.id.text_large);
                break;
        }
        switch (data.lineStyle) {
            case 0:
                connectionStyle.check(R.id.line_style_straight);
                break;
            case 1:
                connectionStyle.check(R.id.line_style_dashed);
                break;
            case 2:
                connectionStyle.check(R.id.connection_style_dotted_dash);
                break;
        }
        btnTextColor.setColor(data.textColor);
        btnFillColor.setColor(data.fillColor);
        btnOutlineColor.setColor(data.outlineColor);
        btnLineColor.setColor(data.lineColor);
        switch (data.arrow){
            case 0:
                arrow.uncheck(R.id.arrow_back);
                arrow.uncheck(R.id.arrow_forward);
                break;
            case 1:
                arrow.check(R.id.arrow_back);
                arrow.uncheck(R.id.arrow_forward);
                break;
            case 2:
                arrow.uncheck(R.id.arrow_back);
                arrow.check(R.id.arrow_forward);
                break;
            case 3:
                arrow.check(R.id.arrow_back);
                arrow.check(R.id.arrow_forward);
                break;

        }
        evenDisabled=false;
    }

    public void setMapView(MapView mapView) {
        this.mapView = mapView;
    }
}
