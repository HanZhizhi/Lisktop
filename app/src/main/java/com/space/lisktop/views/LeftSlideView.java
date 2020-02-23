package com.space.lisktop.views;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.HorizontalScrollView;
import android.widget.TextView;

import com.space.lisktop.R;

public class LeftSlideView extends HorizontalScrollView {
    private TextView tvHide,tvUninstall;
    private int mScrollWidth;  //记录滚动条可以滚动的距离
    private boolean once=false;
    private boolean isOpen=false;
    private OnSlideViewActionListener slideListener;

    public LeftSlideView(Context context) {
        super(context,null);
    }

    public LeftSlideView(Context context, AttributeSet attrs) {
        super(context, attrs,0);
    }

    public LeftSlideView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.setOverScrollMode(OVER_SCROLL_NEVER);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        if(!once){
            tvHide=findViewById(R.id.tv_hide_or_not);
            tvUninstall=findViewById(R.id.tv_uninstall);
            once=true;
        }
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);
        if (changed){
            this.scrollTo(0,0);
            mScrollWidth=tvHide.getWidth()+tvUninstall.getWidth();
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        int action=ev.getAction();
        switch (action){
            case MotionEvent.ACTION_DOWN:
            case MotionEvent.ACTION_MOVE:
                slideListener.OnDrawOrMove(this);
                Log.i("changeS","move"+getScrollX());
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                Log.i("changeScrollx","action_up");
                changeScrollx();
                break;
        }
        return super.onTouchEvent(ev);
    }

    @Override
    protected void onScrollChanged(int l, int t, int oldl, int oldt) {
        super.onScrollChanged(l, t, oldl, oldt);
        //tvHide.setTranslationX(1);
    }

    public void changeScrollx(){
        Log.i("changeScrollx","getScroolX:"+getScrollX()+", mscrollwIDTH:"+mScrollWidth);
        if (getScrollX() >= (mScrollWidth/2)){
            smoothScrollTo(mScrollWidth,0);
            isOpen=true;
            slideListener.OnMenuOpened(this);
        }
        else{
            smoothScrollTo(0,0);
            isOpen=false;
        }
    }

    public void OpenMenu(){
        if (isOpen){
            return;
        }
        this.smoothScrollTo(mScrollWidth,0);
        isOpen=true;
        slideListener.OnMenuOpened(this);
    }

    public void closeMenu(){
        if (!isOpen){
            return;
        }
        this.smoothScrollTo(0,0);
        isOpen=false;
    }

    public void setSlideViewActionListener(OnSlideViewActionListener listener){
        slideListener=listener;
    }

    public interface OnSlideViewActionListener{
        void OnMenuOpened(View view);
        void OnDrawOrMove(LeftSlideView slideView);
    }
}
