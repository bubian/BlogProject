package com.pds.base.adapter.viewhold;

import androidx.annotation.NonNull;
import androidx.databinding.ViewDataBinding;
import androidx.recyclerview.widget.RecyclerView;

/**
 * @author: pengdaosong
 * @CreateTime: 2020/9/24 5:27 PM
 * @Email: pengdaosong@medlinker.com
 * @Description:
 */
public class DBViewHolder<DB extends ViewDataBinding> extends RecyclerView.ViewHolder {

    public final DB binding;

    public DBViewHolder(@NonNull DB db) {
        super(db.getRoot());
        binding = db;
    }
}
