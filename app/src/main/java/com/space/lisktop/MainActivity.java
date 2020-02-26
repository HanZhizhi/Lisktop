package com.space.lisktop;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.viewpager.widget.ViewPager;

import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Window;
import android.widget.ListView;

import com.space.lisktop.activities.FragLeft;
import com.space.lisktop.activities.FragRight;
import com.space.lisktop.activities.WelActivity;
import com.space.lisktop.adapters.launcherPageViewerAdapter;
import com.space.lisktop.bcastreceiver.HomePressBReceiver;
import com.space.lisktop.services.UsgStatsService;

import java.util.ArrayList;

public class MainActivity extends FragmentActivity {
    private ListView lvPackages;
    private PackageManager pManager;
    private ArrayList<Fragment> fragList;
    private FragLeft fLeft;
    private FragRight fRight;
    private launcherPageViewerAdapter pgAdapter;
    private ViewPager pager;
    private FragmentManager fragManager;

    public static Handler mainHandler;
    private HomePressBReceiver homeReceiver=null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(Window.FEATURE_NO_TITLE,Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main);

//        ActionBar actionBar = getSupportActionBar();
//        if (actionBar != null) {
//            actionBar.hide();
//        }

        Log.i("mainact","oncreate");

        if (LisktopApp.isFirstOpen()){
            Intent welIntent=new Intent(this, WelActivity.class);
            startActivity(welIntent);
            Log.i("first_open","go to wel");
            finish();
        }

        mainHandler=new Handler(){
            @Override
            public void handleMessage(@NonNull Message msg) {
                super.handleMessage(msg);
                switch (msg.what){
                    case 0:           //0:来自homepressreceiver，点击home键后返回左页面
                        Log.i("mainact","receive homeclick message");
                        pager.setCurrentItem(0,false);
                        break;
                    case 1:           //来自APPclickreceiver，
                        Log.i("mainact","receive APPclick message");
                        pager.setCurrentItem(0,false);
                        break;
                }
            }
        };

        fragManager=getSupportFragmentManager();

        initViews();

        pManager=getPackageManager();
        homeReceiver=new HomePressBReceiver();

        Intent usgIntent =new Intent(this, UsgStatsService.class);
        startService(usgIntent);

        /*if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP){
            UsageStatsManager usgManager= (UsageStatsManager) getSystemService(this.USAGE_STATS_SERVICE);
            long time = System.currentTimeMillis();
            List<UsageStats> stats = usgManager.queryUsageStats(UsageStatsManager.INTERVAL_DAILY, time - 1000 * 10, time);
            if (stats != null) {
                SortedMap<Long, UsageStats> runningTask = new TreeMap<Long, UsageStats>();
                for (UsageStats usageStats : stats) {
                    runningTask.put(usageStats.getLastTimeUsed(), usageStats);
                }
                if (runningTask.isEmpty()) {
                    //continue;
                }
                String packagename = runningTask.get(runningTask.lastKey()).getPackageName();
                Log.i("usage","dayu lollipop: "+packagename);
            }
        }
        else{
            ActivityManager am = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
            //4.获取正在开启应用的任务栈
            List<ActivityManager.RunningTaskInfo> runningTasks = am.getRunningTasks(1);
            ActivityManager.RunningTaskInfo runningTaskInfo = runningTasks.get(0);
            //5.获取栈顶的activity,然后在获取此activity所在应用的包名
            String packagename = runningTaskInfo.topActivity.getPackageName();
            Log.i("usage","xiaoyu lollipop: "+packagename);
        }*/

    }



    private void initViews()
    {
        pager=findViewById(R.id.main_viewpager);

        fragList=new ArrayList<>();
        fLeft=new FragLeft();
        fRight= new FragRight();

        fragList.add(fLeft);
        fragList.add(fRight);

        pgAdapter=new launcherPageViewerAdapter(fragManager,fragList);
        pager.setAdapter(pgAdapter);
        /*pager.setAdapter(new FragmentStatePagerAdapter(getSupportFragmentManager()) {
            @Override
            public Fragment getItem(int position) {
                return fragList.get(position);
            }

            @Override
            public int getCount() {
                return fragList.size();
            }
        });*/
        pager.setCurrentItem(0);
        pager.setOffscreenPageLimit(fragList.size());
    }

    @Override
    //屏蔽返回按键
    public boolean onKeyDown(int keyCode,KeyEvent event) {
        if(keyCode== KeyEvent.KEYCODE_BACK)
        {
            return true;//不执行父类点击事件
        }
        /*if (keyCode==KeyEvent.KEYCODE_HOME){
            pager.setCurrentItem(0,false);
            Log.i("main act KEY_HOME","clicked");
            return true;
        }*/
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onResume() {
        super.onResume();


        final IntentFilter homeFilter=new IntentFilter(Intent.ACTION_CLOSE_SYSTEM_DIALOGS);
        registerReceiver(homeReceiver,homeFilter);

        Log.i("mainact receiver","reg");
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (homeReceiver!=null){
            unregisterReceiver(homeReceiver);
            Log.i("mainact receiver","unreg");
        }
    }
}
