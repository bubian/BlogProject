package com.pds.util.link;

import android.text.TextPaint;
import android.text.style.URLSpan;
import android.view.View;

import com.pds.util.ui.ViewUtils;

/**
 * @author <a href="mailto:ganyu@medlinker.com">ganyu</a>
 * @version 3.4
 * @description 文本链接span
 * @time 2016/7/9 15:18
 */
public class TextLinkSpan extends URLSpan {
    private String mTitle;
    private int mColor;

    public TextLinkSpan(String url, String title, int color) {
        super(url);
        this.mTitle = title;
        this.mColor = color;
    }

    @Override
    public void onClick(View widget) {
        if (ViewUtils.isFastDoubleClick()) {
            return;
        }
//        ModuleBaseManager.getInstance().getModuleService().toWebDefaultActivity(widget.getContext(), mTitle, getURL());
    }

    @Override
    public void updateDrawState(TextPaint ds) {
        ds.setUnderlineText(false);
        ds.setColor(mColor);
    }
}
