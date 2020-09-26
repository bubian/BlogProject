package com.pds.blog.frame.paging;

import androidx.lifecycle.LiveData;
import androidx.paging.LivePagedListBuilder;
import androidx.paging.PagedList;

import com.pds.entity.PagingEntity;
import com.pds.frame.mvvm.BaseViewModel;

/**
 * @author: pengdaosong
 * CreateTime:  2020/9/19 11:46 AM
 * Email：pengdaosong@medlinker.com
 * Description:
 */
public class PagingVM extends BaseViewModel {

    private PagedList.Config myPagingConfig = new PagedList.Config.Builder()       // 分页设置
            .setPageSize(8) // 页面大小即每次加载时加载的数量
            .setInitialLoadSizeHint(24)
            .setPrefetchDistance(8) // 预取距离，给定UI中最后一个可见的Item，超过这个item应该预取一段数据
            .setMaxSize(24)
            .setEnablePlaceholders(true)
            .build();


    private ItemDataSourceFactory mItemDataSourceFactory = new ItemDataSourceFactory();
    public LiveData<PagedList<PagingEntity>> concertList = new LivePagedListBuilder<>(mItemDataSourceFactory, myPagingConfig)
            .build();

    public void loadMore() {
        mItemDataSourceFactory.sourceLiveData.getValue();
    }
}
