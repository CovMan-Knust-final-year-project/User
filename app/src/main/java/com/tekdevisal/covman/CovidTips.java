package com.tekdevisal.covman;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class CovidTips extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_covid_tips);

        getSupportActionBar().setTitle("Covaid | Tips");
    }
}
