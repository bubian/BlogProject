package com.pds.base.adapter.vlayout;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.LayoutRes;
import androidx.recyclerview.widget.RecyclerView;

import com.alibaba.android.vlayout.DelegateAdapter.Adapter;
import com.alibaba.android.vlayout.LayoutHelper;
import com.alibaba.android.vlayout.layout.GridLayoutHelper;
import com.pds.base.adapter.viewhold.ViewHolder;
import com.pds.util.unit.UnitConversionUtils;

import java.util.List;

/**
 * @author: pengdaosong.
 * @CreateTime: 2019/1/9 3:36 PM
 * @Email：pengdaosong@medlinker.com.
 * @Description:
 */
public class VLayoutGridAdapter<T> extends Adapter {

    public List<T> mDataList;
    private int mLayoutId;
    private GridLayoutHelper mGridLayoutHelper;
    private int mGap;

    public VLayoutGridAdapter() {
    }

    public VLayoutGridAdapter(@LayoutRes int id) {
        this.mLayoutId = id;
    }

    public VLayoutGridAdapter(@LayoutRes int id, List<T> data) {
        this.mLayoutId = id;
        mDataList = data;
    }

    public void setDataList(List<T> dataList) {
        mDataList = dataList;
        notifyDataSetChanged();
    }

    public void setSpanCount(int spanCount) {
        if (null != mGridLayoutHelper) {
            mGridLayoutHelper.setSpanCount(spanCount);
        }
    }

    @Override
    public LayoutHelper onCreateLayoutHelper() {
        mGridLayoutHelper = new GridLayoutHelper(3);
        mGridLayoutHelper.setAutoExpand(false);
        mGridLayoutHelper.setGap(mGap);
        return mGridLayoutHelper;
    }

    public int getGap() {
        return mGap;
    }

    public View createItemView(ViewGroup parent, int viewType){
        return null;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView;
        if (mLayoutId <= 0) {
            itemView = createItemView(parent, viewType);
        } else {
            itemView = LayoutInflater.from(parent.getContext()).inflate(mLayoutId, parent, false);
        }
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (null == mDataList || position >= mDataList.size()) {
            return;
        }
        final ViewHolder viewHolder = (ViewHolder) holder;
        T data = mDataList.get(position);
        convert(viewHolder, data, position);

    }

    public void convert(ViewHolder viewHolder, T data, int position){}

    @Override
    public int getItemCount() {
        return null == mDataList ? 0 : mDataList.size();
    }
}
