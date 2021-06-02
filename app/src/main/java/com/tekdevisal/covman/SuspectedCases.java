package com.tekdevisal.covman;

import android.os.AsyncTask;
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
import com.tekdevisal.covman.Adapters.ScansAdapter;
import com.tekdevisal.covman.Adapters.SuspectedCasesAdapter;
import com.tekdevisal.covman.Helpers.Accessories;
import com.tekdevisal.covman.Helpers.Functions;
import com.tekdevisal.covman.Helpers.Urls;
import com.tekdevisal.covman.Models.ScansModel;
import com.tekdevisal.covman.Models.SuspectedCasesModel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class SuspectedCases extends AppCompatActivity {

    //users recycler initializations
    private RecyclerView                    _recycler;
    private RecyclerView.Adapter            _adapter;
    private RecyclerView.LayoutManager      _layout;
    private ArrayList<SuspectedCasesModel>  _list;

    private Accessories                     _accesssor;

    private ProgressBar                     loading;

    private Snackbar                        snackbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_suspected_cases);

        _accesssor      = new Accessories(this);

        loading              = findViewById(R.id.loading);

        findViewById(R.id.goback).setOnClickListener(view -> {
            finish();
        });

        initializeRecyclerView();

        if(_accesssor.isNetworkAvailable()){
            new FetchSuspectedCases().execute();
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
        _adapter    = new SuspectedCasesAdapter(_list,getApplicationContext());
        _recycler.setAdapter(_adapter);
    }

    private class FetchSuspectedCases extends AsyncTask<String, String, String> {

        String responseCode = "";

        @Override
        protected void onPreExecute() {
            loading.setVisibility(View.VISIBLE);
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... strings) {
            RequestQueue requestQueue = Volley.newRequestQueue(SuspectedCases.this);
            StringRequest postRequest = new StringRequest(Request.Method.POST, new Urls().fetchSuspectedCases_url,
                    response -> {
                        // response
                        Log.d("Response", response);
                        if(responseCode.equals("200")){
                            try {
                                //getting the whole json object from the response
                                JSONObject obj = new JSONObject(response);

                                //we have the array named results inside the object
                                //so here we are getting that json array
                                JSONArray array = obj.getJSONArray("Server response");

                                //now looping through all the elements of the json array
                                for (int i = 0; i < array.length(); i++) {
                                    //getting the json object of the particular index inside the array
                                    JSONObject object = array.getJSONObject(i);
                                    SuspectedCasesModel obj_model = null;
                                    try {
                                        obj_model = new SuspectedCasesModel(object.getString("id"),object.getString("first_name"),
                                                object.getString("last_name"),object.getString("phone_number"), object.getString("date"),
                                                object.getString("time"), object.getString("org_name"));
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
//                            catch (NullPointerException e){
//                                e.printStackTrace();
//                            }
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
                    params.put("user_id", _accesssor.getString("user_id"));
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
            findViewById(R.id.no_case).setVisibility(View.GONE);
            loading.setVisibility(View.GONE);
        }
    }

}