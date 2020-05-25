package com.uit.mindmap;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.coordinatorlayout.widget.CoordinatorLayout;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.LinearLayout;

import com.uit.mindmap.mapdrawer.FloatingMenu;
import com.uit.mindmap.mapdrawer.MapView;
import com.uit.mindmap.mapdrawer.Node;
import com.uit.mindmap.mapdrawer.ZoomLayout;

import org.w3c.dom.NodeList;

import java.util.List;

public class MainActivity extends AppCompatActivity {
    public ZoomLayout zoomLayout;
    public MapView mapView;
    public FloatingMenu menu;
    public String mapName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mapView = findViewById(R.id.map_view);
        zoomLayout = findViewById(R.id.zoom);
        menu=findViewById(R.id.floating_menu);
        zoomLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (zoomLayout.holdTime < 300) {
                    mapView.deselectAll();
                    zoomLayout.requestFocus();
                    zoomLayout.bringToFront();
                    View view = getCurrentFocus();
                    if (view != null) {
                        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                    }

                }
            }
        });
        menu.assignMap(mapView);
        menu.bringToFront();
    }


}
