package com.space.lisktop.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.space.lisktop.LisktopApp;
import com.space.lisktop.R;
import com.space.lisktop.obj.AppInfo;
import com.space.lisktop.utility.LisktopDAO;
import com.space.lisktop.utility.PackageManageHelper;

import java.util.ArrayList;

public class WelActivity extends AppCompatActivity {
    private Button btGo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wel);

//        SharedPreferences sPref=getSharedPreferences("first_open",MODE_PRIVATE);
//        SharedPreferences.Editor editor=sPref.edit();
//        editor.putBoolean("first_open",false).apply();
        LisktopApp.setFirstOpen(false);

        ArrayList<AppInfo> startableApps=new PackageManageHelper(this).getStartableApps(true);

        LisktopDAO lisktopDAO =new LisktopDAO(this);
        lisktopDAO.writeInstalledAppsWithOrder(startableApps);
        Log.i("welActivity","已写入应用列表");

        btGo=findViewById(R.id.btGo);
        btGo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent chooseIntent=new Intent(WelActivity.this, ChooseDockActivity.class);
                startActivity(chooseIntent);
                finish();
            }
        });

    }
}
