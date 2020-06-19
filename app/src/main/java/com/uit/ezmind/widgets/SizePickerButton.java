package com.uit.ezmind.widgets;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.NumberPicker;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;

import com.uit.ezmind.R;

public class SizePickerButton extends RelativeLayout {
    private int value=14;
    private int max,min;
    Button btn;

    public interface OnFinishListener{
        void onFinish(int value);
    }

    public interface OnSizePickedListener{
        void onSizePicked(int value);
    }
    private OnSizePickedListener onSizePickedListener;
    public void setOnSizePickedListener(OnSizePickedListener onSizePickedListener){
        this.onSizePickedListener=onSizePickedListener;
    }

    public SizePickerButton(Context context) {
        super(context);
        init();
    }

    public SizePickerButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public SizePickerButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    public SizePickerButton(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }

    private void init(){
        inflate(getContext(), R.layout.size_picker_button,this);
        btn=findViewById(R.id.size_picker);
        btn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                LayoutInflater inflater=(LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                final View popup= inflater.inflate(R.layout.number_picker,null);
                NumberPicker numberPicker=popup.findViewById(R.id.number_picker);
                numberPicker.setMinValue(min);
                numberPicker.setMaxValue(max);
                numberPicker.setValue(value);
                numberPicker.setDescendantFocusability(FOCUS_BLOCK_DESCENDANTS);
                numberPicker.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
                    @Override
                    public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                        setValue(newVal);
                        if(onSizePickedListener!=null) onSizePickedListener.onSizePicked(newVal);
                    }
                });
                int height=(int)getContext().getResources().getDimension(R.dimen.number_picker_height);
                int width=(int)getContext().getResources().getDimension(R.dimen.number_picker_width);
                final PopupWindow popupWindow=new PopupWindow(popup, width, height ,true);
                popupWindow.setBackgroundDrawable(getContext().getDrawable(R.drawable.rounded_flat));
                popupWindow.setElevation(getContext().getResources().getDimension(R.dimen.elevation));
                popupWindow.showAsDropDown(v,0,-height/2-v.getHeight()/2);
            }
        });
    }

    public void setValue(int value) {
        this.value = value;
        btn.setText(value+"");
    }

    public void setMax(int max){
        this.max=max;
    }

    public void setMin(int min) {
        this.min = min;
    }
    public void setLimit(int min, int max){
        this.min=min;
        this.max=max;
    }
}
