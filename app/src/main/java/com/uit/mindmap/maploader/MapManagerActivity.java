package com.uit.mindmap.maploader;

import android.content.Intent;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.uit.mindmap.R;
import com.uit.mindmap.mapdrawer.MapDrawerActivity;

public class MapManagerActivity extends AppCompatActivity {
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState, @Nullable PersistableBundle persistentState) {
        super.onCreate(savedInstanceState, persistentState);
        setContentView(R.layout.activity_main);

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
        ListView lvMap= (ListView) findViewById(R.id.lv_map);
        ArrayAdapter<String> adapter =new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1,names);

        lvMap.setAdapter(adapter);
        lvMap.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent= new Intent(MapManagerActivity.this, MapDrawerActivity.class);
                intent.putExtra("mapName",names[position]);
                startActivity(intent);
            }
        });
    }
}
