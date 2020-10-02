package com.pds.ui.span;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.style.ReplacementSpan;

import androidx.annotation.NonNull;

import com.pds.util.unit.UnitConversionUtils;

/**
 * Author: AllenWen
 * CreateTime: 2017/4/18
 * Email: wenxueguo@medlinker.com
 * Description:
 */

public class CustomVerticalCenterSpan extends ReplacementSpan {

    private int fontSizeSp;    //字体大小sp

    private Context mContext;
    public CustomVerticalCenterSpan(Context context,int fontSizeSp) {
        mContext = context;
        this.fontSizeSp = fontSizeSp;
    }

    @Override
    public int getSize(@NonNull Paint paint, CharSequence text, int start, int end, Paint.FontMetricsInt fm) {
        text = text.subSequence(start, end);
        Paint p = getCustomTextPaint(paint);
        return (int) p.measureText(text.toString());
    }

    @Override
    public void draw(@NonNull Canvas canvas, CharSequence text, int start, int end, float x, int top, int y, int bottom, @NonNull Paint paint) {
        text = text.subSequence(start, end);
        Paint p = getCustomTextPaint(paint);
        Paint.FontMetricsInt fm = p.getFontMetricsInt();
        canvas.drawText(text.toString(), x, y - ((y + fm.descent + y + fm.ascent) / 2 - (bottom + top) / 2), p);    //此处重新计算y坐标，使字体居中
    }

    private TextPaint getCustomTextPaint(Paint srcPaint) {
        TextPaint paint = new TextPaint(srcPaint);
        paint.setTextSize(UnitConversionUtils.sp2Px(mContext, fontSizeSp));   //设定字体大小, sp转换为px
        return paint;
    }

    /**
     * 转换乘数字，并垂直居中显示  例x10
     *
     * @param num
     * @return
     */
    public static SpannableString toxString(Context context,int num) {
        SpannableString spannableString = new SpannableString("x " + String.valueOf(num));
        spannableString.setSpan(new CustomVerticalCenterSpan(context,15), 0, 1, Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
        return spannableString;
    }

}
