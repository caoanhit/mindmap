package com.uit.ezmind.data;

import android.graphics.Color;

public class NodePreferences {
    public int color, outlineColor, outlineWidth;

    public NodePreferences() {
        color = Color.WHITE;
        outlineColor = Color.GRAY;
        outlineWidth = 4;
    }

    public NodePreferences( int fillColor, int outlineColor, int outlineThickness) {
        this.color = fillColor;
        this.outlineColor = outlineColor;
        this.outlineWidth = outlineThickness;
    }

    public NodePreferences(NodePreferences preferences) {
        this.color = preferences.color;
        this.outlineColor = preferences.outlineColor;
        this.outlineWidth = preferences.outlineWidth;
    }
}
