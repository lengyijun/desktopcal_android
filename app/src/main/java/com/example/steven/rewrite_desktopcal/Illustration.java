package com.example.steven.rewrite_desktopcal;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;

/**
 * Created by steven on 2016/1/22.
 */
public class Illustration extends DialogFragment{
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder=new AlertDialog.Builder(getActivity());
        builder.setTitle("说明");
        builder.setMessage("本软件需要你手工下载同步软件将电脑上desktopcal的数据库文件同步到手机任意目录，然后进行访问\n\n任何问题可联系qq 2801171954");
        return builder.create();

    }
}
