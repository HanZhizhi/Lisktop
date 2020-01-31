package com.space.lisktop.activities;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.PreferenceManager;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.space.lisktop.LisktopApp;
import com.space.lisktop.R;

public class SettingsActivity extends AppCompatActivity {
    private View.OnClickListener tvClicker;
    private Switch swShowIcon;
    private TextView btChooseDock,btSortMech,btMotto,btAppLevel;
    int selected_method=LisktopApp.getSortMethod();     // 用于记录选择的排序方式

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

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
        btMotto=findViewById(R.id.set_motto);
        btAppLevel=findViewById(R.id.set_app_level);

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
                    case R.id.set_motto:
                        AlertDialog.Builder builder_motto=new AlertDialog.Builder(SettingsActivity.this);
                        final EditText etMotto=new EditText(SettingsActivity.this);
                        String motto=LisktopApp.getMotto();
                        etMotto.setText(motto);
                        builder_motto.setTitle("设置桌面提示")
                                .setView(etMotto)
                                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        String strMt=etMotto.getText().toString();
                                        LisktopApp.setMotto(strMt);
                                    }
                                }).setNegativeButton("取消",null);
                        AlertDialog dlMotto=builder_motto.create();
                        dlMotto.show();
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
                }
            }
        };

        btChooseDock.setOnClickListener(tvClicker);
        btSortMech.setOnClickListener(tvClicker);
        btMotto.setOnClickListener(tvClicker);
        btAppLevel.setOnClickListener(tvClicker);
    }
}
