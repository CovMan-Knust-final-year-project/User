package com.tekdevisal.covman;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseException;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.hbb20.CountryCodePicker;
import com.tekdevisal.covman.Helpers.Accessories;
import com.tekdevisal.covman.Helpers.Functions;
import com.tekdevisal.covman.Helpers.Urls;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class Login_ extends AppCompatActivity {

    private CountryCodePicker ccp;
    private EditText phonetext;
    private EditText codeText;
    private Button continueNextButton;
    private String checker="", phoneNumber="", which_user="";
    private RelativeLayout layout;
    private Snackbar snackbar;
    private ProgressDialog loadingbar;
    private CardView user_, doctor_;
    private Accessories login_accessor;

    //firebase phone number verification
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks callbacks;
    private FirebaseAuth mAuth;
    private String mVerificationId;
    private PhoneAuthProvider.ForceResendingToken mResendingToken;

    private String responseCode = "";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_);

        login_accessor      = new Accessories(this);
        mAuth               = FirebaseAuth.getInstance();
        loadingbar          = new ProgressDialog(this);

        phonetext           = findViewById(R.id.phoneText);

        codeText            = findViewById(R.id.codeText);
        continueNextButton  = findViewById(R.id.continueNextButton);
        layout              = findViewById(R.id.phoneAuth);

        user_               = findViewById(R.id.user_cardview);
        doctor_             = findViewById(R.id.doctor_cardview);

        ccp = findViewById(R.id.ccp);
        ccp.registerCarrierNumberEditText(phonetext);

        user_.setOnClickListener(v -> {
            doctor_.setBackground(getResources().getDrawable(R.drawable.curved_corner_white));
            user_.setBackground(getResources().getDrawable(R.drawable.curved_corner_red));
            which_user = "normal_user";
            showSnackBar("Login as User");
        });
        doctor_.setOnClickListener(v -> {
            doctor_.setBackground(getResources().getDrawable(R.drawable.curved_corner_red));
            user_.setBackground(getResources().getDrawable(R.drawable.curved_corner_white));
            which_user = "doctor";
            showSnackBar("Login as Doctor");
        });

        continueNextButton.setOnClickListener(v -> {
            if(continueNextButton.getText().equals("Submit") || checker.equals("Code Sent")){
//                if(isNetworkAvailable()){
                String verificationcode = codeText.getText().toString().trim();

                if(verificationcode.equals("")){
                    showSnackBar("Code required");
                }else{
                    showProgressBar("Code Verification", "Please Wait. We are verifying your phone number");
                    //TODO: check to see if the user has been registered by the admin.

                    PhoneAuthCredential credential = PhoneAuthProvider.getCredential(mVerificationId, verificationcode);
                    signInWithPhoneAuthCredential(credential);
                }
            }else{
//                    phonetext.getText().toString().trim();
                phoneNumber = ccp.getFullNumberWithPlus();
                if(!phoneNumber.equals("")){
                    if(!which_user.equals("")){

                        //TODO: check to see if the user is in the database before sending the code.
                        if(which_user.equals("normal_user")){
                            new CheckIfUserIsPresent(phonetext.getText().toString().trim()).execute();
                        }
                        else{
                            new CheckIfDoctorIsPresent(phonetext.getText().toString().trim()).execute();
                        }
                    }else{
                        showSnackBar("Choose user type(eg. user,doctor)");
                    }
                    // OnVerificationStateChangedCallbacks
                }else{
                    showSnackBar("Number Invalid");
                }
            }
//                }else{
//                    snackbar = Snackbar.make(findViewById(android.R.id.content),
//                            "No internet connection", Snackbar.LENGTH_LONG);
//                    snackbar.show();
//                }

        });

        callbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            @Override
            public void onVerificationCompleted(PhoneAuthCredential phoneAuthCredential) {
                signInWithPhoneAuthCredential(phoneAuthCredential);
            }

            @Override
            public void onVerificationFailed(FirebaseException e) {

                loadingbar.dismiss();
                showSnackBar("Code could not send");
                layout.setVisibility(View.VISIBLE);

                continueNextButton.setText("Continue");
                codeText.setVisibility(View.GONE);
            }

            @Override
            public void onCodeAutoRetrievalTimeOut(String s) {
                super.onCodeAutoRetrievalTimeOut(s);
                loadingbar.dismiss();
                showSnackBar("Something went wrong. Try again later");
            }

            @Override
            public void onCodeSent(String s, PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                super.onCodeSent(s, forceResendingToken);

                mVerificationId = s;
                mResendingToken = forceResendingToken;

                layout.setVisibility(View.GONE);
                checker = "Code Sent";
                continueNextButton.setText("Submit");
                codeText.setVisibility(View.VISIBLE);
                loadingbar.dismiss();
                showSnackBar("Code has been sent.");
            }
        };
    }

    private class CheckIfUserIsPresent extends AsyncTask<String, String, String> {
        String phoneNumber = "";

        public CheckIfUserIsPresent(String phoneNumber) {
            this.phoneNumber = phoneNumber;
        }

        @Override
        protected void onPreExecute() {
            showProgressBar("Checking Eligibility..","Please Wait. We are checking your eligibility to use this service");
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... strings) {
            RequestQueue requestQueue = Volley.newRequestQueue(Login_.this);
            StringRequest postRequest = new StringRequest(Request.Method.POST, new Urls().userAvailability_url,
                    response -> {
                        // response
                        Log.d("Response", response);
                        if(responseCode.equals("200")){
                            JSONObject object = new Functions(Login_.this).FetchDataFromJson(response);
                            try {
                                if(object.getString("message").equals("user present")){
                                    login_accessor.put("raw_phone_number", phonetext.getText().toString().trim());
                                    sendSMsCode();
                                }else{
                                    new Functions(Login_.this).showAlertDialogueWithOK("You are not eligible to use this service");
                                    return;
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

        @Override
        protected void onPostExecute(String s) {
            loadingbar.dismiss();
            super.onPostExecute(s);
        }
    }

    private class CheckIfDoctorIsPresent extends AsyncTask<String, String, String> {
        String phoneNumber = "";

        public CheckIfDoctorIsPresent(String phoneNumber) {
            this.phoneNumber = phoneNumber;
        }

        @Override
        protected void onPreExecute() {
            showProgressBar("Checking Eligibility..","Please Wait. We are checking your eligibility to use this service");
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... strings) {
            RequestQueue requestQueue = Volley.newRequestQueue(Login_.this);
            StringRequest postRequest = new StringRequest(Request.Method.POST, new Urls().docAvailability_url,
                    response -> {
                        // response
                        Log.d("Response", response);
                        if(responseCode.equals("200")){
                            JSONObject object = new Functions(Login_.this).FetchDataFromJson(response);
                            try {
                                if(object.getString("message").equals("doc present")){
                                    login_accessor.put("raw_phone_number", phonetext.getText().toString().trim());
                                    sendSMsCode();
                                }else{
                                    new Functions(Login_.this).showAlertDialogueWithOK("You are not a registered Doctor");
                                    return;
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

        @Override
        protected void onPostExecute(String s) {
            loadingbar.dismiss();
            super.onPostExecute(s);
        }
    }

    private void sendSMsCode(){
        //replace first 0 zero with a white space.
        phoneNumber.replaceFirst("^0+(?!$)","");

        //show progress bar
        showProgressBar("Sending code","Please Wait. We are sending code to phone number");

        //sending the sms code
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                phoneNumber,        // Phone number to verify
                60,                 // Timeout duration
                TimeUnit.SECONDS,   // Unit of timeout
                this,               // Activity (for callback binding)
                callbacks);
        //sending sms code ends here
    }

    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        loadingbar.dismiss();
                        snackbar = Snackbar.make(findViewById(android.R.id.content),
                                "Login successful", Snackbar.LENGTH_LONG);
                        snackbar.show();

                        //check if user already has name in database
                        if(mAuth.getCurrentUser() != null){

                            if(which_user.equals("normal_user")){
                                try{
                                    DatabaseReference find_user = FirebaseDatabase.getInstance().getReference("users")
                                            .child(mAuth.getCurrentUser().getUid());
                                    find_user.addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                            if(dataSnapshot.exists() && dataSnapshot.hasChild("name")){
                                                Intent gohome = new Intent(Login_.this, MainActivity.class);
                                                login_accessor.put("user_type", which_user);
                                                startActivity(gohome);
                                                finish();
                                            }else{
                                                Intent go_register = new Intent(Login_.this, Register.class);
                                                login_accessor.put("user_type", which_user);
                                                startActivity(go_register);
                                                finish();
                                            }
                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError databaseError) {

                                        }
                                    });
                                }catch (DatabaseException e){
                                    e.printStackTrace();
                                }

                            }else{
                                try {
                                    DatabaseReference find_user = FirebaseDatabase.getInstance().getReference("doctors")
                                            .child(mAuth.getCurrentUser().getUid());
                                    find_user.addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                            if(dataSnapshot.child("name").getValue() != null){
                                                Intent gohome = new Intent(Login_.this, MainActivity.class);
                                                login_accessor.put("user_type", which_user);
                                                startActivity(gohome);
                                                finish();
                                            }else{
                                                Intent go_register = new Intent(Login_.this, Register.class);
                                                login_accessor.put("user_type", which_user);
                                                startActivity(go_register);
                                                finish();
                                            }
                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError databaseError) {

                                        }
                                    });
                                }catch (DatabaseException e){
                                    e.printStackTrace();
                                }
                            }

                        }
                    } else {
                        // Sign in failed, display a message and update the UI
//                            Log.w(TAG, "signInWithCredential:failure", task.getException());
                        if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                            // The verification code entered was invalid
                        }
                    }
                });
    }

//    private void CreateUserNode(FirebaseUser currentUser) {
//            DatabaseReference user_ref = FirebaseDatabase.getInstance().getReference("users");
//            user_ref.child(currentUser.getUid()).child("registered").setValue("yes");
//    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_HOME);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    private void showProgressBar(String title, String message){
        loadingbar.setTitle(title);
        loadingbar.setMessage(message);
        loadingbar.setCanceledOnTouchOutside(false);
        loadingbar.show();
    }

    private void showSnackBar(String message){
        snackbar = Snackbar.make(findViewById(android.R.id.content),
                message, Snackbar.LENGTH_LONG);
        snackbar.show();
    }
}