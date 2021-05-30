package com.tekdevisal.covman.Helpers;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;

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
}
