package com.space.lisktop.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.space.lisktop.R;
import com.space.lisktop.adapters.AppsLvAdapter;
import com.space.lisktop.obj.AppInfo;
import com.space.lisktop.utility.LisktopDAO;
import com.space.lisktop.utility.PackageManageHelper;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class ChooseMainActivity extends AppCompatActivity  implements View.OnClickListener {
    private ListView lvApps;
    private Button btOK;
    private LinearLayout llSeled;   //显示已选应用
    private ArrayList<AppInfo> apps=new ArrayList<>(),selApps=new ArrayList<>();
    private AppsLvAdapter adapter;
    private int numSelected=0;
    private LisktopDAO lisktopDAO;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_main);

        lisktopDAO=new LisktopDAO(this);
        initViews();
    }

    private void initViews()
    {
        btOK=findViewById(R.id.bt_choose_ok);
        lvApps=findViewById(R.id.lv_choose);
        llSeled=findViewById(R.id.llayout_choosed);
        llSeled.setOrientation(LinearLayout.HORIZONTAL);

        btOK.setOnClickListener(this);

        apps=new PackageManageHelper(this).getStartableApps(true);
        adapter=new AppsLvAdapter(apps,this,true);
        lvApps.setAdapter(adapter);
        lvApps.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (numSelected<5)
                {
                    selectApp(position);
                    numSelected+=1;
                }
                else
                    Toast.makeText(ChooseMainActivity.this,"最多五个哦~", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void selectApp(int arg)
    {
        ImageView iv=new ImageView(this);
        iv.setImageDrawable(apps.get(arg).getAppIcon());

        // 也可以自己想要的宽度，参数（int width, int height）均表示px
        // 如dp单位，首先获取屏幕的分辨率在求出密度，根据屏幕ppi=160时，1px=1dp
        //则公式为 dp * ppi / 160 = px ——> dp * dendity = px
        //如设置为48dp：1、获取屏幕的分辨率 2、求出density 3、设置
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        float density = displayMetrics.density;
        LinearLayout.LayoutParams params1 = new LinearLayout.LayoutParams(
                (int)(60 * density),
                (int)(60 * density));
        //相当于android:layout_marginLeft="8dp"
        params1.leftMargin = 8;
        params1.rightMargin=8;

//        ViewGroup.LayoutParams param=iv.getLayoutParams();
//        param.height=60;
//        param.width=60;

        //llSeled.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,0,1));
        iv.setTag(numSelected);
        iv.setOnClickListener(this);
        llSeled.addView(iv,params1);

        selApps.add(apps.get(arg));
        apps.remove(apps.get(arg));
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId())
        {
            case R.id.bt_choose_ok:
                if (numSelected>0)
                {
                    lisktopDAO.deleteTable();
                    boolean sus=lisktopDAO.writeMainApps(selApps);
                    Log.i("write","sucsess:"+sus);
                    finish();
                }
                else {
                    Toast.makeText(this,"请选择主页应用",Toast.LENGTH_SHORT).show();
                }
                break;
            default:
                int idx=(int)v.getTag();
                llSeled.removeView(llSeled.getChildAt(idx));

                //添加到下面并排序，更新列表
                apps.add(selApps.get(idx));
                Collections.sort(apps, new Comparator<AppInfo>() {
                    @Override
                    public int compare(AppInfo o1, AppInfo o2) {
                        return String.CASE_INSENSITIVE_ORDER.compare(o1.getAppName(),o2.getAppName());
                    }
                });
                adapter.notifyDataSetChanged();
                // TODO:可以将线性布局中添加图片改为列表
                selApps.remove(selApps.get(idx));
                numSelected-=1;
                Toast.makeText(ChooseMainActivity.this,"选择个数："+selApps.size()+llSeled.getChildCount(), Toast.LENGTH_SHORT).show();
                break;
        }
    }
}
