package com.pds.util.unit;

import android.content.Context;
import android.util.DisplayMetrics;

/**
 * @author: pengdaosong.
 * CreateTime:  2018/12/9 3:33 PM
 * Email：pengdaosong@medlinker.com.
 * Description:
 */
public class UnitConversionUtils {

    /***============= Android控件常用单位转换=================*/
    public static DisplayMetrics getDisplayMetrics(Context context){
        return context.getResources().getDisplayMetrics();
    }
    public static int px2dip(Context context, float pxValue) {
        return (int) (pxValue / getDisplayMetrics(context).density + 0.5f);
    }

    public static int dip2px(Context context, float dpValue) {
        return (int) (dpValue * getDisplayMetrics(context).density + 0.5f);
    }

    public static int sp2Px(Context context,float spValue) {
        return (int) (spValue * getDisplayMetrics(context).scaledDensity + 0.5f);
    }
}
