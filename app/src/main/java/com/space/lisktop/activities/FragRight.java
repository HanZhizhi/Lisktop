package com.space.lisktop.activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.preference.PreferenceManager;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.space.lisktop.R;
import com.space.lisktop.adapters.AppsLvAdapter;
import com.space.lisktop.bcastreceiver.AppClickBroadcastReceiver;
import com.space.lisktop.obj.AppInfo;
import com.space.lisktop.utility.PackageManageHelper;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class FragRight extends Fragment {
    private PackageManager packageManager;
    private ListView lvApps;
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
        packageManager=this.getActivity().getPackageManager();
        //TODO :获取（排序的）应用显示列表，当前直接packagemanager读取，改为读数据库
        //获取应用列表
        arrAppInfo= new PackageManageHelper(getActivity()).getStartableApps(true);
        //适配至ListView
        setAppsToList();

        return fRview;
    }

    private void initViews(View rootView)
    {
        lvApps=rootView.findViewById(R.id.installed_apps);
    }

    private void setAppsToList()
    {
        // 获取系统设置判断是否显示应用图标
        SharedPreferences sPref=PreferenceManager.getDefaultSharedPreferences(getActivity());
        Log.i("leftshowicon","show:"+sPref.getBoolean("showicon",false));
        boolean if_show_icons=sPref.getBoolean("showicon",false);

        alAdapter=new AppsLvAdapter(arrAppInfo,getContext(),if_show_icons);
        lvApps.setAdapter(alAdapter);
        lvApps.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //TODO:改为appclickservice中统一处理(回左页，reArrange，写数据库)
                String packName=arrAppInfo.get(position).getPackageName();
                Intent intent=packageManager.getLaunchIntentForPackage(packName);
                startActivity(intent);

//                Intent AppClickIntent=new Intent();
//                Bundle acBundle=new Bundle();
//                acBundle.putString("appPackName",packName);       //传入包名称进行后续操作
//                AppClickIntent.putExtras(acBundle);
//                getActivity().startService(AppClickIntent);

                Intent clickIntent=new Intent(getActivity(), AppClickBroadcastReceiver.class);
                clickIntent.setAction("APP_CLICK_ACTION");
                clickIntent.putExtra("package_name",packName);
                getActivity().sendBroadcast(clickIntent,"com.space.lisktop.permission_app_click");

                reArrangeApps(position,packName);
            }
        });
    }

    // 传入当前打开的序号及包名，以便进一步操作
    private void reArrangeApps(int arg,String pack)
    {
        AppInfo aIfo=arrAppInfo.get(arg);
        String packageName=arrAppInfo.get(arg).getPackageName();
        if (packageName.equals(pack))     //验证
        {
            //Log.i("length","before remove"+arrAppInfo.size());
            arrAppInfo.remove(arrAppInfo.get(arg));
            //Log.i("length","after remove"+arrAppInfo.size());
            //Log.i("length","before add"+arrAppInfo.size()+arrAppInfo.get(0).getAppName());
            arrAppInfo.add(0,aIfo);
            //Log.i("length","after add"+arrAppInfo.size()+arrAppInfo.get(0).getAppName());
            alAdapter.notifyDataSetChanged();
            //TODO:以上为LRU实现，改为读写数据库实现分类控制
        }

    }

    @Override
    public void onResume() {
        setAppsToList();
        super.onResume();
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
