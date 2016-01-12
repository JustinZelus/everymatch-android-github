package com.everymatch.saas.singeltones;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;

/**
 * Created by PopApp_laptop on 11/10/2015.
 */
public class DialogFactory {
    private static DialogFactory _instance;

    public static DialogFactory get_instance() {
        if (_instance == null)
            _instance = new DialogFactory();
        return _instance;

    }

    public static AlertDialog.Builder getDialogBuilder(Context context, String title, String message) {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
        alertDialogBuilder.setTitle(title);
        alertDialogBuilder.setMessage(message);

        //AlertDialog alertDialog = alertDialogBuilder.create();

        return alertDialogBuilder;
    }

    public static android.app.Dialog getDialogOkButton(Context context, String title, String message) {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
        alertDialogBuilder.setTitle(title);
        alertDialogBuilder.setMessage(message);
        alertDialogBuilder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {

                dialog.dismiss();
            }
        });
        AlertDialog alertDialog = alertDialogBuilder.create();

        return alertDialog;
    }

}
