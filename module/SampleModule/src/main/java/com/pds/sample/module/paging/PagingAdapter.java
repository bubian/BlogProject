package com.pds.sample.module.paging;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.paging.PagedListAdapter;
import androidx.recyclerview.widget.DiffUtil;

import com.pds.base.adapter.viewhold.ViewHolder;
import com.pds.entity.PagingEntity;
import com.pds.sample.R;

/**
 * @author: pengdaosong
 * CreateTime:  2020/9/19 12:58 PM
 * Email：pengdaosong@medlinker.com
 * Description:
 */
class PagingAdapter extends PagedListAdapter<PagingEntity, ViewHolder> {


    protected PagingAdapter(@NonNull DiffUtil.ItemCallback diffCallback) {
        super(diffCallback);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_main, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        PagingEntity entity = getItem(position);
        holder.setText(R.id.title_tv, entity.getTitle()).setText(R.id.des_tv, entity.getContent());
    }
}
