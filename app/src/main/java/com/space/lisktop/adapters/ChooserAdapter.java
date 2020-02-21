package com.space.lisktop.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.space.lisktop.R;
import com.space.lisktop.obj.AppInfo;

import java.util.ArrayList;

public class ChooserAdapter extends RecyclerView.Adapter {
    private ArrayList<AppInfo> selectedApps;
    private IconClickListener iconClickListener;

    class SelHolder extends RecyclerView.ViewHolder{
        ImageView appIcon;

        public SelHolder(@NonNull View itemView) {
            super(itemView);
            appIcon=itemView.findViewById(R.id.iv_seled_icon);
        }
    }

    public ChooserAdapter(ArrayList<AppInfo> selApps){
        this.selectedApps=selApps;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.choosed_app_layout,parent,false);
        SelHolder vHolder=new SelHolder(view);
        return vHolder;
    }

    public void setOnIconClickListener(IconClickListener listener) {
        this.iconClickListener = listener;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, final int position) {
        if(holder instanceof SelHolder){
            ((SelHolder) holder).appIcon.setImageDrawable(selectedApps.get(position).getAppIcon());
            ((SelHolder) holder).appIcon.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(iconClickListener!=null){
                        iconClickListener.OnIconClicked(position);
                    }
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return selectedApps.size()==0?0:selectedApps.size();
    }

    public interface IconClickListener{
        public void OnIconClicked(int index);
    }

    public void insert111App(int pos,AppInfo app){
        //selectedApps.add(pos,app);
        notifyItemInserted(pos);
    }

    public void removeApp(int pos){
        if(selectedApps==null || selectedApps.isEmpty())
            return;
        selectedApps.remove(pos);
        notifyItemRemoved(pos);
    }
}
