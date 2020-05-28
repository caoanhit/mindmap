package com.uit.mindmap.mapdrawer;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.inputmethod.InputMethodManager;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;

import com.google.android.material.bottomappbar.BottomAppBar;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.uit.mindmap.R;

public class MapDrawerActivity extends AppCompatActivity {
    private MapView mapView;
    private LinearLayout bottomSheet;
    private NodeCustomizer nodeCustomizer;
    public FloatingMenu menu;
    public BottomSheetBehavior bottomSheetBehavior;
    private String mapName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mapView = findViewById(R.id.map_view);
        final ZoomLayout zoomLayout = findViewById(R.id.zoom);
        menu = findViewById(R.id.floating_menu);
        Bundle extra = getIntent().getExtras();
        mapName = null;
        if (extra != null) {
            mapName = extra.getString("mapName");
            Log.i("map", mapName);
        }
        mapView.loadMap(mapName);
        bottomSheet = findViewById(R.id.bottom_sheet);
        nodeCustomizer = (NodeCustomizer) findViewById(R.id.node_customizer);
        mapView.setNodeCustomizer(nodeCustomizer);
        bottomSheetBehavior = BottomSheetBehavior.from(bottomSheet);
        zoomLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (zoomLayout.holdTime < 200) {
                    mapView.deselectAll();
                    zoomLayout.requestFocus();
                    menu.setVisibility((View.GONE));
                    View view = getCurrentFocus();
                    if (view != null) {
                        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                    }
                    bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                }
            }
        });
        menu.assignMap(mapView);
        ((ImageButton) menu.findViewById(R.id.customize)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                mapView.setSheetData();
            }
        });
        bottomSheet.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus)
                    bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
            }
        });
        menu.bringToFront();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.mapdrawer_action_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        Log.i("Button", item.getItemId() + "");
        switch (id) {
            case android.R.id.home:
                if (mapView.changed) {
                    AlertDialog.Builder alertDialog = new AlertDialog.Builder(MapDrawerActivity.this);
                    alertDialog.setMessage("Some changes are not saved. Do you want to exit?");
                    alertDialog.setIcon(R.mipmap.ic_launcher);
                    alertDialog.setPositiveButton(R.string.no, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    });
                    alertDialog.setNegativeButton(R.string.yes, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                            finish();
                        }
                    });
                    alertDialog.show();
                    return true;
                }
                break;
            case R.id.save:
                mapView.saveData();
                break;
            case R.id.save_as:
                mapView.saveAs();
        }
        return super.onOptionsItemSelected(item);
    }
}
