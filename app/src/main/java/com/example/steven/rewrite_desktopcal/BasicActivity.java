package com.example.steven.rewrite_desktopcal;

import android.content.ContentValues;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.design.widget.NavigationView.OnNavigationItemSelectedListener;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.github.jjobes.slidedatetimepicker.SlideDateTimeListener;
import com.github.jjobes.slidedatetimepicker.SlideDateTimePicker;
import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;
import com.prolificinteractive.materialcalendarview.OnDateSelectedListener;
import com.prolificinteractive.materialcalendarview.OnMonthChangedListener;
import com.squareup.okhttp.MultipartBuilder;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;
import com.zhy.http.okhttp.request.RequestCall;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.RegexFileFilter;
import org.apache.commons.io.filefilter.TrueFileFilter;
import org.apache.commons.io.output.TaggedOutputStream;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;

import butterknife.Bind;
import butterknife.ButterKnife;
import okhttp3.Call;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;

/**
 * Shows off the most basic usage
 */
public class BasicActivity extends AppCompatActivity implements OnDateSelectedListener,
        OnMonthChangedListener,update_db_dialogfragment.NoticeDialogListener,OnNavigationItemSelectedListener,Choose_db.Choose_db_interface{

    private static final DateFormat FORMATTER = SimpleDateFormat.getDateInstance();
    @Bind(R.id.calendarView) MaterialCalendarView widget;
    @Bind(R.id.textView) TextView textView;

//    DBopenhelper dBopenhelper;
    SQLiteDatabase db;
    SQLiteDatabase path_db;
    Cursor cursor;

    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        sharedPreferences=getSharedPreferences("data_record", MODE_ENABLE_WRITE_AHEAD_LOGGING);
        editor=sharedPreferences.edit();

        NavigationView navigationView= (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        widget.setSelectedDate(Calendar.getInstance());
        widget.setOnDateChangedListener(this);
        widget.setOnMonthChangedListener(this);

        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String date = FORMATTER.format(widget.getSelectedDate().getDate());
                Text_dialog text_dialog = new Text_dialog(textView.getText().toString(), date);
                text_dialog.show(getFragmentManager(), "text");
            }
        });
