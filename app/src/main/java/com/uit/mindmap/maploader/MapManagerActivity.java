package com.uit.mindmap.maploader;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toolbar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.uit.mindmap.R;
import com.uit.mindmap.mapdrawer.MapDrawerActivity;

public class MapManagerActivity extends AppCompatActivity {
    ListView lvMap;
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map_manager);

        if (ContextCompat.checkSelfPermission(this,Manifest.permission.READ_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED);
        else  ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
        if (ContextCompat.checkSelfPermission(this,Manifest.permission.WRITE_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED);
        else  ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);

        Button bttNewMap= (Button)findViewById(R.id.new_map);
        bttNewMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent= new Intent(MapManagerActivity.this, MapDrawerActivity.class);
                startActivity(intent);
            }
        });
        MapLoader loader=new MapLoader();
        final String[] names=loader.getSavedMapsName();
        if(names.length>0) {
            findViewById(R.id.tv_empty).setVisibility(View.INVISIBLE);
            lvMap = (ListView) findViewById(R.id.lv_map);
            ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, names);

            lvMap.setAdapter(adapter);
            lvMap.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Intent intent = new Intent(MapManagerActivity.this, MapDrawerActivity.class);
                    intent.putExtra("mapName", names[position]);
                    startActivity(intent);
                }
            });
        }
        else findViewById(R.id.tv_empty).setVisibility(View.VISIBLE);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.mapmanager_action_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.refresh:
                MapLoader loader=new MapLoader();
                final String[] names=loader.getSavedMapsName();
                ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1,names);
                lvMap.setAdapter(adapter);
        }
        return super.onOptionsItemSelected(item);
    }
}
