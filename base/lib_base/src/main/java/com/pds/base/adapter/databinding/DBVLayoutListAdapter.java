package com.pds.base.adapter.databinding;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.ViewDataBinding;

import com.alibaba.android.vlayout.DelegateAdapter.Adapter;
import com.alibaba.android.vlayout.LayoutHelper;
import com.alibaba.android.vlayout.layout.LinearLayoutHelper;
import com.pds.base.adapter.viewhold.DBViewHolder;
import java.util.List;

/**
 * @author: pengdaosong
 * @CreateTime: 2020/9/24 4:59 PM
 * @Email: pengdaosong@medlinker.com
 * @Description:
 */
public abstract class DBVLayoutListAdapter<T, DB extends ViewDataBinding> extends
        Adapter<DBViewHolder<DB>> {

    private int mLayoutId;
    private List<T> mDataList;

    public DBVLayoutListAdapter(@LayoutRes int id) {
        this.mLayoutId = id;
    }

    public DBVLayoutListAdapter(@LayoutRes int id, List<T> data) {
        this.mLayoutId = id;
        mDataList = data;
    }

    public void setDataList(List<T> dataList) {
        mDataList = dataList;
        notifyDataSetChanged();
    }

    public List<T> getDataList() {
        return mDataList;
    }

    @Override
    public LayoutHelper onCreateLayoutHelper() {
        return new LinearLayoutHelper();
    }

    @NonNull
    @Override
    public DBViewHolder<DB> onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        DB bing = DataBindingUtil
                .inflate(LayoutInflater.from(parent.getContext()), mLayoutId, parent, false);
        return new DBViewHolder<>(bing);
    }

    @Override
    public void onBindViewHolder(@NonNull DBViewHolder<DB> holder, int position) {
        if (null == mDataList || position >= mDataList.size()) {
            return;
        }
        T entity = mDataList.get(position);
        onBindView(holder, position, entity);
        holder.binding.executePendingBindings();
    }

    public abstract void onBindView(DBViewHolder<DB> holder, int position, T data);

    @Override
    public int getItemCount() {
        return null == mDataList ? 0 : mDataList.size();
    }
}
