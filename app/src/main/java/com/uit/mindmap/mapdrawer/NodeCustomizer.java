package com.uit.mindmap.mapdrawer;

import android.content.Context;
import android.util.AttributeSet;

import androidx.annotation.Nullable;
import androidx.coordinatorlayout.widget.CoordinatorLayout;

import com.uit.mindmap.R;
import com.uit.mindmap.data.NodeData;
import com.uit.mindmap.data.NodePreferences;
import com.uit.mindmap.widgets.ColorPickerButton;
import com.uit.mindmap.widgets.SizePickerButton;

public class NodeCustomizer extends CoordinatorLayout {
    NodePreferences data;
    private MapView mapView;
    private SizePickerButton outlineWidth;
    private ColorPickerButton btnFillColor, btnOutlineColor;

    public interface OnPreferenceChangeListener{
        void OnChange(NodePreferences data);
    }
    private OnPreferenceChangeListener onPreferenceChange;
    public void setOnPreferenceChange(OnPreferenceChangeListener onPreferenceChange) {
        this.onPreferenceChange = onPreferenceChange;
    }

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
        inflate(getContext(), R.layout.node_customizer, this);
        data=new NodePreferences();
        findViewsByIds();

        btnFillColor.setOnColorPickedListener(new ColorPickerButton.OnColorPickedListener() {
            @Override
            public void onPick(int color) {
                data.color=color;
                if (onPreferenceChange!=null) onPreferenceChange.OnChange(data);
            }
        });
        btnOutlineColor.setOnColorPickedListener(new ColorPickerButton.OnColorPickedListener() {
            @Override
            public void onPick(int color) {
                data.outlineColor=color;
                if (onPreferenceChange!=null) onPreferenceChange.OnChange(data);
            }
        });
        outlineWidth.setOnSizePickedListener(new SizePickerButton.OnSizePickedListener() {
            @Override
            public void onSizePicked(int value) {
                data.outlineWidth=value;
                if (onPreferenceChange!=null) onPreferenceChange.OnChange(data);
            }
        });

    }

    private void findViewsByIds(){
        outlineWidth = findViewById(R.id.outline_width);
        outlineWidth.setLimit(0,10);
        btnFillColor = findViewById(R.id.btn_fill_color);
        btnOutlineColor = findViewById(R.id.btn_outline_color);
    }
    public void setPreferences(NodePreferences data) {
        this.data=new NodePreferences(data);
        outlineWidth.setValue(data.outlineWidth);
        btnFillColor.setColor(data.color);
        btnOutlineColor.setColor(data.outlineColor);
    }

    public void applyPreferences(MapView map){
        map.setNodePreferences(data);
    }

    public void setMapView(MapView mapView) {
        this.mapView = mapView;
    }
}
