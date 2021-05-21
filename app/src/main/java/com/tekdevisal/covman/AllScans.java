package com.tekdevisal.covman;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class AllScans extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_scans);

        findViewById(R.id.goback).setOnClickListener(view -> {
            finish();
        });
    }
}