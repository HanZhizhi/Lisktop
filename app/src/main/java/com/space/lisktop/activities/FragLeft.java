package com.space.lisktop.activities;

import androidx.lifecycle.ViewModelProviders;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.space.lisktop.R;

public class FragLeft extends Fragment implements View.OnClickListener {
    private TextView tvPhone,tvMessage,tvCam,tvWeb;

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
    public void onClick(View v) {
        switch (v.getId())
        {
            case R.id.tvPhoneCall:
                bottomAction("com.android.phone");
                break;
            case R.id.tvCamera:
                bottomAction("com.lge.camera");
                break;
            case R.id.tvBroswer:
                bottomAction("com.android.chrome");
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
