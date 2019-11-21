package com.space.lisktop.obj;

import android.graphics.drawable.Drawable;

public class AppInfo {
    private String appName; //应用名
    private String appAlias;   //别名
    private String packageName;    //包名称
    private Drawable appIcon;
    private int OpendTimesToday;     //今日打开次数
    private int is_dock_app;
    private int right_index;    //在右页应用列表中的顺序


    public AppInfo()
    {}

    public AppInfo(String aName,String pName,Drawable aIcon)
    {
        this.appName=aName;
        this.packageName=pName;
        this.appIcon=aIcon;
    }

    public void setAppAlias(String appAlias) {
        this.appAlias = appAlias;
    }

    public String getAppAlias() {
        return appAlias;
    }

    public void setIs_dock_app(int is_dock_app) {
        this.is_dock_app = is_dock_app;
    }

    public void setRight_index(int right_index) {
        this.right_index = right_index;
    }

    public int getIs_dock_app() {
        return is_dock_app;
    }

    public int getRight_index() {
        return right_index;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    public String getAppName() {
        return appName;
    }

    public String getPackageName() {
        return packageName;
    }

    public Drawable getAppIcon() {
        return appIcon;
    }

    public void setAppIcon(Drawable appIcon) {
        this.appIcon = appIcon;
    }
}
