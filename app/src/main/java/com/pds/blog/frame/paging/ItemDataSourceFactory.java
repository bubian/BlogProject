package com.pds.blog.frame.paging;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;
import androidx.paging.DataSource;

import com.pds.entity.PagingEntity;

/**
 * @author: pengdaosong
 * CreateTime:  2020-09-17 17:50
 * Email：pengdaosong@medlinker.com
 * Description:
 */
public class ItemDataSourceFactory extends DataSource.Factory<Integer, PagingEntity> {

    // 可以调用sourceLiveData相关方法，来刷新数据，当然这里也可以不用LiveData来包装
    public MutableLiveData<ItemDataSource> sourceLiveData = new MutableLiveData<ItemDataSource>();
    @NonNull
    @Override
    public DataSource<Integer, PagingEntity> create() {
        ItemDataSource source = new ItemDataSource();
        sourceLiveData.postValue(source);
        return source;
    }
}
