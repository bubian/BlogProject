package com.blog.pds.net.exception;

import com.blog.pds.net.utils.ResponseUtil;

import io.reactivex.rxjava3.functions.Consumer;

/**
 * 网络请求错误捕获
 *
 * @author hmy
 */
public class ErrorConsumer implements Consumer<Throwable> {
    @Override
    public void accept(Throwable throwable) {
        try {
            ResponseUtil.errorHandler(throwable);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
