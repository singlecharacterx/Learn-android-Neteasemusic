package com.lr.musiceasynet;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class RecommandPagerViewModel extends ViewModel {
    private MutableLiveData<Integer> pageIndex = new MutableLiveData<>(0);;

    public MutableLiveData<Integer> getPageIndex(){

        return pageIndex;
    }

    public void setPageIndex(Integer i){
        pageIndex.setValue(i);
    }
}
