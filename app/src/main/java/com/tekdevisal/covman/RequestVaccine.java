package com.tekdevisal.covman;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Location;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;

import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.tekdevisal.covman.Helpers.Accessories;
import com.tekdevisal.covman.Helpers.Functions;
import com.tekdevisal.covman.Helpers.Urls;
import com.tekdevisal.covman.LocationUtil.LocationHelper;
import com.tekdevisal.covman.Models.AttendanceModel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class RequestVaccine extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener, ActivityCompat.OnRequestPermissionsResultCallback{

    private LocationHelper      locationHelper;

    private Location            myLocation;

    private double              latitudeD,longitudeD;

    private FirebaseAuth        myauth;

    private DatabaseReference   reference;

    private Accessories         request_accessor;

    private Functions           functions;

    private Snackbar            snackbar;

    private ProgressDialog       progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_request_vaccine);

        findViewById(R.id.goback).setOnClickListener(view -> {
            finish();
        });

        request_accessor = new Accessories(this);

        functions        = new Functions(this);

        myauth           = FirebaseAuth.getInstance();

        progressBar      = new ProgressDialog(this);

        locationHelper = new LocationHelper(RequestVaccine.this);
        locationHelper.checkpermission();

        if(locationHelper.checkPlayServices()){
            locationHelper.buildGoogleApiClient();
        }

        findViewById(R.id.request_vaccine).setOnClickListener(view -> {
            if(request_accessor.isNetworkAvailable()){
                AlertDialog.Builder alert = new AlertDialog.Builder(this);
                alert.setMessage("By Clicking proceed, you agree to the risks involved in taking the COVID-19 vaccine. \n\nVaccine would be delivered to your current location.\n\nDo you wish to proceed?");
                alert.setPositiveButton("Proceed", (dialogInterface, i) -> {
                    new RequestForVaccine(request_accessor.getString("user_id"), request_accessor.getString("first_name"),
                            request_accessor.getString("last_name"), request_accessor.getString("raw_phone_number"),
                            myLocation, new Date().toString()).execute();
                });
                alert.setNegativeButton("Cancel", (dialogInterface, i) -> dialogInterface.dismiss());
                alert.show();
            }
            else{
                functions.showAlertDialogueWithOK("No internet connection");
            }
        });
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        myLocation = locationHelper.getLocation();
    }

    @Override
    public void onConnectionSuspended(int i) {
        locationHelper.connectApiClient();
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.i("Connection Failure","Connnection Error = " +
                connectionResult.getErrorCode());
    }

    @Override
    protected void onResume() {
        super.onResume();
        locationHelper.checkPlayServices();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
        locationHelper.onActivityResult(requestCode,resultCode,data);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
//        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        locationHelper.onRequestPermissionsResult(requestCode,permissions,grantResults);
    }

    private class RequestForVaccine extends AsyncTask<String, String, String> {
        String userid;
        String first_name;
        String last_name;
        String phone_number;
        Location myLocation;
        String date;

        public RequestForVaccine(String userid, String first_name, String last_name, String phone_number, Location myLocation, String date) {
            this.userid = userid;
            this.first_name = first_name;
            this.last_name = last_name;
            this.phone_number = phone_number;
            this.myLocation = myLocation;
            this.date = date;
        }

        @Override
        protected void onPreExecute() {
            //TODO: place progress bar here
            progressBar.setTitle("Saving username");
            progressBar.setMessage("Please Wait...");
            progressBar.setCanceledOnTouchOutside(false);
            progressBar.show();
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... strings) {
            reference = FirebaseDatabase.getInstance().getReference("vaccines")
                    .child(myauth.getCurrentUser().getUid());
            if(myLocation != null){

                final HashMap<String, Object> report_ = new HashMap<>();
                report_.put("user_id", userid);
                report_.put("first_name", first_name);
                report_.put("last_name", last_name);
                report_.put("phone_number", phone_number);
                report_.put("latitude", myLocation.getLatitude());
                report_.put("longitude", myLocation.getLongitude());
                report_.put("date", request_accessor.timeStamp());

                reference.setValue(report_).addOnCompleteListener(task -> {
                    if(task.isSuccessful()){
                        functions.showAlertDialogueWithOK("Order has been placed. We will get back to you shortly");
                    }
                });
            }else {
                functions.showSnackBar("Location not found");
            }
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            //TODO: dismiss progress dialogue
            progressBar.dismiss();
        }
    }

}