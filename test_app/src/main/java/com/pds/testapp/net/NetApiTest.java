package com.pds.testapp.net;

import com.blog.pds.net.SchedulersCompat;
import com.blog.pds.net.exception.ErrorConsumer;
import com.pds.api.func.HttpResultFunc;
import com.pds.api.manager.ApiManager;
import com.pds.entity.base.BaseEntity;
import com.pds.entity.base.DataEntity;

import org.junit.Test;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.functions.Consumer;
import io.reactivex.rxjava3.schedulers.Schedulers;

/**
 * @author: pengdaosong
 * CreateTime:  2020-05-14 16:34
 * Email：pengdaosong@medlinker.com
 * Description:
 */
public class NetApiTest {

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
                });
    }
}
