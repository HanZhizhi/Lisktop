package com.space.lisktop;

import androidx.appcompat.app.ActionBar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.viewpager.widget.ViewPager;

import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ListView;

import com.space.lisktop.activities.FragLeft;
import com.space.lisktop.activities.FragRight;
import com.space.lisktop.adapters.launcherPageViewerAdapter;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(Window.FEATURE_NO_TITLE,Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main);

//        ActionBar actionBar = getSupportActionBar();
//        if (actionBar != null) {
//            actionBar.hide();
//        }

        fragManager=getSupportFragmentManager();

        initViews();

        pManager=getPackageManager();
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
        pager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                Log.i("page_change","now page:"+position+"--"+positionOffset+"--"+positionOffsetPixels);
            }

            @Override
            public void onPageSelected(int position) {

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    @Override
    //屏蔽返回按键
    public boolean onKeyDown(int keyCode,KeyEvent event) {
        if(keyCode== KeyEvent.KEYCODE_BACK)
            return true;//不执行父类点击事件
        if (keyCode==KeyEvent.KEYCODE_HOME)
            return true;
        return super.onKeyDown(keyCode, event);
    }
}
