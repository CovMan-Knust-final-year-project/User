package com.tekdevisal.covman;

import android.app.ProgressDialog;
import android.content.Intent;
import android.location.Location;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.tekdevisal.covman.Helpers.Accessories;
import com.tekdevisal.covman.Helpers.Functions;
import com.tekdevisal.covman.LocationUtil.LocationHelper;

import java.util.HashMap;
import java.util.function.Function;

public class Report_ extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,ActivityCompat.OnRequestPermissionsResultCallback{

    private String                  title, message, location_address, symptoms = "";

    private EditText                title_edittext, message_edittext;

    private Snackbar                snackbar;

    private LocationHelper          locationHelper;

    private Location                myLocation;

    private double                  latitudeD,longitudeD;

    private FirebaseAuth            myauth;

    private DatabaseReference       reference;

    private CheckBox                drycough_checkbox, fever_checkbox, tiredness_checkbox,
                                    aches_checkbox, sorethroat_checkbox, diarrheoa_checkbox,
                                    conjunct_checkbox, headache, loss_of_taste, rash_checkbox,
                                    difficulty_breathing, chest_pain, loss_of_speech;

    private CheckBox[]              symptoms_checkbox;

    private Accessories             report_accessories;

    private ProgressDialog          progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report_);

        findViewById(R.id.goback).setOnClickListener(view -> finish());

        report_accessories  = new Accessories(this);

        progressBar         = new ProgressDialog(this);

        myauth              = FirebaseAuth.getInstance();

        locationHelper = new LocationHelper(Report_.this);
        locationHelper.checkpermission();

        if(locationHelper.checkPlayServices()){
            locationHelper.buildGoogleApiClient();

        }

        //checkboxes
        drycough_checkbox   = findViewById(R.id.drycough_checkbox);
        fever_checkbox      = findViewById(R.id.fever_checkbox);
        tiredness_checkbox  = findViewById(R.id.tiredness_checkbox);
        aches_checkbox      = findViewById(R.id.aches_checkbox);
        sorethroat_checkbox = findViewById(R.id.sorethroat_checkbox);
        diarrheoa_checkbox  = findViewById(R.id.diarrheoa_checkbox);
        conjunct_checkbox   = findViewById(R.id.conjunct_checkbox);
        headache            = findViewById(R.id.headache);
        loss_of_taste       = findViewById(R.id.loss_of_taste);
        rash_checkbox       = findViewById(R.id.rash_checkbox);
        difficulty_breathing = findViewById(R.id.difficulty_breathing);
        chest_pain          = findViewById(R.id.chest_pain);
        loss_of_speech      = findViewById(R.id.loss_of_speech);;

        symptoms_checkbox   = new CheckBox[]{drycough_checkbox, fever_checkbox, tiredness_checkbox,
                                aches_checkbox, sorethroat_checkbox, diarrheoa_checkbox, conjunct_checkbox, headache, loss_of_taste,
                                rash_checkbox, difficulty_breathing, chest_pain, loss_of_speech};

        title_edittext      = findViewById(R.id.subject);
        message_edittext    = findViewById(R.id.message);

        findViewById(R.id.call).setOnClickListener(v -> {
            openDialer(v,"112");
        });

        findViewById(R.id.whatsapp).setOnClickListener(v -> {
            openWhatsApp();
        });

        findViewById(R.id.send).setOnClickListener(v -> {
            title = title_edittext.getText().toString().trim();
            message = message_edittext.getText().toString().trim();

            if(title.equals("")){
                snackbar = Snackbar.make(findViewById(android.R.id.content),
                        "Title required", Snackbar.LENGTH_LONG);
                snackbar.show();
            }

            else if(message.equals("")){
                snackbar = Snackbar.make(findViewById(android.R.id.content),
                        "Message required", Snackbar.LENGTH_LONG);
                snackbar.show();
            }

            else{
                //saving the symptoms
                for(int i = 0; i < symptoms_checkbox.length; i++){
                    if(symptoms_checkbox[i].isChecked()){
                        symptoms += symptoms_checkbox[i].getText() + ",";
                    }
                }
                if(report_accessories.isNetworkAvailable()){
                    progressBar.setTitle("Reporting case");
                    progressBar.setMessage("Please Wait...");
                    progressBar.setCanceledOnTouchOutside(false);
                    progressBar.show();
                    Upload_report(title, message, symptoms);
                }
                else{
                    new Functions(this).showAlertDialogueWithOK("No internet connection");
                }
            }
        });
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        myLocation = locationHelper.getLocation();
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

    @Override
    public void onConnectionSuspended(int i) {
        locationHelper.connectApiClient();

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.i("Connection Failure","Connnection Error = " +
                connectionResult.getErrorCode());
    }

    private void openDialer(View v, String call_number){
        Intent intent = new Intent(Intent.ACTION_DIAL);
        intent.setData(Uri.parse("tel:" + call_number));
        v.getContext().startActivity(intent);
    }

    private void openWhatsApp() {
        String smsNumber = "233268977129"; // E164 format without '+' sign
        Intent sendIntent = new Intent(Intent.ACTION_SEND);
        sendIntent.setType("text/plain");
        sendIntent.putExtra(Intent.EXTRA_TEXT, "Suspected covid 19 case");
        sendIntent.putExtra("jid", smsNumber + "@s.whatsapp.net"); //phone number without "+" prefix
        sendIntent.setPackage("com.whatsapp");
        if (sendIntent.resolveActivity(getPackageManager()) == null) {
            Toast.makeText(this, "Whatsapp not installed", Toast.LENGTH_SHORT).show();
            return;
        }
        startActivity(sendIntent);
    }

    private void Upload_report(String title, String message, String symptoms) {

        myLocation = locationHelper.getLocation();

        latitudeD = myLocation.getLatitude();
        longitudeD = myLocation.getLongitude();
//        location_address = locationHelper.getAddress(latitudeD, longitudeD).toString();
        reference = FirebaseDatabase.getInstance().getReference("reports")
                .child(myauth.getCurrentUser().getUid());
        if(myLocation != null){
            final HashMap<String, Object> report_ = new HashMap<>();
            report_.put("title", title);
            report_.put("message", message);
            report_.put("latitude", latitudeD);
            report_.put("longitude", longitudeD);
            report_.put("phone_number", myauth.getCurrentUser().getPhoneNumber());
            report_.put("symptoms", symptoms);

            reference.push().setValue(report_).addOnCompleteListener(task -> {
                if(task.isSuccessful()){
                    progressBar.dismiss();
                    title_edittext.setText("");
                    message_edittext.setText("");
                    new Functions(this).showAlertDialogueWithOK("Report successfully submitted");
                }
            });
           }else {
            snackbar = Snackbar.make(findViewById(android.R.id.content),
                    "Location not found", Snackbar.LENGTH_LONG);
            snackbar.show();
        }
    }
}
