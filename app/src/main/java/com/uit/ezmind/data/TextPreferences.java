package com.uit.ezmind.data;

import android.graphics.Color;

public class TextPreferences {
    public int size, color, alignment, effect;

    public TextPreferences() {
        size = 5;
        color= Color.BLACK;
        alignment=0;
        effect=0;
    }

    public TextPreferences(int size, int color, int alignment, int effect) {
        this.size= size;
        this.color = color;
        this.alignment=alignment;
        this.effect = effect;
    }

    public TextPreferences(TextPreferences preferences) {
        this.size= preferences.size;
        this.color = preferences.color;
        this.alignment=preferences.alignment;
        this.effect = preferences.effect;
    }
}
