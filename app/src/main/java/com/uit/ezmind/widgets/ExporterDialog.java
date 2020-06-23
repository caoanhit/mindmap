package com.uit.ezmind.widgets;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.uit.ezmind.R;

public class ExporterDialog extends AlertDialog.Builder {

    private View customDialogView;

    public ExporterDialog(Context context) {
        super(context);
        init();
    }

    public ExporterDialog(Context context, int themeResId) {
        super(context, themeResId);
        init();
    }

    private void init() {
        TextView t = customDialogView.findViewById(R.id.tv_dialog);
        t.setText(getContext().getResources().getString(R.string.rename_map));
        setView(customDialogView);
    }
}
