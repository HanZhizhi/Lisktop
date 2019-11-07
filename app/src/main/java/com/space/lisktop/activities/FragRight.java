package com.space.lisktop.activities;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.space.lisktop.R;
import com.space.lisktop.adapters.AppsLvAdapter;
import com.space.lisktop.obj.AppInfo;

import java.util.ArrayList;
import java.util.List;

public class FragRight extends Fragment {
    private ListView lvApps;
    private PackageManager packMan;
    private AppsLvAdapter alAdapter;
    private ArrayList<AppInfo> arrAppInfo;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View fRview=inflater.inflate(R.layout.frag_right_fragment, container, false);
        initViews(fRview);
        packMan=this.getActivity().getPackageManager();
        //getInstalledApps();
        getStartableApps();
        setAppsToList();

        return fRview;
    }

    private void initViews(View rootView)
    {
        lvApps=rootView.findViewById(R.id.installed_apps);
    }

    private void getInstalledApps()
    {

        List<PackageInfo> packages=packMan.getInstalledPackages(0);

        for(PackageInfo pi:packages)
        {
            if((pi.applicationInfo.flags&ApplicationInfo.FLAG_SYSTEM)==0)
                    //    && (pi.applicationInfo.flags & ApplicationInfo.FLAG_UPDATED_SYSTEM_APP) == 0)     //用户应用
            {
                Log.i("app_user",pi.packageName);
//                String namea=pi.applicationInfo.loadLabel(packMan).toString();
//                String namepack=pi.packageName;
//                AppInfo aIn=new AppInfo(namea,namepack);
//                arrAppInfo.add(aIn);
            }
            else
                Log.i("app_sys",pi.packageName);
        }
    }

    private void getStartableApps()
    {
        arrAppInfo=new ArrayList<>();

        Intent startupIntent = new Intent(Intent.ACTION_MAIN);

        startupIntent.addCategory(Intent.CATEGORY_LAUNCHER);
        //final PackageManager packageManager = getActivity().getPackageManager();
        List<ResolveInfo> startApps = packMan.queryIntentActivities(startupIntent,0);
        for(ResolveInfo ri:startApps)
        {

            String namea=ri.loadLabel(packMan).toString();
            String namepack=ri.activityInfo.packageName;
            AppInfo aIn=new AppInfo(namea,namepack);
            arrAppInfo.add(aIn);
        }
        Log.i("startable","length:"+arrAppInfo.size());
    }

    private void setAppsToList()
    {
        alAdapter=new AppsLvAdapter(arrAppInfo,getContext());
        lvApps.setAdapter(alAdapter);
        lvApps.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent=packMan.getLaunchIntentForPackage(arrAppInfo.get(position).getPackageName());
                startActivity(intent);
            }
        });
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

}
