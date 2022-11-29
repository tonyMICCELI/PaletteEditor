package com.example.paletteeditor;

import androidx.appcompat.app.AppCompatActivity;

import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.Arrays;

public class PaletteActivity extends AppCompatActivity {

    private String[] paletteArrayString;

    private ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_palette);
        loadData();
        displayData();
    }

    public void loadData() {
        SharedPreferences sharedPreferences = getSharedPreferences(MainActivity.SHARED_PREFS_COLOR, MODE_PRIVATE);
        String paletteString = sharedPreferences.getString(MainActivity.COLORS_STRING_LIST, "");
        paletteArrayString = paletteString.split("\n");
    }

    public void displayData() {
        listView = (ListView) findViewById(R.id.paletteList);
        final ArrayAdapter arrayAdapter = new ArrayAdapter(getApplicationContext(), android.R.layout.simple_list_item_1,paletteArrayString) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                View view = super.getView(position, convertView, parent);
                ViewGroup.LayoutParams params = view.getLayoutParams();
                view.setLayoutParams(params);
                TextView textview = (TextView) view;
                textview.setTextSize(35);
                textview.setBackgroundColor(Color.parseColor(textview.getText().toString()));
                return textview;
            }
        };
        listView.setAdapter(arrayAdapter);

    }
}