package com.uit.ezmind.data;

import android.graphics.Color;

public class LinePreferences {
    public int color, effect, curve, arrow, width;

    public LinePreferences() {
        color = Color.DKGRAY;
        effect = 0;
        curve = 1;
        arrow = 0;
        width = 4;
    }

    public LinePreferences(int color, int effect, int curve, int arrow, int width) {
        this.color = color;
        this.effect = effect;
        this.curve = curve;
        this.arrow = arrow;
        this.width = width;
    }

    public LinePreferences(LinePreferences preferences) {
        this.color = preferences.color;
        this.effect = preferences.effect;
        this.curve = preferences.curve;
        this.arrow = preferences.arrow;
        this.width = preferences.width;
    }
}
