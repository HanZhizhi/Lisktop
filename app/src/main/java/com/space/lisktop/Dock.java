package com.space.lisktop;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;

import com.space.lisktop.obj.AppInfo;

import java.util.ArrayList;

public class Dock extends LinearLayout {
    private int dockHeight,dockWidth;
    private int dockBgColor;
    private float displayDensity;
    private LinearLayout dockLinearLayout;

    public Dock(Context context) {
        super(context);
        initView(context);
    }

    public Dock(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initView(context);
        initAttrs(context,attrs);
    }

    public Dock(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.setOrientation(LinearLayout.HORIZONTAL);

        initView(context);
        initAttrs(context,attrs);
    }

    private void initView(Context context){
        LayoutInflater.from(context).inflate(R.layout.dock_layout,this,true);
        dockLinearLayout=findViewById(R.id.dock_l_layout);
        dockLinearLayout.setOrientation(LinearLayout.HORIZONTAL);
    }

    private void initAttrs(Context context, AttributeSet attrs){
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();  //getSystemService(Context.WINDOW_SERVICE).getDefaultDisplay().getMetrics(displayMetrics);
        displayDensity=displayMetrics.density;
        dockWidth=displayMetrics.widthPixels;

        TypedArray ta=context.obtainStyledAttributes(attrs,R.styleable.Dock);

        dockHeight=(int)ta.getDimension(R.styleable.dockHeight,dp2px(context,100));

    }

    private void addDockApps(ArrayList<AppInfo> apps){

    }

    private int dp2px(Context context, float dp) {
        float density = context.getResources().getDisplayMetrics().density;
        return (int) (density * dp + 0.5f);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
//        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        setMeasuredDimension(dockWidth,dockHeight);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);
    }
}
