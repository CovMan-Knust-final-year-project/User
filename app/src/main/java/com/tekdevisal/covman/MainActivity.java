package com.tekdevisal.covman;

import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SwitchCompat;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.joooonho.SelectableRoundedImageView;
import com.roughike.swipeselector.SwipeItem;
import com.roughike.swipeselector.SwipeSelector;
import com.tekdevisal.covman.Adapters.ScansAdapter;
import com.tekdevisal.covman.Helpers.Accessories;
import com.tekdevisal.covman.Helpers.Functions;
import com.tekdevisal.covman.Helpers.Urls;
import com.tekdevisal.covman.LocationUtil.LocationHelper;
import com.tekdevisal.covman.Models.ScansModel;
import com.tekdevisal.covman.Services.Incoming_calls_service;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

public class MainActivity extends AppCompatActivity {
//        implements GoogleApiClient.ConnectionCallbacks,
//        GoogleApiClient.OnConnectionFailedListener, ActivityCompat.OnRequestPermissionsResultCallback,
//        LocationListener {

    private FirebaseAuth mauth;
    private String caller_id="", doc_name, caller_name, has_open_video="", has_opened="";
    public  String has_open_dialer="";
    private Snackbar snackbar;
    private Accessories main_accessor;
    private String usertype;
    private LinearLayout doc_reports;
    private TextView top_text, welcome_user, video_chat_text_one, video_chat_text_two;
    private SelectableRoundedImageView image;
    private LinearLayout is_available_layout, covid_info, complain_, chat_, statistic, about_app,
                        request_vaccine, scans, recent_scan_layout, request_vaccine_and_scans_layout,
                        vaccine_requests_and_cases_layout, attendance_layout, suspected_cases;
    private SwitchCompat is_available_switch;
    private LocationHelper locationHelper;
    private Location myLocation;
    double latitudeD,longitudeD;

    // recycler initializations
    private RecyclerView                    _recycler;
    private RecyclerView.Adapter            _adapter;
    private RecyclerView.LayoutManager      _layout;
    private ArrayList<ScansModel>           _list;

    private ProgressBar                     loading;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);

        MenuItem wish_list      = menu.findItem(R.id.profile);

        if(usertype.equals("doctor")){
            wish_list.setVisible(false);
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.chats:
                startActivity(new Intent(MainActivity.this, MyChats.class));
                break;

            case R.id.profile:
                startActivity(new Intent(MainActivity.this, ProfileActivity.class));
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            setContentView(R.layout.activity_main);
        }catch (OutOfMemoryError e){
            e.printStackTrace();
        }

        mauth = FirebaseAuth.getInstance();
        getSupportActionBar().setTitle("CovMan | HOME");

//        locationHelper = new LocationHelper(MainActivity.this);
//        locationHelper.checkpermission();
//
//        if(locationHelper.checkPlayServices()){
//            locationHelper.buildGoogleApiClient();
//        }

        main_accessor = new Accessories(MainActivity.this);
        usertype = main_accessor.getString("user_type");

