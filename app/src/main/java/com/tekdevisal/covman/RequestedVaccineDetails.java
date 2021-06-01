package com.tekdevisal.covman;

import android.content.Context;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.FirebaseDatabase;
import com.tekdevisal.covman.Helpers.Accessories;
import com.tekdevisal.covman.Helpers.Functions;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class RequestedVaccineDetails extends AppCompatActivity implements OnMapReadyCallback {

    private String                      user_id,request_id,firstname, lastname, latitude, longitude,
                                        phone_number, date, user_address, add;

    private Accessories                 request_accessor;

    private GoogleMap                   mMap;

    private TextView                    fullnameTextview, phonenumber_textview, time_textview, address_textview;

    private Snackbar                    snackbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_requested_vaccine_details);

        getSupportActionBar().setTitle("CovMan | Request Details");

        request_accessor = new Accessories(this);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        user_id         = request_accessor.getString("user_id");
        request_id      = request_accessor.getString("request_id");
        firstname       = request_accessor.getString("v_fname");
        lastname        = request_accessor.getString("v_lname");
        latitude        = request_accessor.getString("v_latitude");
        longitude       = request_accessor.getString("v_longitude");
        phone_number    = request_accessor.getString("v_phone_number");
        date            = request_accessor.getString("v_date");

        fullnameTextview = findViewById(R.id.patient_name);
        phonenumber_textview = findViewById(R.id.patient_phone);
        address_textview =findViewById(R.id.address);
        time_textview =findViewById(R.id.thetime);

        fullnameTextview.setText(firstname + " " + lastname);
        phonenumber_textview.setText(phone_number);
        time_textview.setText(request_accessor.OneMinuteAgoTimeFormat(date));

        getAddress(Double.parseDouble(latitude), Double.parseDouble(longitude),address_textview);

        findViewById(R.id.call_patient_layout).setOnClickListener(v -> request_accessor.openDialer(v, phone_number));

        findViewById(R.id.message_patient_layout).setOnClickListener(v -> Toast.makeText(RequestedVaccineDetails.this, "Messaging functionality would be added soon", Toast.LENGTH_LONG).show());

        findViewById(R.id.dismiss_patient_layout).setOnClickListener(v -> {
            final AlertDialog.Builder logout = new AlertDialog.Builder(RequestedVaccineDetails.this, R.style.Myalert);
            logout.setTitle("Dismiss Request?");
            logout.setMessage("Are you sure you want to dismiss this request from "+ firstname + " " + lastname);
            logout.setNegativeButton("Confirm", (dialog, which) -> {
                if(request_accessor.isNetworkAvailable()){
                    RemoveReport(user_id,request_id);
                }else{
                    new Functions(this).showAlertDialogueWithOK("No internet connection");
                }
            });

            logout.setPositiveButton("Cancel", (dialog, which) -> dialog.cancel());
            logout.show();
        });
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Add a marker in Sydney, Australia, and move the camera.
        LatLng user_location = new LatLng(Double.parseDouble(latitude), Double.parseDouble(longitude));
        CameraPosition cameraPosition = new CameraPosition.Builder()
                .target(user_location)      // Sets the center of the map to Mountain View
                .zoom(17)                   // Sets the zoom
                .bearing(90)                // Sets the orientation of the camera to east
                .tilt(30)                   // Sets the tilt of the camera to 30 degrees
                .build();                   // Creates a CameraPosition from the builder
        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
        mMap.addMarker(new MarkerOptions().position(user_location).title("Patient"));
    }

    public void RemoveReport(String the_patient_id,String the_report_id){
        FirebaseDatabase.getInstance().getReference("vaccines").child(the_patient_id)
                .removeValue().addOnCompleteListener(task -> {
            if(task.isSuccessful()){
                new Functions(this).showAlertDialogueWithOK("Request Deleted");
                startActivity(new Intent(RequestedVaccineDetails.this, Reports.class));
                finish();
            }
        });
    }

    public void getAddress(double lat, double lng, TextView addressView) {
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        try {
            List<Address> addresses = geocoder.getFromLocation(lat, lng, 1);
            Address obj = addresses.get(0);
            add = obj.getAddressLine(0);

            addressView.setText(add);


        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }
}