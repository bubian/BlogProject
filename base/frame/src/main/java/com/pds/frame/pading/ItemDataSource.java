package com.pds.frame.pading;

import androidx.annotation.NonNull;
import androidx.paging.ItemKeyedDataSource;

/**
 * @author: pengdaosong
 * CreateTime:  2020-09-17 17:47
 * Email：pengdaosong@medlinker.com
 * Description:
 */
public class ItemDataSource extends ItemKeyedDataSource<Integer,Concert> {
    @Override
    public void loadInitial(@NonNull LoadInitialParams params, @NonNull LoadInitialCallback callback) {
//        api.getArticleList(0)  //初始化加载第一页
//                .compose(RxHelper.rxSchedulerHelper())
//                .subscribe({
//                        callback.onResult(it?.data!!.datas!!)
//                }, {
//            refreshFailed(it.message, params, callback)
//        })
    }

    @Override
    public void loadAfter(@NonNull LoadParams params, @NonNull LoadCallback callback) {
//        api.getArticleList(page)  // 下拉加载更多数据
//                .compose(RxHelper.rxSchedulerHelper())
//                .subscribe({
//                        callback.onResult(it.data!!.datas!!)
//                }, {
//            networkFailed(it.message, params, callback)
//        })
    }

    @Override
    public void loadBefore(@NonNull LoadParams params, @NonNull LoadCallback callback) {

    }

    @NonNull
    @Override
    public Integer getKey(@NonNull Concert item) {
        return item.id;
    }
}
