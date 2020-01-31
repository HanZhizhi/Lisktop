package com.space.lisktop.adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.space.lisktop.R;
import com.space.lisktop.obj.AppInfo;
import java.util.ArrayList;

public class AppsLvAdapter extends BaseAdapter {
    private ArrayList<AppInfo> infoList;
    private LayoutInflater inflater;

    public AppsLvAdapter(ArrayList<AppInfo> info, Context ctx)
    {
        this.infoList=info;
        this.inflater=LayoutInflater.from(ctx);
    }

    @Override
    public int getCount() {
        return infoList.size();
    }

    @Override
    public Object getItem(int position) {
        return infoList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Log.i("adapter","getView called");
        View view=inflater.inflate(R.layout.app_item_layout,null);
        TextView tvAppName=view.findViewById(R.id.list_app_name);
        ImageView icon=view.findViewById(R.id.image_icon);

        final AppInfo aInfo= (AppInfo) getItem(position);
        tvAppName.setText(aInfo.getAppName());

        if (aInfo.getIs_show_icon())
        {
            icon.setVisibility(View.VISIBLE);
            icon.setImageDrawable(aInfo.getAppIcon());
        }
        else
            icon.setVisibility(View.GONE);

        return view;
    }
}
