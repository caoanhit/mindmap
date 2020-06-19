package com.uit.ezmind.widgets;

import android.content.Context;
import android.util.AttributeSet;


import androidx.annotation.Nullable;

import com.uit.ezmind.data.TextPreferences;

public class CustomizableTextView extends androidx.appcompat.widget.AppCompatTextView {
    private TextPreferences preferences;
    public CustomizableTextView(Context context) {
        super(context);
        init();
    }

    public CustomizableTextView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public CustomizableTextView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    public void init(){

    }
    public void applyPreferences(){

    }
    public void setPreferences(TextPreferences preferences){
        this.preferences=preferences;
    }
}
