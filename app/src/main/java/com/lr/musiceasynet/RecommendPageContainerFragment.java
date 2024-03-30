package com.lr.musiceasynet;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.tabs.TabLayoutMediator;
import com.lr.musiceasynet.databinding.FragmentRecommandPageContainerBinding;

import java.util.ArrayList;
import java.util.List;

public class RecommendPageContainerFragment extends Fragment {

    FragmentPagerAdapter fragmentPagerAdapter;
    FragmentRecommandPageContainerBinding binding;
    RecommandPagerViewModel viewModel;
    List<Fragment> fragments = new ArrayList<>();

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater,R.layout.fragment_recommand_page_container,container,false);
        viewModel = new ViewModelProvider(getActivity()).get(RecommandPagerViewModel.class);
        binding.setFragmentPagerData(viewModel);
        binding.setLifecycleOwner(getActivity());



        fragments.add(new RecommendationFragment());
        fragments.add(new LocalAlbumFragment());
        binding.fragmentContainerTab.addTab(binding.fragmentContainerTab.newTab());
        binding.fragmentContainerTab.addTab(binding.fragmentContainerTab.newTab());

        binding.fragmentPager.setCurrentItem(viewModel.getPageIndex().getValue());
        fragmentPagerAdapter = new FragmentPagerAdapter(getParentFragmentManager(),getLifecycle(),fragments);
        binding.fragmentPager.setAdapter(fragmentPagerAdapter);

        binding.fragmentPager.setCurrentItem(viewModel.getPageIndex().getValue());

        binding.fragmentPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                viewModel.setPageIndex(position);
            }
        });

        TabLayoutMediator tabLayoutMediator = new TabLayoutMediator(binding.fragmentContainerTab, binding.fragmentPager, (tab, i) -> {
               if (i==0){
                   tab.setText(getString(R.string.discover_new_songs));
               }else {
                   tab.setText(getString(R.string.local_songs));
               }
        });
        tabLayoutMediator.attach();

        return binding.getRoot();
    }
}