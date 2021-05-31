package com.tekdevisal.covman;

import android.app.ProgressDialog;
import android.content.Intent;
import android.location.Location;
import android.os.AsyncTask;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.tekdevisal.covman.Helpers.Accessories;
import com.tekdevisal.covman.Helpers.Functions;
import com.tekdevisal.covman.Helpers.Urls;
import com.tekdevisal.covman.LocationUtil.LocationHelper;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class Register extends AppCompatActivity {
//        implements GoogleApiClient.ConnectionCallbacks,
//        GoogleApiClient.OnConnectionFailedListener, ActivityCompat.OnRequestPermissionsResultCallback {

    private EditText name;
    private Accessories register_accessor;
    private String name_string="", user_type="", raw_phone_number="", responseCode = "";
    private Snackbar snackbar;
    private ProgressDialog progressBar;
    private DatabaseReference user_reference, doctor_reference;
    private FirebaseAuth mauth;
    private LocationHelper locationHelper;
    private Location myLocation;
    double latitudeD,longitudeD;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        mauth = FirebaseAuth.getInstance();

        register_accessor = new Accessories(Register.this);

//        locationHelper = new LocationHelper(Register.this);
//        locationHelper.checkpermission();
//
//        if(locationHelper.checkPlayServices()){
//            locationHelper.buildGoogleApiClient();
//
//        }

        progressBar         = new ProgressDialog(this);

        user_type           = register_accessor.getString("user_type");
        raw_phone_number    = register_accessor.getString("raw_phone_number");
        name                = findViewById(R.id.name);

        findViewById(R.id.continueNextButton).setOnClickListener(v -> {
            progressBar.dismiss();
            name_string = name.getText().toString().trim();
            if(name_string.equals("")){
                progressBar.dismiss();
                snackbar = Snackbar.make(findViewById(android.R.id.content),
                        "Name required", Snackbar.LENGTH_LONG);
                snackbar.show();
            }else{
                progressBar.setTitle("Saving username");
                progressBar.setMessage("Please Wait...");
                progressBar.setCanceledOnTouchOutside(false);
                progressBar.show();
                Save_name(name_string);
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_HOME);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    private void Save_name(String name) {
        try {
//            myLocation = locationHelper.getLocation();
//
//            latitudeD = myLocation.getLatitude();
//            longitudeD = myLocation.getLongitude();

            final HashMap<String, Object> register = new HashMap<>();
            register.put("name", name);
            register.put("image", "None");
//            register.put("latitude", latitudeD);
//            register.put("longitude", longitudeD);

            if (user_type.equals("normal_user")){
                if(mauth.getCurrentUser() != null){
                    user_reference = FirebaseDatabase.getInstance().getReference("users").child(mauth.getCurrentUser().getUid());
                    user_reference.setValue(register).addOnCompleteListener(task -> {
                        if(task.isSuccessful()){
                            register_accessor.put("has_named", "true");
                            register_accessor.put("my_name", name);
//                            register_accessor.put("saved_latitude", String.valueOf(latitudeD));
//                            register_accessor.put("saved_longitude", String.valueOf(longitudeD));
                            startActivity(new Intent(Register.this, MainActivity.class));
                            finish();
                        }
                    });

                }
            }
            else{
                if(mauth.getCurrentUser() != null){
                    doctor_reference = FirebaseDatabase.getInstance().getReference("doctors").child(mauth.getCurrentUser().getUid());
                    doctor_reference.setValue(register).addOnCompleteListener(task -> {
                        if(task.isSuccessful()){
                            register_accessor.put("has_named", "true");
                            register_accessor.put("my_name", name);
//                            register_accessor.put("saved_latitude", String.valueOf(latitudeD));
//                            register_accessor.put("saved_longitude", String.valueOf(longitudeD));
                            startActivity(new Intent(Register.this, MainActivity.class));
                            finish();
                        }
                    });

                }
            }
        }catch (NullPointerException e){
            e.printStackTrace();
        }
    }

    @Override
    protected void onStart() {
        if (register_accessor.isNetworkAvailable()){
            if(register_accessor.getString("user_type").equals("normal_user")){
                new FetchUserDetails(raw_phone_number).execute();
            }
        }
        else{
            Toast.makeText(Register.this,"No internet connection", Toast.LENGTH_LONG).show();
        }
        super.onStart();
    }

    private class FetchUserDetails extends AsyncTask<String, String, String> {
        String phoneNumber = "";

        public FetchUserDetails(String phoneNumber) {
            this.phoneNumber = phoneNumber;
        }

        @Override
        protected String doInBackground(String... strings) {
            RequestQueue requestQueue = Volley.newRequestQueue(Register.this);
            StringRequest postRequest = new StringRequest(Request.Method.POST, new Urls().fetchUserDetails_url,
                    response -> {
                        // response
                        Log.d("Response", response);
                        if(responseCode.equals("200")){
                            JSONObject object = new Functions(Register.this).FetchDataFromJson(response);
                            Log.d("object", object.toString());

                            try {
                                register_accessor.put("user_id", object.getString("user_id"));
                                register_accessor.put("org_id", object.getString("org_id"));
                                register_accessor.put("image", object.getString("image"));
                                register_accessor.put("first_name", object.getString("first_name"));
                                register_accessor.put("last_name", object.getString("last_name"));
                                register_accessor.put("dob", object.getString("dob"));
                                register_accessor.put("level", object.getString("level"));
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }else{
                            snackbar = Snackbar.make(findViewById(android.R.id.content),
                                    "Something went wrong.", Snackbar.LENGTH_LONG);
                            snackbar.show();
                            return;
                        }
                    },
                    error -> {
                        // error
                        Log.d("Error.Response", error.toString());
                        return;
                    }
            ) {
                @Override
                protected Map<String, String> getParams()
                {
                    Map<String, String>  params = new HashMap<String, String>();
                    params.put("phone_number", phoneNumber);
                    return params;
                }

                @Override
                protected Response<String> parseNetworkResponse(NetworkResponse response) {
                    responseCode = String.valueOf(response.statusCode);
                    return super.parseNetworkResponse(response);
                }
            };
            requestQueue.add(postRequest);
            return null;
        }
    }

    //    @Override
//    public void onConnected(@Nullable Bundle bundle) {
//        myLocation = locationHelper.getLocation();
//    }
//
//    @Override
//    public void onConnectionSuspended(int i) {
//        locationHelper.connectApiClient();
//    }
//
//    @Override
//    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
//        Log.i("Connection Failure","Connnection Error = " +
//                connectionResult.getErrorCode());
//    }
//
//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//        locationHelper.onActivityResult(requestCode,resultCode,data);
//
//    }
//
//    @Override
//    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
//        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
//        locationHelper.onRequestPermissionsResult(requestCode,permissions,grantResults);
//
//    }
//
//    @Override
//    protected void onResume() {
//        super.onResume();
//        locationHelper.checkPlayServices();
//
//    }
}
