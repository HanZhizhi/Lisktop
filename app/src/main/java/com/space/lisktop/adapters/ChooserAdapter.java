package com.space.lisktop.adapters;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.space.lisktop.R;
import com.space.lisktop.obj.AppInfo;

import java.util.ArrayList;
import java.util.Collections;

public class ChooserAdapter extends RecyclerView.Adapter<ChooserAdapter.SelHolder> {
    private ArrayList<AppInfo> selectedApps;
    private IconClickListener iconClickListener;

    static class SelHolder extends RecyclerView.ViewHolder{
        ImageView appIcon;

        public SelHolder(@NonNull View itemView) {
            super(itemView);
            appIcon=itemView.findViewById(R.id.iv_seled_icon);
        }
    }

    public ChooserAdapter(){
        this.selectedApps=new ArrayList<>();
    }

    @NonNull
    @Override
    public SelHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.choosed_app_layout,parent,false);
        return new SelHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SelHolder holder, final int position) {
        ((SelHolder) holder).appIcon.setImageDrawable(selectedApps.get(position).getAppIcon());
        ((SelHolder) holder).appIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i("onBind","pos:"+position);
                if(iconClickListener!=null){
                    iconClickListener.OnIconClicked(position);
                }
            }
        });
    }

    public void setOnIconClickListener(IconClickListener listener) {
        this.iconClickListener = listener;
    }


    @Override
    public int getItemCount() {
        return selectedApps.size();
    }

    public interface IconClickListener{
        public void OnIconClicked(int index);
    }

    public void addApp(AppInfo app){
        selectedApps.add(app);
        //notifyItemInserted();
        notifyDataSetChanged();
    }

    public AppInfo removeApp(int pos){
        if(selectedApps==null || selectedApps.isEmpty())
            return null;
        AppInfo delApp=selectedApps.get(pos);
        selectedApps.remove(pos);
        notifyItemRemoved(pos);
        notifyItemRangeChanged(pos,selectedApps.size()-pos);         //删除后更新下标
        return delApp;
    }

    public void swapApps(int from, int to){
        Collections.swap(selectedApps,from,to);
        //logSeledApps(selApps);
        notifyItemMoved(from, to);
    }

    public int getSelectedNum(){
        return this.selectedApps.size();
    }

    public ArrayList<AppInfo> getSelectedApps(){
        return this.selectedApps;
    }
}
