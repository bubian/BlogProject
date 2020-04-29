package com.pds.blog.web.widget;

import android.content.Context;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.pds.blog.R;

import blog.pds.com.three.glide.util.GlideUtil;

public class SearchBar extends FrameLayout {

    private ViewGroup mLeftView, mRightView;
    private LayoutInflater mInflater;
    private EditText mEditText;

    public SearchBar(Context context) {
        super(context);
        init();
    }

    public SearchBar(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public SearchBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        mInflater = LayoutInflater.from(getContext());
        View view = mInflater.inflate(R.layout.hb_searchbar, this, false);
        mLeftView = view.findViewById(R.id.hb_search_bar_left);
        mRightView = view.findViewById(R.id.hb_search_bar_right);
        mEditText = view.findViewById(R.id.hb_search_bar_center);
        mEditText.setOnLongClickListener(v -> true);
        addView(view);
    }

    public SearchBar cleanNavgation(Direct direct) {
        switch (direct) {
            case LEFT:
                mLeftView.removeAllViews();
                break;
            case RIGHT:
                mRightView.removeAllViews();
                break;
        }
        return this;
    }

    public void setData(boolean focus, String hint, int bgColor, OnFocusChangeListener onClickListener, TextWatcher textWatcher) {
        mEditText.setFocusable(focus);
        mEditText.setFocusableInTouchMode(focus);
        mEditText.setHint(hint);
        if (focus) {
            mEditText.requestFocus();
        }
        setBackgroundColor(bgColor);
        try {
            if (null != onClickListener) mEditText.setOnFocusChangeListener(onClickListener);
            if (null != textWatcher) mEditText.addTextChangedListener(textWatcher);
        } catch (Exception e) {
            e.printStackTrace();
        }
        mEditText.setOnEditorActionListener((v, actionId, event) -> {
            try {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    mEditText.clearFocus();
                    return true;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return false;
        });
    }

    public SearchBar cleanSearchbar() {
        mRightView.removeAllViews();
        mLeftView.removeAllViews();
        return this;
    }

    public SearchBar appendSearch(Direct direct, String label, String icon, OnClickListener clickListener) {
        appendInner(direct, label, icon, clickListener);
        return this;
    }

    public SearchBar appendSearch(Direct direct, String label, int iconSource, OnClickListener clickListener) {
        appendInner(direct, label, iconSource, clickListener);
        return this;
    }

    private void appendInner(Direct direct, String label, Object icon, OnClickListener clickListener) {
        ViewGroup viewGroup = (ViewGroup) mInflater.inflate(R.layout.hb_navgation_item, direct.equals(Direct.LEFT) ? mLeftView : mRightView, false);
        ImageView iconView = viewGroup.findViewById(R.id.hb_icon);
        TextView textView = viewGroup.findViewById(R.id.hb_tx);
        if (icon instanceof String) {
            iconView.setVisibility(VISIBLE);
            GlideUtil.load(getContext(), ((String) icon));
        } else if (icon instanceof Integer) {
            int iconSource = (int) icon;
            if (iconSource > 0) {
                iconView.setVisibility(VISIBLE);
                iconView.setImageResource(iconSource);
            } else {
                iconView.setVisibility(GONE);
            }
        }
        viewGroup.setOnClickListener(clickListener);
        textView.setText(label);
        switch (direct) {
            case LEFT:
                mLeftView.addView(viewGroup);
                break;
            case RIGHT:
                mRightView.addView(viewGroup);
                break;
        }
    }

    public enum Direct {
        RIGHT, LEFT
    }
}
