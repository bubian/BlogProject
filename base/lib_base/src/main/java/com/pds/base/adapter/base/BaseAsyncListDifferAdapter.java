package com.pds.base.adapter.base;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.AsyncListDiffer;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

/**
 * @author: pengdaosong
 * @CreateTime: 2020/10/28 11:01 AM
 * @Email: pengdaosong@medlinker.com
 * @Description:
 */
public abstract class BaseAsyncListDifferAdapter<T> extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    protected final AsyncListDiffer<T> mDiffer;

    public BaseAsyncListDifferAdapter(DiffUtil.ItemCallback<T> callback) {
        mDiffer = new AsyncListDiffer(this, callback);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        T t = mDiffer.getCurrentList().get(position);
        onBindView(t, position);
    }

    public void onBindView(T t, int position) {
    }

    /**
     * 使用这个方法提交数据集合
     *
     * @param list
     */
    public void submitList(List<T> list) {
        mDiffer.submitList(list);
    }

    @Override
    public int getItemCount() {
        return mDiffer.getCurrentList().size();
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position, @NonNull List<Object> payloads) {
        if (payloads.isEmpty()){
            super.onBindViewHolder(holder, position, payloads);
        }else {
            Object object = payloads.get(0);
        }
    }
}
