package com.pds.sample.module.rxjava;

import android.os.Looper;
import android.util.Log;

import androidx.annotation.NonNull;

import com.bumptech.glide.load.model.GlideUrl;

import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.BiFunction;

/**
 * @author: pengdaosong
 * CreateTime:  2020-03-18 18:04
 * Email：pengdaosong@medlinker.com
 * Description:
 */
public class AndOrThenOrWhenOrInterval {

    private void AndOrThenOrWhen() {
        Observable.<Integer>create(emitter -> emitter.onNext(1))
                .join(observer -> observer.onNext(2),
                        integer -> (ObservableSource<Integer>) observer -> observer.onComplete(),
                        integer -> (ObservableSource<Integer>) observer -> observer.onComplete(),
                        (BiFunction<Integer, Integer, Integer>) (integer, integer2) -> integer + integer2)
                .subscribe(new Observer<Integer>() {
                    @Override
                    public void onSubscribe(@NonNull Disposable d) {

                    }

                    @Override
                    public void onNext(@NonNull Integer integer) {
                        System.out.println("create operate--->join: " + integer);
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {

                    }

                    @Override
                    public void onComplete() {

                    }
                });

    }

    private void interval() {
        Observable.interval(5, 2, TimeUnit.SECONDS).observeOn(AndroidSchedulers.mainThread()).subscribe(new Observer<Long>() {
            @Override
            public void onSubscribe(@NonNull Disposable d) {
                Log.d("ddd", "interval operate--->onSubscribe");
            }

            @Override
            public void onNext(@NonNull Long aLong) {
                Log.d("ddd", "interval operate--->Next: " + aLong + " current thread = " + Looper.myLooper());
            }

            @Override
            public void onError(@NonNull Throwable e) {

            }

            @Override
            public void onComplete() {

            }
        });
    }

    public class CustomGlideUrl extends GlideUrl {

        private String mUrl;

        public CustomGlideUrl(String url) {
            super(url);
            mUrl = url;
        }

        @Override
        public String getCacheKey() {
            return mUrl.replace(findTokenParam(), "");
        }

        private String findTokenParam() {
            String tokenParam = "";
            int tokenKeyIndex = mUrl.contains("?token=") ? mUrl.indexOf("?token=") : mUrl.indexOf("&token=");
            if (tokenKeyIndex != -1) {
                int nextAndIndex = mUrl.indexOf("&", tokenKeyIndex + 1);
                if (nextAndIndex != -1) {
                    tokenParam = mUrl.substring(tokenKeyIndex + 1, nextAndIndex + 1);
                } else {
                    tokenParam = mUrl.substring(tokenKeyIndex);
                }
            }
            return tokenParam;
        }

    }
}
