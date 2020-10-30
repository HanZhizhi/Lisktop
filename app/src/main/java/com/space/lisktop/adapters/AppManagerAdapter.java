package com.space.lisktop.adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.space.lisktop.R;
import com.space.lisktop.obj.AppInfo;
import com.space.lisktop.utility.DisPlayUtils;
import com.space.lisktop.utility.LisktopDAO;
import com.space.lisktop.views.LeftSlideView;

import java.util.ArrayList;

public class AppManagerAdapter extends RecyclerView.Adapter implements LeftSlideView.OnSlideViewActionListener {
    private Context context;
    private ArrayList<AppInfo> apps;
    private OnSlidingViewClickListener mHideListener;
    private OnSlidingViewClickListener unInstListener;

    private LeftSlideView mSlideMenu =null;

    public AppManagerAdapter(Context context){
        this.context=context;
        apps=new LisktopDAO(context).getHiddenApps();
        mHideListener= (OnSlidingViewClickListener) context;
        unInstListener= (OnSlidingViewClickListener) context;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(context).inflate(R.layout.left_slide_layout,parent,false);
        ManHolder manHolder=new ManHolder(view);
        return manHolder;
    }


    @Override
    public void onBindViewHolder(@NonNull final RecyclerView.ViewHolder holder, int position) {
        if(holder instanceof ManHolder){
            ((ManHolder) holder).layout_content.getLayoutParams().width= DisPlayUtils.getScreenWidth(context);

            ((ManHolder) holder).btHide.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (IsMenuOpen()){
                        closeMenu();
                    }else {
                        int n=holder.getLayoutPosition();
                        mHideListener.onHideBtnCilck(v,n);
                    }
                }
            });
        }

    }

    @Override
    public int getItemCount() {
        return apps.size();
    }

    @Override
    public int getItemViewType(int position) {
        return super.getItemViewType(position);
    }

    @Override
    public void OnMenuOpened(View view) {
        mSlideMenu = (LeftSlideView) view;
    }

    @Override
    public void OnDrawOrMove(LeftSlideView slideView) {
        if(this.mSlideMenu !=slideView){
            slideView.closeMenu();
        }
    }

    public boolean IsMenuOpen(){
        if(mSlideMenu !=null){
            return true;
        }
        return false;
    }

    public void Log(int pos){
        Log.i("manadapter",apps.get(pos).getAppName());
    }

    public void closeMenu(){
        mSlideMenu.closeMenu();
        mSlideMenu=null;
    }

    class ManHolder extends RecyclerView.ViewHolder{
        TextView btHide,btUninstall;
        ViewGroup layout_content;

        public ManHolder(@NonNull View itemView) {
            super(itemView);
            btHide=itemView.findViewById(R.id.tv_hide_or_not);
            btUninstall=itemView.findViewById(R.id.tv_uninstall);
            layout_content=itemView.findViewById(R.id.content_layout);

            ((LeftSlideView) itemView).setSlideViewActionListener(AppManagerAdapter.this);
        }
    }

    /**
     * 注册接口的方法：点击事件。在Mactivity.java实现这些方法。
     */
    public interface OnSlidingViewClickListener {
        void onItemClick(View view, int position);//点击item正文
        void onHideBtnCilck(View view, int position);//点击“删除”
        void onSetBtnCilck(View view, int position);//点击“设置”
    }

}