//        latitudeD = Double.parseDouble(main_accessor.getString("saved_latitude"));
//        longitudeD = Double.parseDouble(main_accessor.getString("saved_longitude"));

        loading             = findViewById(R.id.loading);
        top_text            = findViewById(R.id.top_text);
        welcome_user        = findViewById(R.id.welcome_user);
        image               = findViewById(R.id.image);
        recent_scan_layout  = findViewById(R.id.recent_scan_layout);
        is_available_layout = findViewById(R.id.is_available_layout);
        video_chat_text_one = findViewById(R.id.video_chat_text_one);
        video_chat_text_two = findViewById(R.id.video_chat_text_two);
        covid_info          = findViewById(R.id.info);
        complain_           = findViewById(R.id.complain);
        attendance_layout   = findViewById(R.id.attendance_layout);
        chat_               = findViewById(R.id.chat);
        statistic           = findViewById(R.id.statistic);
        about_app           = findViewById(R.id.about_app);
        request_vaccine     = findViewById(R.id.request_vaccine);
        scans               = findViewById(R.id.scans);
        request_vaccine_and_scans_layout            = findViewById(R.id.request_vaccine_and_scans_layout);
        vaccine_requests_and_cases_layout           = findViewById(R.id.vaccine_requests_and_caseslayout);
        suspected_cases     = findViewById(R.id.suspected_cases);

        doc_reports = findViewById(R.id.reports);
        is_available_switch = findViewById(R.id.is_available_switch);

        if(usertype.equals("doctor")){
//            top_text.setText("Help us aid in the fight against covid 19");
//            image.setImageDrawable(getResources().getDrawable(R.drawable.doctors));
            top_text.setVisibility(View.GONE);
            recent_scan_layout.setVisibility(View.GONE);
            welcome_user.setText("Hi, \nDoc");

            video_chat_text_one.setText("Video Chat Users");
            video_chat_text_two.setText("Have a a face to face chat with Users");

            request_vaccine_and_scans_layout.setVisibility(View.GONE);
            vaccine_requests_and_cases_layout.setVisibility(View.VISIBLE);
            attendance_layout.setVisibility(View.GONE);
            complain_.setVisibility(View.GONE);
            statistic.setVisibility(View.GONE);
            doc_reports.setVisibility(View.VISIBLE);
            is_available_layout.setVisibility(View.VISIBLE);
            suspected_cases.setVisibility(View.VISIBLE);

            is_available_switch.setOnCheckedChangeListener((buttonView, isChecked) -> {
                if(main_accessor.isNetworkAvailable()){
                    if (isChecked) {
                        Doctor_is_Available();
                    } else {
                        Doctor_is_UnAvailable();
                    }
                }
                else{
                    new Functions(this).showAlertDialogueWithOK("No internet connection");
                }
            });

        }else{
            top_text.setTextSize(25);
            if(main_accessor.isNetworkAvailable()){
                initializeRecyclerView();
                new FetchRecentScan().execute();
            }
            else{
                new Functions(this).showAlertDialogueWithOK("No internet connection");
            }
        }
        //swipe selections
//        SwipeSelector swipeSelector = (SwipeSelector) findViewById(R.id.swipe_selector);
//        swipeSelector.setItems(
//                // The first argument is the value for that item, and should in most cases be unique for the
//                // current SwipeSelector, just as you would assign values to radio buttons.
//                // You can use the value later on to check what the selected item was.
//                // The value can be any Object, here we're using ints.
//                new SwipeItem(0, "Tip 1", "Clean your hands often. Use soap and water, or an alcohol-based hand rub."),
//                new SwipeItem(1, "Tip 2", "Maintain a safe distance from anyone who is coughing or sneezing."),
//                new SwipeItem(2, "Tip 3", "Don’t touch your eyes, nose or mouth.")
//        );

        covid_info.setOnClickListener(v -> {
            startActivity(new Intent(MainActivity.this, About_.class));
        });

        complain_.setOnClickListener(v -> {
            startActivity(new Intent(MainActivity.this, Report_.class));
        });

        chat_.setOnClickListener(v -> {
            startActivity(new Intent(MainActivity.this, Live_Chat.class));
        });

        statistic.setOnClickListener(v -> {
            startActivity(new Intent(MainActivity.this, Statistics.class));
        });

        request_vaccine.setOnClickListener(v -> {
            startActivity(new Intent(MainActivity.this, RequestVaccine.class));
        });

         scans.setOnClickListener(v -> {
            startActivity(new Intent(MainActivity.this, AllScans.class));
        });

        about_app.setOnClickListener(v -> {
            startActivity(new Intent(MainActivity.this, App_description.class));
        });

        findViewById(R.id.attendance).setOnClickListener(view -> {
            startActivity(new Intent(MainActivity.this, Attendance.class));
        });

        findViewById(R.id.reports).setOnClickListener(v ->
            startActivity(new Intent(MainActivity.this, Reports.class)
        ));

        findViewById(R.id.vaccine_requests).setOnClickListener(view -> {
            startActivity(new Intent(MainActivity.this, VaccinesRequestedFor.class));
        });

        suspected_cases.setOnClickListener(view -> {
            startActivity(new Intent(MainActivity.this, SuspectedCases.class));
        });

