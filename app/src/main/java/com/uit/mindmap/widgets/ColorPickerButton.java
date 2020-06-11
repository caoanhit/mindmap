package com.uit.mindmap.widgets;

import android.app.Activity;
import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import androidx.appcompat.widget.AppCompatButton;

import com.uit.mindmap.R;

import petrov.kristiyan.colorpicker.ColorPicker;

public class ColorPickerButton extends RelativeLayout {
    AppCompatButton button;
    private static int[] colorList;

    public interface OnColorPickedListener{
        void onPick(int color);
    }
    private OnColorPickedListener onColorPickedListener;

    public void setOnColorPickedListener(ColorPickerButton.OnColorPickedListener onColorPickedListener) {
        this.onColorPickedListener = onColorPickedListener;
    }

    public ColorPickerButton(Context context) {
        super(context);
        init();
    }

    public ColorPickerButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public ColorPickerButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }
    public void init(){
        inflate(getContext(),R.layout.color_picker_button,this);
        if (colorList==null) {
            String colors[] = getResources().getStringArray(R.array.colors);
            colorList = new int[colors.length];
            for (int i = 0; i < colors.length; i++) {
                colorList[i] = Color.parseColor(colors[i]);
            }
        }
        button=findViewById(R.id.btn_color);
        button.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                final ColorPicker colorPicker = new ColorPicker((Activity) getContext());
                colorPicker.setOnFastChooseColorListener(new ColorPicker.OnFastChooseColorListener() {
                    @Override
                    public void setOnFastChooseColorListener(int position, int color) {
                        setColor(color);
                        if(onColorPickedListener!=null){
                            onColorPickedListener.onPick(color);
                        }
                    }

                    @Override
                    public void onCancel() {
                        // put code
                    }
                })
                        .disableDefaultButtons(true)
                        .setColumns(7)
                        .setColors(colorList)
                        .setColorButtonSize(30, 30)
                        .show();
            }
        });
    }
    public void setColor(int color){
        button.setBackgroundTintList(ColorStateList.valueOf(color));
    }

}
