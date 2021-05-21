package com.tekdevisal.covman;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

public class ProfileActivity extends AppCompatActivity {

    private Spinner                             level_spinner;

    private String[]                            levels = {"Select level","100", "200", "300", "400", "500", "600", "Other"};

    private String                              slevel;

    private ArrayAdapter<String>                tutor_type;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        findViewById(R.id.goback).setOnClickListener(view -> {
            finish();
        });

        level_spinner = findViewById(R.id.level);

        //level type spinner
        level_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                slevel = levels[position];
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                slevel = "Other";
            }
        });
        tutor_type = new ArrayAdapter<String>(this,R.layout.spinner_layout,levels);
        level_spinner.setAdapter(tutor_type);

    }
}