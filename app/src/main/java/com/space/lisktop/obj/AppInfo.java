package com.space.lisktop.obj;

public class AppInfo {
    private String appName; //应用名
    private String packageName;    //包名称

    public AppInfo()
    {}

    public AppInfo(String aName,String pName)
    {
        this.appName=aName;
        this.packageName=pName;
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
}
