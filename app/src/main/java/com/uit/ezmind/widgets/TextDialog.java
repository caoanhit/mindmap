package com.uit.ezmind.widgets;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.uit.ezmind.R;

import org.w3c.dom.Text;

public class TextDialog extends AlertDialog.Builder {
    private View customDialogView;
    private TextView title;
    private EditText etName;
    private AlertDialog alertDialog;

    public interface OnOKClicked{
        void OnClick(String text, AlertDialog alertDialog);
    }
    private OnOKClicked onOKClicked;

    public TextDialog setOnOKClicked(OnOKClicked onOKClicked) {
        this.onOKClicked = onOKClicked;
        return this;
    }

    public TextDialog(Context context) {
        super(context);
        init();
    }

    public TextDialog(Context context, int themeResId) {
        super(context, themeResId);
        init();
    }

    private void init(){
        LayoutInflater li = LayoutInflater.from(getContext());
        customDialogView = li.inflate(R.layout.edit_text_dialog, null);
        title = customDialogView.findViewById(R.id.tv_dialog);
        etName = customDialogView.findViewById(R.id.name);
        setView(customDialogView);
        setCancelable(true).setPositiveButton("OK", null)
                .setNegativeButton("Cancel", null);
        alertDialog = create();
        alertDialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialog) {
                Button ok = alertDialog.getButton(AlertDialog.BUTTON_POSITIVE);
                ok.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(onOKClicked!=null) onOKClicked.OnClick(getText(),alertDialog);
                    }
                });
            }
        });
    }

    @Override
    public AlertDialog show() {
        alertDialog.show();
        return alertDialog;
    }

    public TextDialog setDialogTitle(int titleId){
        title.setText(getContext().getText(titleId).toString());
        return this;
    }

    public TextDialog setDialogText(String text){
        etName.setText(text);
        etName.selectAll();
        return this;
    }
    public String getText(){
        return etName.getText().toString();
    }
}
