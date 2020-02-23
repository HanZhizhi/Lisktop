package com.space.lisktop.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.space.lisktop.R;
import com.space.lisktop.adapters.AppsLvAdapter;
import com.space.lisktop.obj.AppInfo;
import com.space.lisktop.utility.LisktopDAO;

import java.util.ArrayList;

public class HiddenActivity extends AppCompatActivity {
    private ListView lvHidden;
    private LisktopDAO lisktopDAO;
    private ArrayList<AppInfo> hiddenApps;
    private AppsLvAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hidden);

        lvHidden=findViewById(R.id.hidden_list);
        lisktopDAO=new LisktopDAO(this);
        hiddenApps=lisktopDAO.getHiddenApps();
        for (AppInfo app:hiddenApps){
            app.setIs_show_icon(true);
        }
        adapter=new AppsLvAdapter(hiddenApps,this);
        lvHidden.setAdapter(adapter);
        lvHidden.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String packageName=hiddenApps.get(position).getPackageName();
                lisktopDAO.cancelHideApp(packageName);
                FragRight.lHandler.sendEmptyMessage(0);

                hiddenApps.remove(position);
                adapter.notifyDataSetChanged();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        Toast.makeText(this,"点击取消隐藏",Toast.LENGTH_SHORT).show();
    }
}
