package com.pds.blog;

import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestBuilder;
import com.bumptech.glide.load.model.GlideUrl;
import com.bumptech.glide.request.FutureTarget;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.target.Target;
import com.bumptech.glide.request.transition.Transition;

import java.util.concurrent.TimeUnit;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.ObservableEmitter;
import io.reactivex.rxjava3.core.ObservableOnSubscribe;
import io.reactivex.rxjava3.core.ObservableSource;
import io.reactivex.rxjava3.core.Observer;
import io.reactivex.rxjava3.core.Scheduler;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.functions.BiFunction;
import io.reactivex.rxjava3.functions.Function;
import io.reactivex.rxjava3.schedulers.Schedulers;


/**
 * @author: pengdaosong
 * CreateTime:  2020-03-18 18:04
 * Email：pengdaosong@medlinker.com
 * Description:
 */
public class GlideTestActivity extends AppCompatActivity {

    private static final String URL = "http://47.104.91.148/11111.jpeg";
    private static final String THUM = "http://47.104.91.148/22222.jpeg";
    private ImageView imageView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_glide);
        imageView = findViewById(R.id.image_glide);

        RequestBuilder requestBuilder = Glide.with(this).load(THUM);

//        Glide.with(this)
//                .load(new CustomGlideUrl(URL))
//                .into(imageView);

//        downloadImage();

        Glide.with(this)
                .load(URL)
                .override(Target.SIZE_ORIGINAL, Target.SIZE_ORIGINAL)
                .into(simpleTarget);

        AndOrThenOrWhen();
    }

    private void AndOrThenOrWhen(){
        Observable.<Integer>create(new ObservableOnSubscribe<Integer>() {
            @Override
            public void subscribe(@io.reactivex.rxjava3.annotations.NonNull ObservableEmitter<Integer> emitter) throws Throwable {
                emitter.onNext(1);
            }
        }).join(new ObservableSource<Integer>() {

            @Override
            public void subscribe(@io.reactivex.rxjava3.annotations.NonNull Observer<? super Integer> observer) {
                observer.onNext(2);
            }
        }, new Function<Integer, ObservableSource<Integer>>() {
            @Override
            public ObservableSource<Integer> apply(Integer integer) throws Throwable {

                return new ObservableSource<Integer>() {
                    @Override
                    public void subscribe(@io.reactivex.rxjava3.annotations.NonNull Observer<? super Integer> observer) {
                        observer.onComplete();
                    }
                };
            }
        }, new Function<Integer, ObservableSource<Integer>>() {
            @Override
            public ObservableSource<Integer> apply(Integer integer) throws Throwable {
                return new ObservableSource<Integer>() {
                    @Override
                    public void subscribe(@io.reactivex.rxjava3.annotations.NonNull Observer<? super Integer> observer) {
                        observer.onComplete();
                    }
                };
            }
        }, new BiFunction<Integer, Integer, Integer>() {
            @Override
            public Integer apply(Integer integer, Integer integer2) throws Throwable {
                return integer + integer2;
            }
        }).subscribe(new Observer<Integer>() {
            @Override
            public void onSubscribe(@io.reactivex.rxjava3.annotations.NonNull Disposable d) {

            }

            @Override
            public void onNext(@io.reactivex.rxjava3.annotations.NonNull Integer integer) {
                System.out.println("create operate--->join: " + integer);
            }

            @Override
            public void onError(@io.reactivex.rxjava3.annotations.NonNull Throwable e) {

            }

            @Override
            public void onComplete() {

            }
        });

    }

    private void interval(){
        Observable.interval(5,2, TimeUnit.SECONDS).observeOn(AndroidSchedulers.mainThread()).subscribe(new Observer<Long>() {
            @Override
            public void onSubscribe(@io.reactivex.rxjava3.annotations.NonNull Disposable d) {
                Log.d("ddd","interval operate--->onSubscribe");
            }

            @Override
            public void onNext(@io.reactivex.rxjava3.annotations.NonNull Long aLong) {
                Log.d("ddd","interval operate--->Next: " + aLong + " current thread = "+ Looper.myLooper());
            }

            @Override
            public void onError(@io.reactivex.rxjava3.annotations.NonNull Throwable e) {

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


    public void downloadImage() {
        new Thread(() -> {
            try {
                FutureTarget<Bitmap> target = Glide.with(GlideTestActivity.this)
                        .asBitmap()
                        .load(URL)
                        .submit();
                final Bitmap imageBitmap = target.get();
                runOnUiThread(() -> imageView.setImageBitmap(imageBitmap));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }

    SimpleTarget<Drawable> simpleTarget = new SimpleTarget<Drawable>() {
        @Override
        public void onResourceReady(@NonNull Drawable resource, @Nullable Transition<? super Drawable> transition) {

        }
    };
}
