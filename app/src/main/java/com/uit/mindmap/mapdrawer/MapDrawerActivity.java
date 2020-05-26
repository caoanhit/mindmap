package com.uit.mindmap.mapdrawer;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import com.uit.mindmap.R;

public class MapDrawerActivity extends AppCompatActivity {
    public ZoomLayout zoomLayout;
    public MapView mapView;
    public FloatingMenu menu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mapView = findViewById(R.id.map_view);
        zoomLayout = findViewById(R.id.zoom);
        menu=findViewById(R.id.floating_menu);
        //String mapName= getIntent().getExtras().getString("mapName");
        mapView.loadMap(null);
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
