package com.uit.mindmap.mapdrawer;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.util.AttributeSet;
import android.util.Log;
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
import com.uit.mindmap.maploader.NodeData;

import java.util.ArrayList;
import java.util.List;

public class Node extends RelativeLayout {
    public NodeData data;

    TextView text_field;
    ViewGroup highlight;
    ViewGroup outline;
    MapView map;

    private float dx = 0f;
    private float dy = 0f;
    private float prevDx = 0f;
    private float prevDy = 0f;

    private int mapsize;
    boolean moved;
    public boolean deleted;
    //region Constructor
    public Node(Context context) {
        super(context);

        init(null);
    }

    public Node(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);

        init(attrs);
    }

    public Node(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        init(attrs);
    }
    @SuppressLint("ClickableViewAccessibility")
    public  void init(@Nullable AttributeSet set){
        data=new NodeData();
        data.children=new ArrayList<>();
        data.pos=new int[2];
        inflate(getContext(), R.layout.node_view,this);
        highlight=findViewById(R.id.highlight);
        outline= findViewById(R.id.outline);
        text_field = findViewById(R.id.text);
        LinearLayout.LayoutParams params=new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        params.gravity= Gravity.CENTER;
        text_field.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction() & MotionEvent.ACTION_MASK) {
                    case MotionEvent.ACTION_DOWN:
                        Node.this.bringToFront();
                        prevDx = event.getX();
                        prevDy = event.getY();
                        map.selectNode(data.id);
                        break;
                    case MotionEvent.ACTION_MOVE:
                        if (!moved){
                            moved=true;
                            map.addCommand();
                        }
                        text_field.clearFocus();
                        dx = event.getX() - prevDx;
                        dy = event.getY() - prevDy;
                        map.moveNode((int)dx,(int)dy);
                        break;
                    case MotionEvent.ACTION_POINTER_DOWN:
                        prevDx = event.getX();
                        prevDy = event.getY();
                        break;
                    case MotionEvent.ACTION_POINTER_UP:
                        break;
                    case MotionEvent.ACTION_UP:
                        moved=false;
                        break;
                }
                return false;
            }
        });
        text_field.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Node.this.callOnClick();
            }
        });
        text_field.setOnFocusChangeListener(new OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(hasFocus) Node.this.callOnClick();
            }
        });
        mapsize=(int)getResources().getDimension(R.dimen.map_size);
        final ViewTreeObserver observer=getViewTreeObserver();
        observer.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                applyPosition();
            }
        });
    }
    //endregion
    public void focus(){
        highlight.setBackgroundResource(R.drawable.rounded_bounding_box);
    }

    public void defocus(){
        highlight.setBackground(null);
    }

    public String getText(){
        return text_field.getText().toString();
    }
    public void setText(String text){
        data.text=text;
        text_field.setText(text);
    }
    public void setId(int id){
        data.id=id;
    }
    public void setTextSize(int text_size){
        data.textSize=text_size;
        switch (text_size){
            case 0:
                text_field.setTextSize(TypedValue.COMPLEX_UNIT_SP,12);
                break;
            case 1:
                text_field.setTextSize(TypedValue.COMPLEX_UNIT_SP,16);
                break;
            case 2:
                text_field.setTextSize(TypedValue.COMPLEX_UNIT_SP,20);
                break;
            default:
                data.textSize=1;
                text_field.setTextSize(TypedValue.COMPLEX_UNIT_SP,14);
                break;
        }
    }
    public void setTextColor(int color){
        data.textColor=color;
        text_field.setTextColor(color);
    }
    public void setFillColor(int color){
        data.fillColor=color;
        text_field.setBackgroundTintList(ColorStateList.valueOf(color));
    }
    public void setOutlineColor(int color){
        data.outlineColor=color;
        outline.setBackgroundTintList(ColorStateList.valueOf(color));
    }
    public void setLineColor(int color){
        data.lineColor=color;
    }
    public void setLineStyle(int style){
        data.lineStyle=style;
    }
    public void setArrow(int arrow) {
        Log.i("arrow",arrow+"");
        data.arrow=arrow;
    }

    public void applyData(){
        applyPosition();
        setText(data.text);
        setTextSize(data.textSize);
        setTextColor(data.textColor);
        setFillColor(data.fillColor);
        setOutlineColor(data.outlineColor);
        setLineColor(data.lineColor);
        setLineStyle(data.lineStyle);
    }
    public void setData(NodeData data){
        this.data=data;
        applyData();
    }
    public void setPosition(int[] pos ){
        int maxX=mapsize-getWidth()/2;
        int maxY=mapsize-getHeight()/2;
        data.pos[0]=Math.min(Math.max(getWidth()/2,pos[0]),maxX);
        data.pos[1]=Math.min(Math.max(getHeight()/2,pos[1]),maxY);
        applyPosition();
    }

    public void applyPosition(){
        int left,top;
        left=data.pos[0]-getWidth()/2;
        top=data.pos[1]-getHeight()/2;
        RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT);
        lp.setMargins(left, top, 0, 0);
        setLayoutParams(lp);
    }
    public void movePosition(int x,int y){
        setPosition(new int[]{data.pos[0]+x,data.pos[1]+y});
    }
    public void removeChildren(int id){
        int i= data.children.indexOf(id);
        if (i!=-1) data.children.remove(i);
    }
    public void addChild(int id){
        data.children.add(id);
    }
    public int[] anchor(Node other){
        int[] a=new  int[4];
        int[] a1=new int[2];
        int width=outline.getWidth();
        int height=outline.getHeight();
        outline.getLocationOnScreen(a1);
        float scale=((MapDrawerActivity)getContext()).zoomLayout.scale;
        int distanceX=other.data.pos[0]-data.pos[0];
        int distanceY=other.data.pos[1]-data.pos[1];
        a[0]=a1[0];
        a[1]=a1[1];
        float x;
        if(distanceX==0) x=2;
        else x=Math.abs(distanceY/distanceX);
        if (x<1&& Math.abs( distanceX)> other.getWidth()/2+getWidth()/2){
            if (distanceX<0)
            {
                a[1]+=height*Math.max(Math.min((float)distanceY/height/4f+0.5f,0.9f),0.1f)*scale;
                a[2]=-1;
            }
            else{
                a[0]+=width*scale;
                a[1]+=height*Math.max(Math.min((float)distanceY/height/4f+0.5f,0.9f),0.1f)*scale;
                a[2]=1;
            }
        }
        else{
            if(distanceY<0){

                a[0]+=width*Math.max(Math.min((float)distanceX/width/4f+0.5f,0.9f),0.1f)*scale;
                a[3]=-1;
            }
            else {
                a[0]+=width*Math.max(Math.min((float)distanceX/width/4f+0.5f,0.9f),0.1f)*scale;
                a[1]+=height*scale;
                a[3]=1;
            }
        }
        return  a;
    }
    public void setMap(MapView map){
        this.map=map;
    }
}
