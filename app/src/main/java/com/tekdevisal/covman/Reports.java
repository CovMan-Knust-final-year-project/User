package com.tekdevisal.covman;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.tekdevisal.covman.Adapters.ReportAdapter;
import com.tekdevisal.covman.Models.Report_Model;

import java.util.ArrayList;

public class Reports extends AppCompatActivity {

    private ArrayList               reportArray = new ArrayList<Report_Model>();

    private RecyclerView            report_RecyclerView;

    private RecyclerView.Adapter    report_Adapter;

    private TextView                no_reports;

    private Snackbar                snackbar;

    private String                  title, message, phone_number, symptoms;

    private Double                  latitude_, longitude_;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reports);

        findViewById(R.id.goback).setOnClickListener(view -> finish());

        report_RecyclerView = findViewById(R.id.report_recyclerView);
        no_reports = findViewById(R.id.no_case);

        if(isNetworkAvailable()){
            PersonWhoUploadedID();
            report_RecyclerView.setHasFixedSize(true);
            report_Adapter = new ReportAdapter(getFromDatabase(),Reports.this);
            report_RecyclerView.setAdapter(report_Adapter);
        }else{
            snackbar = Snackbar.make(findViewById(android.R.id.content),
                    "No internet connection", Snackbar.LENGTH_LONG);
            snackbar.show();
        }
    }

    private void PersonWhoUploadedID() {
            try {
                DatabaseReference person_id = FirebaseDatabase.getInstance().getReference("reports");
                person_id.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if(dataSnapshot.exists()){
                            for(DataSnapshot child : dataSnapshot.getChildren()){
                                fetchReportsIDs(child.getKey());
                            }
                        }else{

                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }catch (NullPointerException e){

            }
    }

    private void fetchReportsIDs(String key) {
        try {
            DatabaseReference report_id = FirebaseDatabase.getInstance().getReference("reports")
                    .child(key);
            report_id.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if(dataSnapshot.exists()){
                        for(DataSnapshot child : dataSnapshot.getChildren()){
                            fetchReports(key,child.getKey());
                        }
                    }else{

                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }catch (NullPointerException e){

        }
    }

    private void fetchReports(String person_who_uploaded_id,String report_key) {
        try {
            no_reports.setVisibility(View.GONE);
            DatabaseReference fetch_report =
                    FirebaseDatabase.getInstance().getReference("reports")
                            .child(person_who_uploaded_id).child(report_key);
            fetch_report.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if(dataSnapshot.exists()){
                        for(DataSnapshot child : dataSnapshot.getChildren()){
                            if(child.getKey().equals("title")){
                                title = child.getValue().toString();

                            }
                            if(child.getKey().equals("message")){
                                message = child.getValue().toString();
                            }

                            if(child.getKey().equals("latitude")){
                                latitude_ = Double.valueOf(child.getValue().toString());
                            }

                            if(child.getKey().equals("longitude")){
                                longitude_ = Double.valueOf(child.getValue().toString());
                            }

                            if(child.getKey().equals("phone_number")){
                                phone_number = child.getValue().toString();
                            }

                            if(child.getKey().equals("symptoms")){
                                symptoms = child.getValue().toString();
                            }

                            else{
//                            Toast.makeText(getActivity(),"Couldn't fetch posts",Toast.LENGTH_LONG).show();
                            }
                        }
                        LatLng user_location = new LatLng(latitude_,longitude_);
                        Report_Model obj = new Report_Model(report_key,person_who_uploaded_id,
                                title, message, user_location,phone_number, symptoms);
                        reportArray.add(obj);
                        report_RecyclerView.setAdapter(report_Adapter);
                        report_Adapter.notifyDataSetChanged();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Toast.makeText(Reports.this,"Cancelled",Toast.LENGTH_LONG).show();

                }
            });
        }catch (NullPointerException e){

        }
    }

    public ArrayList<Report_Model> getFromDatabase(){
        return reportArray;
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

}
