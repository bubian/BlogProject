package com.pds.blog.span;

import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.AbsoluteSizeSpan;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
import android.widget.TextView;

/**
 * SpannableString工具类
 *
 * @author hmy
 */
public class SpanUtil {

    /**
     *
     */
    public static void setSpannableStringByPx(TextView textView, String[] text, int[] textColor, int[] textSize, int[] styleSpan) {
        setSpannableString(textView, text, textColor, textSize, styleSpan, false);
    }

    /**
     *
     */
    public static void setSpannableStringByDip(TextView textView, String[] text, int[] textColor, int[] textSize, int[] styleSpan) {
        setSpannableString(textView, text, textColor, textSize, styleSpan, true);
    }

    /**
     *
     */
    public static void setSpannableString(TextView textView, String[] text, int[] textColor,
                                          int[] textSize, int[] styleSpan, boolean dip) {
        if (text == null || text.length == 0) {
            return;
        }
        String str = "";
        int length = text.length;
        int colorLength = getArrayLength(textColor);
        int sizeLength = getArrayLength(textSize);
        for (int i = 0; i < length; i++) {
            str = str + text[i];
        }

        SpannableString ss = new SpannableString(str);
        for (int j = 0; j < colorLength; j++) {
            int start = getStrStartPosition(text, j);
            ss.setSpan(new ForegroundColorSpan(textColor[j]), start, start + text[j].length(),
                    Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        }

        for (int k = 0; k < sizeLength; k++) {
            int start = getStrStartPosition(text, k);
            ss.setSpan(new AbsoluteSizeSpan(textSize[k], dip), start, start + text[k].length(),
                    Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            //第二个参数boolean dip，如果为true，表示前面的字体大小单位为dip，否则为像素，同上。
        }

        int styleSpanCount = 0;
        if (styleSpan != null) {
            styleSpanCount = styleSpan.length;
        }
        for (int m = 0; m < styleSpanCount; m++) {
            int start = getStrStartPosition(text, m);
            ss.setSpan(new StyleSpan(styleSpan[m]), start, start + text[m].length(),
                    Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        }

        textView.setText(ss);
    }

    private static int getStrStartPosition(String[] text, int position) {
        String str = "";
        for (int i = 0; i < position; i++) {
            str = str + text[i];
        }
        return str.length();
    }

    private static int getArrayLength(int[] array) {
        if (array == null) {
            return 0;
        }
        return array.length;
    }


}
