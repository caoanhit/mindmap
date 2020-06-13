package com.uit.mindmap.mapdrawer;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.uit.mindmap.R;
import com.uit.mindmap.data.LinePreferences;
import com.uit.mindmap.data.NodePreferences;
import com.uit.mindmap.data.TextPreferences;
import com.uit.mindmap.maploader.MapManagerActivity;
import com.uit.mindmap.utils.SettingActivity;
import com.uit.mindmap.widgets.ZoomLayout;

public class MapDrawerActivity extends AppCompatActivity {
    private MapView mapView;
    private LinearLayout nodeSheet, textSheet, lineSheet, menu;
    private NodeCustomizer nodeCustomizer;
    private LineCustomizer lineCustomizer;
    private TextCustomizer textCustomizer;
    private BottomSheetBehavior nodeCustomizerBehavior, textCustomizerBehavior, lineCustomizerBehavior;
    private String mapName;
    private ZoomLayout zoomLayout;
    private TextView zoomPercentage;
    private CountDownTimer timer;
    private ImageButton btnLine;

    private boolean undoAvailable;
    private boolean redoAvailable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_keyboard_arrow_left_black_24dp);

        initViews();

        initZoomPercentage();

        Bundle extra = getIntent().getExtras();
        mapName = null;
        if (extra != null) {
            mapName = extra.getString("mapName");
            Log.i("map", mapName);
        }
        mapView.loadMap(mapName);
        mapView.setNodeCustomizer(nodeCustomizer);
        mapView.setOnChangeListener(new MapView.onChangeListener() {
            @Override
            public void onChange(boolean undoAvailable, boolean redoAvailable) {
                if (undoAvailable) {
                    enableUndo();
                } else {
                    disableUndo();
                }
                if (redoAvailable) {
                    enableRedo();
                } else {
                    disableRedo();
                }
            }
        });

        findViewById(R.id.new_node).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mapView.addNode();
            }
        });
        findViewById(R.id.edit).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mapView.editText();
            }
        });
        findViewById(R.id.delete).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mapView.removeNode();
                deselect();
            }
        });
        findViewById(R.id.node_customize).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                nodeCustomizerBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                nodeCustomizer.setPreferences(mapView.getFirstData().nodePreferences);
            }
        });
        findViewById(R.id.text_customize).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                textCustomizerBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                textCustomizer.setPreferences(mapView.getFirstData().textPreferences);
            }
        });
        findViewById(R.id.line_customize).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                lineCustomizerBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                lineCustomizer.setPreferences(mapView.getFirstData().linePreferences);
            }
        });
        nodeCustomizer.setOnPreferenceChange(new NodeCustomizer.OnPreferenceChangeListener() {
            @Override
            public void OnChange(NodePreferences data) {
                mapView.setNodePreferences(data);
            }
        });

        textCustomizer.setOnPreferenceChange(new TextCustomizer.OnPreferenceChangeListener() {
            @Override
            public void OnChange(TextPreferences data) {
                mapView.setTextPreferences(data);
            }
        });

        lineCustomizer.setOnPreferenceChange(new LineCustomizer.OnPreferenceChangeListener() {
            @Override
            public void OnChange(LinePreferences data) {
                mapView.setLinePreferences(data);
            }
        });

    }

    private void initViews() {
        zoomLayout = findViewById(R.id.zoom);
        zoomLayout.setOnScaleListener(new ZoomLayout.onScaleListener() {
            @Override
            public void onScale(float scale) {
                zoomPercentage.setAlpha(0.5f);
                zoomPercentage.setVisibility(View.VISIBLE);
                zoomPercentage.setText((int) (scale * 100) + "%");
                timer.start();
            }
        });
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
                    nodeCustomizerBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                    textCustomizerBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                    lineCustomizerBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                }
            }

        });

        mapView = findViewById(R.id.map_view);
        menu = findViewById(R.id.floating_menu);
        zoomPercentage = findViewById(R.id.zoom_percentage);
        btnLine=findViewById(R.id.line_customize);

        nodeSheet = findViewById(R.id.node_customizer_sheet);
        nodeCustomizer = findViewById(R.id.node_customizer);
        nodeCustomizerBehavior = BottomSheetBehavior.from(nodeSheet);
        nodeSheet.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus)
                    nodeCustomizerBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
            }
        });

        textSheet = findViewById(R.id.text_customizer_sheet);
        textCustomizer = findViewById(R.id.text_customizer);
        textCustomizerBehavior = BottomSheetBehavior.from(textSheet);
        textSheet.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus)
                    textCustomizerBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
            }
        });

        lineSheet = findViewById(R.id.line_customizer_sheet);
        lineCustomizer = findViewById(R.id.line_customizer);
        lineCustomizerBehavior = BottomSheetBehavior.from(lineSheet);
        lineSheet.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus)
                    lineCustomizerBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
            }
        });
    }

    private void initZoomPercentage() {
        timer = new CountDownTimer(500, 500) {
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
    }

    @Override
    protected void onResume() {
        int backgroundColor=getSharedPreferences("MyPrefs", Context.MODE_PRIVATE).getInt("background_color",0);
        if (backgroundColor!=0)
        findViewById(R.id.zoom).setBackgroundColor(backgroundColor);
        super.onResume();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.mapdrawer_action_menu, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        Log.i("Button", item.getItemId() + "");
        switch (id) {
            case android.R.id.home:
                exit();
                return true;
            case R.id.save:
                mapView.saveData();
                break;
            case R.id.save_as:
                mapView.saveAs();
                break;
            case R.id.undo:
                mapView.undo();
                if (nodeCustomizerBehavior.getState()==BottomSheetBehavior.STATE_EXPANDED)
                    nodeCustomizer.setPreferences(mapView.getFirstData().nodePreferences);
                else if (textCustomizerBehavior.getState()==BottomSheetBehavior.STATE_EXPANDED)
                    textCustomizer.setPreferences(mapView.getFirstData().textPreferences);
                else if (lineCustomizerBehavior.getState()==BottomSheetBehavior.STATE_EXPANDED)
                    lineCustomizer.setPreferences(mapView.getFirstData().linePreferences);
                break;
            case R.id.redo:
                mapView.redo();
                if (nodeCustomizerBehavior.getState()==BottomSheetBehavior.STATE_EXPANDED)
                    nodeCustomizer.setPreferences(mapView.getFirstData().nodePreferences);
                else if (textCustomizerBehavior.getState()==BottomSheetBehavior.STATE_EXPANDED)
                    textCustomizer.setPreferences(mapView.getFirstData().textPreferences);
                else if (lineCustomizerBehavior.getState()==BottomSheetBehavior.STATE_EXPANDED)
                    lineCustomizer.setPreferences(mapView.getFirstData().linePreferences);
                break;
            case R.id.preferences:
                Intent intent = new Intent(this,
                        SettingActivity.class);
                startActivityForResult(intent,0);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        if (nodeCustomizerBehavior.getState()==BottomSheetBehavior.STATE_EXPANDED)
            nodeCustomizerBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
        else if (textCustomizerBehavior.getState()==BottomSheetBehavior.STATE_EXPANDED)
            textCustomizerBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
        else if (lineCustomizerBehavior.getState()==BottomSheetBehavior.STATE_EXPANDED)
            lineCustomizerBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
        else exit();
    }

    private void exit() {
        if (mapView.changed) {
            AlertDialog.Builder alertDialog = new AlertDialog.Builder(MapDrawerActivity.this);
            alertDialog.setMessage("Some changes are not saved. Do you want to exit?");
            alertDialog.setIcon(R.mipmap.ic_launcher);
            alertDialog.setPositiveButton(R.string.no, null);
            alertDialog.setNegativeButton(R.string.yes, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
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

    public void deselect() {
        if (menu != null)
            menu.setVisibility(View.GONE);
        nodeCustomizerBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
        textCustomizerBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
        lineCustomizerBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
    }

    public void select() {
        if (menu != null) {
            if(mapView.isRootSelected())
                btnLine.setVisibility(View.GONE);
            else btnLine.setVisibility(View.VISIBLE);
            menu.setVisibility(View.VISIBLE);
        }
    }

}
