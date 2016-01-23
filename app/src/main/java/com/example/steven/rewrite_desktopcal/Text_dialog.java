package com.example.steven.rewrite_desktopcal;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;

/**
 * Created by steven on 2016/1/23.
 */
@SuppressLint("ValidFragment")
public class Text_dialog extends DialogFragment{
   String record_date;
    String date;

    public Text_dialog(String record_date,String date) {
        this.record_date=record_date;
        this.date=date;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder=new AlertDialog.Builder(getActivity());
        builder.setTitle(date);
        builder.setMessage(record_date);
        return builder.create();
    }
}
