package com.tekdevisal.covman;

import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.tekdevisal.covman.Adapters.ScansAdapter;
import com.tekdevisal.covman.Adapters.VaccinesAdapter;
import com.tekdevisal.covman.Helpers.Accessories;
import com.tekdevisal.covman.Helpers.Functions;
import com.tekdevisal.covman.Helpers.Urls;
import com.tekdevisal.covman.Models.Report_Model;
import com.tekdevisal.covman.Models.ScansModel;
import com.tekdevisal.covman.Models.VaccinesModel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class VaccinesRequestedFor extends AppCompatActivity {

    //users recycler initializations
    private RecyclerView                     _recycler;
    private RecyclerView.Adapter            _adapter;
    private RecyclerView.LayoutManager      _layout;
    private ArrayList<VaccinesModel>          _list;

    private Accessories                     vaccines_accesssor;

    private ProgressBar                     loading;

    private String                          date, fname, lname, latitude, longitude, user_id, phone_number;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vaccines_requested_for);


        vaccines_accesssor   = new Accessories(this);

        loading              = findViewById(R.id.loading);

        findViewById(R.id.goback).setOnClickListener(view -> {
            finish();
        });

        initializeRecyclerView();

        if(vaccines_accesssor.isNetworkAvailable()){
            new FetchRequestedVaccines().execute();
        }else{
            new Functions(this).showAlertDialogueWithOK("No internet connection");
        }
    }

    private void initializeRecyclerView() {
        _list       = new ArrayList<>();
        _recycler   = findViewById(R.id.recyclerView);
        _recycler.setNestedScrollingEnabled(false);
        _recycler.setHasFixedSize(false);
        _layout     = new LinearLayoutManager(getApplicationContext(), LinearLayout.VERTICAL, false);
        _recycler.setLayoutManager(_layout);
        _adapter    = new VaccinesAdapter(_list,getApplicationContext());
        _recycler.setAdapter(_adapter);
    }

    private class FetchRequestedVaccines extends AsyncTask<String, String, String> {

        @Override
        protected void onPreExecute() {
            loading.setVisibility(View.VISIBLE);
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... strings) {
            DatabaseReference vaccines = FirebaseDatabase.getInstance()
                    .getReference("vaccines");
            vaccines.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if(dataSnapshot.exists()){
                        for(DataSnapshot child : dataSnapshot.getChildren()){
                            FetchVaccineData(child.getKey());
                        }
                    }
                }

                private void FetchVaccineData(String vaccine_request_code) {
                    DatabaseReference vacines = FirebaseDatabase.getInstance().getReference("vaccines")
                            .child(vaccine_request_code);
                    vacines.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            if(dataSnapshot.exists()){
                                for(DataSnapshot child : dataSnapshot.getChildren()){
                                    if(child.getKey().equals("date")){
                                        date = child.getValue().toString();

                                    }
                                    if(child.getKey().equals("first_name")){
                                        fname = child.getValue().toString();
                                    }

                                    if(child.getKey().equals("last_name")){
                                        lname = child.getValue().toString();
                                    }

                                    if(child.getKey().equals("latitude")){
                                        latitude = child.getValue().toString();
                                    }

                                    if(child.getKey().equals("longitude")){
                                        longitude = child.getValue().toString();
                                    }

                                    if(child.getKey().equals("phone_number")){
                                        phone_number = child.getValue().toString();
                                    }

                                    if(child.getKey().equals("user_id")){
                                        user_id = child.getValue().toString();
                                    }
                                }
                                VaccinesModel obj = new VaccinesModel(vaccine_request_code,date,
                                        fname, lname, latitude, longitude, phone_number, user_id);
                                _list.add(obj);
                                try {
                                    _adapter.notifyDataSetChanged();
                                }catch (ClassCastException e){
                                    e.printStackTrace();
                                }
                                catch (NullPointerException e){
                                    e.printStackTrace();
                                }
                                catch (IndexOutOfBoundsException e){
                                    e.printStackTrace();
                                }

                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            findViewById(R.id.no_requests).setVisibility(View.GONE);
            loading.setVisibility(View.GONE);
        }
    }

}