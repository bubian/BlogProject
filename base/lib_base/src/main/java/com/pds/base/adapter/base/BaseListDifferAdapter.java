package com.pds.base.adapter.base;

import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

/**
 * @author: pengdaosong
 * @CreateTime: 2020/10/28 11:17 AM
 * @Email: pengdaosong@medlinker.com
 * @Description:
 */
public abstract class BaseListDifferAdapter<T> extends ListAdapter<T, RecyclerView.ViewHolder> {

    public BaseListDifferAdapter(DiffUtil.ItemCallback<T> itemCallback) {
        super(itemCallback);
    }
}
