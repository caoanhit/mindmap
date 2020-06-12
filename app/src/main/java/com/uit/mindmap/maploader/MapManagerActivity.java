package com.uit.mindmap.maploader;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
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

import com.google.android.material.button.MaterialButtonToggleGroup;
import com.uit.mindmap.R;
import com.uit.mindmap.data.MapData;
import com.uit.mindmap.mapdrawer.MapDrawerActivity;
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
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map_manager);

        sharedpreferences = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);

        initViews();
    }

    private void initViews() {
        loadMapNames();
        sortOptionSelector = findViewById(R.id.sort_options);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.sort_options, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sortOptionSelector.setAdapter(adapter);
        sortOption = sharedpreferences.getInt("sort", 0);
        sortOptionSelector.setSelection(sortOption);

        sortOptionSelector.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                SharedPreferences.Editor editor = sharedpreferences.edit();
                sortOption=position;
                sortMapList();
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
        switch (layoutOption=sharedpreferences.getInt("layout", 0)) {
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
                if(isChecked) {
                    switch (checkedId) {
                        case R.id.list:
                            layoutOption = 0;
                            break;
                        case R.id.card:
                            layoutOption = 1;
                            break;
                    }
                    setLayout();
                    SharedPreferences.Editor editor = sharedpreferences.edit();
                    editor.putInt("layout", layoutOption);
                    editor.apply();
                }
            }
        });
    }

    @Override
    protected void onResume() {
        mapList = loader.loadMapList();
        sortMapList();
        setLayout();
        adapter.setData(mapList);
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
        super.onResume();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.mapmanager_action_menu, menu);
        this.menu = menu;
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

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

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if (requestCode == 1) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                loadMapNames();
        }
    }

    private void loadMapNames() {
        loader = new MapLoader(this);
        mapList = loader.loadMapList();
        gvMap = findViewById(R.id.lv_map);
        if (mapList != null && mapList.size() > 0) {
            findViewById(R.id.sort_bar).setVisibility(View.VISIBLE);
            findViewById(R.id.tv_empty).setVisibility(View.INVISIBLE);
            adapter = new MapListAdapter(this, mapList, layoutOption);
            sortMapList();
            gvMap.setAdapter(adapter);
        } else setEmpty();
        switch (layoutOption){
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
                startActivity(intent);
            }
        });
    }

    public void setEmpty() {
        findViewById(R.id.sort_bar).setVisibility(View.GONE);
        findViewById(R.id.tv_empty).setVisibility(View.VISIBLE);
    }

    private void sortMapList() {
        mapList.sort(new Comparator<MapData>() {
            @Override
            public int compare(MapData o1, MapData o2) {
                switch (sortOption){
                    case 0: return o1.name.compareTo(o2.name);
                    case 1: return o2.name.compareTo(o1.name);
                    case 2: return (o1.date>o2.date)? -1: 1;
                    case 3: return (o1.date>o2.date)? 1: -1;
                }
                return o1.name.compareTo(o2.name);
            }
        });
    }
    private void setLayout(){
        switch (layoutOption){
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
