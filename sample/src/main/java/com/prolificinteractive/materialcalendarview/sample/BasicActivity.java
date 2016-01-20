package com.prolificinteractive.materialcalendarview.sample;

import android.app.DialogFragment;
import android.app.Notification;
import android.app.NotificationManager;
import android.content.ContentValues;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;
import com.prolificinteractive.materialcalendarview.OnDateSelectedListener;
import com.prolificinteractive.materialcalendarview.OnMonthChangedListener;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.RegexFileFilter;
import org.apache.commons.io.filefilter.TrueFileFilter;

import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Collection;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Shows off the most basic usage
 */
public class BasicActivity extends AppCompatActivity implements OnDateSelectedListener, OnMonthChangedListener,update_db_dialogfragment.NoticeDialogListener {

    private static final DateFormat FORMATTER = SimpleDateFormat.getDateInstance();
    @Bind(R.id.calendarView)
    MaterialCalendarView widget;

    @Bind(R.id.textView)
    TextView textView;
    DBopenhelper dBopenhelper;
    SQLiteDatabase db;
    Cursor cursor;
    Button update_button;

    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_basic);
        ButterKnife.bind(this);
        sharedPreferences=getSharedPreferences("data_record",MODE_ENABLE_WRITE_AHEAD_LOGGING);
        editor=sharedPreferences.edit();

        update_button= (Button) findViewById(R.id.button);
        update_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editor.putString("record",textView.getText().toString());
                editor.putString("date",getSupportActionBar().getTitle().toString());
//                editor.putString("date",FORMATTER.format(widget.getSelectedDate().getDate()));
                editor.commit();

                update_db_dialogfragment dialog=new update_db_dialogfragment();
                dialog.show(getFragmentManager(),"hello");
            }
        });

        widget.setSelectedDate(Calendar.getInstance());
        widget.setOnDateChangedListener(this);
        widget.setOnMonthChangedListener(this);

//        初始化设置
        onDateSelected(widget, widget.getSelectedDate(), true);
    }

    public String find_path_from_db() {
         dBopenhelper=new DBopenhelper(this,"main_tb",null,1);
         SQLiteDatabase path_db=dBopenhelper.getReadableDatabase();
//        先尝试从数据库中读取路径
         Cursor path_cursor=path_db.query("main_tb",null,"st_name like ?",new String[]{"db_path"},null,null,null);
         path_cursor.moveToFirst();
         String aboslute_path=path_cursor.getString(path_cursor.getColumnIndex("st_val"));
         try {
             if(aboslute_path.length()==0){
                 throw new Exception();
             }
             SQLiteDatabase db_temp = SQLiteDatabase.openDatabase(aboslute_path, null, 0);
             db_temp.close();
         }catch (Exception e){
              aboslute_path=find_path_from_whole_phone();
              if(aboslute_path.length()==0){
                 Toast.makeText(this,"对不起，找不到数据库文件，请手动同步后再尝试",Toast.LENGTH_LONG).show();
                 return null;
             }
              ContentValues values=new ContentValues();
              values.put("st_val", aboslute_path);
//             把新得到的数据放到数据库中
              path_db.update("main_tb", values, "st_name like ?", new String[]{"db_path"});
         }
         path_cursor.close();
         path_db.close();
         return aboslute_path;
    }

    public String find_path_from_whole_phone(){
        String ss="";
        Collection<File> files= FileUtils.listFiles(Environment.getExternalStorageDirectory(),
                new RegexFileFilter("calendar.db"),
                TrueFileFilter.TRUE
        );

        for(File f:files){
            if(f.getName().equals("calendar.db")){
                ss=f.getAbsolutePath();
                break;
            }
        }
        return ss;
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
        cursor.close();
        db.close();
    }

    public String get_record_of_day(CalendarDay date){
        String [] whereArgs=process_date(date);
        try{
            db = SQLiteDatabase.openDatabase(find_path_from_db(), null, 0);
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
        textView.setText(get_record_of_day(date));
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
        db.replace("item_table",null,values);
    }
}
