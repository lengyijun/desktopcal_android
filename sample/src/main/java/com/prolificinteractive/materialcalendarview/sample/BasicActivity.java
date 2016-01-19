package com.prolificinteractive.materialcalendarview.sample;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

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
public class BasicActivity extends AppCompatActivity implements OnDateSelectedListener, OnMonthChangedListener {

    private static final DateFormat FORMATTER = SimpleDateFormat.getDateInstance();
    DBopenhelper dBopenhelper;

    @Bind(R.id.calendarView)
    MaterialCalendarView widget;

    @Bind(R.id.textView)
    TextView textView;
    SQLiteDatabase db;
    Cursor cursor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_basic);
        ButterKnife.bind(this);

        widget.setSelectedDate(Calendar.getInstance());
        widget.setOnDateChangedListener(this);
        widget.setOnMonthChangedListener(this);

//        初始化设置
        onDateSelected(widget, widget.getSelectedDate(), true);
    }

    public String find_path_from_db() {
         dBopenhelper=new DBopenhelper(this,"main_tb",null,1);
         SQLiteDatabase path_db=dBopenhelper.getReadableDatabase();
         Cursor path_cursor=path_db.query("main_tb",null,"st_name like ?",new String[]{"db_path"},null,null,null);
         path_cursor.moveToFirst();
         String aboslute_path=path_cursor.getString(path_cursor.getColumnIndex("st_val"));
         try {
             db = SQLiteDatabase.openDatabase(aboslute_path,null, 0);
             db.close();
         }catch (Exception e){
              aboslute_path=find_path_from_whole_phone();
              ContentValues values=new ContentValues();
              values.put("st_val",aboslute_path);
              path_db.update("main_tb",values,"st_name like ?",new String[]{"db_path"});
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

    @Override
    public void onDateSelected(@NonNull MaterialCalendarView widget, @Nullable CalendarDay date, boolean selected) {
//        设置标题
        getSupportActionBar().setTitle(FORMATTER.format(date.getDate()));

        String [] whereArgs=process_date(date);
        try{
        db = SQLiteDatabase.openDatabase(find_path_from_db(), null, 0);
//            db = SQLiteDatabase.openDatabase("/storage/emulated/0/Android/data/nutstore.android/cache/objectcache/1/calendar.db", null, 0);
        cursor = db.query("item_table", new String[]{"it_content", "it_unique_id"}, "it_unique_id like ?", whereArgs, null, null, null);

        cursor.moveToFirst();
        String ss= cursor.getString(cursor.getColumnIndex("it_content"));
        if (ss.length()==0){
            textView.setText("（该日你没有记录）");
        }else {
            textView.setText(ss);
        }
        }catch (Exception e){
            textView.setText("(查询不到这条记录)");
        }
    }

    @Override
    public void onMonthChanged(MaterialCalendarView widget, CalendarDay date) {
        //noinspection ConstantConditions
        getSupportActionBar().setTitle(FORMATTER.format(date.getDate()));
    }

    private String getSelectedDatesString() {
        CalendarDay date = widget.getSelectedDate();
        if (date == null) {
            return "No Selection";
        }
        return FORMATTER.format(date.getDate());
    }
}
