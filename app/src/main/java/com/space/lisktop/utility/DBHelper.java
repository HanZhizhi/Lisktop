package com.space.lisktop.utility;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

public class DBHelper extends SQLiteOpenHelper {
    private static DBHelper instance=null;       //单li
    private static final int DB_VERSION=1;
    private static final String DB_NAME="lisktop.db";
    public static final String TABLE_MAINAPPS="mainApps";


    public static final String CREATE_MAINAPPS= "create table if not exists " + TABLE_MAINAPPS+
            "(id integer primary key autoincrement," +
            "package_name text ,"+
            "app_name text," +
            "app_icon blob)";

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
        db.execSQL(CREATE_MAINAPPS);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        String drop="DROP TABLE IF EXISTS " + TABLE_MAINAPPS;
        db.execSQL(drop);
        onCreate(db);
    }
}
