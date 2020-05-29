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
    public int id;
    public int parent;
    TextView text_field;
    ViewGroup highlight;
    ViewGroup outline;
    List<Integer> children;
    int[] pos;

    //region Styling
    String text="Root node";
    int textSize=1;
    int textColor=Color.DKGRAY ;
    int backgroundColor=Color.WHITE;
    int outlineColor=Color.GRAY;
    int connectionStyle=0;
    int connectionColor=Color.BLACK;

    //endregion
    private float dx = 0f;
    private float dy = 0f;
    private float prevDx = 0f;
    private float prevDy = 0f;

    private int mapsize;
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
        children=new ArrayList<>();
        pos=new int[2];
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
                        break;
                    case MotionEvent.ACTION_MOVE:
                        text_field.clearFocus();
                        dx = event.getX() - prevDx;
                        dy = event.getY() - prevDy;
                        movePosition((int)dx,(int)dy);
                        break;
                    case MotionEvent.ACTION_POINTER_DOWN:
                        prevDx = event.getX();
                        prevDy = event.getY();
                        break;
                    case MotionEvent.ACTION_POINTER_UP:
                        break;
                    case MotionEvent.ACTION_UP:
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
        this.text=text;
        text_field.setText(text);
    }
    public void applyTextSize(int text_size){
        this.textSize=text_size;
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
                this.textSize=1;
                text_field.setTextSize(TypedValue.COMPLEX_UNIT_SP,14);
                break;
        }
    }
    public void applyTextColor(int color){
        textColor=color;
        text_field.setTextColor(color);
    }
    public void applyBackgroundColor(int color){
        backgroundColor=color;
        text_field.setBackgroundTintList(ColorStateList.valueOf(color));
    }
    public void applyConnectionStyle(int connection_style){

        connectionStyle=connection_style;
        Log.i("connection1",""+this.connectionStyle);
    }
    public void applyOutlineColor(int color){
        outlineColor=color;
        outline.setBackgroundTintList(ColorStateList.valueOf(color));
    }
    public void applyConnectionColor(int color){
        connectionColor=color;
    }


    public NodeData getData(){
        NodeData data=new NodeData();
        data.text=text;
        data.id=id;
        data.parent=parent;
        data.children=children;
        data.pos=pos;
        data.text_size=textSize;
        data.text_color=textColor;
        data.background_color=backgroundColor;
        data.outline_color=outlineColor;
        data.connection_style=connectionStyle;
        data.connection_color=connectionColor;
        return data;
    }
    public void setData(NodeData data){
        text=data.text;
        id=data.id;
        parent=data.parent;
        children=data.children;
        pos=data.pos;
        textSize=data.text_size;
        textColor=data.text_color;
        backgroundColor=data.background_color;
        outlineColor=data.outline_color;
        connectionStyle=data.connection_style;
        connectionColor=data.connection_color;
    }
    public void applyData(){
        setText(text);
        applyTextSize(textSize);
        applyTextColor(textColor);
        applyBackgroundColor(backgroundColor);
        applyOutlineColor(outlineColor);
        applyConnectionColor(connectionColor);
    }
    public void setId(int id){
        this.id=id;
    }
    public int[] getPosition(){
        return pos;
    }
    public void setPosition(int[] pos ){
        int maxX=mapsize-getWidth()/2;
        int maxY=mapsize-getHeight()/2;
        this.pos[0]=Math.min(Math.max(getWidth()/2,pos[0]),maxX);
        this.pos[1]=Math.min(Math.max(getHeight()/2,pos[1]),maxY);
        applyPosition();
    }

    public int getConnectionStyle() {
        return connectionStyle;
    }
    public int getConnectionColor(){ return connectionColor; }

    public void setPosition(int x, int y){
        int maxX=mapsize-getWidth()/2;
        int maxY=mapsize-getHeight()/2;
        pos[0]=Math.min(Math.max(getWidth()/2,x),maxX);
        pos[1]=Math.min(Math.max(getHeight()/2,y),maxY);
        applyPosition();
    }
    public void applyPosition(){
        int left,top;
        left=pos[0]-getWidth()/2;
        top=pos[1]-getHeight()/2;
        RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT);
        lp.setMargins(left, top, 0, 0);
        setLayoutParams(lp);
    }
    public void movePosition(int x,int y){
        setPosition(pos[0]+x,pos[1]+y);
    }
    public void removeChildren(int id){
        int i= children.indexOf(id);
        if (i!=-1) children.remove(i);
    }
    public void addChild(int id){
        if(children==null) children=new ArrayList<>();
        children.add(id);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
    }

}
