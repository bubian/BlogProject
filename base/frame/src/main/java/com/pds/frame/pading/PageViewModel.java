package com.pds.frame.pading;

import androidx.lifecycle.LiveData;
import androidx.paging.LivePagedListBuilder;
import androidx.paging.PagedList;

import com.pds.frame.mvvm.BaseViewModel;

/**
 * @author: pengdaosong
 * CreateTime:  2020-09-17 17:33
 * Email：pengdaosong@medlinker.com
 * Description:
 */
public class PageViewModel extends BaseViewModel {

    private PagedList.Config myPagingConfig = new PagedList.Config.Builder()       // 分页设置
            .setPageSize(50) // 页面大小即每次加载时加载的数量
            .setPrefetchDistance(150) // 预取距离，给定UI中最后一个可见的Item，超过这个item应该预取一段数据
            .setEnablePlaceholders(true)
            .build();


    public LiveData<PagedList<Concert>> concertList = new LivePagedListBuilder(new ItemDataSourceFactory(), myPagingConfig)
            // .setFetchExecutor() 设置执行器
            .build();
}
