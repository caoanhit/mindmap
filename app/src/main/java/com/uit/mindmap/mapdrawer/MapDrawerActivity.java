package com.uit.mindmap.mapdrawer;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.renderscript.Sampler;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageButton;
import android.widget.LinearLayout;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.uit.mindmap.R;

public class MapDrawerActivity extends AppCompatActivity {
    private MapView mapView;
    private LinearLayout bottomSheet;
    private NodeCustomizer nodeCustomizer;
    public FloatingMenu menu;
    public BottomSheetBehavior bottomSheetBehavior;
    private String mapName;
    public ZoomLayout zoomLayout;

    private boolean undoAvailable;
    private boolean redoAvailable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED)
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE,Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
        mapView = findViewById(R.id.map_view);
        zoomLayout = findViewById(R.id.zoom);
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

    @Override
    public void onBackPressed() {
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
        }
        else finish();
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        MenuItem btnUndo = menu.findItem(R.id.undo);
        MenuItem btnRedo = menu.findItem(R.id.redo);
        if (undoAvailable){

            btnUndo.getIcon().setAlpha(255);
            btnUndo.setEnabled(true);
        }
        else {
            btnUndo.getIcon().setAlpha(130);
            btnUndo.setEnabled(false);
        }
        if (redoAvailable){

            btnRedo.getIcon().setAlpha(255);
            btnRedo.setEnabled(true);
        }
        else {
            btnRedo.getIcon().setAlpha(130);
            btnRedo.setEnabled(false);
        }
        return true;
    }

    private void enableUndo(){
        undoAvailable=true;
        invalidateOptionsMenu();
    }

    private void enableRedo(){
        redoAvailable=true;
        invalidateOptionsMenu();
    }

    private void disableUndo(){
        undoAvailable=false;
        invalidateOptionsMenu();
    }
    private void disableRedo(){
        redoAvailable=false;
        invalidateOptionsMenu();
    }

}
