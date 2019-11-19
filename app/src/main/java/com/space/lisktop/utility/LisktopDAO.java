package com.space.lisktop.utility;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.hardware.biometrics.BiometricManager;
import android.util.Log;

import com.space.lisktop.obj.AppInfo;

import java.io.ByteArrayOutputStream;
import java.sql.SQLInput;
import java.util.ArrayList;

public class LisktopDAO {
    private DBHelper dbHelper;
    private SQLiteDatabase database;
    private Context context;
    private final String[] ORDER_COLUMNS = new String[] {"id", "package_name","app_name","app_icon"};

    public LisktopDAO(Context ctx)
    {
        this.context=ctx;
        dbHelper=DBHelper.getInstance(context);
        database=dbHelper.getWritableDatabase();
    }

    public boolean isDataExist()
    {
        int count=0;
        Cursor cursor=null;

        synchronized (dbHelper)
        {
            try{
                cursor=database.query(DBHelper.TABLE_MAINAPPS,new String[]{"COUNT(id)"},null,null,null,null,null);
                if (cursor.moveToFirst())
                {
                    count=cursor.getCount();
                    Log.i("count",count+"");
                    if (count>0)
                        return true;
                }
                else {
                    Log.i("count",count+"else");
                }
            }
            catch (Exception E)
            {
                Log.i("daoerror",E.toString());
            }
            finally {
                if (cursor!=null)
                    cursor.close();
                if (database!=null)
                    database.close();
            }
        }
        return false;
    }

    public ArrayList<AppInfo> getMainApps()
    {
        ArrayList<AppInfo> mainApps=new ArrayList<>(4);
        synchronized (dbHelper){
            if (!database.isOpen())
            {
                database=dbHelper.getWritableDatabase();
            }
            Cursor cursor=database.query(DBHelper.TABLE_MAINAPPS,ORDER_COLUMNS,null,null,null,null,null);
            try {
                while (cursor.moveToNext())
                {
                    mainApps.add(parseAppInfo(cursor));
                }
            }catch (Exception e){
                e.printStackTrace();
            }finally {
                cursor.close();
                database.close();
            }
        }
        return mainApps;
    }

    private AppInfo parseAppInfo(Cursor cursor)
    {
        AppInfo ai=new AppInfo();
        ai.setAppName(cursor.getString(cursor.getColumnIndex("app_name")));
        ai.setPackageName(cursor.getString(cursor.getColumnIndex("package_name")));
        //读取blob转为drawable
        byte[] blob=cursor.getBlob(cursor.getColumnIndex("app_icon"));
        Bitmap bm=BitmapFactory.decodeByteArray(blob,0,blob.length);
        BitmapDrawable bmDrwable=new BitmapDrawable(bm);
        ai.setAppIcon(bmDrwable);
        return ai;
    }

    public void deleteTable()
    {
        synchronized (dbHelper){
            if (!database.isOpen())
            {
                database=dbHelper.getWritableDatabase();
            }
            database.beginTransaction();
            try {
                database.execSQL("delete from "+DBHelper.TABLE_MAINAPPS);
                database.setTransactionSuccessful();
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
            finally {
                database.endTransaction();
                database.close();
            }
        }
//        try{
//            db=dbHelper.getWritableDatabase();
//            db.beginTransaction();
//            int delNums=db.delete(DBHelper.TABLE_MAINAPPS,null,null);
//            Log.i("deleted","delete "+delNums);
//        }
//        catch (Exception e)
//        {
//
//        }
//        finally {
//            if (db!=null)
//                db.close();
//        }
    }

    public boolean writeMainApps(ArrayList<AppInfo> selectedApps)
    {
        synchronized (dbHelper){
            if (!database.isOpen())
            {
                database=dbHelper.getWritableDatabase();
            }
            //开始插入
            database.beginTransaction();
            try {
                for(int i=0;i<selectedApps.size();i++)
                {
                    Log.i("towrite","idx:"+i+"packName:"+selectedApps.get(i).getPackageName());
                    ContentValues value=new ContentValues();
                    value.put("id",i);
                    value.put("package_name",selectedApps.get(i).getPackageName());
                    value.put("app_name",selectedApps.get(i).getAppName());
                    //drawable转为bitmap使用字节输出流
//                    BitmapDrawable bmDraw=(BitmapDrawable)selectedApps.get(i).getAppIcon();
                    Bitmap bmp= getBitmapFromDrawable(selectedApps.get(i).getAppIcon());         // bmDraw.getBitmap();
                    ByteArrayOutputStream outStream=new ByteArrayOutputStream();
                    bmp.compress(Bitmap.CompressFormat.PNG,100,outStream);
                    value.put("app_icon",outStream.toByteArray());
                    database.insert(DBHelper.TABLE_MAINAPPS,null,value);
                }
                database.setTransactionSuccessful();
            }catch (Exception e){
                e.printStackTrace();
                return false;
            }finally {
                database.endTransaction();
                database.close();
            }
        }
        return true;
    }

    static private Bitmap getBitmapFromDrawable(Drawable drawable) {
        final Bitmap bmp = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        final Canvas canvas = new Canvas(bmp);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);
        return bmp;
    }

    // 无同步，报错：android.database.sqlite.SQLiteDatabaseLockedException: database is locked (code 5 SQLITE_BUSY)
    public boolean writeMainAppsOrg(ArrayList<AppInfo> selectedApps)
    {
        SQLiteDatabase db=null;
        try {
            db=dbHelper.getWritableDatabase();
            db.beginTransaction();
            for(int i=0;i<selectedApps.size();i++)
            {
                Log.i("towrite","idx:"+i+"packName:"+selectedApps.get(i).getPackageName());
                ContentValues value=new ContentValues();
                value.put("id",i);
                value.put("package_name",selectedApps.get(i).getPackageName());
                value.put("app_name",selectedApps.get(i).getAppName());
                value.put("app_index",i);
                db.insert(DBHelper.TABLE_MAINAPPS,null,value);
            }
            db.setTransactionSuccessful();
            return true;
            //db.execSQL();
        }
        catch (Exception ex)
        {
            Log.i("daoerror_insert",ex.toString());
        }
        finally {
            if (db!=null)
            {
                //db.endTransaction();
                db.close();
            }
        }
        return false;
    }
}
