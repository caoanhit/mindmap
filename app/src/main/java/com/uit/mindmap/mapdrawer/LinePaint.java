package com.uit.mindmap.mapdrawer;

import android.graphics.Canvas;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.Path;

public class LinePaint extends Paint {
    public void drawConnection(Node parent, Node child, Canvas canvas) {
        applyLineEffect(child);
        int curve = child.data.linePreferences.curve;
        setStyle(Paint.Style.STROKE);
        setColor(child.data.linePreferences.color);
        setStrokeWidth(child.data.linePreferences.width);

        int[] p = parent.anchor(child);
        int[] c = child.anchor(parent);

        switch (curve) {
            case 0:
                drawStraightLine(p, c, canvas);
                break;
            case 1:
                drawCurve(p, c, canvas);
                break;
            case 2:
                drawElbow(p, c, canvas);
                break;
        }

        setPathEffect(null);

        switch (child.data.linePreferences.arrow){
            case 1:
                drawArrow(p, canvas);
                break;
            case 2:
                drawArrow(c, canvas);
                break;
            case 3:
                drawArrow(p, canvas);
                drawArrow(c, canvas);
                break;
        }
    }

    private void applyLineEffect(Node node) {
        float[] intervals;
        int style = node.data.linePreferences.effect;
        switch (style) {
            case 0:
                setPathEffect(null);
                break;
            case 1:
                intervals = new float[]{15, 15};
                setPathEffect(new DashPathEffect(intervals, 0));
                break;
            case 2:
                intervals = new float[]{15, 15, 5, 15};
                setPathEffect(new DashPathEffect(intervals, 0));
                break;
        }
    }

    private void drawCurve(int[] p, int[] c, Canvas canvas) {
        Path path = new Path();

        int c2 = Math.abs(c[2]);
        int c3 = Math.abs(c[3]);
        int p2 = Math.abs(p[2]);
        int p3 = Math.abs(p[3]);
        path.moveTo(p[0], p[1]);
        path.cubicTo(p[0] * c3 + (c[0] / 2 + p[0] / 2) * c2
                , p[1] * p2 + p3 * (c[1] / 2 + p[1] / 2)
                , c[0] * p3 + (p[0] / 2 + c[0] / 2) * p2
                , c[1] * c2 + c3 * (p[1] / 2 + c[1] / 2), c[0], c[1]);
        canvas.drawPath(path, this);
    }

    private void drawStraightLine(int[] p, int[] c, Canvas canvas) {
        canvas.drawLine(p[0], p[1], c[0], c[1], this);
    }

    private void drawElbow(int[] p, int[] c, Canvas canvas) {
        Path path = new Path();

        path.moveTo(p[0], p[1]);
        if(p[2]!=0) {
            path.lineTo((p[0] + c[0]) / 2, p[1]);
            path.lineTo((p[0] + c[0]) / 2, c[1]);
        }
        else{
            path.lineTo(p[0], (p[1] + c[1]) / 2);
            path.lineTo(c[0], (p[1] + c[1]) / 2);
        }
        path.lineTo(c[0],c[1]);
        canvas.drawPath(path, this);
    }

    private void drawArrow(int[] a, Canvas canvas) {
        setStyle(Style.FILL_AND_STROKE);
        int size = 15;
        Path path =new Path();
        path.moveTo(a[0], a[1]);
        if (a[3] == 0) {
            path.lineTo(a[0] + a[2] * size, a[1] - size / 2);
            path.lineTo(a[0] + a[2] * size, a[1] + size / 2);
            path.close();
        } else {
            path.lineTo(a[0] - size / 2, a[1] + a[3] * size);
            path.lineTo(a[0] + size / 2, a[1] + a[3] * size);
            path.close();
        }
        canvas.drawPath(path, this);
    }
}
