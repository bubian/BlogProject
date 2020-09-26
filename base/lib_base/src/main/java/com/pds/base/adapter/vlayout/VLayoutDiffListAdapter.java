package com.pds.base.adapter.vlayout;


import androidx.recyclerview.widget.DiffUtil;

import java.util.List;

/**
 * @author: pengdaosong.
 * @CreateTime: 2019/1/14 12:49 PM
 * @Email：pengdaosong@medlinker.com.
 * @Description:
 */
public abstract class VLayoutDiffListAdapter<T> extends VLayoutListAdapter<T> {

    private List<T> mOldListData;
    private List<T> mNewListData;

    @Override
    public void setDataList(final List<T> dataList) {
        mNewListData = dataList;
        if (null == dataList || dataList.size() < 1) {
            super.setDataList(dataList);
            notifyDataSetChanged();
        }
        mOldListData = getDataList();
        if (mOldListData == null) {
            super.setDataList(dataList);
            notifyItemRangeInserted(0, dataList.size());
        } else {
            DiffUtil.DiffResult result = DiffUtil.calculateDiff(new DiffUtil.Callback() {
                @Override
                public int getOldListSize() {
                    return mOldListData.size();
                }

                @Override
                public int getNewListSize() {
                    return dataList.size();
                }

                @Override
                public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
                    if (check(oldItemPosition, newItemPosition)) {
                        return isItemsTheSame(oldItemPosition, newItemPosition);
                    }
                    return false;
                }

                @Override
                public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
                    if (check(oldItemPosition, newItemPosition)) {
                        return isContentsTheSame(oldItemPosition, newItemPosition);
                    }
                    return false;
                }
            });
            super.setDataList(dataList);
            result.dispatchUpdatesTo(this);
        }
    }

    protected boolean isItemsTheSame(int oldItemPosition, int newItemPosition) {
        return mOldListData.get(oldItemPosition) == mNewListData.get(newItemPosition);
    }

    protected boolean isContentsTheSame(int oldItemPosition, int newItemPosition) {
        return false;
    }

    private boolean check(int oldItemPosition, int newItemPosition) {
        if (null == mOldListData || null == mNewListData) {
            return false;
        }
        if (oldItemPosition >= mOldListData.size() || newItemPosition >= mNewListData.size()) {
            return false;
        }
        return true;
    }
}
