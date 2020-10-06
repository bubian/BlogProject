package com.pds.testapp.thirdsdk;

import org.junit.Test;
import java.util.List;
import java.util.concurrent.TimeUnit;
import io.reactivex.rxjava3.annotations.NonNull;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.ObservableEmitter;
import io.reactivex.rxjava3.core.ObservableOnSubscribe;
import io.reactivex.rxjava3.core.ObservableSource;
import io.reactivex.rxjava3.core.Observer;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.functions.Action;
import io.reactivex.rxjava3.functions.BiFunction;
import io.reactivex.rxjava3.functions.Function;
import io.reactivex.rxjava3.functions.Supplier;

/**
 * @author: pengdaosong
 * CreateTime:  2020-03-20 17:24
 * Email：pengdaosong@medlinker.com
 * Description:
 */

public class RxJavaTest {

    @Test
    private void create(){
        Observable.create((ObservableOnSubscribe<Integer>) emitter -> {
            ObservableEmitter<Integer> observableEmitter = emitter.serialize();
            try {
                // 序列化
                if (!emitter.isDisposed()) {
                    for (int i = 1; i < 3; i++) {
                        System.out.println("create operate--->emitter: "+i);
                        if (1==i){
                             // ExceptionHelper.TERMINATED
//                            observableEmitter.onError(new Throwable("error"));
                        }else {
                            observableEmitter.onNext(i);
                        }

                    }
                    observableEmitter.onComplete();
                }
            } catch (Exception e) {
                observableEmitter.onError(e);
            }
        }).subscribe(new Observer<Integer>() {
            @Override
            public void onSubscribe(@NonNull Disposable d) {
                System.out.println("create operate--->onSubscribe: "+d.toString());
            }

            @Override
            public void onNext(@NonNull Integer integer) {
                System.out.println("create operate--->Next: " + integer);
            }

            @Override
            public void onError(@NonNull Throwable e) {
                System.out.println("create operate--->Error: " + e.getMessage());
            }

            @Override
            public void onComplete() {
                System.out.println("create operate--->Sequence complete.");
            }
        });
    }

    private void defer(){
        Observable.defer(new Supplier<ObservableSource<Integer>>() {
            @Override
            public ObservableSource<Integer> get() throws Throwable {
                return new ObservableSource<Integer>() {

                    @Override
                    public void subscribe(@NonNull Observer<? super Integer> observer) {
                        observer.onNext(1);
                    }
                };
            }
        }).subscribe(new Observer<Integer>() {
            @Override
            public void onSubscribe(@NonNull Disposable d) {

            }

            @Override
            public void onNext(@NonNull Integer o) {
                System.out.println("create operate--->Next: " + o);
            }

            @Override
            public void onError(@NonNull Throwable e) {

            }

            @Override
            public void onComplete() {

            }
        });
    }

    private void EmptyOrNeverOrThrow(){
        Observable.empty();
        Observable.never();
        Observable.error(new Exception(""));
    }

    private void from(){
        Observable.<Integer>fromAction(new Action() {
            @Override
            public void run() throws Throwable {

            }
        });

        Integer[] items = { 0, 1, 2, 3, 4, 5 };
        Observable.fromArray(items);

    }

    private void interval(){
        Observable.interval(2, TimeUnit.SECONDS).subscribe(new Observer<Long>() {
            @Override
            public void onSubscribe(@NonNull Disposable d) {
                System.out.println("interval operate--->onSubscribe");
            }

            @Override
            public void onNext(@NonNull Long aLong) {
                System.out.println("interval operate--->Next: " + aLong);
            }

            @Override
            public void onError(@NonNull Throwable e) {

            }

            @Override
            public void onComplete() {

            }
        });
    }

    private void just(){
        Observable.just(1).subscribe(new Observer<Integer>() {
            @Override
            public void onSubscribe(@NonNull Disposable d) {

            }

            @Override
            public void onNext(@NonNull Integer integer) {

            }

            @Override
            public void onError(@NonNull Throwable e) {

            }

            @Override
            public void onComplete() {

            }
        });
    }

    private void range(){
        Observable.range(1,3).subscribe(new Observer<Integer>() {
            @Override
            public void onSubscribe(@NonNull Disposable d) {

            }

            @Override
            public void onNext(@NonNull Integer integer) {
                System.out.println("interval operate--->Next: " + integer);
            }

            @Override
            public void onError(@NonNull Throwable e) {

            }

            @Override
            public void onComplete() {

            }
        });
    }

    private void repeat(){
        Observable.just(1,3).repeatWhen(new Function<Observable<Object>, ObservableSource<?>>() {
            @Override
            public ObservableSource<?> apply(Observable<Object> objectObservable) throws Throwable {
                return null;
            }
        });
    }

    private void timer(){
        Observable.timer(5,TimeUnit.SECONDS).subscribe();
    }

    private void Buffer(){
        Observable.create((ObservableOnSubscribe<Integer>) emitter -> {
            for (int i = 0 ; i < 10 ; i++){
                emitter.onNext(i);
            }
        }).buffer(5,2).subscribe(new Observer<List<Integer>>() {
            @Override
            public void onSubscribe(@NonNull Disposable d) {


            }

            @Override
            public void onNext(@NonNull List<Integer> integers) {
                for (Integer i : integers){
                    System.out.println("buffer operate--->Next: " + i);
                }
                System.out.println("buffer operate--->------------ ");
            }

            @Override
            public void onError(@NonNull Throwable e) {

            }

            @Override
            public void onComplete() {

            }
        });
    }

