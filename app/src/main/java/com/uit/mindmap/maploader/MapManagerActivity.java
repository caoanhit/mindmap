package com.uit.mindmap.maploader;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
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

import java.util.ArrayList;
import java.util.Arrays;

public class MapManagerActivity extends AppCompatActivity {
    ListView lvMap;
    Button bttNewMap;
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map_manager);

        if (ContextCompat.checkSelfPermission(this,Manifest.permission.READ_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED);
        else  ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE,Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
        loadMapNames();
        bttNewMap= (Button)findViewById(R.id.new_map);
        bttNewMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                        WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                Intent intent= new Intent(MapManagerActivity.this, MapDrawerActivity.class);
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
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.refresh:
                loadMapNames();
        }
        return super.onOptionsItemSelected(item);
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode==1){
            if (grantResults.length>0 && grantResults[0]==PackageManager.PERMISSION_GRANTED)
                loadMapNames();
        }
    }
    private  void loadMapNames(){
        MapLoader loader=new MapLoader(this);
        final String[] names=loader.getSavedMapsName();
        if(names!=null && names.length>0) {
            findViewById(R.id.tv_empty).setVisibility(View.INVISIBLE);
            lvMap = (ListView) findViewById(R.id.lv_map);
            final MapListAdapter adapter = new MapListAdapter(this, new ArrayList<String>(Arrays.asList(names)));

            lvMap.setAdapter(adapter);
            lvMap.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                            WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                    Intent intent = new Intent(MapManagerActivity.this, MapDrawerActivity.class);
                    intent.putExtra("mapName",(String) adapter.getItem(position));
                    startActivity(intent);
                }
            });
        }
        else findViewById(R.id.tv_empty).setVisibility(View.VISIBLE);
    }
    public void showEmptyText(){
        findViewById(R.id.tv_empty).setVisibility(View.VISIBLE);
    }
}
