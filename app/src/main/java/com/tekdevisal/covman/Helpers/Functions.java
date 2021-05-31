package com.tekdevisal.covman.Helpers;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class Functions {
    Context context;

    public Functions(Context context) {
        this.context = context;
    }

    public void showAlertDialogueWithOK(String message){
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(context);
        alertDialog.setMessage(message);
        alertDialog.setPositiveButton("OK", (dialogInterface, i) -> {
            dialogInterface.dismiss();
        });
        alertDialog.show();
    }

    public void showSnackBar(String message){

    }

    public JSONObject FetchDataFromJson(String response) {
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
                return object;
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }
}