    private void flatMap(){
        Observable.fromArray(1).flatMap(new Function<Integer, ObservableSource<?>>() {
            @Override
            public ObservableSource<?> apply(Integer integer) throws Throwable {

                return new ObservableSource<Object>() {
                    @Override
                    public void subscribe(@NonNull Observer<? super Object> observer) {

                    }
                };
            }
        }).subscribe(new Observer<Object>() {
            @Override
            public void onSubscribe(@NonNull Disposable d) {

            }

            @Override
            public void onNext(@NonNull Object o) {

            }

            @Override
            public void onError(@NonNull Throwable e) {

            }

            @Override
            public void onComplete() {

            }
        });
    }

    private void GroupBy(){
        Observable.create((ObservableOnSubscribe<Integer>) emitter -> {
            for (int i = 0 ; i < 10 ; i++){
                emitter.onNext(i);
            }
        }).groupBy(new Function<Integer, Object>() {
            @Override
            public Object apply(Integer integer) throws Throwable {
                return null;
            }
        });
    }

    private void Map(){
        Observable.create((ObservableOnSubscribe<Integer>) emitter -> {
            for (int i = 0 ; i < 10 ; i++){
                emitter.onNext(i);
            }
        }).map(new Function<Integer, Object>() {
            @Override
            public Object apply(Integer integer) throws Throwable {
                return null;
            }
        });
    }

    private void Scan(){
        Observable.create((ObservableOnSubscribe<Integer>) emitter -> {
            for (int i = 0 ; i < 10 ; i++){
                emitter.onNext(i);
            }
        }).scan(null);
    }

    private void Window(){
        Observable.create((ObservableOnSubscribe<Integer>) emitter -> {
            for (int i = 0 ; i < 10 ; i++){
                emitter.onNext(i);
            }
        }).window(null);
    }

    private void Debounce(){
        Observable.create(null).debounce(null);
    }

    private void Distinct(){
        Observable.create(null).distinct(null);
    }

    private void ElementAt(){
        Observable.create(null).elementAt(2);
    }

    private void filter(){
        Observable.create(null).filter(null);
    }

    private void First(){
        Observable.create(null).first(null);
    }

    private void last(){
        Observable.create(null).last(null);
    }

    private void IgnoreElements(){
        Observable.create(null).ignoreElements();
    }

    private void Sample(){
        Observable.create(null).sample(null);
    }

    private void skip(){
        Observable.create(null).skip(4);
    }

    private void take(){
        Observable.create(null).take(4);
    }

    private void AndOrThenOrWhen(){
        Observable.<Integer>create(new ObservableOnSubscribe<Integer>() {
            @Override
            public void subscribe(@NonNull ObservableEmitter<Integer> emitter) throws Throwable {
                emitter.onNext(1);
            }
        }).join(new ObservableSource<Integer>() {

            @Override
            public void subscribe(@NonNull Observer<? super Integer> observer) {
                observer.onNext(2);
            }
        }, new Function<Integer, ObservableSource<Integer>>() {
            @Override
            public ObservableSource<Integer> apply(Integer integer) throws Throwable {

                return new ObservableSource<Integer>() {
                    @Override
                    public void subscribe(@NonNull Observer<? super Integer> observer) {
                          // 立即调用，发射的数据会被移除，最后观察者得不到任何数据
                          // observer.onComplete();
                    }
                };
            }
        }, new Function<Integer, ObservableSource<Integer>>() {
            @Override
            public ObservableSource<Integer> apply(Integer integer) throws Throwable {
                return new ObservableSource<Integer>() {
                    @Override
                    public void subscribe(@NonNull Observer<? super Integer> observer) {
                        // 立即调用，发射的数据会被移除，最后观察者得不到任何数据
                        // observer.onComplete();
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
            public void onSubscribe(@NonNull Disposable d) {

            }

            @Override
            public void onNext(@NonNull Integer integer) {
                // 输出为：create operate--->join: 3
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

    private void Merge(){
        Observable.merge(null,null);
        Observable.mergeDelayError(null,null);
        Observable.create(null).publish().connect();


        Observable.switchOnNext(null);

    }

    @Test
    public void publish(){
        Observable<Integer> observable = Observable.<Integer>create(emitter -> {
            ObservableEmitter<Integer> observableEmitter = emitter.serialize();
            try {
                // 序列化
                if (!emitter.isDisposed()) {
                    for (int i = 0; i < 4; i++) {
                        System.out.println("create operate--->emitter: "+i);
                        if (1==i){
                            // ExceptionHelper.TERMINATED
                            observableEmitter.onError(new Throwable("error"));
                        }else {
                            observableEmitter.onNext(i);
                        }
                    }
                    observableEmitter.onComplete();
                }
            } catch (Exception e) {
                observableEmitter.onError(e);
            }
        }).publish().refCount(2);

        observable.subscribe(new Observer<Integer>() {
            @Override
            public void onSubscribe(@NonNull Disposable d) {
                System.out.println("publish operate--->onSubscribe: "+d.toString());
            }

            @Override
            public void onNext(@NonNull Integer integer) {
                System.out.println("publish operate--->Next: " + integer);
            }

            @Override
            public void onError(@NonNull Throwable e) {
                System.out.println("publish operate--->Error: " + e.getMessage());
            }

            @Override
            public void onComplete() {
                System.out.println("publish operate--->Sequence complete.");
            }
        });
        observable.subscribe(new Observer<Integer>() {
            @Override
            public void onSubscribe(@NonNull Disposable d) {
                System.out.println("publish operate--->1onSubscribe: "+d.toString());
            }

            @Override
            public void onNext(@NonNull Integer integer) {
                System.out.println("publish operate--->1Next: " + integer);
            }

            @Override
            public void onError(@NonNull Throwable e) {
                System.out.println("publish operate--->1Error: " + e.getMessage());
            }

            @Override
            public void onComplete() {
                System.out.println("publish operate--->1Sequence complete.");
            }
        });


//        observable.connect();
    }

}
