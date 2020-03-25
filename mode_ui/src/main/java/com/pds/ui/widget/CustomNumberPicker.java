package com.pds.ui.widget;

import android.content.Context;
import android.content.res.Resources.NotFoundException;
import android.graphics.drawable.ColorDrawable;
import android.os.Build.VERSION_CODES;
import android.util.AttributeSet;
import android.widget.NumberPicker;

import androidx.annotation.ColorRes;
import androidx.annotation.RequiresApi;

import java.lang.reflect.Field;

/**
 * @author: pengdaosong. CreateTime:  2018/12/16 11:01 AM Email：pengdaosong@medlinker.com. Description:
 */
@RequiresApi(api = VERSION_CODES.HONEYCOMB)
public class CustomNumberPicker extends NumberPicker {

	private Context mContext;

	public CustomNumberPicker(Context context) {
		super(context);
		mContext = context;
	}

	public CustomNumberPicker(Context context, AttributeSet attrs) {
		super(context, attrs);
		mContext = context;
	}

	public CustomNumberPicker(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		mContext = context;
	}

	/**
	 *
	 * 设置选择器的分割线颜色 和高度
	 *
	 */
	public void setNumberPickerDividerColor(@ColorRes int color, int height) {
		Field[] pickerFields = NumberPicker.class.getDeclaredFields();
		for (Field pf : pickerFields) {
			if (pf.getName().equals("mSelectionDivider")) {
				pf.setAccessible(true);
				try {
					pf.set(this, new ColorDrawable(mContext.getResources().getColor(color)));
				} catch (IllegalArgumentException | IllegalAccessException | NotFoundException e) {
					e.printStackTrace();
				}
				break;
			}

			if (pf.getName().equals("mSelectionDividerHeight")) {
				pf.setAccessible(true);
				try {
					pf.set(this, height);
				} catch (Exception e){
					e.printStackTrace();
				}
				break;
			}
		}
	}
}
