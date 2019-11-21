package com.space.lisktop.utility;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DBHelper extends SQLiteOpenHelper {
    private static DBHelper instance=null;       //单li
    private static final int DB_VERSION=1;
    private static final String DB_NAME="lisktop.db";

    public static final String TABLE_USERAPPS="userApp";
    public static final String TABLE_APPUSAGE="appUsage";
    public static final String TABLE_NOTES="note";

//
//    public static final String CREATE_MAINAPPS= "create table if not exists " + TABLE_MAINAPPS+
//            "(id integer primary key autoincrement," +
//            "package_name text ,"+
//            "app_name text," +
//            "app_icon blob)";

    public static final String CREATE_USERAPPS="create table if not exists " + TABLE_USERAPPS +
            //"app_id integer primary key autoincrement unique, " +
            "(package_name text primary key unique, " +
            "app_name text, " +
            "app_icon blob, " +
            "app_alias text, " +
            "is_dock_app int8 default 0, " +
            "app_right_index integer, " +
            "app_priority integer default 0, " +
            "app_cur_click integer default 0, " +
            "app_hidden boolean default 0)";

    public static final String CREATE_USAGE="create table if not exists " + TABLE_APPUSAGE +
            "(id integer primary key autoincrement unique," +
            "clicked_app text," +
            "click_time datetime," +
            "foreign key(clicked_app) references userApp(app_id)" +
            ")";

    public static final String CREATE_NOTE="create table if not exists " + TABLE_NOTES +
            "(id integer primary key autoincrement unique," +
            "note text" +
            ")";

    public DBHelper(Context context) {
        super(context, DB_NAME, null,DB_VERSION);
    }

    public synchronized static DBHelper getInstance(Context context)
    {
        if(instance==null)
        {
            instance=new DBHelper(context);
        }
        return instance;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_USERAPPS);
        db.execSQL(CREATE_USAGE);
        db.execSQL(CREATE_NOTE);
        Log.i("database","tables created");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        String drop_note="DROP TABLE IF EXISTS " + TABLE_NOTES;
        String drop_usage="DROP TABLE IF EXISTS " + TABLE_APPUSAGE;
        String drop_apps="DROP TABLE IF EXISTS " + TABLE_USERAPPS;
        db.execSQL(drop_note);
        db.execSQL(drop_usage);
        db.execSQL(drop_apps);
        onCreate(db);
    }
}
