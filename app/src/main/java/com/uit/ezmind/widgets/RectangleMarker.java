package com.uit.ezmind.widgets;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.RelativeLayout;

import com.uit.ezmind.R;

public class RectangleMarker extends RelativeLayout {
    public

    int[] startPoint, endPoint;
    Paint paint;
    boolean isDrawing;

    public interface OnTapListener {
        void OnTap(int[] pos);
    }

    private OnTapListener onTapListener;

    public void setOnTapListener(OnTapListener onTapListener) {
        this.onTapListener = onTapListener;
    }

    public interface OnSelectListener {
        void onSelect(int[] start, int[] end);
    }

    private OnSelectListener onSelectListener;

    public void setOnSelectListener(OnSelectListener onSelectListener) {
        this.onSelectListener = onSelectListener;
    }

    public RectangleMarker(Context context) {
        super(context);
        init();
    }

    public RectangleMarker(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public RectangleMarker(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        paint = new Paint();
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(getContext().getResources().getDimension(R.dimen.outline_width));
        paint.setColor(getContext().getResources().getColor(R.color.colorAccent));
        float[] intervals = new float[]{15, 15};
        paint.setPathEffect(new DashPathEffect(intervals, 0));
        startPoint = new int[2];
        endPoint = new int[2];
        setOnTouchListener(new OnTouchListener() {
            @SuppressLint("ClickableViewAccessibility")
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction() & MotionEvent.ACTION_MASK) {
                    case MotionEvent.ACTION_DOWN:
                        isDrawing = true;
                        startPoint[0] = (int) event.getX();
                        startPoint[1] = (int) event.getY();
                        Log.i("start", "" + startPoint[0] + "  " + startPoint[1]);
                        break;
                    case MotionEvent.ACTION_MOVE:
                        endPoint[0] = (int) event.getX();
                        endPoint[1] = (int) event.getY();
                        Log.i("end", "" + endPoint[0] + "  " + endPoint[1]);
                        invalidate();
                        break;
                    case MotionEvent.ACTION_POINTER_DOWN:
                        break;
                    case MotionEvent.ACTION_POINTER_UP:
                        break;
                    case MotionEvent.ACTION_UP:
                        endPoint[0] = (int) event.getX();
                        endPoint[1] = (int) event.getY();
                        isDrawing = false;
                        long touchtime = event.getEventTime() - event.getDownTime();
                        if (touchtime < 200 && onTapListener != null)
                            onTapListener.OnTap(endPoint);
                        if (onSelectListener != null)
                            onSelectListener.onSelect(startPoint, endPoint);
                        invalidate();
                        break;
                }
                return false;
            }
        });
    }

    @Override
    protected void dispatchDraw(Canvas canvas) {
        super.dispatchDraw(canvas);
        if (isDrawing) {
            drawRectangle(startPoint, endPoint, canvas);

        }
    }

    private void drawRectangle(int[] startPoint, int[] endPoint, Canvas canvas) {
        int left = Math.min(startPoint[0], endPoint[0]);
        int top = Math.min(startPoint[1], endPoint[1]);
        int right = Math.max(startPoint[0], endPoint[0]);
        int bottom = Math.max(startPoint[1], endPoint[1]);
        Rect rect = new Rect(left, top, right, bottom);

        canvas.drawRect(rect, paint);
    }
}
