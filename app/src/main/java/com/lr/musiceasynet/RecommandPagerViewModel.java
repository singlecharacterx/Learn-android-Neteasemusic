package com.lr.musiceasynet;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class RecommandPagerViewModel extends ViewModel {
    private MutableLiveData<Integer> pageIndex;

    public MutableLiveData<Integer> getPageIndex(){
        if (pageIndex==null){
             pageIndex = new MutableLiveData<>();
             pageIndex.setValue(0);
        }
        return pageIndex;
    }

    public void setPageIndex(Integer i){
        pageIndex.setValue(i);
    }
}
