package com.pds.blog.frame.paging;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.paging.PagedListAdapter;
import androidx.recyclerview.widget.DiffUtil;

import com.pds.base.holder.BaseViewHolder;
import com.pds.blog.R;
import com.pds.entity.PagingEntity;

/**
 * @author: pengdaosong
 * CreateTime:  2020/9/19 12:58 PM
 * Email：pengdaosong@medlinker.com
 * Description:
 */
class PagingAdapter extends PagedListAdapter<PagingEntity, BaseViewHolder> {


    protected PagingAdapter(@NonNull DiffUtil.ItemCallback diffCallback) {
        super(diffCallback);
    }

    @NonNull
    @Override
    public BaseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_main, parent, false);
        return new BaseViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BaseViewHolder holder, int position) {
        PagingEntity entity = getItem(position);
        holder.setText(R.id.title_tv, entity.getTitle()).setText(R.id.des_tv, entity.getContent());
    }
}
