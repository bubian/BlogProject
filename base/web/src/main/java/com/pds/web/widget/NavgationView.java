package com.pds.web.widget;

import android.content.Context;
import android.net.Uri;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.bumptech.glide.request.RequestOptions;
import com.pds.glide.util.GlideUtil;
import com.pds.web.R;


public class NavgationView extends FrameLayout {

    private ViewGroup mLeftView, mRightView, mTitleGroup;
    private TextView mTitleView, mSubTitleView;
    private LayoutInflater mInflater;
    private ImageView mLeftIcon, mRightIcon, mCloseIcon;
    private int textColor = 0xff007eff;

    public NavgationView(Context context) {
        super(context);
        init();
    }

    public NavgationView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public NavgationView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        mInflater = LayoutInflater.from(getContext());
        mInflater.inflate(R.layout.hybrid_widget_navgation, this);
        mLeftView = findViewById(R.id.hybrid_navgation_left);
        mRightView = findViewById(R.id.hybrid_navgation_right);
        mTitleGroup = findViewById(R.id.hybrid_navgation_title_group);
        mTitleView = findViewById(R.id.hybrid_navgation_title);
        mSubTitleView = findViewById(R.id.hybrid_navgation_subtitle);

        mLeftIcon = findViewById(R.id.hybrid_icon_left);
        mRightIcon = findViewById(R.id.hybrid_icon_right);
        mCloseIcon = findViewById(R.id.hybrid_icon_close);
    }

    public void setTitle(String title) {
        setTitle(title, null, null, null, null);
    }

    public void setTitle(String title, String subTitle, String licon, String ricon, OnClickListener clickListener) {
        mTitleView.setText(title);
        mSubTitleView.setText(subTitle);
        if (TextUtils.isEmpty(subTitle)) {
            mSubTitleView.setVisibility(GONE);
            mTitleView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
        } else {
            mSubTitleView.setVisibility(VISIBLE);
            mTitleView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
            mSubTitleView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12);
        }
        if (TextUtils.isEmpty(licon)) {
            mLeftIcon.setVisibility(GONE);
        } else {
            mLeftIcon.setVisibility(VISIBLE);
            mLeftIcon.setImageURI(Uri.parse(licon));
        }
        if (TextUtils.isEmpty(ricon)) {
            mRightIcon.setVisibility(GONE);
        } else {
            mRightIcon.setVisibility(VISIBLE);
            mRightIcon.setImageURI(Uri.parse(ricon));
        }
        mTitleGroup.setOnClickListener(clickListener);
    }

    public NavgationView cleanNavgation() {
        mRightView.removeAllViews();
        mLeftView.removeAllViews();
        mRightView.setVisibility(GONE);
        mLeftView.setVisibility(GONE);
        return this;
    }

    public NavgationView appendNavgation(Direct direct, String label, String icon, OnClickListener clickListener) {
        appendInner(direct, label, icon, clickListener);
        return this;
    }

    public NavgationView appendNavgation(Direct direct, String label, int iconSource, OnClickListener clickListener) {
        appendInner(direct, label, iconSource, clickListener);
        return this;
    }

    private void appendInner(Direct direct, String label, Object icon, OnClickListener clickListener) {
        if (direct.equals(Direct.LEFT)) {
            mLeftView.setVisibility(VISIBLE);
        } else {
            mRightView.setVisibility(VISIBLE);
        }
        ViewGroup viewGroup = (ViewGroup) mInflater.inflate(R.layout.hybrid_navgation_item, direct.equals(Direct.LEFT) ? mLeftView : mRightView, false);
        ImageView iconView = viewGroup.findViewById(R.id.hybrid_icon);
        TextView textView = viewGroup.findViewById(R.id.hybrid_textview);
        textView.setTextColor(textColor);
        if (icon instanceof String) {
            GlideUtil.load(getContext(), ((String) icon)).apply(new RequestOptions().transform(new CircleCrop())).into(iconView);
            iconView.setVisibility(VISIBLE);
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
            default:
                break;
        }
    }

    public enum Direct {
        RIGHT, LEFT
    }

    /**
     * @param show 是否显示close icon
     */
    public void setCloseIconVisible(boolean show) {
        mCloseIcon.setVisibility(show ? VISIBLE : GONE);
    }

    public void setCloseIconOnClickListener(OnClickListener listener) {
        mCloseIcon.setOnClickListener(listener);
    }
}
