package com.tekdevisal.covman;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class Attendance extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_attendance);

        findViewById(R.id.goback).setOnClickListener(view -> {
            finish();
        });
    }
}