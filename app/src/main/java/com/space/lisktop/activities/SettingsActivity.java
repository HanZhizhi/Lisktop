package com.space.lisktop.activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ProcessLifecycleOwnerInitializer;
import androidx.preference.PreferenceManager;

import android.app.AppOpsManager;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.os.Message;
import android.provider.Settings;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.gyf.immersionbar.ImmersionBar;
import com.space.lisktop.LisktopApp;
import com.space.lisktop.R;

public class SettingsActivity extends AppCompatActivity {
    private View.OnClickListener tvClicker;
    private static final int REQUEST_USAGE_PERMMISION=0;
    private Switch swShowIcon;
    private TextView btChooseDock,btSortMech,btAppLevel,btHidden,btUsagePermmision;
    int selected_method=LisktopApp.getSortMethod();     // 用于记录选择的排序方式

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        ImmersionBar.with(this)
                .statusBarColor(R.color.color_status_bar)
                .navigationBarColor(R.color.color_status_bar)
                .fitsSystemWindows(true)
                .autoDarkModeEnable(true)
                .init();

        initViews();
    }

    private void initViews()
    {
        swShowIcon=findViewById(R.id.set_switch_show_icon);

        boolean if_show_icons= LisktopApp.getWhetherShowIcon();      //仅用于初始化
        swShowIcon.setChecked(if_show_icons);

        swShowIcon.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                swShowIcon.setChecked(isChecked);
                LisktopApp.setWhetherShowIcon(isChecked);
                FragRight.lHandler.sendEmptyMessage(2);   // 先更改设置再改变界面
            }
        });

        btChooseDock=findViewById(R.id.set_chs_dock);
        btSortMech=findViewById(R.id.set_sort_mech);
        btAppLevel=findViewById(R.id.set_app_level);
        btHidden=findViewById(R.id.set_man_hidden);
        btUsagePermmision=findViewById(R.id.get_Usage_permission);

        tvClicker=new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()){
                    case R.id.set_chs_dock:
                        startActivity(new Intent(SettingsActivity.this, ChooseDockActivity.class));
                        break;
                    case R.id.set_sort_mech:
                        final String[] methods = {"LRU方式", "冒泡法", "次数限制"};
                        AlertDialog.Builder altBuilder=new AlertDialog.Builder(SettingsActivity.this);
                        altBuilder.setTitle("选择应用排序方式");
                        int default_method=LisktopApp.getSortMethod();
                        altBuilder.setSingleChoiceItems(methods, default_method, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                //Toast.makeText(SettingsActivity.this, methods[which], Toast.LENGTH_SHORT).show();
                                selected_method=which;
                            }
                        });
                        altBuilder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                LisktopApp.setSortMethod(selected_method);
                            }
                        });
                        altBuilder.setNegativeButton("取消", null);
                        AlertDialog dialog=altBuilder.create();
                        dialog.show();
                        break;
                    case R.id.set_app_level:
                        final String[] level_methods={"一级","五级","三级"};
                        AlertDialog.Builder altBuilder_level=new AlertDialog.Builder(SettingsActivity.this);
                        altBuilder_level.setTitle("选择应用分级方法");
                        int choosed_level=0;
                        altBuilder_level.setSingleChoiceItems(level_methods, choosed_level, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Toast.makeText(SettingsActivity.this, level_methods[which], Toast.LENGTH_SHORT).show();
                            }
                        });
                        altBuilder_level.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        });
                        altBuilder_level.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        });
                        AlertDialog dialog_level=altBuilder_level.create();
                        dialog_level.show();
                        break;
                    case R.id.set_man_hidden:
                        //startActivity(new Intent(SettingsActivity.this,HiddenAppActivity.class));
                        startActivity(new Intent(SettingsActivity.this,HiddenActivity.class));
                        break;
                    case R.id.get_Usage_permission:
                        Intent usgRqst=new Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS);
                        startActivityForResult(usgRqst,REQUEST_USAGE_PERMMISION);
                }
            }
        };

        btChooseDock.setOnClickListener(tvClicker);
        btSortMech.setOnClickListener(tvClicker);
        btHidden.setOnClickListener(tvClicker);
        btAppLevel.setOnClickListener(tvClicker);
        btUsagePermmision.setOnClickListener(tvClicker);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.setting_menu,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.menu_About:
                Toast.makeText(SettingsActivity.this,"关于软件",Toast.LENGTH_SHORT).show();
                break;
        }
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        switch (requestCode){
            case REQUEST_USAGE_PERMMISION:
                AppOpsManager appOps = (AppOpsManager)getSystemService(Context.APP_OPS_SERVICE);
                int mode = appOps.checkOpNoThrow("android:get_usage_stats",android.os.Process.myUid(), getPackageName());
                boolean granted = mode == AppOpsManager.MODE_ALLOWED;
                LisktopApp.setUsageStasticGranted(granted);
                if (granted)
                    Toast.makeText(this,"已授予权限！",Toast.LENGTH_SHORT).show();
                else
                    Toast.makeText(this,"未授予权限！",Toast.LENGTH_SHORT).show();
                Log.i("onResult","granted=="+granted);
                break;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
}
