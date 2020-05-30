package com.pds.edit.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;

import com.pds.edit.core.IMGMode;

public class IMGStickerImageView extends IMGStickerView {

    private CustomOverlayView mImageView;

    private IMGMode mode;
    private int color;
    private int size = 20;

    public IMGStickerImageView(Context context) {
        super(context);
    }

    public IMGStickerImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public IMGStickerImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void mode(IMGMode mode){
        this.mode = mode;
    }

    public void color(int mode){
        this.color = color;
    }

    public void size(int size){
        this.size = size;
    }

    @Override
    public View onCreateContentView(Context context) {
        mImageView = new CustomOverlayView(context);
        return mImageView;
    }
}
