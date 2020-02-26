package com.space.lisktop.activities;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.space.lisktop.LisktopApp;
import com.space.lisktop.R;
import com.space.lisktop.adapters.AppsLvAdapter;
import com.space.lisktop.bcastreceiver.packInfoReceiver;
import com.space.lisktop.obj.AppInfo;
import com.space.lisktop.services.AppReorderService;
import com.space.lisktop.utility.LisktopDAO;

import java.lang.ref.WeakReference;
import java.util.ArrayList;

public class FragRight extends Fragment {
    private PackageManager packageManager;
    private ListView lvApps;
    private AppsLvAdapter alAdapter;
    private ArrayList<AppInfo> arrAppInfo;
    private LisktopDAO lisktopDAO;

    private packInfoReceiver piRec;       //监听应用安装卸载
    public static ListHandler lHandler;

    private boolean if_show_icons;     //全局变量，创建adapter时初始化，appHandler收到更改时改变
    private int longclickedItem=-1;    //长按的应用号，在长按中赋值，contextMenu中使用

    public static class ListHandler extends Handler{
        WeakReference<FragRight> refer;

        public ListHandler(FragRight fr){
            this.refer=new WeakReference<>(fr);
        }

        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            FragRight fRight=refer.get();
            switch (msg.what){
                case 0:    //应用安装与卸载、隐藏、排序
                    // Log.i("right handler","receive msg 0");
                    fRight.arrAppInfo.clear();
                    fRight.arrAppInfo.addAll(fRight.lisktopDAO.getUnhiddenApps());
                    for (AppInfo app:fRight.arrAppInfo){
                        app.setIs_show_icon(fRight.if_show_icons);
                    }
                    //arrAppInfo= lisktopDAO.getAllApps();     这么写未改变构造adapter时的内存数组，刷新无效
                    fRight.alAdapter.notifyDataSetChanged();
                    break;
                case 2:   //控制应用图标显示、隐藏
                    fRight.if_show_icons=LisktopApp.getWhetherShowIcon();
                    for (AppInfo app:fRight.arrAppInfo){
                        app.setIs_show_icon(fRight.if_show_icons);
                    }
                    fRight.alAdapter.notifyDataSetChanged();
                    break;
            }
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        packageManager=this.getActivity().getPackageManager();
        lisktopDAO=new LisktopDAO(getActivity());

        piRec=new packInfoReceiver();    //在onCreate中创建防止多次接受多个
        IntentFilter filter=new IntentFilter();
        filter.addAction(Intent.ACTION_PACKAGE_ADDED);
        filter.addAction(Intent.ACTION_PACKAGE_REMOVED);
        filter.addAction(Intent.ACTION_PACKAGE_REPLACED);
        filter.addAction(Intent.ACTION_PACKAGE_INSTALL);
        filter.addDataScheme("package");
        getActivity().registerReceiver(piRec,filter);

        lHandler=new ListHandler(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View fRview=inflater.inflate(R.layout.frag_right_fragment, container, false);
        lvApps=fRview.findViewById(R.id.installed_apps);

        // 获取应用列表
        arrAppInfo= lisktopDAO.getUnhiddenApps();
        Log.i("before hide","num:"+arrAppInfo.size());
        if_show_icons= LisktopApp.getWhetherShowIcon();
        for (AppInfo app:arrAppInfo){
            app.setIs_show_icon(if_show_icons);
        }

        // ListView初始化操作
        alAdapter=new AppsLvAdapter(arrAppInfo,getContext());
        lvApps.setAdapter(alAdapter);
        setListEvents();

        return fRview;
    }

    private void setListEvents()
    {
        lvApps.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // 打开应用
                String packName=arrAppInfo.get(position).getPackageName();
                String appName=arrAppInfo.get(position).getAppName();
                Intent intent=packageManager.getLaunchIntentForPackage(packName);
                startActivity(intent);

                // 后续Service操作：重排序、数据库、界面响应
                Intent appClickServiceIntent=new Intent(getActivity(), AppReorderService.class);
                Bundle acBundle=new Bundle();
                //acBundle.putInt("click_pos",position);
                acBundle.putString("packName",packName);       //传入包名称进行后续操作
                acBundle.putString("appName",appName);
                appClickServiceIntent.putExtras(acBundle);
                getActivity().startService(appClickServiceIntent);
            }
        });
        lvApps.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                longclickedItem=position;
                return false;
            }
        });
        lvApps.setOnCreateContextMenuListener(new View.OnCreateContextMenuListener() {
            @Override
            public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
                menu.add(0,0,0,"隐藏");
                menu.add(0,1,0,"卸载");
            }
        });
    }

    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item
                .getMenuInfo();
        String pack_tlong_clicked=arrAppInfo.get(longclickedItem).getPackageName();
        switch (item.getItemId()) {
            case 0:
                // 添加操作
                Toast.makeText(getActivity(),"隐藏",Toast.LENGTH_SHORT).show();
                lisktopDAO.hideApp(pack_tlong_clicked);
                //arrAppInfo=lisktopDAO.getAllApps();
                lHandler.sendEmptyMessage(0);
                Log.i("after hide","num:"+arrAppInfo.size());
                alAdapter.notifyDataSetChanged();
                break;
            case 1:

                Uri uri = Uri.parse("package:" + pack_tlong_clicked);
                Intent intent = new Intent(Intent.ACTION_DELETE, uri);
                getActivity().startActivity(intent);
                break;
            default:
                break;
        }
        return super.onContextItemSelected(item);
    }


    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        //Log.i("frag right","ondestory adn unregistered");
        if (piRec!=null){
            getActivity().unregisterReceiver(piRec);
        }
    }
}
