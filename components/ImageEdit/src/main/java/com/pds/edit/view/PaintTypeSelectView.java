package com.pds.edit.view;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.LinearInterpolator;
import android.widget.RelativeLayout;

import com.pds.edit.R;
import com.pds.edit.core.util.DisplayUtils;

public class PaintTypeSelectView extends RelativeLayout implements ValueAnimator.AnimatorUpdateListener, Animator.AnimatorListener{


    private  ColorCircleView colorCircle1;
    private  ColorCircleView colorCircle2;
    private  ColorCircleView colorCircle3;
    private  ColorCircleView colorCircle4;
    private  ColorCircleView colorCircle5;

    private SizeProgressView progressView;

    private int currentSelectedColor;
    private int currentSelectedId;
    private ColorCircleView lastSelectedColorCircleView;
    private View view;
    private Call call;
    private boolean isFold = false;
    private Context context;

    private ValueAnimator valueAnimator;
    private int _18DP;
    private int _174DP;
    private int _127DP;
    private int _83DP;
    private int _43DP;
    private int _26DP;
    private int _68DP;

    public static final int CIRCLE_1 = R.id.PaintCircle1;
    public static final int CIRCLE_2 = R.id.PaintCircle2;
    public static final int CIRCLE_3 = R.id.PaintCircle3;
    public static final int CIRCLE_4 = R.id.PaintCircle4;
    public static final int CIRCLE_5 = R.id.PaintCircle5;

    public PaintTypeSelectView(Context context) {
        this(context,null);
    }

    public PaintTypeSelectView(Context context, AttributeSet attrs) {
        this(context, attrs,0);
    }

    public PaintTypeSelectView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        view = (LayoutInflater.from(context).inflate(R.layout.paint_type_select_view,this,true));

        this.context = context;
        colorCircle1 = setOnClickListener(CIRCLE_1);
        colorCircle2 = setOnClickListener(CIRCLE_2);
        colorCircle3 = setOnClickListener(CIRCLE_3);
        colorCircle4 = setOnClickListener(CIRCLE_4);
        colorCircle5 = setOnClickListener(CIRCLE_5);

        progressView = view.findViewById(R.id.size_progress);
        valueAnimator = new ValueAnimator();
        valueAnimator.addUpdateListener(this);
        valueAnimator.addListener(this);
        valueAnimator.setDuration(100);
        valueAnimator.setInterpolator(new LinearInterpolator());
        _18DP = DisplayUtils.dip2px(context,18);
        _174DP = DisplayUtils.dip2px(context,174);
        _127DP = DisplayUtils.dip2px(context,127);
        _83DP = DisplayUtils.dip2px(context,83);
        _43DP = DisplayUtils.dip2px(context,43);
        _26DP = DisplayUtils.dip2px(context,26);
        _68DP = DisplayUtils.dip2px(context,68);
        progressView.setPadding(DisplayUtils.dip2px(context,26),0,DisplayUtils.dip2px(context,68),0);
    }

    public void setCurrentSelectedId(int id){
        onClickListener.onClick(getColorCircle(id));
    }

    public boolean isColorCircleVisibility(){
        return colorCircle1.getVisibility() == VISIBLE;
    }

    public boolean getSizeProgressViewVisibility(){
        return progressView.getVisibility() == VISIBLE;
    }

    private ColorCircleView setOnClickListener(int id){
        ColorCircleView v = view.findViewById(id);
        v.setOnClickListener(onClickListener);
        return v;
    }

    public PaintTypeSelectView addCallBack(Call call){
        this.call = call;
        return this;
    }

    public PaintTypeSelectView addProgressChangedListener(SizeProgressView.OnProgressChangedListener onProgressChangedListener){
        progressView.setOnProgressChangedListener(onProgressChangedListener);
        return this;
    }

    public void showColorCircleSelect(boolean isShow){
        if (isShow){
            progressView.setPadding(_26DP,0,_68DP,0);
        }else {
            progressView.setPadding(_26DP,0,_26DP,0);
        }
        showColorCircleSelect1(isShow);
        colorCircle5.setVisibility(isShow ? VISIBLE : GONE);
    }

    private void showColorCircleSelect1(boolean isShow){
        colorCircle1.setVisibility(isShow ? VISIBLE : GONE);
        colorCircle2.setVisibility(isShow ? VISIBLE : GONE);
        colorCircle3.setVisibility(isShow ? VISIBLE : GONE);
        colorCircle4.setVisibility(isShow ? VISIBLE : GONE);
    }

    public void showSizeProgress(boolean isShow){
        progressView.setVisibility(isShow ? VISIBLE : GONE);
    }

    private OnClickListener onClickListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            int id = v.getId();
            ColorCircleView colorCircleView = (ColorCircleView) v;
            if (id == R.id.PaintCircle5){
                if (isFold){
                    isFold = false;
                    valueAnimator.setFloatValues(0,1);
                    showColorCircleSelect1(true);
                }else {
                    valueAnimator.setFloatValues(1,0);
                    isFold = true;
                }
                valueAnimator.start();
            }
            if (lastSelectedColorCircleView == colorCircleView)return;
            if (null != lastSelectedColorCircleView)lastSelectedColorCircleView.resetScale();
            currentSelectedColor = colorCircleView.getColor();
            currentSelectedId = id;
            colorCircleView.doSelected();
            if (null != call)call.call(PaintTypeSelectView.this);
            lastSelectedColorCircleView = colorCircleView;
        }
    };

    public int getCurrentSelectedColor(){
        return currentSelectedColor;
    }

    public int getCurrentSelectedId(){
        return currentSelectedId;
    }

    public void updateSelectedColorCircleView(int id){
        ColorCircleView v = getColorCircle(id);
        if (lastSelectedColorCircleView == v)return;
        if (null != lastSelectedColorCircleView)lastSelectedColorCircleView.resetScale();
        if (null != v){
            v.doSelected();
            currentSelectedColor = v.getColor();
            currentSelectedId = id;
        }
    }

    @Override
    public void onAnimationStart(Animator animation) {
        colorCircle5.setEnabled(false);
    }

    @Override
    public void onAnimationEnd(Animator animation) {
        if (isFold){
            showColorCircleSelect1(false);
        }
        colorCircle5.setEnabled(true);
    }

    @Override
    public void onAnimationCancel(Animator animation) {
        colorCircle5.setEnabled(true);
    }

    @Override
    public void onAnimationRepeat(Animator animation) {

    }

    @Override
    public void onAnimationUpdate(ValueAnimator animation) {
        float value = (float) animation.getAnimatedValue();
        changeMargin(colorCircle1, (int) (value*_174DP));
        changeMargin(colorCircle2, (int) (value*_127DP));
        changeMargin(colorCircle3, (int) (value*_83DP));
        changeMargin(colorCircle4, (int) (value*_43DP));
    }

    private void changeMargin(ColorCircleView view,int v){
        LayoutParams params = (LayoutParams) view.getLayoutParams();
        params.bottomMargin = _18DP + v;
        view.setLayoutParams(params);
    }

    public interface Call{
        void call(PaintTypeSelectView paintTypeSelectView);
    }

    private ColorCircleView getColorCircle(int id){
        if (id == CIRCLE_1) {
            return colorCircle1;
        } else if (id == CIRCLE_2) {
            return colorCircle2;
        } else if (id == CIRCLE_3) {
            return colorCircle3;
        } else if (id == CIRCLE_4) {
            return colorCircle4;
        } else if (id == CIRCLE_5) {
            return colorCircle5;
        } else {
            return null;
        }
    }
}
