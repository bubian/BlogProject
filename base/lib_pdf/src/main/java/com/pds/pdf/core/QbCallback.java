package com.pds.pdf.core;

/**
 * @author: pengdaosong
 * @CreateTime: 2020/10/26 5:15 PM
 * @Email: pengdaosong@medlinker.com
 * @Description:
 */
public interface QbCallback<T> {
    void onReceiveValue(T value);
}
