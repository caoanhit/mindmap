package com.uit.ezmind.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import com.uit.ezmind.R;
import com.uit.ezmind.widgets.ColorPickerButton;

public class SettingActivity extends AppCompatActivity {
    int theme;
    int backgroundColor;

    SharedPreferences sharedPreferences;
    ColorPickerButton btnBackground;
    Spinner spnTheme;
    boolean changed;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settting);

        ActionBar bar=getSupportActionBar();
        bar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        bar.setCustomView(R.layout.action_bar);
        bar.setDisplayHomeAsUpEnabled(true);
        bar.setHomeAsUpIndicator(R.drawable.ic_close_black_24dp);

        initViews();

        loadSetting();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.setting_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                reverseSetting();
                setResult(RESULT_CANCELED);
                finish();
                break;
            case R.id.confirm:
                saveSetting();
                finish();
                break;
        }
        return true;
    }

    @Override
    public void onBackPressed() {
        reverseSetting();
        super.onBackPressed();
    }

    private void initViews(){
        sharedPreferences= getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
        btnBackground=findViewById(R.id.btn_background_color);
        spnTheme=findViewById(R.id.theme_options);
        ArrayAdapter<CharSequence> a = ArrayAdapter.createFromResource(this,
                R.array.theme_options, android.R.layout.simple_spinner_item);
        a.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spnTheme.setAdapter(a);
        spnTheme.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                applyTheme(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    private void loadSetting(){
        theme= sharedPreferences.getInt("theme",0);
        spnTheme.setSelection(theme);
        backgroundColor= sharedPreferences.getInt("background_color", 0);
        if (backgroundColor!=0){
            btnBackground.setColor(backgroundColor);
        }
        else btnBackground.setColor(getResources().getColor(R.color.canvas));
    }

    private void reverseSetting(){
        applyTheme(theme);
    }
    private void saveSetting(){
        SharedPreferences.Editor editor=sharedPreferences.edit();
        int t=spnTheme.getSelectedItemPosition();
        if (theme!=t){
            setResult(RESULT_OK);
            editor.putInt("theme",spnTheme.getSelectedItemPosition());
        }
        else setResult(RESULT_CANCELED);
        editor.putInt("background_color", btnBackground.getColor());
        editor.apply();
    }
    private void applyTheme(int theme){
        switch (theme){
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

    }
}
