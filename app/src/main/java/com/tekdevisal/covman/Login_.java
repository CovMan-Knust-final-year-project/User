package com.tekdevisal.covman;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RelativeLayout;

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
            snackbar = Snackbar.make(findViewById(android.R.id.content),
                    "Login as User", Snackbar.LENGTH_LONG);
            snackbar.setActionTextColor(getResources().getColor(R.color.colorAccent));
            snackbar.show();
        });
        doctor_.setOnClickListener(v -> {
            doctor_.setBackground(getResources().getDrawable(R.drawable.curved_corner_red));
            user_.setBackground(getResources().getDrawable(R.drawable.curved_corner_white));
            which_user = "doctor";
            snackbar = Snackbar.make(findViewById(android.R.id.content),
                    "Login as Doctor", Snackbar.LENGTH_LONG);
            snackbar.show();
        });

        continueNextButton.setOnClickListener(v -> {
            if(continueNextButton.getText().equals("Submit") || checker.equals("Code Sent")){
//                if(isNetworkAvailable()){
                    String verificationcode = codeText.getText().toString().trim();

                    if(verificationcode.equals("")){
                        snackbar = Snackbar.make(findViewById(android.R.id.content),
                                "Code required", Snackbar.LENGTH_LONG);
                        snackbar.show();
                    }else{
                        loadingbar.setTitle("Code Verification");
                        loadingbar.setMessage("Please Wait. We are verifying your phone number");
                        loadingbar.setCanceledOnTouchOutside(false);
                        loadingbar.show();

                        //TODO: check to see if the user has been registered by the admin.

                        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(mVerificationId, verificationcode);
                        signInWithPhoneAuthCredential(credential);
                    }
                }else{
                    phoneNumber = ccp.getFullNumberWithPlus();
                    if(!phoneNumber.equals("")){
                        if(!which_user.equals("")){
                            loadingbar.setTitle("Sending code");
                            loadingbar.setMessage("Please Wait. We are sending code to phone number");
                            loadingbar.setCanceledOnTouchOutside(false);
                            loadingbar.show();
                            phoneNumber.replaceFirst("^0+(?!$)","");
                            PhoneAuthProvider.getInstance().verifyPhoneNumber(
                                    phoneNumber,        // Phone number to verify
                                    60,                 // Timeout duration
                                    TimeUnit.SECONDS,   // Unit of timeout
                                    this,               // Activity (for callback binding)
                                    callbacks);
                        }else{
                            snackbar = Snackbar.make(findViewById(android.R.id.content),
                                    "Choose user type(eg. user,doctor)", Snackbar.LENGTH_LONG);
                            snackbar.show();
                        }
                            // OnVerificationStateChangedCallbacks
                    }else{
                        snackbar = Snackbar.make(findViewById(android.R.id.content),
                                "Number Invalid", Snackbar.LENGTH_LONG);
                        snackbar.show();
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
                snackbar = Snackbar.make(findViewById(android.R.id.content),
                        "Number Invalid", Snackbar.LENGTH_LONG);
                snackbar.show();
                layout.setVisibility(View.VISIBLE);

                continueNextButton.setText("Continue");
                codeText.setVisibility(View.GONE);
            }

            @Override
            public void onCodeAutoRetrievalTimeOut(String s) {
                super.onCodeAutoRetrievalTimeOut(s);
                loadingbar.dismiss();
                snackbar = Snackbar.make(findViewById(android.R.id.content),
                        "Something went wrong. Try again later", Snackbar.LENGTH_LONG);
                snackbar.show();
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
                snackbar = Snackbar.make(findViewById(android.R.id.content),
                        "Code has been sent.", Snackbar.LENGTH_LONG);
                snackbar.show();
            }
        };
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

                            //create node here
//                            CreateUserNode(mAuth.getCurrentUser());

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

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_HOME);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }
}
