package com.space.lisktop.utility;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.Icon;
import android.util.Log;

import com.space.lisktop.obj.AppInfo;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class PackageManageHelper {
    private PackageManager packMan;
    private Context context;

    public PackageManageHelper(Context context)
    {
        this.context=context;
        this.packMan=context.getPackageManager();
    }

    public ArrayList<AppInfo> getStartableApps(boolean sortAppsByName)
    {
        ArrayList<AppInfo> launcherApps=new ArrayList<>();

        Intent startupIntent = new Intent(Intent.ACTION_MAIN);
        startupIntent.addCategory(Intent.CATEGORY_LAUNCHER);

        List<ResolveInfo> startApps = packMan.queryIntentActivities(startupIntent,0);

        for(ResolveInfo ri:startApps)
        {
            String namea=ri.loadLabel(packMan).toString();
            String namepack=ri.activityInfo.packageName;
            Drawable icona=ri.loadIcon(packMan);
            AppInfo aIn=new AppInfo(namea,namepack,icona);
            if(context.getPackageName().equals(namepack)) continue;
            launcherApps.add(aIn);
        }

        if (sortAppsByName)
        {
            //排序
            Collections.sort(launcherApps, new Comparator<AppInfo>() {
                @Override
                public int compare(AppInfo o1, AppInfo o2) {
                    return String.CASE_INSENSITIVE_ORDER.compare(o1.getAppName(),o2.getAppName());
                }
            });
        }
        return launcherApps;
    }

    private ArrayList<AppInfo> getInstalledApps()
    {
        ArrayList<AppInfo> installedApps=new ArrayList<>();

        List<PackageInfo> packages=packMan.getInstalledPackages(0);

        for(PackageInfo pi:packages)
        {
            if((pi.applicationInfo.flags& ApplicationInfo.FLAG_SYSTEM)==0)
            //    && (pi.applicationInfo.flags & ApplicationInfo.FLAG_UPDATED_SYSTEM_APP) == 0)     //用户应用
            {
                Log.i("app_user",pi.packageName);
                String namea=pi.applicationInfo.loadLabel(packMan).toString();
                String namepack=pi.packageName;
                Drawable icona=pi.applicationInfo.loadIcon(packMan);
                AppInfo aIn=new AppInfo(namea,namepack,icona);
                if(! "com.space.lisktop".equals(namepack))
                    installedApps.add(aIn);
            }
            else
                Log.i("app_sys",pi.packageName);
        }
        return installedApps;
    }
}
