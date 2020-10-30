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

import com.space.lisktop.activities.FragRight;
import com.space.lisktop.obj.AppInfo;

import java.io.ByteArrayOutputStream;
import java.sql.SQLInput;
import java.util.ArrayList;

public class LisktopDAO {
    private DBHelper dbHelper;
    private SQLiteDatabase database;
    private String table_apps=DBHelper.TABLE_USERAPPS,table_usage=DBHelper.CREATE_USAGE,table_note=DBHelper.TABLE_NOTES;

    private Context context;
    private final String[] ORDER_COLUMNS = new String[] {"id", "package_name","app_name","app_icon"};

    public LisktopDAO(Context ctx)
    {
        this.context=ctx;
        dbHelper=DBHelper.getInstance(context);
        database=dbHelper.getWritableDatabase();
    }

    //传入表名查询其中记录个数并返回是否存在数据
    public boolean isDataExist(String tableName)
    {
        int count=0;
        Cursor cursor=null;

        synchronized (dbHelper)
        {
            try{
                cursor=database.query(tableName,new String[]{"COUNT(id)"},null,null,null,null,null);
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

    //获取右页列表应用按right_index排序
    public ArrayList<AppInfo> getUnhiddenApps(){
        ArrayList<AppInfo> appList=new ArrayList<>(96);
        synchronized (dbHelper){
            if (!database.isOpen())
            {
                database=dbHelper.getWritableDatabase();
            }
            Cursor cursor=database.query(table_apps,null,"app_hidden=?",new String[]{"0"},null,null,"app_right_index");
            try {
                while (cursor.moveToNext()){
                    appList.add(parseAppInfo(cursor));
                }
            }catch (Exception e){
                e.printStackTrace();
            }finally {
                cursor.close();
                database.close();
            }
        }
        return appList;
    }

    //获取已隐藏的应用
    public ArrayList<AppInfo> getHiddenApps(){
        ArrayList<AppInfo> appList=new ArrayList<>(32);
        synchronized (dbHelper){
            if (!database.isOpen())
            {
                database=dbHelper.getWritableDatabase();
            }
            Cursor cursor=database.query(table_apps,null,"app_hidden=?",new String[]{"1"},null,null,"app_name");
            try {
                while (cursor.moveToNext()){
                    appList.add(parseAppInfo(cursor));
                }
            }catch (Exception e){
                e.printStackTrace();
            }finally {
                cursor.close();
                database.close();
            }
        }
        return appList;
    }

    //查询dockApp按顺序返回
    public ArrayList<AppInfo> getDockApps()
    {
        ArrayList<AppInfo> dockApps=new ArrayList<>(5);
        synchronized (dbHelper){
            if (!database.isOpen())
            {
                database=dbHelper.getWritableDatabase();
            }
            //Cursor cursor=database.query(DBHelper.TABLE_MAINAPPS,ORDER_COLUMNS,null,null,null,null,null);
            //String queryDockApp="select * from "+ table_apps +" where is_dock_app != 0 order by is_dock_app";
            Cursor cursor=database.query(table_apps,null,"is_dock_app!=?",new String[]{"0"},null,null,"is_dock_app asc","5");
            try {
                while (cursor.moveToNext())
                {
                    dockApps.add(parseAppInfo(cursor));
                }
            }catch (Exception e){
                e.printStackTrace();
            }finally {
                cursor.close();
                database.close();
            }
        }
        return dockApps;
    }

    //按顺序将ResolveInfo获取的全部应用写入数据库
    public void writeInstalledAppsWithOrder(ArrayList<AppInfo> userApps){
        synchronized (dbHelper) {
            if (!database.isOpen()) {
                database = dbHelper.getWritableDatabase();
            }
            for(int i=0;i<userApps.size();i++){


                AppInfo appInfo=userApps.get(i);
                String nameOfPackage=appInfo.getPackageName();

                if(context.getPackageName().equals(nameOfPackage)){
                    Log.i("writeJump", "跳过本包"+nameOfPackage);
                    continue;
                }

                ContentValues value=new ContentValues();
                value.put("package_name",nameOfPackage);
                value.put("app_name",appInfo.getAppName());

                //drawable转为bitmap使用字节输出流
//                    BitmapDrawable bmDraw=(BitmapDrawable)selectedApps.get(i).getAppIcon();
                Bitmap bmp= getBitmapFromDrawable(appInfo.getAppIcon());         // bmDraw.getBitmap();
                ByteArrayOutputStream outStream=new ByteArrayOutputStream();
                bmp.compress(Bitmap.CompressFormat.PNG,100,outStream);
                value.put("app_icon",outStream.toByteArray());

                value.put("app_alias",appInfo.getAppAlias());
                value.put("is_dock_app",0);
                value.put("app_right_index",(i+1));

                database.insert(table_apps,null,value);
            }
        }
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

        ai.setAppAlias(cursor.getString(cursor.getColumnIndex("app_alias")));
        ai.setIs_dock_app(cursor.getInt(cursor.getColumnIndex("is_dock_app")));
        ai.setRight_index(cursor.getInt(cursor.getColumnIndex("app_right_index")));
        return ai;
    }

    //取消全部is_dock_app值，改为0
    public void cancelDockApp()
    {
        synchronized (dbHelper){
            if (!database.isOpen())
            {
                database=dbHelper.getWritableDatabase();
            }
            database.beginTransaction();
            try {
                //database.execSQL("delete from "+DBHelper.TABLE_MAINAPPS);
                database.execSQL("update " + table_apps +" set is_dock_app = 0");
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
    }

    public boolean writeDockApps(ArrayList<AppInfo> selectedApps)
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
                    String dockapp_packageName=selectedApps.get(i).getPackageName();
                    String dockapp_appName=selectedApps.get(i).getAppName();
                    database.execSQL("update " + table_apps+" set is_dock_app = "+(i+1)+" where package_name = \"" +dockapp_packageName+"\" and app_name =\"" + dockapp_appName +"\"");
                    Log.i("write finish","dockApp:"+(i+1)+"packName:"+dockapp_packageName);
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

    //发生点击后重新写入应用的app_right_index
    public void reOrderApps(ArrayList<AppInfo> apps)
    {
        //update "userApp" set app_right_index =21 where app_name="Alipay"
        synchronized (dbHelper) {
            if (!database.isOpen()) {
                database = dbHelper.getWritableDatabase();
            }
            database.beginTransaction();
            try {
                for (int i=0;i<apps.size();i++)
                {
                    String packageName=apps.get(i).getPackageName(),appName=apps.get(i).getAppName();
                    if(context.getPackageName().equals(packageName)){
                        Log.i("reOrderJump", "跳过本包"+packageName);
                        continue;
                    }

                    int app_index= i+1; //apps.get(i).getRight_index();
                    String sqlUpdate="update "+table_apps+" set app_right_index ="+app_index +" where package_name= \""+packageName+"\" and app_name =\"" + appName +"\"";
                    database.execSQL(sqlUpdate);
                    /*ContentValues cv=new ContentValues();
                    cv.put("app_right_index",(i+1));
                    database.update(table_apps,cv,"package_name=?",new String[]{packageName});*/
                    Log.i("reOrder","wrote "+apps.get(i).getAppName()+",index:"+(i+1));
                }
                database.setTransactionSuccessful();
            }catch (Exception e){
                e.printStackTrace();
            }finally {
                database.endTransaction();
                database.close();
            }
        }
    }

    public int getRightIndex(String pName_ri){    //,String aName_ri
        synchronized (dbHelper){
            if (!database.isOpen()){
                database=dbHelper.getWritableDatabase();
            }
            String sqlRightIndex="select app_right_index from "+table_apps+" where package_name=\""+pName_ri+"\"";    // and app_name=\""+aName_ri+"\"";
            Cursor cursorRightIndex=database.rawQuery(sqlRightIndex,null);
            cursorRightIndex.moveToFirst();
            try {
                int rightIndex=cursorRightIndex.getInt(cursorRightIndex.getColumnIndex("app_right_index"));
                return rightIndex;
            }catch (Exception e){
                return -1;
            }
        }
    }

    public void hideApp(String packageName){
        synchronized (dbHelper){
            if(!database.isOpen()){
                database=dbHelper.getWritableDatabase();
            }
            database.beginTransaction();
            try {
                String hideSql="update "+table_apps+" set app_hidden=1 where package_name=\""+packageName+"\"";
                database.execSQL(hideSql);
                database.setTransactionSuccessful();
            }catch (Exception e){
                e.printStackTrace();
            }finally {
                database.endTransaction();
                database.close();
            }
        }
    }

    public void cancelHideApp(String packageName){
        synchronized (dbHelper){
            if(!database.isOpen()){
                database=dbHelper.getWritableDatabase();
            }
            database.beginTransaction();
            try {
                String hideSql="update "+table_apps+" set app_hidden=0 where package_name=\""+packageName+"\"";
                database.execSQL(hideSql);
                database.setTransactionSuccessful();
            }catch (Exception e){
                e.printStackTrace();
            }finally {
                database.endTransaction();
                database.close();
            }
        }
    }

    static private Bitmap getBitmapFromDrawable(Drawable drawable) {
        final Bitmap bmp = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        final Canvas canvas = new Canvas(bmp);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);
        return bmp;
    }

    //安装应用后添加
    public void insertInstalledApp(String packName_insert,String appName_insert,Drawable appIcon)
    {
        synchronized (dbHelper) {
            if (!database.isOpen()) {
                database = dbHelper.getWritableDatabase();
            }
            //判断ADDED的package是否已存在
            String sqlPcgExist="select count(package_name) from "+table_apps+" where package_name=\""+packName_insert+"\" and app_name= \""+appName_insert+"\"";
            Cursor cursorExist=database.rawQuery(sqlPcgExist,null);
            cursorExist.moveToFirst();
            int existPck=cursorExist.getInt(cursorExist.getColumnIndex("count(package_name)"));
            if (existPck>0){
                Log.i("rawQery","包已存在");
                return;
            }
            //查询当前应用个数/或最大right_index以确定插入的app_right_index
            String sqlCur="select max(app_right_index) from "+table_apps;
            Cursor cursorMaxIndex=database.rawQuery(sqlCur,null);
            cursorMaxIndex.moveToFirst();
            int curRight=cursorMaxIndex.getInt(cursorMaxIndex.getColumnIndex("max(app_right_index)"));
            //插入新的package
            ContentValues cv=new ContentValues();
            cv.put("package_name",packName_insert);
            cv.put("app_name",appName_insert);

            //drawable转为bitmap使用字节输出流
//                    BitmapDrawable bmDraw=(BitmapDrawable)selectedApps.get(i).getAppIcon();
            Bitmap bmp= getBitmapFromDrawable(appIcon);         // bmDraw.getBitmap();
            ByteArrayOutputStream outStream=new ByteArrayOutputStream();
            bmp.compress(Bitmap.CompressFormat.PNG,100,outStream);
            cv.put("app_icon",outStream.toByteArray());

            cv.put("app_alias",appName_insert);
            cv.put("is_dock_app",0);
            cv.put("app_right_index",(curRight+1));
            database.insert(table_apps,null,cv);
            Log.i("rawQuery","cur"+curRight+"已插入");
        }
    }

    private int getAppNums()
    {
        synchronized (dbHelper){
            if (!database.isOpen()){
                database=dbHelper.getWritableDatabase();
            }
            String sqlCount="select count(*) from \""+table_apps+"\"";
            Cursor cursor=database.rawQuery(sqlCount,null);
            cursor.moveToFirst();
            return cursor.getInt(cursor.getColumnIndex("count(*)"));
        }
    }

    //卸载后从数据库删除，更新列表
    public void deleteUninstalledApp(String package_name)
    {
        synchronized (dbHelper){
            if (!database.isOpen()) {
                database = dbHelper.getWritableDatabase();
            }

            Log.i("删除前",getAppNums()+"个应用");
            database.delete(table_apps,"package_name=?",new String[]{package_name});
            Log.i("删除后",getAppNums()+"个应用");
        }
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
                db.insert(table_apps,null,value);
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


    // 待办事项相关
    public ArrayList<String> getMainTodos(){
        ArrayList<String> todos=new ArrayList<>(32);
        synchronized (dbHelper){
            if (!database.isOpen())
            {
                database=dbHelper.getWritableDatabase();
            }
            Cursor cursor=database.rawQuery("select * from \""+table_note +"\" where finished =0 order by id",null);
            try {
                while (cursor.moveToNext()){
                    todos.add(cursor.getString(cursor.getColumnIndex("note_todo")));
                }
            }catch (Exception e){
                e.printStackTrace();
            }finally {
                cursor.close();
                database.close();
            }
        }
        return todos;
    }

    public void insertTodo(String todo){
        synchronized (dbHelper){
            if (!database.isOpen()){
                database=dbHelper.getWritableDatabase();
            }
            ContentValues cv=new ContentValues();
            cv.put("note_todo",todo);
            cv.put("finished",0);
            database.insert(table_note,null,cv);
        }
    }

    public void finishTodo(String todo){
        synchronized (dbHelper){
            if(!database.isOpen()){
                database=dbHelper.getWritableDatabase();
            }
            database.beginTransaction();
            try {
                //database.execSQL("delete from "+DBHelper.TABLE_MAINAPPS);
                database.execSQL("update " + table_note +" set finished = 1 where note_todo = \""+todo+"\"");
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
    }

}
