package com.uit.mindmap.widgets;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.TextView;


import androidx.annotation.Nullable;

import com.uit.mindmap.data.TextPreferences;

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
