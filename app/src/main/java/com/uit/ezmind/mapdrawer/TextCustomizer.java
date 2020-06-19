package com.uit.ezmind.mapdrawer;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.coordinatorlayout.widget.CoordinatorLayout;

import com.google.android.material.button.MaterialButtonToggleGroup;
import com.uit.ezmind.R;
import com.uit.ezmind.data.TextPreferences;
import com.uit.ezmind.widgets.ColorPickerButton;
import com.uit.ezmind.widgets.SizePickerButton;

public class TextCustomizer extends CoordinatorLayout {
    private TextPreferences data;
    private SizePickerButton textSize;
    private ColorPickerButton textColor;
    private MaterialButtonToggleGroup textAlignment, textEffect;
    boolean isEnable=true;

    public interface OnPreferenceChangeListener {
        void OnChange(TextPreferences data);
    }

    private OnPreferenceChangeListener onPreferenceChange;

    public void setOnPreferenceChange(OnPreferenceChangeListener onPreferenceChange) {
        this.onPreferenceChange = onPreferenceChange;
    }

    public TextCustomizer(@NonNull Context context) {
        super(context);
        init();
    }

    public TextCustomizer(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public TextCustomizer(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        inflate(getContext(), R.layout.text_customizer, this);
        textSize = findViewById(R.id.text_size);
        textColor = findViewById(R.id.btn_text_color);
        textAlignment = findViewById(R.id.text_alignment);
        textEffect = findViewById(R.id.text_effect);

        textSize.setLimit(1, 20);
        textSize.setOnSizePickedListener(new SizePickerButton.OnSizePickedListener() {
            @Override
            public void onSizePicked(int value) {
                data.size=value;
                if (onPreferenceChange!=null) onPreferenceChange.OnChange(new TextPreferences(value,-10,-1,-1));
            }
        });
        textColor.setOnColorPickedListener(new ColorPickerButton.OnColorPickedListener() {
            @Override
            public void onPick(int color) {
                data.color=color;
                if (onPreferenceChange!=null) onPreferenceChange.OnChange(new TextPreferences(-1,color,-1,-1));
            }
        });
        textAlignment.addOnButtonCheckedListener(new MaterialButtonToggleGroup.OnButtonCheckedListener() {
            @Override
            public void onButtonChecked(MaterialButtonToggleGroup group, int checkedId, boolean isChecked) {
                if (isChecked && isEnable){
                    switch (checkedId){
                        case R.id.text_alignment_left:
                            data.alignment=0;
                            break;
                        case R.id.text_alignment_center:
                            data.alignment=1;
                            break;
                        case R.id.text_alignment_right:
                            data.alignment=2;
                            break;
                    }
                    if (onPreferenceChange!=null) onPreferenceChange.OnChange(new TextPreferences(-1,-10,data.alignment,-1));
                }
            }
        });
        textEffect.addOnButtonCheckedListener(new MaterialButtonToggleGroup.OnButtonCheckedListener() {
            @Override
            public void onButtonChecked(MaterialButtonToggleGroup group, int checkedId, boolean isChecked) {
                if(isEnable){
                    if(isChecked) {
                        switch (checkedId) {
                            case R.id.text_effect_bold:
                                data.effect+=1;
                                break;
                            case R.id.text_effect_italic:
                                data.effect+=2;
                                break;
                            case R.id.text_effect_underline:
                                data.effect+=4;
                                break;
                        }
                        Log.i("text effect", ""+data.effect);
                    }
                    else {
                        switch (checkedId) {
                            case R.id.text_effect_bold:
                                data.effect-=1;
                                break;
                            case R.id.text_effect_italic:
                                data.effect-=2;
                                break;
                            case R.id.text_effect_underline:
                                data.effect-=4;
                                break;
                        }
                    }
                    Log.i("text effect", ""+data.effect);
                    if (onPreferenceChange!=null) onPreferenceChange.OnChange(new TextPreferences(-1,-10,-1,data.effect));
                }
            }
        });

    }

    public void setPreferences(TextPreferences data) {
        isEnable=false;
        this.data = new TextPreferences(data);
        textSize.setValue(data.size);
        textColor.setColor(data.color);
        switch (data.alignment) {
            case 0:
                textAlignment.check(R.id.text_alignment_left);
                break;
            case 1:
                textAlignment.check(R.id.text_alignment_center);
                break;
            case 2:
                textAlignment.check(R.id.text_alignment_right);
                break;
        }
        int effect=data.effect;
        if (effect==0) textEffect.clearChecked();
        if (effect>=4){
            textEffect.check(R.id.text_effect_underline);
            effect-=4;
        }
        if (effect>=2){
            textEffect.check(R.id.text_effect_italic);
            effect-=2;
        }
        if (effect>=1){
            textEffect.check(R.id.text_effect_bold);
        }
        isEnable =true;
    }

    public void applyPreferences(MapView map){
        map.setTextPreferences(data);
    }
}
