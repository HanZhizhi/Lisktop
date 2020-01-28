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

import com.space.lisktop.R;

public class SettingsActivity extends AppCompatActivity {
    private CompoundButton.OnCheckedChangeListener swCheckListener;
    private View.OnClickListener tvClicker;
    private Switch swShowIcon;
    private SharedPreferences sPref;
    private TextView btChooseDock,btSortMech,btMotto,btAppLevel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        sPref= PreferenceManager.getDefaultSharedPreferences(this);

        initViews();
    }

    private void initViews()
    {

        swShowIcon=findViewById(R.id.set_switch_show_icon);

        boolean if_show_icons=sPref.getBoolean("showicon",false);     //仅用于初始化
        swShowIcon.setChecked(if_show_icons);

        swCheckListener=new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                switch (buttonView.getId()){
                    case R.id.set_switch_show_icon:
                        swShowIcon.setChecked(isChecked);
                        Message msg=new Message();
                        Bundle data=new Bundle();
                        data.putBoolean("show_icon??",isChecked);
                        msg.setData(data);
                        msg.what=2;
                        FragRight.appHandler.sendMessage(msg);
                        sPref.edit().putBoolean("showicon",isChecked).commit();
                        break;
                }
            }
        };

        swShowIcon.setOnCheckedChangeListener(swCheckListener);

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
                        int choosed_method=0;
                        altBuilder.setSingleChoiceItems(methods, choosed_method, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Toast.makeText(SettingsActivity.this, methods[which], Toast.LENGTH_SHORT).show();
                            }
                        });
                        altBuilder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        });
                        altBuilder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        });
                        AlertDialog dialog=altBuilder.create();
                        dialog.show();
                        break;
                    case R.id.set_motto:
                        AlertDialog.Builder builder_motto=new AlertDialog.Builder(SettingsActivity.this);
                        final EditText etMotto=new EditText(SettingsActivity.this);
                        etMotto.setText("从sharedprefernce获取");
                        builder_motto.setTitle("设置桌面提示")
                                .setView(etMotto)
                                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        Toast.makeText(SettingsActivity.this, etMotto.getText().toString(), Toast.LENGTH_SHORT).show();
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
