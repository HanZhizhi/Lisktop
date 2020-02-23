package com.space.lisktop.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;
import android.widget.ExpandableListView;
import android.widget.ListView;

import com.space.lisktop.R;
import com.space.lisktop.adapters.AppManagerAdapter;
import com.space.lisktop.adapters.AppsLvAdapter;
import com.space.lisktop.obj.AppInfo;
import com.space.lisktop.utility.LisktopDAO;

import java.util.ArrayList;

public class HiddenAppActivity extends AppCompatActivity implements AppManagerAdapter.OnSlidingViewClickListener {
    private LisktopDAO lisktopDAO;
    private ArrayList<AppInfo> hiddenApps;
    private AppManagerAdapter manAdapter;
    private RecyclerView recApps;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hidden_app);


        lisktopDAO=new LisktopDAO(this);
        hiddenApps=lisktopDAO.getHiddenApps();

        recApps=findViewById(R.id.rec_manage_apps);
        recApps.setLayoutManager(new LinearLayoutManager(this));
        recApps.setAdapter(manAdapter=new AppManagerAdapter(this));
        recApps.setItemAnimator(new DefaultItemAnimator());
    }

    @Override
    public void onItemClick(View view, int position) {

    }

    @Override
    public void onHideBtnCilck(View view, int position) {
        manAdapter.Log(position);
    }

    @Override
    public void onSetBtnCilck(View view, int position) {

    }
}
