package com.lr.musiceasynet;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class RecommendPagerViewModel extends ViewModel {
    private MutableLiveData<Integer> pageIndex = new MutableLiveData<>(0);

    public Integer getPageIndex(){
        return pageIndex.getValue();
    }

    public void setPageIndex(Integer i){
        pageIndex.setValue(i);
    }
}
