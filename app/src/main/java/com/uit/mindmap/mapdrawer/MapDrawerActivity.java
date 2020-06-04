package com.uit.mindmap.mapdrawer;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.uit.mindmap.R;
import com.uit.mindmap.widgets.FloatingMenu;
import com.uit.mindmap.widgets.ZoomLayout;

public class MapDrawerActivity extends AppCompatActivity {
    private MapView mapView;
    private LinearLayout bottomSheet;
    private NodeCustomizer nodeCustomizer;
    public FloatingMenu menu;
    public BottomSheetBehavior bottomSheetBehavior;
    private String mapName;
    public ZoomLayout zoomLayout;
    private TextView zoomPercentage;

    private boolean undoAvailable;
    private boolean redoAvailable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        findViewByIds();
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        final CountDownTimer timer=new CountDownTimer(500,500) {
            @Override
            public void onTick(long millisUntilFinished) {

            }

            @Override
            public void onFinish() {

                zoomPercentage.clearAnimation();
                zoomPercentage.animate()
                        .alpha(0f)
                        .setDuration(500)
                        .setListener(new AnimatorListenerAdapter() {
                            @Override
                            public void onAnimationEnd(Animator animation) {
                                zoomPercentage.setVisibility(View.GONE);
                            }
                        });
            }
        };

        zoomPercentage.setVisibility(View.GONE);
        zoomLayout.setOnScaleListener(new ZoomLayout.onScaleListener() {
            @Override
            public void onScale(float scale) {
                zoomPercentage.setAlpha(0.5f);
                zoomPercentage.setVisibility(View.VISIBLE);
                zoomPercentage.setText((int) (scale * 100) + "%");
                timer.start();
            }
        });
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);

        Bundle extra = getIntent().getExtras();
        mapName = null;
        if (extra != null) {
            mapName = extra.getString("mapName");
            Log.i("map", mapName);
        }
        mapView.loadMap(mapName);
        mapView.setNodeCustomizer(nodeCustomizer);
        bottomSheetBehavior = BottomSheetBehavior.from(bottomSheet);
        zoomLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (zoomLayout.holdTime < 200) {
                    mapView.deselectAll();
                    zoomLayout.requestFocus();
                    View view = getCurrentFocus();
                    if (view != null) {
                        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                    }
                    bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                }
            }

        });
        mapView.setOnChangeListener(new MapView.onChangeListener() {
            @Override
            public void onChange(boolean undoAvailable, boolean redoAvailable) {
                if (undoAvailable){
                    Log.i("button", "enable undo");
                    enableUndo();
                }
                else {
                    Log.i("button", "disable undo");
                    disableUndo();
                }
                if (redoAvailable){
                    Log.i("button", "enable redo");
                    enableRedo();
                }
                else {
                    Log.i("button", "disable redo");
                    disableRedo();
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

    private void findViewByIds(){
        zoomLayout = findViewById(R.id.zoom);
        mapView = findViewById(R.id.map_view);
        menu = findViewById(R.id.floating_menu);
        zoomPercentage = findViewById(R.id.zoom_percentage);
        bottomSheet = findViewById(R.id.bottom_sheet);
        nodeCustomizer = (NodeCustomizer) findViewById(R.id.node_customizer);
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
                break;
            case R.id.undo:
                mapView.undo();
                break;
            case R.id.redo:
                mapView.redo();
                break;
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
        } else finish();
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        MenuItem btnUndo = menu.findItem(R.id.undo);
        MenuItem btnRedo = menu.findItem(R.id.redo);
        if (undoAvailable) {

            btnUndo.getIcon().setAlpha(255);
            btnUndo.setEnabled(true);
        } else {
            btnUndo.getIcon().setAlpha(130);
            btnUndo.setEnabled(false);
        }
        if (redoAvailable) {

            btnRedo.getIcon().setAlpha(255);
            btnRedo.setEnabled(true);
        } else {
            btnRedo.getIcon().setAlpha(130);
            btnRedo.setEnabled(false);
        }
        return true;
    }

    private void enableUndo() {
        undoAvailable = true;
        invalidateOptionsMenu();
    }

    private void enableRedo() {
        redoAvailable = true;
        invalidateOptionsMenu();
    }

    private void disableUndo() {
        undoAvailable = false;
        invalidateOptionsMenu();
    }

    private void disableRedo() {
        redoAvailable = false;
        invalidateOptionsMenu();
    }

}
