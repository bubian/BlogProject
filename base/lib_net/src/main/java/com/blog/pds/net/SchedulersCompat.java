package com.blog.pds.net;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.ObservableSource;
import io.reactivex.rxjava3.core.ObservableTransformer;
import io.reactivex.rxjava3.schedulers.Schedulers;

/**
 * @author <a href="mailto:tql@medlinker.net">tqlmorepassion</a>
 * @version 4.0
 * @description 网络请求公共的observable
 * @time 2016-11-21-15:12
 */

public class SchedulersCompat {

    private static final ObservableTransformer computationTransformer = new ObservableTransformer() {
        @Override
        public ObservableSource apply(Observable observable) {
            return observable.subscribeOn(Schedulers.computation())
                    .observeOn(AndroidSchedulers.mainThread());
        }
    };

    private static final ObservableTransformer ioTransformer = new ObservableTransformer() {
        @Override
        public ObservableSource apply(Observable observable) {
            return observable.subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread());
        }
    };

    private static final ObservableTransformer newTransformer = new ObservableTransformer() {
        @Override
        public ObservableSource apply(Observable observable) {
            return observable.subscribeOn(Schedulers.newThread())
                    .observeOn(AndroidSchedulers.mainThread());
        }
    };

    private static final ObservableTransformer trampolineTransformer = new ObservableTransformer() {
        @Override
        public ObservableSource apply(Observable observable) {
            return observable.subscribeOn(Schedulers.trampoline())
                    .observeOn(AndroidSchedulers.mainThread());
        }
    };

    private static final ObservableTransformer executorTransformer = new ObservableTransformer() {
        @Override
        public ObservableSource apply(Observable observable) {
            return observable.subscribeOn(Schedulers.from(ExecutorManager.eventExecutor))
                    .observeOn(AndroidSchedulers.mainThread());
        }
    };

    /**
     * Don't break the chain: use RxJava's compose() operator
     */

    public static <T> ObservableTransformer<T, T> applyComputationSchedulers() {
        return (ObservableTransformer<T, T>) computationTransformer;
    }

    public static <T> ObservableTransformer<T, T> applyIoSchedulers() {
        return (ObservableTransformer<T, T>) ioTransformer;
    }

    public static <T> ObservableTransformer<T, T> applyNewSchedulers() {
        return (ObservableTransformer<T, T>) newTransformer;
    }

    public static <T> ObservableTransformer<T, T> applyTrampolineSchedulers() {
        return (ObservableTransformer<T, T>) trampolineTransformer;
    }

    public static <T> ObservableTransformer<T, T> applyExecutorSchedulers() {
        return (ObservableTransformer<T, T>) executorTransformer;
    }

}