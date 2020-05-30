package com.pds.edit.view;

import android.content.Context;
import android.graphics.Color;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.widget.TextView;

import com.pds.edit.IMGTextEditDialog;
import com.pds.edit.R;
import com.pds.edit.core.IMGText;
import com.pds.edit.core.util.DisplayUtils;

public class IMGStickerTextView extends IMGStickerView implements IMGTextEditDialog.Callback {

    private static final String TAG = "IMGStickerTextView";

    private TextView mTextView;

    private IMGText mText;

    private IMGTextEditDialog mDialog;

    private static float mBaseTextSize = -1f;

    private static final int PADDING = 26;

    private static final float TEXT_SIZE_SP = 13f;

    public IMGStickerTextView(Context context) {
        this(context, null, 0);
    }

    public IMGStickerTextView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public IMGStickerTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public void onInitialize(Context context) {
        if (mBaseTextSize <= 0) {
            mBaseTextSize = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP,
                    TEXT_SIZE_SP, context.getResources().getDisplayMetrics());
        }
        super.onInitialize(context);
    }

    @Override
    public View onCreateContentView(Context context) {
        mTextView = new TextView(context);
        mTextView.setBackgroundResource(R.mipmap.text_bg);
        mTextView.setSingleLine(true);
        mTextView.setEllipsize(TextUtils.TruncateAt.END);
        mTextView.setGravity(Gravity.CENTER_VERTICAL);
        mTextView.setPadding(DisplayUtils.dip2px(context,13),0,DisplayUtils.dip2px(context,18),0);
        mTextView.setMaxWidth(DisplayUtils.dip2px(context,230));
        mTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP,TEXT_SIZE_SP);
        mTextView.setTextColor(Color.WHITE);

        return mTextView;
    }

    public void setText(IMGText text) {
        mText = text;
        if (mText != null && mTextView != null) {
            mTextView.setText(mText.getText());
            mTextView.setTextColor(mText.getColor());
        }
    }

    public IMGText getText() {
        return mText;
    }

    @Override
    public void onContentTap() {
        IMGTextEditDialog dialog = getDialog();
        dialog.setText(mText);
        dialog.show();
    }

    private IMGTextEditDialog getDialog() {
        if (mDialog == null) {
            mDialog = new IMGTextEditDialog(getContext(), this);
        }
        return mDialog;
    }

    @Override
    public void onText(IMGText text) {
        mText = text;
        if (mText != null && mTextView != null) {
            mTextView.setText(mText.getText());
            mTextView.setTextColor(mText.getColor());
        }
    }
}