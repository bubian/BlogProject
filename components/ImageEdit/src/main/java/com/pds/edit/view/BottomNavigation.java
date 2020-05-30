package com.pds.edit.view;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.IdRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.pds.edit.R;

public class BottomNavigation extends LinearLayout{

    private Context context;
    private OnClickListener listener;
    private int[] imageResIds;
    private int[] textResIds;
    private Resources resources;
    private TextView lastTextView;
    private boolean isSelectedState = false;
    private boolean isFirstClick = true;

    public BottomNavigation(Context context) {
        this(context,null);
    }

    public BottomNavigation(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs,0);
    }

    public BottomNavigation(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;
        resources = context.getResources();
        setOrientation(HORIZONTAL);
        setGravity(Gravity.CENTER_VERTICAL);
        int p = resources.getDimensionPixelSize(R.dimen.image_12_5);
        setPadding(p,0,p,0);
    }

    public void setData(@IdRes @NonNull int[] imageResIds, @IdRes @NonNull int[] textResIds){
        if (imageResIds.length != textResIds.length){
            throw new IllegalArgumentException("imageResIds.length != textResIds.length");
        }

        int len = imageResIds.length;

        this.imageResIds = imageResIds;
        this.textResIds = textResIds;
        LayoutParams params = new LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT,1);
        for (int i = 0 ; i < len ; i++){
            TextView item = createTextView(i);
            addView(item,params);
        }
    }

    public BottomNavigation listener(OnClickListener listener){
        this.listener = listener;
        return this;
    }

    private TextView createTextView(int index){
        String title = resources.getString(textResIds[index]);
        TextView view = new TextView(context);
        view.setId(imageResIds[index]);
        view.setGravity(Gravity.CENTER_HORIZONTAL);
        view.setText(title);
        Drawable drawable = resources.getDrawable(imageResIds[index]);
        drawable.setAlpha(255);
        view.setCompoundDrawablesWithIntrinsicBounds(null,drawable,null,null);
        view.setCompoundDrawablePadding(resources.getDimensionPixelSize(R.dimen.image_6_5));
        view.setTextColor(resources.getColor(R.color.image_color_BD6B6B7F));
        view.setTextSize(TypedValue.COMPLEX_UNIT_SP,13);
        view.setOnClickListener(onClickListener);
        return view;
    }

    private OnClickListener onClickListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            doClick(v,true);
        }
    };


    private void doClick(View v,boolean isCall){
        isSelectedState = !isSelectedState;
        if (isFirstClick){
            isFirstClick = false;
            int num = getChildCount();
            for (int i = 0;i < num ; i++){
                View itemVew = getChildAt(i);
                if (itemVew instanceof TextView){
                    changeTextViewDrawableAlpha((TextView) itemVew,66);
                }
            }
        }
        TextView item = (TextView) v;
        if (null != lastTextView ){
            lastTextView.setTextColor(resources.getColor(R.color.image_color_BD6B6B7F));
            changeTextViewDrawableAlpha(lastTextView,66);
        }

        if (lastTextView != item || !(boolean)item .getTag()){
            item.setTextColor(resources.getColor(R.color.image_color_FF6B6B7F));
            changeTextViewDrawableAlpha(item,255);
        }
        if (null != listener && isCall){
            listener.onClick(v);
        }
        item.setTag(isSelectedState);
        lastTextView = item;
    }

    private void changeTextViewDrawableAlpha(TextView textView,int alpha){
        Drawable[] drawables = textView.getCompoundDrawables();
        if (null != drawables && drawables.length > 0){
            Drawable drawable = drawables[1];
            drawable.setAlpha(alpha);
        }
    }

    public void resetLastSelect(@IdRes int id) {
        View child = findViewById(id);
        if (null != child){
            doClick(child,false);
        }
    }
}
