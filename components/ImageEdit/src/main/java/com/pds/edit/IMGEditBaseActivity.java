package com.pds.edit;

import android.app.Activity;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.pds.edit.core.IMGMode;
import com.pds.edit.core.IMGText;
import com.pds.edit.core.manage.HistoryStepRecords;
import com.pds.edit.core.util.DisplayUtils;
import com.pds.edit.view.BottomNavigation;
import com.pds.edit.view.IMGDrawView;
import com.pds.edit.view.PaintTypeSelectView;
import com.pds.edit.view.SizeProgressView;

abstract class IMGEditBaseActivity extends Activity implements View.OnClickListener, IMGTextEditDialog.Callback,
        DialogInterface.OnShowListener, DialogInterface.OnDismissListener {

    protected BottomNavigation bottomNavigation;
    protected PaintTypeSelectView paintTypeSelectView;
    protected View clipViewLayout;
    protected IMGDrawView mImgView;
    protected IMGTextEditDialog mTextDialog;

    private static final int PAINT_ID = R.mipmap.bottom_paint;
    private static final int CIRCLE_ID = R.mipmap.bottom_cricle;
    private static final int ARROW_ID = R.mipmap.bottom_arrow;
    private static final int MOSAIC_ID = R.mipmap.bottom_mosaic;
    private static final int TEXT_ID = R.mipmap.bottom_text;
    private static final int CLIP_ID = R.mipmap.bottom_clip;

    private static final int[] imageResIds = {PAINT_ID,CIRCLE_ID,ARROW_ID, MOSAIC_ID,TEXT_ID,CLIP_ID};
    private static final int[] textResIds = {R.string.image_paint,R.string.image_circle,R.string.image_arrow,
            R.string.image_mosaic,R.string.image_text,R.string.image_clip};
    protected View topTitle;
    private ImageView navBack;
    private ImageView navForward;

    private int _64DP;
    private int _72DP;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.image_activity_main);
        _64DP = DisplayUtils.dip2px(this,64);
        _72DP = DisplayUtils.dip2px(this,72);
        initViews();
        mImgView.registerHistoryRecodesListener(new HistoryStepRecords.HistoryStepRecordsChange() {
            @Override
            public void call(boolean isBack, boolean isForward) {
                int back = isBack ? R.mipmap.operate_forward_h : R.mipmap.operate_forward_n;
//                int back = isBack ? R.mipmap.operate_back_h : R.mipmap.operate_back_n;
                int forward = isForward ? R.mipmap.operate_forward_h : R.mipmap.operate_forward_n;
                navBack.setImageDrawable(IMGEditBaseActivity.this.getResources().getDrawable(back));
//                navForward.setImageDrawable(IMGEditBaseActivity.this.getResources().getDrawable(forward));
            }
        });

        doFetchImage(savedInstanceState);
    }

    protected void doFetchImage(Bundle savedInstanceState) { }

    private void initViews() {
        mImgView = findViewById(R.id.image_canvas);
        bottomNavigation = findViewById(R.id.editors_bottom_nav);
        topTitle = findViewById(R.id.top_title);
        clipViewLayout = findViewById(R.id.edit_clip);
        paintTypeSelectView = findViewById(R.id.paint_type_select);
        paintTypeSelectView.setCurrentSelectedId(PaintTypeSelectView.CIRCLE_5);
        doUiChange(false,false);
        mImgView.setPenColor(Color.RED);
        paintTypeSelectView.addCallBack(new PaintTypeSelectView.Call() {
            @Override
            public void call(PaintTypeSelectView paintTypeSelectView) {

                mImgView.setPenColor(paintTypeSelectView.getCurrentSelectedColor());
            }
        }).addProgressChangedListener(new SizeProgressView.OnProgressChangedListener() {
            @Override
            public void onProgressChanged(View view, float progress) {
                mImgView.setPaintRatio(progress);
            }
        });

//        navForward = findViewById(R.id.top_nav_step_forward);
        navBack = findViewById(R.id.top_nav_step_back);

        findViewById(R.id.top_nav_back).setOnClickListener(onClickListener);
//        navForward.setOnClickListener(onClickListener);
        navBack.setOnClickListener(onClickListener);
        findViewById(R.id.top_nav_complete).setOnClickListener(onClickListener);

        findViewById(R.id.ib_clip_cancel).setOnClickListener(onClickListener);
        findViewById(R.id.ib_clip_done).setOnClickListener(onClickListener);
        findViewById(R.id.tv_clip_reset).setOnClickListener(onClickListener);
        findViewById(R.id.ib_clip_rotate).setOnClickListener(onClickListener);


        bottomNavigation.listener(onClickListener).setData(imageResIds,textResIds);
    }

    private View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            int id = v.getId();
            clipViewLayout.setVisibility(View.GONE);
            if (id == R.id.top_nav_complete) {
                onDoneClick();
            } else if (id == PAINT_ID) {
                onModeClick(IMGMode.DOODLE);
            } else if (id == CIRCLE_ID) {
                onModeClick(IMGMode.CIRCLE);
            } else if (id == ARROW_ID) {
                onModeClick(IMGMode.ARROW);
            } else if (id == MOSAIC_ID) {
                onModeClick(IMGMode.MOSAIC);
            } else if (id == TEXT_ID) {
                mImgView.setMode(IMGMode.TEXT);
                onTextModeClick();
            } else if (id == CLIP_ID) {
                hideBottomNavigation();
                onModeClick(IMGMode.CLIP);
            } else if (id == R.id.tv_done) {
                onDoneClick();
            } else if (id == R.id.tv_cancel) {
                onCancelClick();
            } else if (id == R.id.ib_clip_cancel) {
                onCancelClipClick();
            } else if (id == R.id.ib_clip_done) {
                onDoneClipClick();
            } else if (id == R.id.tv_clip_reset) {
                onResetClipClick();
            } else if (id == R.id.ib_clip_rotate) {
                onRotateClipClick();
            } else if (id == R.id.top_nav_back){
                setResult(RESULT_CANCELED);
                finish();
            }

