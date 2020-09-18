package com.pds.frame.pading;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;
import androidx.paging.DataSource;

/**
 * @author: pengdaosong
 * CreateTime:  2020-09-17 17:50
 * Email：pengdaosong@medlinker.com
 * Description:
 */
public class ItemDataSourceFactory extends DataSource.Factory<Integer, Concert> {

    private MutableLiveData<ItemDataSource> sourceLiveData = new MutableLiveData<ItemDataSource>();
    @NonNull
    @Override
    public DataSource<Integer, Concert> create() {
        ItemDataSource source = new ItemDataSource();
        sourceLiveData.postValue(source);
        return source;
    }
}
