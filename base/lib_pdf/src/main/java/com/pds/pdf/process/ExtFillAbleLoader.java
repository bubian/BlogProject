package com.pds.pdf.process;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.github.jorgecastillo.FillableLoader;
import com.github.jorgecastillo.clippingtransforms.ClippingTransform;

/**
 * @author: pengdaosong
 * @CreateTime: 2020/10/23 3:30 PM
 * @Email: pengdaosong@medlinker.com
 * @Description:
 */
public class ExtFillAbleLoader extends FrameLayout implements ProgressListener {

    private  FillableLoader mFillAbleLoader;
    private String mSvgPath;

    public ExtFillAbleLoader(Context context) {
        super(context);
    }

    public ExtFillAbleLoader setFillAbleLoader(FillableLoader fillAbleLoader) {
        this.mFillAbleLoader = fillAbleLoader;
        return this;
    }

    @Override
    public void onProgress(int i) {
    }

    public void svgPath(String svgPath) {
        mSvgPath = svgPath;
    }

    @Override
    public void onStartDownload() {
        setVisibility(View.VISIBLE);
        if (null != mFillAbleLoader){
            mFillAbleLoader.setSvgPath(mSvgPath);
            mFillAbleLoader.start();
        }
    }

    @Override
    public void onComplete() {
        if (null != mFillAbleLoader){
            mFillAbleLoader.reset();
        }
        setVisibility(View.GONE);
    }
}
