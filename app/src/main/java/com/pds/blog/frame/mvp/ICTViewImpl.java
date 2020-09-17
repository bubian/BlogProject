package com.pds.blog.frame.mvp;

import com.pds.frame.log.Lg;

/**
 * @author: pengdaosong
 * CreateTime:  2020-09-17 11:31
 * Email：pengdaosong@medlinker.com
 * Description:
 */
public class ICTViewImpl implements ICTView {
    @Override
    public void onResult(String result) {
        Lg.i("frame-mvp-sample:result = " + result);
    }
}
