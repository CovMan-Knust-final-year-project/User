package com.tekdevisal.covman;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.squareup.picasso.Picasso;
import com.tekdevisal.covman.Helpers.Accessories;
import com.tekdevisal.covman.Helpers.Functions;
import com.tekdevisal.covman.Helpers.Urls;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileActivity extends AppCompatActivity {

    private Spinner                             level_spinner;

    private String[]                            levels = {"Select level","100", "200", "300", "400", "500", "600", "Other"};

    private String                              slevel, fname, lname, sphone_number, sdob;

    private ArrayAdapter<String>                tutor_type;

    private Accessories                         profile_accessor;

    private CircleImageView                     profile_image;

    private EditText                            first_name, last_name, phone_number;

    private TextView                            dob;

    private Snackbar                            snackbar;

    private ProgressDialog                      progressbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        profile_accessor = new Accessories(this);

        progressbar = new ProgressDialog(this);

        profile_image = findViewById(R.id.profile_image);
        first_name = findViewById(R.id.first_name);
        last_name  = findViewById(R.id.last_name);
        dob        = findViewById(R.id.dob);

        dob.setOnClickListener(view -> {
            TextView[] textView = {dob};
            profile_accessor.showDatePicker(null, textView);
        });

        phone_number = findViewById(R.id.phone_number);

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

        fetchDataIntoFields();

        findViewById(R.id.continueNextButton).setOnClickListener(view -> {
            fname           = first_name.getText().toString().trim();
            lname           = last_name.getText().toString().trim();
            sdob            = dob.getText().toString().trim();
            sphone_number   = phone_number.getText().toString().trim();

            if(!fname.equals("")){
                if(!lname.equals("")){
                    if(!sdob.equals("")){
                        if(!sphone_number.equals("")){
                            if(!slevel.equals("Select level")){
                                if(profile_accessor.isNetworkAvailable()){
                                    new EditUserDetails(fname, lname,sdob, sphone_number, slevel).execute();
                                }
                                else {
                                    profile_accessor.showToast("No internet connection");
                                }
                            }
                            else{
                                profile_accessor.showToast("Level required");
                            }
                        }
                        else{
                            profile_accessor.showToast("Phone number required");
                        }
                    }
                    else{
                        profile_accessor.showToast("Dob required");
                    }
                }
                else{
                    profile_accessor.showToast("Last name required");
                }
            }
            else{
                profile_accessor.showToast("First name required");
            }

        });

    }

    private void fetchDataIntoFields() {
        first_name.setText(profile_accessor.getString("first_name"));
        last_name.setText(profile_accessor.getString("last_name"));
        dob.setText(profile_accessor.getString("dob"));
        phone_number.setText(profile_accessor.getString("raw_phone_number"));
        level_spinner.setSelection(tutor_type.getPosition(profile_accessor.getString("level")));

        try{
            Picasso.with(ProfileActivity.this).load("https://covman.000webhostapp.com/assets/img/members/" + profile_accessor.getString("image")).error(R.drawable.profile_image).placeholder(R.drawable.profile_image).into(profile_image);
        }catch (NullPointerException e){
            e.printStackTrace();
        }catch (IllegalArgumentException e){
            e.printStackTrace();
        }
//        register_accessor.put("image", object.getString("image"));
    }

    private class EditUserDetails extends AsyncTask<String, String, String> {
        String first_name   = "";
        String last_name    = "";
        String dob          = "";
        String phoneNumber  = "";
        String level        = "";
        String responseCode = "";

        public EditUserDetails(String first_name, String last_name, String dob, String phoneNumber, String level) {
            this.first_name  = first_name;
            this.last_name   = last_name;
            this.dob         = dob;
            this.phoneNumber = phoneNumber;
            this.level       = level;
        }

        @Override
        protected void onPreExecute() {
            profile_accessor.ShowProgressDialogue(progressbar, "Processing...", "Details are being edited");
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... strings) {
            RequestQueue requestQueue = Volley.newRequestQueue(ProfileActivity.this);
            StringRequest postRequest = new StringRequest(Request.Method.POST, new Urls().editUserDetails_url,
                    response -> {
                        // response
                        Log.d("Response", response);
                        if(responseCode.equals("200")){
                            JSONObject object = new Functions(ProfileActivity.this).FetchDataFromJson(response);

                            try {
                                if(object.getString("message").equals("success")){
                                    profile_accessor.put("first_name", first_name);
                                    profile_accessor.put("last_name", last_name);
                                    profile_accessor.put("dob", dob);
                                    profile_accessor.put("raw_phone_number", phoneNumber);
                                    profile_accessor.put("level", level);
                                }
                                else{
                                    new Functions(ProfileActivity.this).showAlertDialogueWithOK("Something went wrong");
                                }
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
                    params.put("user_id", profile_accessor.getString("user_id"));
                    params.put("first_name", first_name);
                    params.put("last_name", last_name);
                    params.put("dob", dob);
                    params.put("phone_number", phoneNumber);
                    params.put("level", level);
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

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            progressbar.dismiss();
            new Functions(ProfileActivity.this).showAlertDialogueWithOK("Details edited successfully");
        }
    }

}