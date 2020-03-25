package com.pds.util.link;

import android.text.TextPaint;
import android.view.View;

import com.pds.util.ui.ViewUtil;

import java.util.regex.Pattern;

/**
 * @author <a href="mailto:yangjiantao@medlinker.net">Jiantao.Yang</a>
 * @version 3.1
 * @description 功能描述
 * @time 2016/3/12 14:48
 */
public class PhoneNumberSpan extends android.text.style.ClickableSpan {
    //cellphone 和 固话格式（028-87658888/02887658888/87658888）
    public static final Pattern PATTERN_PHONE = Pattern.compile("^[1][\\d]{10}" + "|" + "^[0]\\d{2,3}[\\-]*\\d{7,8}" + "|" + "^[1-9]\\d{6,7}");
    public static final String SCHEME_PHONE = "tel:";

    private String phoneNumber;
    private boolean isSelf;

    public PhoneNumberSpan(String phoneNumber, boolean self) {
        this.phoneNumber = phoneNumber;
        isSelf = self;
    }

    @Override
    public void onClick(View widget) {
        if (ViewUtil.isFastDoubleClick()) {
            return;
        }
    }

    @Override
    public void updateDrawState(TextPaint ds) {
        super.updateDrawState(ds);
    }
}
