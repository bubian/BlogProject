package com.pds.api.func;

import com.blog.pds.net.exception.ApiException;
import com.blog.pds.net.utils.ResponseUtil;
import com.pds.entity.base.BaseListEntity;
import com.pds.entity.base.DataEntity;
import com.pds.entity.base.DataListEntity;

import io.reactivex.functions.Function;

public class HttpResultListFunc<T extends DataEntity> implements Function<BaseListEntity<T>, DataListEntity<T>> {

    @Override
    public DataListEntity<T> apply(BaseListEntity<T> httpResult) throws Exception {
        if (!httpResult.isSuccess()) {
            httpResult.setMsg(ResponseUtil.transErrorMsg(httpResult.getMsg()));
            if (httpResult.getCode() == 20004) { // 未登录
            }
            throw new ApiException(httpResult.getCode(), httpResult.getMsg());
        }
        return httpResult.getData();
    }

}