//        初始化设置
        onDateSelected(widget, widget.getSelectedDate(), true);
        getDataFromRemote();
    }

    private void getDataFromRemote() {
        OkHttpUtils.post()
                .url("http://account.desktopcal.com/ajax_login.php")
                .addParams("u_name","qb20110427@163.com")
                .addParams("u_password", "demure")
                .addParams("lang", "chs")
                .addParams("u_keepalive","checked")
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e) {
                        System.out.print("fail");
                        Toast.makeText(getApplicationContext(),"login fail",Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onResponse(String response) {
                        System.out.println(response);
                        Toast.makeText(getApplicationContext(),"login success", Toast.LENGTH_SHORT).show();;

                        OkHttpUtils.post()
                                .url("http://sync.desktopcal.com/ajax_sync.php")
                                .addParams("doaction","queryindex")
                                .build()
                                .execute(new StringCallback() {
                                    @Override
                                    public void onError(Call call, Exception e) {

                                    }

                                    @Override
                                    public void onResponse(String response) {
                                        Toast.makeText(getApplicationContext(), "query success", Toast.LENGTH_SHORT).show();
                                        try {
                                            JSONObject jsonobj = new JSONObject(response);
                                            JSONObject vdata = jsonobj.getJSONObject("vdata");
                                            JSONObject list=vdata.getJSONObject("list");
                                            Iterator<String> it=list.keys();
                                            ArrayList<String> update_data=new ArrayList<String>();

                                            while (it.hasNext()){
                                                String key=it.next();
                                                JSONObject s=list.getJSONObject(key);
                                                update_data.add(s.getString("cd_unique_id"));
                                            }
                                            System.out.println(update_data);

                                            MultipartBuilder builder=new MultipartBuilder().type(MultipartBuilder.FORM)
                                                    .addFormDataPart( "doaction","downloaditems")
                                                    .addFormDataPart("doview", "")
                                                    .addFormDataPart("jid[]", "dkcal_mdays_20160116");
                                            OkHttpUtils.post()
                                                    .url("http://sync.desktopcal.com/ajax_sync.php")

                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }
                                    }
                                });
                    }
                });
    }

    public String [] process_date(CalendarDay date){
        String year=date.getYear()+"";
        String month=(date.getMonth()+1)+"";
        String day=date.getDay()+"";

        if(month.length()==1){
            month="0"+month;
        }
        if(day.length()==1){
            day="0"+day;
        }

        return new String[]{"dkcal_mdays_"+year+month+day};
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        path_db.close();
        cursor.close();
        db.close();
    }

    public String get_record_of_day(CalendarDay date){
        String [] whereArgs=process_date(date);
        try{
//            todo
//            db = SQLiteDatabase.openDatabase(find_path_from_db(), null, 0);
            cursor = db.query("item_table", new String[]{"it_content", "it_unique_id"}, "it_unique_id like ?", whereArgs, null, null, null);

            cursor.moveToFirst();
            String ss= cursor.getString(cursor.getColumnIndex("it_content"));
            if (ss.length()==0){
                return ("（该日你没有记录）");
            }else {
                return (ss);
            }
        }catch (Exception e){
            return ("(查询不到这条记录)");
        }

    }

    @Override
    public void onDateSelected(@NonNull MaterialCalendarView widget, @Nullable CalendarDay date, boolean selected) {
//        设置标题
        getSupportActionBar().setTitle(FORMATTER.format(date.getDate()));
        String record=get_record_of_day(date);
        textView.setText(record);
        Add_date_color add_date_color=new Add_date_color();
        add_date_color.execute(record);
    }

    @Override
    public void onMonthChanged(MaterialCalendarView widget, CalendarDay date) {
        //noinspection ConstantConditions
        getSupportActionBar().setTitle(FORMATTER.format(date.getDate()));
    }

    @Override
    public void onDialogPositiveClick(String ss) {
        String[] whereArgs=process_date(widget.getSelectedDate());
        ContentValues values=new ContentValues();
        values.put("it_content", ss);
        values.put("it_unique_id", whereArgs[0]);
        db.replace("item_table", null, values);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

//    侧边框的pressback
    @Override
    public void onBackPressed() {
        DrawerLayout drawerLayout= (DrawerLayout) findViewById(R.id.drawer_layout);
        if(drawerLayout.isDrawerOpen(GravityCompat.START)){
           drawerLayout.closeDrawer(GravityCompat.START);
        }else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        int id=item.getItemId();
        if(id==R.id.nav_gallery){
            edit_record();
        }else if(id==R.id.nav_slideshow){
            Choose_db choose_db=new Choose_db();
            choose_db.show(getFragmentManager(),"db");
        }else if(id==R.id.illustration){
            Illustration illustration=new Illustration();
            illustration.show(getFragmentManager(),"illustration");;
        }else {
            show_time_dicker();
        }
        DrawerLayout drawer= (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void show_time_dicker() {
       new SlideDateTimePicker.Builder(getSupportFragmentManager())
               .setListener(listener)
               .build()
               .show();
    }

    public void edit_record(){
        editor.putString("record",textView.getText().toString());
        editor.putString("date",getSupportActionBar().getTitle().toString());
        editor.commit();

        update_db_dialogfragment dialog=new update_db_dialogfragment();
        dialog.show(getFragmentManager(),"hello");
    }

    @Override
    public String current_db_location() {
//        todo
//        return find_path_from_db();
        return null;
    }

    @Override
    public String[] find_all_db() {
        ArrayList temp=new ArrayList();
        Collection<File> files= FileUtils.listFiles(Environment.getExternalStorageDirectory(),
                new RegexFileFilter("calendar.db"),
                TrueFileFilter.TRUE
        );

        for(File f:files){
            if(f.getName().equals("calendar.db")){
                temp.add(f.getAbsolutePath());
            }
        }
        String [] path_list=new String[temp.size()];
        path_list= (String[]) temp.toArray(path_list);
        return path_list;
    }

    @Override
    public void write_path_db(String aboslute_path) {
        ContentValues values=new ContentValues();
        values.put("st_val", aboslute_path);
        path_db.update("main_tb", values, "st_name like ?", new String[]{"db_path"});
    }

    private class Add_date_color extends AsyncTask<String,Void,Boolean> {

        @Override
        protected void onPostExecute(Boolean b) {
            ArrayList<CalendarDay> dates=new ArrayList<>();
            dates.add(widget.getSelectedDate());
            if(b){
                widget.addDecorator(new EventDecorator(Color.RED, dates));
            }else {
                widget.addDecorator(new EventDecorator(Color.GREEN, dates));

            }
        }

        @Override
        protected Boolean doInBackground(String... params) {
            if (params[0].equals("（该日你没有记录）") || params[0].equals("(查询不到这条记录)")) {
                return false;
            }else {
                return true;
            }
        }
    }

    private SlideDateTimeListener listener=new SlideDateTimeListener() {
       @Override
       public void onDateTimeSet(Date date) {
          widget.setSelectedDate(date);
           widget.setCurrentDate(date);
       }
   };
}
