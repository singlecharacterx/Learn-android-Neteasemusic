package com.lr.musiceasynet;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Lifecycle;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import java.util.List;

public class FragmentPagerAdapter extends FragmentStateAdapter {

    List<Fragment> fragmentlist;
    public FragmentPagerAdapter(@NonNull FragmentManager fragmentManager,
                                @NonNull Lifecycle lifecycle,List<Fragment> fragmentlist) {
        super(fragmentManager, lifecycle);
        this.fragmentlist =fragmentlist;
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        return fragmentlist.get(position);
    }

    @Override
    public int getItemCount() {
        return fragmentlist.size();
    }
}
