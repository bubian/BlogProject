package com.pds.router.service;

import android.content.Context;
import android.net.Uri;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.alibaba.android.arouter.facade.service.PathReplaceService;
import com.pds.router.core.RouterConstants;

/**
 * @author: pengdaosong
 * @CreateTime: 2020/10/15 9:04 PM
 * @Email: pengdaosong@medlinker.com
 * @Description:重写跳转URL
 */

// 实现PathReplaceService接口，并加上一个Path内容任意的注解即可
@Route(path = RouterConstants.SERVICE_PATH_REPLACE) // 必须标明注解
public class PathReplaceServiceImpl implements PathReplaceService {
    @Override
    public String forString(String path) {
        return path;    // 按照一定的规则处理之后返回处理后的结果
    }

    @Override
    public Uri forUri(Uri uri) {
        return uri;    // 按照一定的规则处理之后返回处理后的结果
    }

    @Override
    public void init(Context context) {

    }
}
