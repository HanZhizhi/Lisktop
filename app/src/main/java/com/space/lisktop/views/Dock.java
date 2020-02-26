package com.space.lisktop.views;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.os.Bundle;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;

import com.space.lisktop.R;
import com.space.lisktop.obj.AppInfo;
import com.space.lisktop.services.AppReorderService;

import java.util.ArrayList;

public class Dock extends LinearLayout implements View.OnClickListener,View.OnTouchListener {
    private int dockHeight=0,dockWidth;
    private int dockBgColor;
    private int iconSize,iconPadTop;
    private float density;
    private Context ctx;
    private PackageManager packageManager;
    private ArrayList<AppInfo> dockApps;

    public Dock(Context context) {
        this(context,null);
    }

    public Dock(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs,0);
    }

    public Dock(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        ctx=context;
        packageManager=ctx.getPackageManager();

        initAttrs(context,attrs);
        initView(context);
    }

    private void initView(Context context){
        //View view= LayoutInflater.from(context).inflate(R.layout.dock_layout,this,true);
        setOrientation(LinearLayout.HORIZONTAL);
        setBackgroundColor(dockBgColor);
    }

    private void initAttrs(Context context, AttributeSet attrs){
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();  //getSystemService(Context.WINDOW_SERVICE).getDefaultDisplay().getMetrics(displayMetrics);
        density =displayMetrics.density;
        dockWidth=displayMetrics.widthPixels;

        TypedArray ta=context.obtainStyledAttributes(attrs, R.styleable.Dock);
        iconSize=(int)ta.getDimension(R.styleable.Dock_dockIconSize,dp2px(context,60));         //图标大小，60dp转为像素
        iconPadTop=(int)ta.getDimension(R.styleable.Dock_iconPaddingTop,dp2px(context,15));     //图标与上下边距，默认15
        //setPadding(iconPadTop*2,iconPadTop,iconPadTop*2,iconPadTop);

        dockHeight=iconSize; //添加图标时设置了padTop，故此处不需要  //(int)ta.getDimension(R.styleable.Dock_dockHeight,dp2px(context,100));
        dockBgColor=ta.getColor(R.styleable.Dock_dockBacgroundColor, Color.parseColor("#f8f8f8"));

        Log.i("dock","height:"+dockHeight+",width:"+dockWidth);
        ta.recycle();
    }

    public void addDockApps(ArrayList<AppInfo> apps){
        this.dockApps=apps;
        this.removeAllViews();
        int numApps=apps.size();
        int sideMargin=dp2px(ctx,20);        // Layout的左右缩进，即屏幕最左右边与图标的左右margin距离,20dp
        int pads=(dockWidth-2*sideMargin-iconSize*numApps)/(2*numApps);    //每个图标左右的margin
        Log.i("dock","numapp"+numApps+",side:"+sideMargin+",pads"+pads+"app:"+iconSize);

        for(int i=0;i<apps.size();i++)
        {
            LinearLayout.LayoutParams params1 = new LinearLayout.LayoutParams(iconSize,iconSize);
            params1.leftMargin = pads;
            params1.rightMargin=pads;
            params1.topMargin=(iconPadTop);
            params1.bottomMargin=(iconPadTop);

            if(i==0)
                params1.leftMargin=sideMargin+pads;
            if(i==apps.size()-1)
                params1.rightMargin=sideMargin+pads;

            ImageView iv=new ImageView(ctx);
            iv.setImageDrawable(apps.get(i).getAppIcon());

            iv.setTag(i);
            iv.setOnClickListener(this);
            this.addView(iv,i,params1);
            Log.i("dock","add"+apps.get(i).getAppName());
        }
    }

    public void removeAppAt(int index){
        this.removeViewAt(index);
    }

    private int dp2px(Context context, float dp) {
        float density = context.getResources().getDisplayMetrics().density;
        return (int) (density * dp + 0.5f);
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        return false;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            default:
                int clickIndex= (int) v.getTag();
                AppInfo appInfo=dockApps.get(clickIndex);
                String pack=appInfo.getPackageName(),app=appInfo.getAppName();
                Intent go=packageManager.getLaunchIntentForPackage(pack);
                ctx.startActivity(go);

                Intent appClickServiceIntent=new Intent(ctx, AppReorderService.class);
                Bundle acBundle=new Bundle();
                acBundle.putString("packName",pack);       //传入包名称进行后续操作
                acBundle.putString("appName",app);
                appClickServiceIntent.putExtras(acBundle);
                ctx.startService(appClickServiceIntent);
                break;
        }
    }
}
