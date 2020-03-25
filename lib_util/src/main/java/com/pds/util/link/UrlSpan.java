package com.pds.util.link;

import android.os.Parcel;
import android.text.TextPaint;
import android.text.style.URLSpan;
import android.view.View;

import com.pds.util.ui.ViewUtil;

/**
 * @author <a href="mailto:yangjiantao@medlinker.net">Jiantao.Yang</a>
 * @version 3.1
 * @description 功能描述
 * @time 2016/3/3 21:21
 */
public class UrlSpan extends URLSpan {

    private boolean isSelf;

    public UrlSpan(String url, boolean self) {
        super(url);
        isSelf = self;
    }

    @Override
    public void onClick(View widget) {
        if (ViewUtil.isFastDoubleClick()) {
            return;
        }
//        ActivityUtil.toWebActivity(widget.getContext(), WebPagerActivity.WebType.FLAG_DEFAULT_PAGE, "", getURL());
//        ModuleBaseManager.getInstance().getModuleService().toWebThridActivity(widget.getContext(), getURL());
    }

    @Override
    public void updateDrawState(TextPaint ds) {
        super.updateDrawState(ds);
//        ds.setColor(BaseApplication.getApplication().getResources().getColor(isSelf ? R.color.white : R.color.c_0064ff));
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeByte(this.isSelf ? (byte) 1 : (byte) 0);
    }

    protected UrlSpan(Parcel in) {
        super(in);
        this.isSelf = in.readByte() != 0;
    }

    public static final Creator<UrlSpan> CREATOR = new Creator<UrlSpan>() {
        @Override
        public UrlSpan createFromParcel(Parcel source) {
            return new UrlSpan(source);
        }

        @Override
        public UrlSpan[] newArray(int size) {
            return new UrlSpan[size];
        }
    };
}
