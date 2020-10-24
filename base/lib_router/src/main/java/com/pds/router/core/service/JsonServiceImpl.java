package com.pds.router.core.service;

import android.content.Context;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.alibaba.android.arouter.facade.service.SerializationService;
import com.pds.router.core.RouterConstants;

import java.lang.reflect.Type;

/**
 * @author: pengdaosong
 * @CreateTime: 2020/10/15 8:30 PM
 * @Email: pengdaosong@medlinker.com
 * @Description:
 */

@Route(path = RouterConstants.SERVICE_JSON)
public class JsonServiceImpl implements SerializationService {
    @Override
    public <T> T json2Object(String input, Class<T> clazz) {
        return null;
    }

    @Override
    public String object2Json(Object instance) {
        return null;
    }

    @Override
    public <T> T parseObject(String input, Type clazz) {
        return null;
    }

    @Override
    public void init(Context context) {

    }
}
