package com.pds.ui.view.rl;

import android.content.Context;
import android.util.AttributeSet;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.alibaba.android.vlayout.DelegateAdapter;
import com.alibaba.android.vlayout.VirtualLayoutManager;

/**
 * @author: pengdaosong
 * CreateTime:  2020-01-14 16:07
 * Email：pengdaosong@medlinker.com
 * Description:
 */
public class VLayoutRecycleView extends RecyclerView {

    private DelegateAdapter mDelegateAdapter;

    public VLayoutRecycleView(Context context) {
        super(context);
        init();
    }

    public VLayoutRecycleView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public VLayoutRecycleView(Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    public DelegateAdapter getDelegateAdapter() {
        return mDelegateAdapter;
    }

    private void init() {
        setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        final VirtualLayoutManager layoutManager = new VirtualLayoutManager(getContext());
        setLayoutManager(layoutManager);
        mDelegateAdapter = new DelegateAdapter(layoutManager);
        setAdapter(mDelegateAdapter);
    }
}
