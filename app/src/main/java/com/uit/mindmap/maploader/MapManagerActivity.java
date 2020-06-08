package com.uit.mindmap.maploader;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.uit.mindmap.R;
import com.uit.mindmap.mapdrawer.MapDrawerActivity;
import com.uit.mindmap.widgets.MapListAdapter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MapManagerActivity extends AppCompatActivity {
    ListView lvMap;
    List<String> mapNames;
    Button bttNewMap;
    Menu menu;
    MapListAdapter adapter;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map_manager);

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)
            ;
        else
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);

        loadMapNames();
        bttNewMap = (Button) findViewById(R.id.new_map);
        bttNewMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                        WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                Intent intent = new Intent(MapManagerActivity.this, MapDrawerActivity.class);
                startActivity(intent);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
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
        if (lvMap.getChoiceMode()==AbsListView.CHOICE_MODE_MULTIPLE){
            lvMap.setChoiceMode(AbsListView.CHOICE_MODE_NONE);
            lvMap.clearChoices();
            adapter.setSelectMode(false);
            menu.setGroupVisible(R.id.map_option,false);
        }
        else super.onBackPressed();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == 1) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                loadMapNames();
        }
    }

    private void loadMapNames() {
        MapLoader loader = new MapLoader(this);
        mapNames = new ArrayList<>(Arrays.asList(loader.getSavedMapsName()));
        lvMap = findViewById(R.id.lv_map);
        if (mapNames != null && mapNames.size() > 0) {
            sortNameList();
            findViewById(R.id.tv_empty).setVisibility(View.INVISIBLE);
            adapter = new MapListAdapter(this, mapNames);
            lvMap.setAdapter(adapter);
        } else findViewById(R.id.tv_empty).setVisibility(View.VISIBLE);
        lvMap.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                            WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                    Intent intent = new Intent(MapManagerActivity.this, MapDrawerActivity.class);
                    intent.putExtra("mapName", (String) adapter.getItem(position));
                    startActivity(intent);
            }
        });
//        lvMap.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
//            @Override
//            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
//                if (lvMap.getChoiceMode() == AbsListView.CHOICE_MODE_NONE) {
//                    lvMap.setChoiceMode(AbsListView.CHOICE_MODE_MULTIPLE);
//                    adapter.setSelectMode(true);
//                    lvMap.setSelection(position);
//                    menu.setGroupVisible(R.id.map_option,true);
//                }
//                return true;
//            }
//        });
    }

    public void setEmpty() {
        findViewById(R.id.tv_empty).setVisibility(View.VISIBLE);
    }

    private void sortNameList() {
    }
}
