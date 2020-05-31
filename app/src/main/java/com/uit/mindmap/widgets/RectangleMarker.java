package com.uit.mindmap.widgets;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.RelativeLayout;

public class RectangleMarker extends RelativeLayout {
    public

    float[] startPoint,endPoint;
    Paint paint;

    public RectangleMarker(Context context) {
        super(context);
        init(null);
    }

    public RectangleMarker(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs);
    }

    public RectangleMarker(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(attrs);
    }

    public RectangleMarker(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(attrs);
    }

    private void init(AttributeSet attrs) {
        startPoint=endPoint=new float[2];
        setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction() & MotionEvent.ACTION_MASK){
                    case MotionEvent.ACTION_DOWN:
                        startPoint[0]=event.getX();

                        break;
                    case MotionEvent.ACTION_MOVE:

                        break;
                    case MotionEvent.ACTION_POINTER_DOWN:

                        break;
                    case MotionEvent.ACTION_POINTER_UP:
                        break;
                    case MotionEvent.ACTION_UP:
                }
                return false;
            }
        });
    }
    private void drawRectange(int[] startPoint, int[] endPoint, Canvas canvas){

    }
}
