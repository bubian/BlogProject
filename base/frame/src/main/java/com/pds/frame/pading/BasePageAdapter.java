package com.pds.frame.pading;

import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.paging.PagedList;
import androidx.paging.PagedListAdapter;
import androidx.recyclerview.widget.DiffUtil;

/**
 * @author: pengdaosong
 * CreateTime:  2020-09-17 16:37
 * Email：pengdaosong@medlinker.com
 * Description:
 */
public class BasePageAdapter<D,VH extends BaseViewHolder> extends PagedListAdapter<D, VH>{

    public LiveData<PagedList<Concert>> concertList;

    private PageViewModel pageViewModel;

    protected BasePageAdapter(@NonNull DiffUtil.ItemCallback<D> diffCallback, LifecycleOwner owner) {
        super(diffCallback);
        pageViewModel.concertList.observe(owner, concerts -> {

        });
    }

    @NonNull
    @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull VH holder, int position) {

    }

    private  class BaseDiff extends DiffUtil.ItemCallback<D>{

        @Override
        public boolean areItemsTheSame(@NonNull D oldItem, @NonNull D newItem) {
            return false;
        }

        @Override
        public boolean areContentsTheSame(@NonNull D oldItem, @NonNull D newItem) {
            return false;
        }
    }
}
