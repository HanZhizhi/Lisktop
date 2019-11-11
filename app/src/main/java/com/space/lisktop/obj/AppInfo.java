package com.space.lisktop.obj;

import android.graphics.drawable.Drawable;

public class AppInfo {
    private String appName; //应用名
    private String appAlias;   //别名
    private String packageName;    //包名称
    private Drawable appIcon;
    private int OpendTimesToday;     //今日打开次数

    public AppInfo()
    {}

    public AppInfo(String aName,String pName,Drawable aIcon)
    {
        this.appName=aName;
        this.packageName=pName;
        this.appIcon=aIcon;
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
