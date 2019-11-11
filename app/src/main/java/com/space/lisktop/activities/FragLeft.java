package com.space.lisktop.activities;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

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

import java.util.ArrayList;

public class FragLeft extends Fragment implements View.OnClickListener {
    private TextView tvPhone,tvMessage,tvCam,tvWeb,tvSettings;
    private LisktopDAO lisktopDAO;
    private LinearLayout layoutMainApps;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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

        tvPhone=rootV.findViewById(R.id.tvPhoneCall);
        tvMessage=rootV.findViewById(R.id.tvMessage);
        tvCam=rootV.findViewById(R.id.tvCamera);
        tvWeb=rootV.findViewById(R.id.tvBroswer);

        tvPhone.setOnClickListener(this);
        tvMessage.setOnClickListener(this);
        tvCam.setOnClickListener(this);
        tvWeb.setOnClickListener(this);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        // TODO: Use the ViewModel
        Log.i("fd","activity created");
    }

    @Override
    public void onResume() {
        lisktopDAO =new LisktopDAO(getActivity());
        //无选择的主页应用，跳至选择页面
        if (!lisktopDAO.isDataExist())
        {
            Intent chsIntent=new Intent(getActivity(),ChooseMainActivity.class);
            startActivity(chsIntent);
        }
        else {      //有选择的主页应用，查找数据库并显示
            ArrayList<AppInfo> mainApps=lisktopDAO.getMainApps();
            for (AppInfo ai:mainApps){
                Log.i("mainApp",ai.getAppName());
                addMainApp(ai);
            }
        }
        super.onResume();
    }

    private void addMainApp(AppInfo aInfo)
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
            case R.id.tvPhoneCall:
//                SharedPreferences sPref= PreferenceManager.getDefaultSharedPreferences(getActivity());
//                Log.i("leftshowicon","show:"+sPref.getBoolean("showicon",false));
                startActivity(new Intent(getActivity(),SettingsActivity.class));
                break;
            case R.id.tvCamera:
                bottomAction("com.lge.camera");
                break;
            case R.id.tvBroswer:
                bottomAction("com.android.chrome");
                break;
            default:
                break;
        }
    }

    //底部按钮功能
    private void bottomAction(String packName)
    {
        Intent intent=getContext().getPackageManager().getLaunchIntentForPackage(packName);
        startActivity(intent);
    }
}
