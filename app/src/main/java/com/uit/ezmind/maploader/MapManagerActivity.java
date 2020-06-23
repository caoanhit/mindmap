package com.uit.ezmind.maploader;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.material.button.MaterialButtonToggleGroup;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.uit.ezmind.R;
import com.uit.ezmind.data.MapData;
import com.uit.ezmind.mapdrawer.MapDrawerActivity;
import com.uit.ezmind.mapdrawer.MapView;
import com.uit.ezmind.utils.LoginActivity;
import com.uit.ezmind.utils.SettingActivity;
import com.uit.ezmind.widgets.MapListAdapter;

import java.util.ArrayList;
import java.util.List;

public class MapManagerActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    GridView gvMap;
    List<MapData> mapList;
    Button bttNewMap;
    Menu menu;
    MapListAdapter adapter;
    MapLoader loader;
    Spinner sortOptionSelector;
    int sortOption;
    private DrawerLayout drawer;
    private ActionBarDrawerToggle toggle;
    NavigationView navigationView;
    private FirebaseAuth mAuth;
    public ProgressDialog progressDialog;
    MapView mapView;

    MaterialButtonToggleGroup btnLayoutSelector;
    int layoutOption;

    SharedPreferences sharedpreferences;

    public static final String MyPREFERENCES = "MyPrefs";
    private static final int FILE_PICKER_REQUEST_CODE = 3;
    public static final int THUMBNAIL_GENERATE_REQUEST_CODE = 506;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map_manager);

        Toolbar toolbar=findViewById(R.id.toolbar_main);
        setSupportActionBar(toolbar);

        drawer=findViewById(R.id.drawer_layout);

        toggle= new ActionBarDrawerToggle(this, drawer,toolbar, R.string.nav_drawer_open,R.string.nav_drawer_close);
        drawer.addDrawerListener(toggle);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_baseline_menu_24);

        navigationView=findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);



        sharedpreferences = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);

        mapView=findViewById(R.id.map_view);
        View btnLogin=navigationView.getHeaderView(0).findViewById(R.id.btn_login);
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(MapManagerActivity.this, LoginActivity.class);
                startActivity(intent);
            }
        });

        mAuth = FirebaseAuth.getInstance();

        initViews();
        int orientation = getResources().getConfiguration().orientation;
        if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
            switch (layoutOption) {
                case 0:
                    gvMap.setNumColumns(1);
                    break;
                case 1:
                    gvMap.setNumColumns(4);
                    break;
            }
        } else {
            switch (layoutOption) {
                case 0:
                    gvMap.setNumColumns(1);
                    break;
                case 1:
                    gvMap.setNumColumns(2);
                    break;
            }
        }
    }

    @Override
    protected void onStart() {
        super.onStart();

        View btnLogin=navigationView.getHeaderView(0).findViewById(R.id.btn_login);
        View accountInfo=navigationView.getHeaderView(0).findViewById(R.id.account_info);
        TextView name=navigationView.getHeaderView(0).findViewById(R.id.nav_header_textView);
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser!=null){
            name.setText(currentUser.getDisplayName());
            btnLogin.setVisibility(View.GONE);
            accountInfo.setVisibility(View.VISIBLE);
            navigationView.getMenu().findItem(R.id.logout).setVisible(true);
        }
        else {
            btnLogin.setVisibility(View.VISIBLE);
            accountInfo.setVisibility(View.GONE);
            navigationView.getMenu().findItem(R.id.logout).setVisible(false);
        }
    }

    private void initViews() {
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
        loader = new MapLoader(this);
        gvMap = findViewById(R.id.lv_map);
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

    @Override
    protected void onResume() {
        super.onResume();
        loadMapNames();
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        for (int g:grantResults) {
            if (g==PackageManager.PERMISSION_GRANTED) {
                loadMapNames();
                return;
            }
        }
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
            switch (layoutOption) {
                case 0:
                    gvMap.setNumColumns(1);
                    break;
                case 1:
                    gvMap.setNumColumns(2);
                    break;
            }
        } else
            switch (layoutOption) {
                case 0:
                    gvMap.setNumColumns(1);
                    break;
                case 1:
                    gvMap.setNumColumns(4);
                    break;
        }
        Log.i("configuration", "changed");
        super.onConfigurationChanged(newConfig);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.mapmanager_action_menu, menu);
        this.menu = menu;
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.import_map:
                Intent intent =new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("*/*");
                startActivityForResult(intent, FILE_PICKER_REQUEST_CODE);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode==FILE_PICKER_REQUEST_CODE){
            if (resultCode== RESULT_OK){
                String path = data.getData().getPath();
                String extension = path.substring(path.lastIndexOf("."));
                if(extension.equals(MapLoader.SAVE_EXTENSION)) Toast.makeText(this,R.string.file_invalid,Toast.LENGTH_SHORT).show();
                else{
                    MapLoader loader=new MapLoader(this);
                    loader.importMap(path);
                }
            }
        }
        else if(requestCode==THUMBNAIL_GENERATE_REQUEST_CODE){
            Log.i("activity result", requestCode+"");
            progressDialog.dismiss();
        }
    }

    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else
        finishAffinity();
    }

    private void loadMapNames() {
        mapList = loader.loadMapList();
        if (mapList != null && mapList.size() > 0) {
            findViewById(R.id.sort_bar).setVisibility(View.VISIBLE);
            findViewById(R.id.tv_empty).setVisibility(View.INVISIBLE);
            if (adapter==null) {
                adapter = new MapListAdapter(this, mapList, layoutOption);
                adapter.sortlist(sortOption);
                gvMap.setAdapter(adapter);
            }
            else {
                if( layoutOption != adapter.getLayout())
                    setLayout();
                adapter.setData(mapList);
                adapter.sortlist(sortOption);
            }
            final String[] m=mapsWithoutThumbnail(mapList);
            if(m.length>0) {
                progressDialog=new ProgressDialog(this);
                progressDialog.setCancelable(false);
                progressDialog.setTitle(R.string.generate_thumbnail);
                progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                progressDialog.setMessage(getText(R.string.please_wait));
                progressDialog.show();
                mapView.makeThumbnails(m);
            }
        } else setEmpty();
    }

    private String[] mapsWithoutThumbnail(List<MapData> data){
        List<String> l=new ArrayList<>();
        int count =0;
        for (MapData d: data) {
            if (d.thumbnail==null){
                l.add(d.name);
                count++;
            }
        }
        String[] arr=new String[count];
        return l.toArray(arr);
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
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                return true;
            case R.id.preferences:
                Intent intent = new Intent(MapManagerActivity.this,
                        SettingActivity.class);
                startActivityForResult(intent, 0);
                break;
            case R.id.logout:
                FirebaseAuth.getInstance().signOut();
                Intent i= new Intent(MapManagerActivity.this,
                        LoginActivity.class);
                startActivityForResult(i, 0);
                break;
        }
        drawer.closeDrawer(GravityCompat.START);
        return false;
    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }

    private void setUserInfo(String name, @Nullable Bitmap bitmap){
        TextView username= navigationView.getHeaderView(0).findViewById(R.id.nav_header_textView);
        username.setText(name);
        if(bitmap!=null){
            ImageView icon=navigationView.getHeaderView(0).findViewById(R.id.nav_header_imageView);
            icon.setImageBitmap(bitmap);
        }
    }
    public void endThumbnailLoading(){
        progressDialog.dismiss();
        loadMapNames();
    }
}
