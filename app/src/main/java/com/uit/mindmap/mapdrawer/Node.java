package com.uit.mindmap.mapdrawer;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.uit.mindmap.R;
import com.uit.mindmap.data.LinePreferences;
import com.uit.mindmap.data.NodeData;
import com.uit.mindmap.data.NodePreferences;
import com.uit.mindmap.data.TextPreferences;

public class Node extends RelativeLayout {
    public NodeData data;

    TextView text;
    ViewGroup highlight;
    ViewGroup outline;
    MapView map;

    private float dx = 0f;
    private float dy = 0f;
    private float prevDx = 0f;
    private float prevDy = 0f;

    private int mapsize;
    boolean moved;
    boolean focused;
    public boolean deleted;
    public long touchtime;

    //region Constructor
    public Node(Context context) {
        super(context);
        data = new NodeData();
        init(null);
    }

    public Node(Context context, NodeData data) {
        super(context);
        this.data = data;
        init(null);
    }

    public Node(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        data = new NodeData();
        init(attrs);
    }

    public Node(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        data = new NodeData();
        init(attrs);
    }

    @SuppressLint("ClickableViewAccessibility")
    public void init(@Nullable AttributeSet set) {
        inflate(getContext(), R.layout.node_view, this);
        highlight = findViewById(R.id.highlight);
        outline = findViewById(R.id.outline);
        text = findViewById(R.id.text);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        params.gravity = Gravity.CENTER;
        text.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction() & MotionEvent.ACTION_MASK) {
                    case MotionEvent.ACTION_DOWN:
                        Node.this.bringToFront();
                        prevDx = event.getX();
                        prevDy = event.getY();
                        if (map.selectionMode == 0) map.selectSingle(data.id);
                        break;
                    case MotionEvent.ACTION_MOVE:
                        if (!moved) {
                            moved = true;
                            map.addCommand();
                        }
                        text.clearFocus();
                        dx = event.getX() - prevDx;
                        dy = event.getY() - prevDy;
                        if (focused)
                            map.moveNode((int) dx, (int) dy);
                        break;
                    case MotionEvent.ACTION_POINTER_DOWN:
                        prevDx = event.getX();
                        prevDy = event.getY();
                        break;
                    case MotionEvent.ACTION_POINTER_UP:
                        break;
                    case MotionEvent.ACTION_UP:
                        touchtime = event.getEventTime() - event.getDownTime();
                        if (touchtime < 200)
                            map.selectNode(data.id);
                        moved = false;
                        break;
                }
                return true;
            }
        });
        text.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Node.this.callOnClick();
            }
        });
        text.setOnFocusChangeListener(new OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) Node.this.callOnClick();
            }
        });
        mapsize = (int) getResources().getDimension(R.dimen.map_size);
        final ViewTreeObserver observer = getViewTreeObserver();
        observer.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                setPosition(data.pos);
            }
        });
        applyData();
    }

    //endregion
    public void focus() {
        focused = true;
        highlight.setBackgroundResource(R.drawable.rounded_bounding_box);
    }

    public void defocus() {
        focused = false;
        highlight.setBackground(null);
    }

    public void setText(String text) {
        data.text = text;
        this.text.setText(text);
    }

    public void setNodePreferences(NodePreferences preference) {
        if (preference.color!=-10) data.nodePreferences.color=preference.color;
        if (preference.outlineWidth!=-1) data.nodePreferences.outlineWidth=preference.outlineWidth;
        if (preference.outlineColor!=-10) data.nodePreferences.outlineColor=preference.outlineColor;

        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) text.getLayoutParams();
        int margin = data.nodePreferences.outlineWidth;
        params.setMargins(margin, margin, margin, margin);
        text.setLayoutParams(params);

        GradientDrawable drawable = (GradientDrawable) outline.getBackground();
        drawable.setStroke(data.nodePreferences.outlineWidth, data.nodePreferences.outlineColor);
        drawable.setColor(data.nodePreferences.color);

        outline.setBackground(drawable);
    }

    public void setTextPreferences(TextPreferences preference) {
        if (preference.color!=-10) data.textPreferences.color=preference.color;
        if (preference.size!=-1) data.textPreferences.size=preference.size;
        if (preference.alignment!=-1) data.textPreferences.alignment=preference.alignment;
        if (preference.effect!=-1) data.textPreferences.effect=preference.effect;
        text.setTextColor(data.textPreferences.color);
        text.setTextSize(TypedValue.COMPLEX_UNIT_PX, data.textPreferences.size + 15);
        switch (data.textPreferences.alignment) {
            case 0:
                text.setGravity(Gravity.LEFT);
                break;
            case 1:
                text.setGravity(Gravity.CENTER);
                break;
            case 2:
                text.setGravity(Gravity.RIGHT);
                break;
        }
        int effect = data.textPreferences.effect;
        if (effect >= 4) {
            effect -= 4;
            text.setPaintFlags(text.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
            invalidate();
        } else {
            text.setPaintFlags(text.getPaintFlags() & (~Paint.UNDERLINE_TEXT_FLAG));
            invalidate();
        }
        switch (effect) {
            case 3:
                text.setTypeface(text.getTypeface(), Typeface.BOLD_ITALIC);
                break;
            case 2:
                text.setTypeface(text.getTypeface(), Typeface.ITALIC);
                break;
            case 1:
                text.setTypeface(text.getTypeface(), Typeface.BOLD);
                break;
            case 0:
                text.setTypeface(null, Typeface.NORMAL);
                break;
        }
    }

    public void setLinePreferences(LinePreferences preference) {
        if (preference.width!=1) data.linePreferences.width=preference.width;
        if (preference.arrow!=1) data.linePreferences.arrow=preference.arrow;
        if (preference.color!=10) data.linePreferences.color=preference.color;
        if (preference.curve!=1) data.linePreferences.curve=preference.curve;
        if (preference.effect!=1) data.linePreferences.effect=preference.effect;
    }

    public void setTextSize(int value) {
        data.textPreferences.size = value;
        text.setTextSize(value);
    }

    public void setTextColor(int color) {
        data.textPreferences.color = color;
        text.setTextColor(color);
    }

    public void applyData() {
        setPosition(data.pos);
        text.setText(data.text);
        setNodePreferences(data.nodePreferences);
        setTextPreferences(data.textPreferences);
    }

    public void setData(NodeData data) {
        this.data = data;
        applyData();
    }

    public void setPosition(int[] pos) {
        int maxX = mapsize - getWidth() / 2;
        int maxY = mapsize - getHeight() / 2;
        data.pos[0] = Math.min(Math.max(getWidth() / 2, pos[0]), maxX);
        data.pos[1] = Math.min(Math.max(getHeight() / 2, pos[1]), maxY);
        RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        lp.setMargins(pos[0] - getWidth() / 2, pos[1] - getHeight() / 2, 0, 0);
        setLayoutParams(lp);
    }

    public void movePosition(int x, int y) {
        setPosition(new int[]{data.pos[0] + x, data.pos[1] + y});
    }

    public void removeChildren(int id) {
        int i = data.children.indexOf(id);
        if (i != -1) data.children.remove(i);
    }

    public void addChild(int id) {
        data.children.add(id);
    }

    public int[] anchor(Node other) {
        int[] a = new int[4];
        int width = outline.getWidth();
        int height = outline.getHeight();
        a[0] = data.pos[0] - width / 2;
        a[1] = data.pos[1] - height / 2;

        float scale = getScaleX();
        int distanceX = other.data.pos[0] - data.pos[0];
        int distanceY = other.data.pos[1] - data.pos[1];
        float x;
        if (distanceX == 0) x = 2;
        else x = Math.abs(distanceY / distanceX);
        if (x < 1 && Math.abs(distanceX) > other.getWidth() / 2 + getWidth() / 2) {
            if (distanceX < 0) {
                a[1] += height * Math.max(Math.min((float) distanceY / height / 4f + 0.5f, 0.9f), 0.1f) * scale;
                a[2] = -1;
            } else {
                a[0] += width * scale;
                a[1] += height * Math.max(Math.min((float) distanceY / height / 4f + 0.5f, 0.9f), 0.1f) * scale;
                a[2] = 1;
            }
        } else {
            if (distanceY < 0) {

                a[0] += width * Math.max(Math.min((float) distanceX / width / 4f + 0.5f, 0.9f), 0.1f) * scale;
                a[3] = -1;
            } else {
                a[0] += width * Math.max(Math.min((float) distanceX / width / 4f + 0.5f, 0.9f), 0.1f) * scale;
                a[1] += height * scale;
                a[3] = 1;
            }
        }
        return a;
    }

    public void setMap(MapView map) {
        this.map = map;
    }

    public int[] bounds() {
        int w = outline.getWidth() / 2;
        int h = outline.getHeight() / 2;
        return new int[]{data.pos[0] - w, data.pos[1] - h, data.pos[0] + w, data.pos[1] + h};
    }
}
