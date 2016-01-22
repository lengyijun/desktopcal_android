package com.prolificinteractive.materialcalendarview.sample;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;


/**
 * Created by steven on 2016/1/20.
 */
public class update_db_dialogfragment extends DialogFragment {
    NoticeDialogListener mListener;
    TextView tv;
    EditText et;
    SharedPreferences sharedPreferences;

    public interface NoticeDialogListener{
        public void onDialogPositiveClick(String ss);
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AlertDialog.Builder builder=new AlertDialog.Builder(getActivity());
        LayoutInflater inflater= getActivity().getLayoutInflater();
        View view=inflater.inflate(R.layout.update_db_dialogfragment,null);
        builder.setView(view);
        tv= (TextView) view.findViewById(R.id.textView2);
        et= (EditText) view.findViewById(R.id.editText);

        sharedPreferences=getActivity().getSharedPreferences("data_record", Context.MODE_ENABLE_WRITE_AHEAD_LOGGING);
        String data_ss=sharedPreferences.getString("date",null);
        String record=sharedPreferences.getString("record",null);

        tv.setText(data_ss);
        et.setText(record);

        builder.setPositiveButton("修改",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        NoticeDialogListener listener= (NoticeDialogListener) getActivity();
                        String ss=et.getText().toString();
                        listener.onDialogPositiveClick(ss);
                    }
                });
        builder.setNegativeButton("取消",null);
        return builder.create();
    }

    @Override
    public void onAttach(Activity activity) {
        super.onDetach();
        mListener=(NoticeDialogListener)activity;
    }

}
