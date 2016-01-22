package com.prolificinteractive.materialcalendarview.sample;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Created by steven on 2016/1/22.
 */
public class Choose_db extends DialogFragment{
    public interface Choose_db_interface{
        public String current_db_location();
        public String [] find_all_db();
        public void write_path_db(String aboslute_path);
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        super.onCreateDialog(savedInstanceState);
        final Choose_db_interface choose_db_interface= (Choose_db_interface) getActivity();
        final String [] all_path=choose_db_interface.find_all_db();
        String current_location=choose_db_interface.current_db_location();

        LayoutInflater inflater=getActivity().getLayoutInflater() ;
        View view=inflater.inflate(R.layout.choose_db, null);
        final ListView listView= (ListView) view.findViewById(R.id.listView);
        TextView tv= (TextView) view.findViewById(R.id.location);
        tv.setText(current_location);
        listView.setAdapter(new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_single_choice, all_path));
        listView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);

        AlertDialog.Builder builder=new AlertDialog.Builder(getActivity());
        builder.setView(view);
        builder.setTitle("选择数据库");
        builder.setPositiveButton("确认", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                int positon=listView.getCheckedItemCount();
                choose_db_interface.write_path_db(all_path[positon]);
            }
        });
        builder.setNegativeButton("取消", null);
        return builder.create();
    }
}
