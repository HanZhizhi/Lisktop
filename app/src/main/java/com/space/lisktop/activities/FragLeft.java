package com.space.lisktop.activities;

import android.app.ActivityManager;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Canvas;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.SystemClock;
import android.provider.AlarmClock;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextClock;
import android.widget.TextView;
import android.widget.Toast;

import com.space.lisktop.views.Dock;
import com.space.lisktop.R;
import com.space.lisktop.adapters.TodoAdapter;
import com.space.lisktop.bcastreceiver.TimeReceiver;
import com.space.lisktop.bcastreceiver.packInfoReceiver;
import com.space.lisktop.obj.AppInfo;
import com.space.lisktop.obj.RecyclerDecorator;
import com.space.lisktop.services.AppReorderService;
import com.space.lisktop.utility.LisktopDAO;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class FragLeft extends Fragment implements View.OnClickListener {
    private Context context;
    private TextView tvSettings;
    private TextClock tcTime;
    private LisktopDAO lisktopDAO;
    private ArrayList<AppInfo> mainApps;
    private PackageManager packMan;
    private IntentFilter intentFilter;
    private TimeReceiver timeChangeReceiver;
    private AlarmManager alarmManager;
    private PendingIntent pendingIntent;
    private int dock_icon_size=60;     //dock栏图标的大小,单位dp，默认60
    private Dock dock1;

    private RecyclerView recTodos;
    private TodoAdapter tdAdapter;
    private RecyclerView.LayoutManager tdLayouter;
    private ItemTouchHelper itemTouchHelper;
    private ArrayList<String> todoList;

    private TextView tvAddTodo;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context=getActivity();
        packMan=context.getPackageManager();
        lisktopDAO =new LisktopDAO(context);
        Log.i("fd","oncreate");

        //使用系统timetick进行监听
        /*intentFilter = new IntentFilter();
        intentFilter.addAction(Intent.ACTION_TIME_TICK);//每分钟变化
        intentFilter.addAction(Intent.ACTION_DATE_CHANGED);//日期变化
        intentFilter.addAction(Intent.ACTION_TIMEZONE_CHANGED);//设置了系统时区
        intentFilter.addAction(Intent.ACTION_TIME_CHANGED);//设置了系统时间

        timeChangeReceiver = new TimeReceiver();
        context.registerReceiver(timeChangeReceiver, intentFilter);*/

        /**
         * 使用AlarmManager进行设定
         */
        alarmManager=(AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
        Intent intent=new Intent(context, packInfoReceiver.class);      //创建Intent时写入目标receiver
        intent.setAction("hahaTest");
        /* 启动service
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            pendingIntent = PendingIntent.getForegroundService(context, 0,
                    intent, PendingIntent.FLAG_UPDATE_CURRENT);
            Log.i("pendingIntent","create > 8.0");
        }else {
            pendingIntent = PendingIntent.getService(context, 0,
                    intent, PendingIntent.FLAG_UPDATE_CURRENT);
            Log.i("pendingIntent","create < 8.0");
        }*/
        pendingIntent=PendingIntent.getBroadcast(context,0,intent,0);

        //通过calendar设置执行时间
        Calendar c1=Calendar.getInstance(),c2=Calendar.getInstance();

        /*c.set(Calendar.YEAR,2019);
        c.set(Calendar.MONTH,Calendar.JUNE);//也可以填数字，0-11,一月为0
        c.set(Calendar.DAY_OF_MONTH, 28);
        c.set(Calendar.HOUR_OF_DAY, 19);
        c.set(Calendar.MINUTE, 50);
        c.set(Calendar.SECOND, 0);
        //设定时间为 2011年6月28日19点50分0秒*/
        c1.set(2020, 1,3, 12,59, 10);
        c2.set(2020, 1,3, 12,51, 10);

        Log.i("alarmaneger compare",""+(c2.getTimeInMillis()-c1.getTimeInMillis()));
        //也可以写在一行里
        //am.set(AlarmManager.RTC_WAKEUP, c.getTimeInMillis(), pi);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {//6.0低电量模式需要使用该方法触发定时任务
            alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, c1.getTimeInMillis(), pendingIntent);    // SystemClock.elapsedRealtime()+2000
            Log.i("alarmaneger set","create > m ："+System.currentTimeMillis()+":"+c1.getTimeInMillis());
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {//4.4以上 需要使用该方法精确执行时间
            Log.i("alarmaneger set","create > 4.4");
            alarmManager.setExact(AlarmManager.ELAPSED_REALTIME_WAKEUP, SystemClock.elapsedRealtime()+1000, pendingIntent);
        } else {//4。4一下 使用老方法
            Log.i("pendingIntent","create <4.4");
            alarmManager.setRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP, SystemClock.elapsedRealtime(), 1000, pendingIntent);
        }
    }

    public boolean isServiceRunning(Class<?> serviceClass) {
        ActivityManager activityManager = (ActivityManager) context
                .getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningServiceInfo> serviceList = activityManager
                .getRunningServices(Integer.MAX_VALUE);
        if(serviceList == null|| serviceList.size() == 0)
            return false;
        for(ActivityManager.RunningServiceInfo info : serviceList) {
            if(info.service.getClassName().equals(serviceClass.getName()))
                return true;
        }
        return false;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View fragLeftView=inflater.inflate(R.layout.frag_left_fragment, container, false);
        Log.i("fd","oncreate view");
        initView(fragLeftView);
        return fragLeftView;
    }

    private void initView(View rootV)
    {
        tvSettings=rootV.findViewById(R.id.tvSettings);
        tvSettings.setOnClickListener(this);

        dock1=rootV.findViewById(R.id.left_dock);

        tcTime=rootV.findViewById(R.id.clock_time);
        tcTime.setOnClickListener(this);

        recTodos=rootV.findViewById(R.id.todos);
        todoList= lisktopDAO.getMainTodos();
        tdAdapter=new TodoAdapter(todoList);
        tdAdapter.setOnItemClickListener(new TodoAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
            }

            @Override
            public void onItemLongClick(View view, int position) {
            }

            @Override
            public void onChecked(View view, int position) {
                String deled=tdAdapter.delete(position);
                lisktopDAO.finishTodo(deled);
            }
        });
        recTodos.setAdapter(tdAdapter);
        recTodos.addItemDecoration(new RecyclerDecorator(getActivity(),LinearLayoutManager.VERTICAL));
        ItemTouchHelper.Callback callBack=new ItemTouchHelper.Callback() {
            @Override
            public int getMovementFlags(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder) {
                //return makeMovementFlags(0,ItemTouchHelper.START | ItemTouchHelper.END);  //滑动删除
                return makeMovementFlags(ItemTouchHelper.RIGHT | ItemTouchHelper.LEFT | ItemTouchHelper.DOWN | ItemTouchHelper.UP, 1);
            }

            @Override
            public boolean isLongPressDragEnabled() {
                return true;
            }

            @Override
            public boolean isItemViewSwipeEnabled() {
                return true;
            }

            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                tdAdapter.move(viewHolder.getAdapterPosition(), target.getAdapterPosition());
                return true;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                tdAdapter.delete(viewHolder.getAdapterPosition());
            }

            /**
             * Item被选中时候，改变Item的背景
             */
            @Override
            public void onSelectedChanged(RecyclerView.ViewHolder viewHolder, int actionState) {
                //  item被选中的操作
                if(actionState != ItemTouchHelper.ACTION_STATE_IDLE) {
                    viewHolder.itemView.setBackgroundColor(Color.parseColor("#aaaaaa"));
                }
                super.onSelectedChanged(viewHolder, actionState);
            }

            /**
             * 移动过程中重新绘制Item，随着滑动的距离，设置Item的透明度
             */
            @Override
            public void onChildDraw(Canvas c, RecyclerView recyclerView,
                                    RecyclerView.ViewHolder viewHolder,
                                    float dX, float dY, int actionState, boolean isCurrentlyActive) {
                float x = Math.abs(dX) + 0.5f;
                float width = viewHolder.itemView.getWidth();
                float alpha = 1f - x / width;
                viewHolder.itemView.setAlpha(alpha);
                //viewHolder.itemView.setScaleX(1.2f);
                //viewHolder.itemView.setScaleY(1.2f);
                super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState,
                        isCurrentlyActive);
            }

            /**
             * 用户操作完毕或者动画完毕后调用，恢复item的背景和透明度
             */
            @Override
            public void clearView(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
                // 操作完毕后恢复颜色
                viewHolder.itemView.setBackgroundColor(Color.WHITE);
                viewHolder.itemView.setAlpha(1.0f);
                //viewHolder.itemView.setScaleX(1.0f);
                //viewHolder.itemView.setScaleY(1.0f);
                super.clearView(recyclerView, viewHolder);
            }
        };
        itemTouchHelper=new ItemTouchHelper(callBack);
        itemTouchHelper.attachToRecyclerView(recTodos);
        tdLayouter=new LinearLayoutManager(getActivity());
        recTodos.setLayoutManager(tdLayouter);
        recTodos.setItemAnimator(new DefaultItemAnimator());

        tvAddTodo=rootV.findViewById(R.id.todo_add);
        tvAddTodo.setOnClickListener(this);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        // TODO: Use the ViewModel
        Log.i("fd","activity created");
    }

    @Override
    //TODO:onResume需要大改
    public void onResume() {
        mainApps=lisktopDAO.getDockApps();

        Log.i("dockleft","num"+mainApps.size());
        if(mainApps.size() >0)
            dock1.addDockApps(mainApps);

        super.onResume();
    }

    private void addMainApp(ArrayList<AppInfo> infoList)
    {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        float density = displayMetrics.density;
        int screenWidth=displayMetrics.widthPixels;
        int numApps=infoList.size();
        // 将屏幕宽度转换为dp，减去左右pad共40dp,根据app个数计算每个app左右的pad值
        int totalWidthInDP=(int)(screenWidth/density-40);
        int pads=(totalWidthInDP-dock_icon_size*numApps)/numApps/2;         //计算每个图标左右padding

        //将图标大小和左右间距转换为像素在屏幕应用
        LinearLayout.LayoutParams params1 = new LinearLayout.LayoutParams(
                (int)(60 * density),
                (int)(60 * density));
        params1.leftMargin = (int)(pads*density);
        params1.rightMargin=(int)(pads*density);

        Log.i("logo",totalWidthInDP+"-"+pads+"--"+numApps+"-"+screenWidth);

        for(int i=0;i<infoList.size();i++)
        {
            ImageView iv=new ImageView(context);
            iv.setImageDrawable(infoList.get(i).getAppIcon());

            iv.setTag(i);
            iv.setOnClickListener(this);
            //layoutMainApps.addView(iv,params1);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId())
        {
            case R.id.tvSettings:
                startActivity(new Intent(context, SettingsActivity.class));
                break;
            case R.id.clock_time:
                Intent itClock=new Intent(AlarmClock.ACTION_SET_ALARM);
                itClock.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(itClock);
                break;
            case R.id.todo_add:
                //AlertDialog
                final EditText etv=new EditText(getActivity());
                etv.setText("");
                AlertDialog.Builder adbuilder=new AlertDialog.Builder(getActivity());
                adbuilder.setTitle(R.string.todo_prompt).setView(etv)
                        .setPositiveButton(R.string.dialog_positive, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                String todoInfo=etv.getText().toString();
                                if(todoList.contains(todoInfo)){
                                    Toast.makeText(getActivity(),"待办已存在！",Toast.LENGTH_SHORT).show();
                                }
                                else{
                                    tdAdapter.addNewItem(todoInfo);
                                    lisktopDAO.insertTodo(todoInfo);
                                }
                            }
                        })
                        .setNegativeButton(R.string.dialog_negative,null);
                adbuilder.create().show();
                tdLayouter.scrollToPosition(0);
                break;
            default:     // 点击dock应用：启动，service。
                int idx=(int)v.getTag();
                AppInfo appInfo=mainApps.get(idx);
                String pack=appInfo.getPackageName(),app=appInfo.getAppName();
                Intent go=packMan.getLaunchIntentForPackage(pack);
                startActivity(go);

                Intent appClickServiceIntent=new Intent(getActivity(), AppReorderService.class);
                Bundle acBundle=new Bundle();
                acBundle.putString("packName",pack);       //传入包名称进行后续操作
                acBundle.putString("appName",app);
                appClickServiceIntent.putExtras(acBundle);
                getActivity().startService(appClickServiceIntent);
                break;
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (timeChangeReceiver!=null){
            context.unregisterReceiver(timeChangeReceiver);
        }
    }
}
