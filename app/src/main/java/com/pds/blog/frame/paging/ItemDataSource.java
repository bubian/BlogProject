package com.pds.blog.frame.paging;

import androidx.annotation.NonNull;
import androidx.paging.ItemKeyedDataSource;

import com.blog.pds.net.SchedulersCompat;
import com.blog.pds.net.exception.ErrorConsumer;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.pds.api.func.HttpResultListFunc;
import com.pds.api.manager.ApiManager;
import com.pds.entity.PagingEntity;
import com.pds.entity.base.BaseListEntity;
import com.pds.frame.log.Lg;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

/**
 * @author: pengdaosong
 * CreateTime:  2020-09-17 17:47
 * Email：pengdaosong@medlinker.com
 * Description:
 */
public class ItemDataSource extends ItemKeyedDataSource<Integer, PagingEntity> {

    private BaseListEntity<PagingEntity> getPageLists(int index) {

        Gson gson = new Gson();
        return gson.fromJson(Data.sDatas.get(index), new TypeToken<BaseListEntity<PagingEntity>>() {
        }.getType());
    }

    @Override
    public void loadInitial(@NonNull LoadInitialParams params, @NonNull LoadInitialCallback callback) {
        Lg.i("=====>loadInitial = "+ params.requestedInitialKey + " size = "+ params.requestedLoadSize);
        Disposable observable
                = ApiManager
                .getPagingApi()
                .getPageList()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .map(new HttpResultListFunc<>())
                .subscribe(data -> {
                    callback.onResult(getPageLists(0).getData().getList());
                }, new ErrorConsumer() {
                    @Override
                    public void accept(Throwable throwable) {
                        super.accept(throwable);
                        callback.onResult(getPageLists(0).getData().getList());
                    }
                });
    }

    @Override
    public void loadAfter(@NonNull LoadParams params, @NonNull LoadCallback callback) {
        Lg.i("=====>loadAfter = "+ params.key + " size = "+ params.requestedLoadSize);
        Disposable observable
                = ApiManager
                .getPagingApi()
                .getPageList()
//                .subscribeOn(Schedulers.io())
//                .observeOn(AndroidSchedulers.mainThread())
                .map(new HttpResultListFunc<>())
                .subscribe(data -> {
                    Thread.sleep(3000);
                    callback.onResult(getPageLists(1).getData().getList());
                }, new ErrorConsumer() {
                    @Override
                    public void accept(Throwable throwable) {
                        super.accept(throwable);
                        try {
                            Thread.sleep(3000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        callback.onResult(getPageLists(1).getData().getList());
                    }
                });
    }

    @Override
    public void loadBefore(@NonNull LoadParams params, @NonNull LoadCallback callback) {
        Lg.i("=====>loadBefore = "+ params.key + " size = "+ params.requestedLoadSize);
    }

    @NonNull
    @Override
    public Integer getKey(@NonNull PagingEntity item) {
        return item.getId();
    }
}
