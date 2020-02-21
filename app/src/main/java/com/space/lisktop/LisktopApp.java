package com.space.lisktop;

import android.app.Application;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.preference.PreferenceManager;

public class LisktopApp extends Application {
    private static SharedPreferences sPref;

    @Override
    public void onCreate() {
        super.onCreate();
        sPref= PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
    }

    // 是否初次打开（显示介绍，及选择dock应用）
    public static void setFirstOpen(boolean b){
        sPref.edit().putBoolean("first_open",b).apply();
    }

    public static boolean getFirstOpen(){
        return sPref.getBoolean("first_open",true);
    }

    // 设置、获取是否显示右页应用图标
    public static boolean getWhetherShowIcon(){
        return sPref.getBoolean("showicon",true);
    }

    public static void setWhetherShowIcon(boolean b){
        sPref.edit().putBoolean("showicon",b).apply();
    }

    /*// 设置及获取左页格言
    public static void setMotto(String motto){
        sPref.edit().putString("motto",motto).apply();
    }

    public static String getMotto(){
        return sPref.getString("motto","长按修改提示");
    }*/

    // 应用点击后排序方式
    public static void setSortMethod(int method){
        // 0：lru；1：pop；2：restricted
        sPref.edit().putInt("sort_method_clicked",method).apply();
    }

    public static int getSortMethod(){
        return sPref.getInt("sort_method_clicked",0);
    }

    @Override
    public void onConfigurationChanged(@NonNull Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
    }
}
