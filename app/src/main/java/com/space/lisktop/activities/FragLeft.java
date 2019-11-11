package com.space.lisktop.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.preference.PreferenceManager;

import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.space.lisktop.R;
import com.space.lisktop.obj.AppInfo;
import com.space.lisktop.utility.LisktopDAO;
import com.space.lisktop.utility.PackageManageHelper;

import java.util.ArrayList;

public class FragLeft extends Fragment implements View.OnClickListener {
    private TextView tvSettings,tvMotto;
    private LisktopDAO lisktopDAO;
    private LinearLayout layoutMainApps;
    private ArrayList<AppInfo> mainApps;
    private PackageManager packMan;
    SharedPreferences sPref;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        packMan=getActivity().getPackageManager();
        Log.i("fd","oncreate");
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

        layoutMainApps=rootV.findViewById(R.id.llayout_mainApps);
        layoutMainApps.setOrientation(LinearLayout.HORIZONTAL);

        tvMotto=rootV.findViewById(R.id.left_motto);
        sPref= PreferenceManager.getDefaultSharedPreferences(getActivity());
        tvMotto.setText(sPref.getString("motto","error!"));
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        // TODO: Use the ViewModel
        Log.i("fd","activity created");
    }

    @Override
    public void onResume() {
        tvMotto.setText(sPref.getString("motto","error!"));

        lisktopDAO =new LisktopDAO(getActivity());
        //无选择的主页应用，跳至选择页面
        if (!lisktopDAO.isDataExist())
        {
            Intent chsIntent=new Intent(getActivity(),ChooseMainActivity.class);
            startActivity(chsIntent);
        }
        else {      //有选择的主页应用，查找数据库并显示
            mainApps=lisktopDAO.getMainApps();
            //删除原有图标添加新的
            Log.i("childs",layoutMainApps.getChildCount()+"");
            int numChilds=layoutMainApps.getChildCount();
            for (int i=0;i<numChilds;i++)      //原本将getChildCount写在循环中，导致循环上届改变，子view未删完
            {
                Log.i("todeleteview",i+"--"+layoutMainApps.getChildAt(0).getId());
                layoutMainApps.removeView(layoutMainApps.getChildAt(0));           //原为getChildAt(i)，改为一直删除第0个
            }
            for (int i=0;i<mainApps.size();i++){
                //Log.i("mainApp",ai.getAppName());
                addMainApp(mainApps.get(i),i);
            }
        }
        super.onResume();
    }

    /**
     *
     * @param aInfo:添加的appInfo
     * @param tag 设置给图标的tag，传入为appInfo在数组中的索引
     */
    private void addMainApp(AppInfo aInfo,int tag)
    {
        ImageView iv=new ImageView(getActivity());
        iv.setImageDrawable(aInfo.getAppIcon());
        // 也可以自己想要的宽度，参数（int width, int height）均表示px
        // 如dp单位，首先获取屏幕的分辨率在求出密度，根据屏幕ppi=160时，1px=1dp
        //则公式为 dp * ppi / 160 = px ——> dp * dendity = px
        //如设置为48dp：1、获取屏幕的分辨率 2、求出density 3、设置
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        float density = displayMetrics.density;
        LinearLayout.LayoutParams params1 = new LinearLayout.LayoutParams(
                (int)(60 * density),
                (int)(60 * density));
        //相当于android:layout_marginLeft="8dp"
        params1.leftMargin = 8;
        params1.rightMargin=8;

        iv.setTag(tag);
        iv.setOnClickListener(this);
        layoutMainApps.addView(iv,params1);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId())
        {
            case R.id.tvSettings:
                startActivity(new Intent(getActivity(),SettingsActivity.class));
                break;
            default:
                int idx=(int)v.getTag();
                Intent go=packMan.getLaunchIntentForPackage(mainApps.get(idx).getPackageName());
                startActivity(go);
                break;
        }
    }

}
