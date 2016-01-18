package com.example.steven.try_db;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.io.BufferedReader;
import java.util.Calendar;

public class MainActivity extends ActionBarActivity {
    private TextView tv;
    private TextView tv_date;
    private Button btn_next;
    private Button btn_pre;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        tv= (TextView) findViewById(R.id.textview_1);
        tv_date= (TextView) findViewById(R.id.textview_date);
        btn_next= (Button) findViewById(R.id.button3);
        btn_pre= (Button) findViewById(R.id.button2);

        final EditText et_year= (EditText) findViewById(R.id.editText);
        final EditText et_month= (EditText) findViewById(R.id.editText2);
        final EditText et_day= (EditText) findViewById(R.id.editText3);
        final Button btn_search = (Button) findViewById(R.id.button);

        btn_search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String  choosed_year = et_year.getText().toString();
                String  choosed_month= et_month.getText().toString();
                String  choosed_day= et_day.getText().toString();

                show_date_contene(choosed_year,choosed_month,choosed_day);
            }
        });

        btn_next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar c1 = Calendar.getInstance();

                int choosed_year_int = Integer.parseInt(et_year.getText().toString());
                int choosed_month_int = Integer.parseInt(et_month.getText().toString())-1;
                int choosed_day_int = Integer.parseInt(et_day.getText().toString());

                c1.set(choosed_year_int, choosed_month_int, choosed_day_int);
                c1.add(Calendar.DATE, +1);

                String year = c1.get(Calendar.YEAR) + "";
                String month = (c1.get(Calendar.MONTH)+1) + "";
                String date = c1.get(Calendar.DATE) + "";

                if(month.length()==1){
                    month="0"+month;
                }
                if(date.length()==1){
                    date="0"+date;
                }

                et_year.setText(year);
                et_month.setText(month);
                et_day.setText(date);

                show_date_contene(year, month, date);
            }});

        btn_pre.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar c1 = Calendar.getInstance();

                int choosed_year_int = Integer.parseInt(et_year.getText().toString());
                int choosed_month_int = Integer.parseInt(et_month.getText().toString())-1;
                int choosed_day_int = Integer.parseInt(et_day.getText().toString());

                c1.set(choosed_year_int, choosed_month_int, choosed_day_int);
                c1.add(Calendar.DATE, -1);

                String year = c1.get(Calendar.YEAR) + "";
                String month = (c1.get(Calendar.MONTH)+1) + "";
                String date = c1.get(Calendar.DATE) + "";

                if(month.length()==1){
                        month="0"+month;
                }
                if(date.length()==1){
                    date="0"+date;
                }

                et_year.setText(year);
                et_month.setText(month);
                et_day.setText(date);

                show_date_contene(year, month, date);
            }});
    }

    public void show_date_contene(String year,String month,String day){
        try {
            String [] whereArgs=new String[]{"dkcal_mdays_"+year+month+day};
            SQLiteDatabase db = SQLiteDatabase.openDatabase("/storage/emulated/0/Android/data/nutstore.android/cache/objectcache/1/calendar.db", null, 0);
            Cursor cursor = db.query("item_table", new String[]{"it_content", "it_unique_id"}, "it_unique_id like ?", whereArgs,null, null, null);
//                Cursor cursor = db.query("item_table", new String[]{"it_content", "it_unique_id"}, "it_unique_id like ?", new String[]{"dkcal_mdays_20150511"}, null, null, null);

            String[] list = new String[cursor.getCount()];
            cursor.moveToFirst();
            list[0] = cursor.getString(cursor.getColumnIndex("it_content"));
            if (list[0].length()==0){
                tv.setText("（该日你没有记录）");
            }else {
                tv.setText(list[0]);
            }

//                    int count = 1;
//                    while (cursor.moveToNext()) {
//                        list[count] = cursor.getString(cursor.getColumnIndex("it_content"));
//                        count++;
//                    }
        }catch (Exception e){
            tv.setText("（查询不到这条记录）");
            String [] whereArgs=new String[]{"dkcal_mdays_"+year+month+day};
        }
        finally {
            tv_date.setText(year+"年"+month+"月"+day+"日");
        }

    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
