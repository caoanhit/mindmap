package com.uit.mindmap.maploader;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ListView;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import com.google.android.material.button.MaterialButtonToggleGroup;
import com.uit.mindmap.R;
import com.uit.mindmap.data.MapData;
import com.uit.mindmap.mapdrawer.MapDrawerActivity;
import com.uit.mindmap.utils.SettingActivity;
import com.uit.mindmap.widgets.MapListAdapter;

import java.util.Comparator;
import java.util.List;

public class MapManagerActivity extends AppCompatActivity {
    GridView gvMap;
    List<MapData> mapList;
    Button bttNewMap;
    Menu menu;
    MapListAdapter adapter;
    MapLoader loader;
    Spinner sortOptionSelector;
    int sortOption;

    MaterialButtonToggleGroup btnLayoutSelector;
    int layoutOption;

    SharedPreferences sharedpreferences;

    public static final String MyPREFERENCES = "MyPrefs";

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        setTheme(R.style.AppDark);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map_manager);

        sharedpreferences = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);

        int theme = sharedpreferences.getInt("theme", 0);
        switch (theme) {
            case 0:
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);
                break;
            case 1:
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                break;
            case 2:
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                break;
        }

        initViews();
    }

    private void initViews() {
        loadMapNames();
        sortOptionSelector = findViewById(R.id.sort_options);
        ArrayAdapter<CharSequence> a = ArrayAdapter.createFromResource(this,
                R.array.sort_options, android.R.layout.simple_spinner_item);
        a.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sortOptionSelector.setAdapter(a);
        sortOption = sharedpreferences.getInt("sort", 0);
        sortOptionSelector.setSelection(sortOption);

        sortOptionSelector.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                SharedPreferences.Editor editor = sharedpreferences.edit();
                sortOption = position;
                adapter.sortlist(sortOption);
                editor.putInt("sort", position);
                editor.apply();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        bttNewMap = findViewById(R.id.new_map);
        bttNewMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                        WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                Intent intent = new Intent(MapManagerActivity.this,
                        MapDrawerActivity.class);
                startActivity(intent);
            }
        });

        btnLayoutSelector = findViewById(R.id.layout_options);
        switch (layoutOption = sharedpreferences.getInt("layout", 0)) {
            case 0:
                btnLayoutSelector.check(R.id.list);
                break;
            case 1:
                btnLayoutSelector.check(R.id.card);
                break;
        }
        btnLayoutSelector.addOnButtonCheckedListener(new MaterialButtonToggleGroup.OnButtonCheckedListener() {
            @Override
            public void onButtonChecked(MaterialButtonToggleGroup group, int checkedId, boolean isChecked) {
                if (isChecked) {
                    switch (checkedId) {
                        case R.id.list:
                            layoutOption = 0;
                            break;
                        case R.id.card:
                            layoutOption = 1;
                            break;
                    }
                    setLayout();
                    adapter.notifyDataSetChanged();
                    SharedPreferences.Editor editor = sharedpreferences.edit();
                    editor.putInt("layout", layoutOption);
                    editor.apply();
                }
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        mapList = loader.loadMapList();
        if (layoutOption != adapter.getLayout())
            setLayout();
        adapter.setData(mapList);
        adapter.sortlist(sortOption);
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        // Checks the orientation of the screen
        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            switch (layoutOption) {
                case 0:
                    gvMap.setNumColumns(1);
                    break;
                case 1:
                    gvMap.setNumColumns(4);
                    break;
            }
        } else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT){
            switch (layoutOption) {
                case 0:
                    gvMap.setNumColumns(1);;
                    break;
                case 1:
                    gvMap.setNumColumns(2);
                    break;
            }
        }
    }

//
//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
//        if (resultCode == RESULT_OK)
//            recreate();
//        super.onActivityResult(requestCode, resultCode, data);
//    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.mapmanager_action_menu, menu);
        this.menu = menu;
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.preferences:
                Intent intent = new Intent(MapManagerActivity.this,
                        SettingActivity.class);
                startActivityForResult(intent, 0);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        if (gvMap.getChoiceMode() == AbsListView.CHOICE_MODE_MULTIPLE) {
            gvMap.setChoiceMode(AbsListView.CHOICE_MODE_NONE);
            gvMap.clearChoices();
            adapter.setSelectMode(false);
            menu.setGroupVisible(R.id.map_option, false);
        } else super.onBackPressed();
    }

    private void loadMapNames() {
        loader = new MapLoader(this);
        mapList = loader.loadMapList();
        gvMap = findViewById(R.id.lv_map);
        if (mapList != null && mapList.size() > 0) {
            findViewById(R.id.sort_bar).setVisibility(View.VISIBLE);
            findViewById(R.id.tv_empty).setVisibility(View.INVISIBLE);
            adapter = new MapListAdapter(this, mapList, layoutOption);
            adapter.sortlist(sortOption);
            gvMap.setAdapter(adapter);
        } else setEmpty();
        switch (layoutOption) {
            case 0:
                gvMap.setNumColumns(1);
                break;
            case 1:
                gvMap.setNumColumns(2);
                break;
        }
        gvMap.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                        WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                Intent intent = new Intent(MapManagerActivity.this,
                        MapDrawerActivity.class);
                intent.putExtra("mapName", ((MapData) adapter.getItem(position)).name);
                startActivityForResult(intent, 0);
            }
        });
    }

    public void setEmpty() {
        findViewById(R.id.sort_bar).setVisibility(View.GONE);
        findViewById(R.id.tv_empty).setVisibility(View.VISIBLE);
    }

    private void setLayout() {
        int orientation = getResources().getConfiguration().orientation;
        if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
            switch (layoutOption) {
                case 0:
                    gvMap.setNumColumns(1);
                    adapter.changeLayout(0);
                    break;
                case 1:
                    gvMap.setNumColumns(4);
                    adapter.changeLayout(1);
                    break;
            }
        } else {
            switch (layoutOption) {
                case 0:
                    gvMap.setNumColumns(1);
                    adapter.changeLayout(0);
                    break;
                case 1:
                    gvMap.setNumColumns(2);
                    adapter.changeLayout(1);
                    break;
            }
        }

    }
}
