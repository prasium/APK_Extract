package com.prasium.apkextract;

import
        android.content.DialogInterface;
import android.content.res.Configuration;
import android.os.Bundle;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;;

import android.view.View;
import android.widget.TextView;

public class Settings extends AppCompatActivity {
    int checkedItem;
    TextView textView ;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        ActionBar actionBar = getSupportActionBar();
        if(actionBar!=null)
        actionBar.setTitle("Settings");
        textView=findViewById(R.id.apptheme);
        switch (getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK){
            case Configuration.UI_MODE_NIGHT_YES:
                textView.setText("Dark");
                checkedItem=1;
                break;
            case Configuration.UI_MODE_NIGHT_NO:
                textView.setText("Light");
                checkedItem=0;
                break;
        }
    }

    public void themechanger(View view) {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(Settings.this);
        alertDialog.setTitle("Select Theme");
        String[] items = {"Light","Dark"};
        alertDialog.setSingleChoiceItems(items, checkedItem, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case 0:
                        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                        textView.setText(R.string.light);
                        checkedItem=0;
                        break;
                    case 1:
                        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                        textView.setText(R.string.dark);
                        checkedItem=1;
                        break;
                }
            }
        });
        AlertDialog alert = alertDialog.create();
        alert.setCanceledOnTouchOutside(false);
        alert.show();
    }
}