//            else if (id == R.id.top_nav_step_forward){
//                mImgView.stepForward();
//            }

            else if (id == R.id.top_nav_step_back){
                mImgView.stepBack();
            }
            updateModeUI();
        }
    };

    protected void showBottomNavigation(){
        topTitle.setVisibility(View.VISIBLE);
        bottomNavigation.setVisibility(View.VISIBLE);
        FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) mImgView.getLayoutParams();
        params.topMargin = _64DP;
        params.bottomMargin = _72DP;
        mImgView.setLayoutParams(params);
    }


    protected void hideBottomNavigation(){
        topTitle.setVisibility(View.GONE);
        bottomNavigation.setVisibility(View.GONE);
        FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) mImgView.getLayoutParams();
        params.topMargin = 0;
        params.bottomMargin = 0;
        mImgView.setLayoutParams(params);
    }

    public void updateModeUI() {
        IMGMode mode = mImgView.getMode();
        switch (mode) {
            case DOODLE:
                doUiChange(true,true);
                break;
            case MOSAIC:
                doUiChange(false,true);
                break;
            case CIRCLE:
            case ARROW:
                doUiChange(true,false);
                break;
            case CLIP:
                clipViewLayout.setVisibility(View.VISIBLE);
            case TEXT:
            case NONE:
                doUiChange(false,false);

        }
    }

    private void doUiChange(boolean circle,boolean progress){
        paintTypeSelectView.showColorCircleSelect(circle);
        paintTypeSelectView.showSizeProgress(progress);
    }

    public void onTextModeClick() {
        if (mTextDialog == null) {
            mTextDialog = new IMGTextEditDialog(this, this);
            mTextDialog.setOnShowListener(this);
            mTextDialog.setOnDismissListener(this);
        }
        mTextDialog.show();
    }

    @Override
    public void onShow(DialogInterface dialog) {
        hideBottomNavigation();
    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        showBottomNavigation();
    }


    @Override
    public void onBackPressed() {
        if (mImgView.getMode() == IMGMode.CLIP){
            clipViewLayout.setVisibility(View.GONE);
            onCancelClipClick();
            return;
        }else {
            super.onBackPressed();
        }

    }

    protected int getBottomNavigationId(IMGMode mode){

        int id = -1;
        if (mode == IMGMode.DOODLE){
            id = imageResIds[0];
        }else if (mode == IMGMode.CIRCLE){
            id = imageResIds[1];
        }else if (mode == IMGMode.ARROW){
            id = imageResIds[2];
        }else if (mode == IMGMode.MOSAIC){
            id = imageResIds[3];
        }else if (mode == IMGMode.TEXT){
            id = imageResIds[4];
        }else if (mode == IMGMode.CLIP){
            id = imageResIds[5];
        }

        return id;
    }


    public abstract Bitmap getBitmap();
    public abstract void onModeClick(IMGMode mode);
    public abstract void onCancelClick();
    public abstract void onDoneClick();
    public abstract void onCancelClipClick();
    public abstract void onDoneClipClick();
    public abstract void onResetClipClick();
    public abstract void onRotateClipClick();
    @Override
    public abstract void onText(IMGText text);
}
