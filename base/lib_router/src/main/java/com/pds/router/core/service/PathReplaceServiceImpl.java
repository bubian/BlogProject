package com.pds.router.core.service;

import android.content.Context;
import android.net.Uri;
import android.text.TextUtils;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.alibaba.android.arouter.facade.service.PathReplaceService;
import com.pds.router.core.RouterConstants;
import com.pds.router.module.ModuleGroupRouter;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

/**
 * @author: pengdaosong
 * @CreateTime: 2020/10/15 9:04 PM
 * @Email: pengdaosong@medlinker.com
 * @Description:重写跳转URL
 */
// 实现PathReplaceService接口，并加上一个Path内容任意的注解即可
@Route(path = RouterConstants.SERVICE_PATH_REPLACE) // 必须标明注解
public class PathReplaceServiceImpl implements PathReplaceService {
    private static final String LINK = "/link";
    @Override
    public String forString(String path) {
        // 按照一定的规则处理之后返回处理后的结果
        return null == path ? null : forUri(Uri.parse(path)).toString();
    }

    @Override
    public Uri forUri(Uri uri) {
        // 按照一定的规则处理之后返回处理后的结果
        if (null == uri) {
            return null;
        }
        String path = uri.getPath();
        if (TextUtils.equals(LINK,path)) {
            try {
                uri.buildUpon().path(ModuleGroupRouter.HYBRID).query(URLEncoder.encode(uri.getQuery(), "utf-8")).build();
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }
        return uri;
    }

    @Override
    public void init(Context context) {

    }
}
