package com.lr.musiceasynet.ui.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.lr.musiceasynet.FragmentPagerAdapter;
import com.lr.musiceasynet.R;
import com.lr.musiceasynet.RecommandPagerViewModel;

import java.util.ArrayList;
import java.util.List;

public class RecommendPageContainerFragment extends Fragment {

    FragmentPagerAdapter fragmentPagerAdapter;
    RecommandPagerViewModel viewModel;
    List<Fragment> fragments = new ArrayList<>();
    TabLayout fragmentContainerTab;
    ViewPager2 fragmentPager;
    View root;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        root = inflater.inflate(R.layout.fragment_recommand_page_container,container,false);
        //binding = DataBindingUtil.inflate(inflater,R.layout.fragment_recommand_page_container,container,false);
        viewModel = new ViewModelProvider(getActivity()).get(RecommandPagerViewModel.class);
        //初始化view
        initView();
        //初始化ViewPager
        initViewPager();

        TabLayoutMediator tabLayoutMediator = new TabLayoutMediator(fragmentContainerTab, fragmentPager, (tab, i) -> {
               if (i==0){
                   tab.setText(getString(R.string.discover_new_songs));
               }else {
                   tab.setText(getString(R.string.local_songs));
               }
        });
        tabLayoutMediator.attach();

        return root;
    }

    private void initView(){
        fragmentContainerTab = root.findViewById(R.id.fragmentContainerTab);
        fragmentPager = root.findViewById(R.id.fragmentPager);
    }

    private void initViewPager(){
        fragments.add(new RecommendationFragment());
        fragments.add(new LocalAlbumFragment());
        fragmentContainerTab.addTab(fragmentContainerTab.newTab());
        fragmentContainerTab.addTab(fragmentContainerTab.newTab());

        fragmentPager.setCurrentItem(viewModel.getPageIndex().getValue());
        fragmentPagerAdapter = new FragmentPagerAdapter(getParentFragmentManager(),getLifecycle(),fragments);
        fragmentPager.setAdapter(fragmentPagerAdapter);

        fragmentPager.setCurrentItem(viewModel.getPageIndex().getValue());

        fragmentPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                viewModel.setPageIndex(position);
            }
        });
    }
}