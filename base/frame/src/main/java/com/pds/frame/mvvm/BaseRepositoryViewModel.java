package com.pds.frame.mvvm;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

/**
 * @author: pengdaosong
 * CreateTime:  2020-09-17 15:45
 * Email：pengdaosong@medlinker.com
 * Description:
 */
public class BaseRepositoryViewModel<R extends BaseRepository> extends BaseViewModel {

    protected R mR;

    public BaseRepositoryViewModel() {
        initR();
    }

    private void initR() {
        Type superClassType = getClass().getGenericSuperclass();
        if (superClassType instanceof ParameterizedType) {
            Type[] types = ((ParameterizedType) superClassType).getActualTypeArguments();
            try {
                Class<R> presenterClassType = (Class<R>) types[0];
                mR = presenterClassType.newInstance();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
