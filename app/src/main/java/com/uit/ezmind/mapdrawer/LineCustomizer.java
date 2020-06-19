package com.uit.ezmind.mapdrawer;

import android.content.Context;
import android.util.AttributeSet;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.coordinatorlayout.widget.CoordinatorLayout;

import com.google.android.material.button.MaterialButtonToggleGroup;
import com.uit.ezmind.R;
import com.uit.ezmind.data.LinePreferences;
import com.uit.ezmind.widgets.ColorPickerButton;
import com.uit.ezmind.widgets.SizePickerButton;

public class LineCustomizer extends CoordinatorLayout {
    private LinePreferences data;
    private SizePickerButton lineWidth;
    private MaterialButtonToggleGroup lineEffect, lineCurve, arrow;
    private ColorPickerButton lineColor;
    boolean isEnable = true;

    public interface OnPreferenceChangeListener {
        void OnChange(LinePreferences data);
    }

    private OnPreferenceChangeListener onPreferenceChange;

    public void setOnPreferenceChange(OnPreferenceChangeListener onPreferenceChange) {
        this.onPreferenceChange = onPreferenceChange;
    }

    public LineCustomizer(@NonNull Context context) {
        super(context);
        init();
    }

    public LineCustomizer(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public LineCustomizer(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        inflate(getContext(), R.layout.line_customizer, this);
        lineColor= findViewById(R.id.btn_line_color);
        lineWidth = findViewById(R.id.line_width);
        lineEffect = findViewById(R.id.line_effect);
        lineCurve = findViewById(R.id.curve);
        arrow = findViewById(R.id.arrow);

        lineWidth.setLimit(1,10);
        lineWidth.setOnSizePickedListener(new SizePickerButton.OnSizePickedListener() {
            @Override
            public void onSizePicked(int value) {
                data.width=value;
                if (onPreferenceChange != null)
                    onPreferenceChange.OnChange(new LinePreferences(-10,-1,-1,-1,value));
            }
        });

        lineColor.setOnColorPickedListener(new ColorPickerButton.OnColorPickedListener() {
            @Override
            public void onPick(int color) {
                data.color=color;
                if (onPreferenceChange != null)
                    onPreferenceChange.OnChange(new LinePreferences(color,-1,-1,-1,-1));
            }
        });

        lineEffect.addOnButtonCheckedListener(new MaterialButtonToggleGroup.OnButtonCheckedListener() {
            @Override
            public void onButtonChecked(MaterialButtonToggleGroup group, int checkedId, boolean isChecked) {
                if (isChecked && isEnable) {
                    switch (checkedId) {
                        case R.id.line_normal:
                            data.effect = 0;
                            break;
                        case R.id.line_dashed:
                            data.effect = 1;
                            break;
                        case R.id.line_dotted_dashed:
                            data.effect = 2;
                            break;
                    }
                    if (onPreferenceChange != null) onPreferenceChange.OnChange(new LinePreferences(-10,data.effect,-1,-1,-1));
                }
            }
        });
        lineCurve.addOnButtonCheckedListener(new MaterialButtonToggleGroup.OnButtonCheckedListener() {
            @Override
            public void onButtonChecked(MaterialButtonToggleGroup group, int checkedId, boolean isChecked) {
                if (isChecked && isEnable) {
                    switch (checkedId) {
                        case R.id.straight:
                            data.curve = 0;
                            break;
                        case R.id.bezier:
                            data.curve = 1;
                            break;
                        case R.id.elbow:
                            data.curve = 2;
                            break;
                    }
                    if (onPreferenceChange != null) onPreferenceChange.OnChange(new LinePreferences(-10,-1,data.curve,-1,-1));
                }
            }
        });
        arrow.addOnButtonCheckedListener(new MaterialButtonToggleGroup.OnButtonCheckedListener() {
            @Override
            public void onButtonChecked(MaterialButtonToggleGroup group, int checkedId, boolean isChecked) {
                if (isEnable) {
                    if (isChecked) {
                        switch (checkedId) {
                            case R.id.arrow_back:
                                data.arrow+=1;
                                break;
                            case R.id.arrow_forward:
                                data.arrow += 2;
                                break;
                        }
                    }
                    else {
                        switch (checkedId) {
                            case R.id.arrow_back:
                                data.arrow-=1;
                                break;
                            case R.id.arrow_forward:
                                data.arrow -= 2;
                                break;
                        }
                    }
                    if (onPreferenceChange != null) onPreferenceChange.OnChange(new LinePreferences(-10,-1,-1,data.arrow,-1));
                }
            }
        });
    }

    public void setPreferences(LinePreferences data) {
        isEnable = false;
        this.data = new LinePreferences(data);
        lineWidth.setValue(data.width);
        lineColor.setColor(data.color);
        switch (data.effect) {
            case 0:
                lineEffect.check(R.id.line_normal);
                break;
            case 1:
                lineEffect.check(R.id.line_dashed);
                break;
            case 2:
                lineEffect.check(R.id.line_dotted_dashed);
                break;
        }
        switch (data.curve) {
            case 0:
                lineCurve.check(R.id.straight);
                break;
            case 1:
                lineCurve.check(R.id.bezier);
                break;
            case 2:
                lineCurve.check(R.id.elbow);
                break;
        }
        switch (data.arrow) {
            case 0:
                arrow.clearChecked();
                break;
            case 1:
                arrow.check(R.id.arrow_back);
                break;
            case 2:
                arrow.check(R.id.arrow_forward);
                break;
            case 3:
                arrow.check(R.id.arrow_back);
                arrow.check(R.id.arrow_forward);
                break;
        }
        isEnable = true;
    }

    public void applyPreferences(MapView map) {
        map.setLinePreferences(data);
    }
}
