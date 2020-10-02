package com.pds.web.widget;

import android.content.Context;
import android.os.Build;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewStub;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.pds.web.R;

public class InputWidget extends FrameLayout {

    //含有图片选择组件
    private ViewStub mHasImgViewStub;
    private ViewStub mNormalViewStub;

    private EditText etComment;
    private RecyclerView rvHeadIcons;
    private TextView sendButton;
    private LinearLayout llContent;

    public InputWidget(Context context) {
        super(context);
        setupViews();
    }

    public InputWidget(Context context, AttributeSet attrs) {
        super(context, attrs);
        setupViews();
    }

    public InputWidget(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setupViews();
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public InputWidget(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        setupViews();
    }

    private void setupViews() {
        View view = LayoutInflater.from(getContext()).inflate(R.layout.hb_imputview_framelayout, this);
        mHasImgViewStub = view.findViewById(R.id.layout_has_img);
        mNormalViewStub = view.findViewById(R.id.layout_normal);
    }


    /**
     * @param hasImg 显示选择图片组件
     */
    public void initContentView(boolean hasImg) {
        View view = null;
        if (hasImg) {
            view = mHasImgViewStub.inflate();
            rvHeadIcons = view.findViewById(R.id.rv_head_icons);
            rvHeadIcons.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        } else {
            view = mNormalViewStub.inflate();
        }
        etComment = view.findViewById(R.id.et_comment);
        sendButton = view.findViewById(R.id.tv_send);
    }


    public RecyclerView getImageRecyclerView() {
        return rvHeadIcons;
    }

    public void setSendButtonClickListener(OnClickListener listener) {
        sendButton.setOnClickListener(listener);
    }

    /**
     * @param action
     */
    public void setkeyBoardActionOptions(String action) {
        int imeOptions = EditorInfo.IME_ACTION_UNSPECIFIED;
        switch (action) {
            case "Done":
                imeOptions = EditorInfo.IME_ACTION_DONE;
                break;

            case "Search":
                imeOptions = EditorInfo.IME_ACTION_SEARCH;
                break;
            default:
                imeOptions = EditorInfo.IME_ACTION_SEND;
                break;
        }
        etComment.setImeOptions(imeOptions);
    }

    public void setSendButtonText(String str) {
        if (!TextUtils.isEmpty(str)) {
            // 这里使用的图片没办法更改文案
        }
    }

    /**
     * 默认填充
     *
     * @param str
     */
    public void setDefaultComment(String str) {
        if (!TextUtils.isEmpty(str)) {
            etComment.setText(str);
        }
    }

    public void setEditViewHint(String hint) {
        if (!TextUtils.isEmpty(hint)) {
            etComment.setHint(hint);
        }
    }

    public EditText getEditText() {
        return etComment;
    }
}