//        request_vaccine_and_scans_layout.setOnClickListener(view -> {
//
//        });
//
//        vaccine_requests_and_cases_layout.setOnClickListener(view -> {
//
//        });

        findViewById(R.id.logout).setOnClickListener(v -> {
            final AlertDialog.Builder logout = new AlertDialog.Builder(MainActivity.this, R.style.Myalert);
            logout.setTitle("Signing Out?");
            logout.setMessage("Leaving us? Please reconsider.");
            logout.setNegativeButton("Sign out", (dialog, which) -> {
                if(main_accessor.isNetworkAvailable()){
                    FirebaseAuth.getInstance().signOut();
                    main_accessor.put("has_named", "false");
                    main_accessor.clearStore();
//                    main_accessor.put("first_open", false);
                    Doctor_is_UnAvailable();
                    startActivity(new Intent(MainActivity.this,Login_.class));
                }else{
                    snackbar = Snackbar.make(findViewById(android.R.id.content),
                            "No internet connection", Snackbar.LENGTH_LONG);
                    snackbar.show();                }
            });

            logout.setPositiveButton("Stay", (dialog, which) -> dialog.cancel());
            logout.show();
        });
    }

    private void initializeRecyclerView() {
        _list       = new ArrayList<>();
        _recycler   = findViewById(R.id.recyclerView);
        _recycler.setNestedScrollingEnabled(false);
        _recycler.setHasFixedSize(false);
        _layout     = new LinearLayoutManager(getApplicationContext(), LinearLayout.VERTICAL, false);
        _recycler.setLayoutManager(_layout);
        _adapter    = new ScansAdapter(_list,getApplicationContext());
        _recycler.setAdapter(_adapter);
    }

    private void Doctor_is_Available() {
      try {
          if(mauth.getCurrentUser() != null){
              final HashMap<String, Object> is_available = new HashMap<>();
              is_available.put("doc", "here");
              DatabaseReference avail_reference = FirebaseDatabase.getInstance().getReference("available")
                      .child(mauth.getCurrentUser().getUid());
              avail_reference.setValue(is_available).addOnCompleteListener(task -> {
                  if(task.isSuccessful()){
                      main_accessor.put("has_made_available", "yes");
                      snackbar = Snackbar.make(findViewById(android.R.id.content),
                              "You are now available for users", Snackbar.LENGTH_LONG);
                      snackbar.show();
                  }
              });
          }
      }catch (NullPointerException e){

      }
    }

    private void Doctor_is_UnAvailable() {
        try {
            if(mauth.getCurrentUser() != null){
                final HashMap<String, Object> is_available = new HashMap<>();
                is_available.put("doc", "here");
                DatabaseReference remove_avail_reference = FirebaseDatabase.getInstance().getReference("available")
                        .child(mauth.getCurrentUser().getUid());
                remove_avail_reference.removeValue().addOnCompleteListener(task -> {
                    main_accessor.put("has_made_available", "no");
                    snackbar = Snackbar.make(findViewById(android.R.id.content),
                            "Users would not be able to reach you.", Snackbar.LENGTH_LONG);
                    snackbar.show();
                });
            }
        }catch (NullPointerException e){

        }
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
    }

    @Override
    protected void onStart() {
        super.onStart();
//        if(main_accessor.getBoolean("first_open")){
            if(mauth.getCurrentUser() != null){
                if(new Accessories(this).getString("has_named").equals("true")){
                    if(main_accessor.getString("has_made_available").equals("yes")){
                        is_available_switch.setChecked(true);
                    }
                    startService(new Intent(MainActivity.this, Incoming_calls_service.class));
                }else{
                    startActivity(new Intent(MainActivity.this, Register.class));
                }
            }else{
                startActivity(new Intent(MainActivity.this, Login_.class));
            }
//        }else{
//            startActivity(new Intent(MainActivity.this, IntroActivity.class));
//        }

    }

    @Override
    protected void onPause() {
        super.onPause();
//        has_open_video = "";
    }

    @Override
    protected void onRestart() {
        super.onRestart();
//        has_open_video = "";
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Doctor_is_UnAvailable();
    }

    private class FetchRecentScan extends AsyncTask<String, String, String> {

        String responseCode = "";

        @Override
        protected void onPreExecute() {
            loading.setVisibility(View.VISIBLE);
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... strings) {
            RequestQueue requestQueue = Volley.newRequestQueue(MainActivity.this);
            StringRequest postRequest = new StringRequest(Request.Method.POST, new Urls().fetchRecentScan_url,
                    response -> {
                        // response
                        Log.d("Response", response);
                        if(responseCode.equals("200")){
                            JSONObject object = new Functions(MainActivity.this).FetchDataFromJson(response);
                            //creating a attendance object and giving them the values from json object
                            ScansModel obj_model = null;
                            try {
                                obj_model = new ScansModel(object.getString("id"), object.getString("date"),
                                        object.getString("time"), object.getString("temperature"), object.getString("status"));
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            _list.add(obj_model);
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
                    params.put("user_id", main_accessor.getString("user_id"));
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
            findViewById(R.id.no_scans).setVisibility(View.GONE);
            loading.setVisibility(View.GONE);
        }
    }
}
