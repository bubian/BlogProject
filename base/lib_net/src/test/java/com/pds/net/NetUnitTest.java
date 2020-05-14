package com.pds.net;

import com.blog.pds.net.ApiManager;
import com.blog.pds.net.SchedulersCompat;
import com.blog.pds.net.exception.ErrorConsumer;
import com.blog.pds.net.func.HttpResultFunc;
import com.pds.entity.BaseEntity;
import com.pds.entity.DataEntity;

import org.junit.Test;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.functions.Consumer;
import io.reactivex.rxjava3.schedulers.Schedulers;

/**
 * @author: pengdaosong
 * CreateTime:  2020-05-14 16:25
 * Email：pengdaosong@medlinker.com
 * Description:
 */
public class NetUnitTest {
    private static final String URL = "http://47.104.91.148/11111.jpeg";
    private static final String THUM = "http://47.104.91.148/22222.jpeg";
    @Test
    public void getPhoneNumber() {
        Disposable observable
                = ApiManager
                .getUserApi()
                .getPhoneNumber("")
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .compose(SchedulersCompat.<BaseEntity<DataEntity>>applyIoSchedulers())
                .map(new HttpResultFunc<DataEntity>())
                .subscribe(new Consumer<DataEntity>() {
                    @Override
                    public void accept(DataEntity data) throws Exception {

                    }
                }, new ErrorConsumer() {
                    @Override
                    public void accept(Throwable throwable) {
                        super.accept(throwable);
                    }
                });
    }
}
