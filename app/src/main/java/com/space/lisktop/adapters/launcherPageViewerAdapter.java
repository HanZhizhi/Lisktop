package com.space.lisktop.adapters;

import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import java.util.List;

public class launcherPageViewerAdapter extends FragmentPagerAdapter {
    private List<Fragment> fragments;

    public launcherPageViewerAdapter(FragmentManager fm, List<Fragment> fragList) {
        super(fm);
        this.fragments=fragList;
    }

    @Override
    public int getCount() {
        return this.fragments.size();
    }

    @Override
    public Fragment getItem(int position) {
        return this.fragments.get(position);
    }

}
