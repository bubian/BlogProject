package com.pds.main.home;

import android.util.SparseArray;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.alibaba.android.vlayout.DelegateAdapter;
import com.pds.base.act.BaseFragment;
import com.pds.base.adapter.viewhold.ViewHolder;
import com.pds.base.adapter.vlayout.VLayoutSingleAdapter;
import com.pds.main.R;
import com.pds.ui.gvp.GVPAdapter;
import com.pds.ui.gvp.GridViewPager;
import com.pds.ui.gvp.OnItemClickListener;
import com.pds.ui.view.RoundRectView;
import com.pds.ui.view.indicator.LinePageIndicator;
import com.pds.ui.view.vlayout.VLayoutRecycleView;
import com.pds.util.unit.UnitConversionUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * @author: pengdaosong
 * @CreateTime: 2020/10/30 5:19 PM
 * @Email: pengdaosong@medlinker.com
 * @Description:
 */
public class AndroidFragment extends BaseFragment {

    private VLayoutRecycleView mRecycleView;
    private DelegateAdapter mDelegateAdapter;

    private static final int[] mTitle = {R.string.base_area};
    private List<ItemEntity> mUIAreaData;
    private SparseArray<List<ItemEntity>> mSparseArray = new SparseArray<List<ItemEntity>>();

    @Override
    protected int layoutId() {
        return R.layout.frag_home_android;
    }

    @Override
    protected void initView() {
        mRecycleView = mView.findViewById(R.id.recycleView);
        mDelegateAdapter = mRecycleView.getDelegateAdapter();
        addBaseAreaData();
        initUIArea();
    }

    private void addBaseAreaData() {
        mUIAreaData = new ArrayList<>();
        mUIAreaData.add(buildItemEntity(R.mipmap.ic_ui, "UI学习"));
        mSparseArray.append(mTitle[0], mUIAreaData);
    }

    private ItemEntity buildItemEntity(int icon, String title) {
        ItemEntity entity = new ItemEntity(icon, title);
        return entity;
    }

    /**
     * UI相关使用
     */
    private void initUIArea() {
        for (int i = 0; i < mTitle.length; i++) {
            mDelegateAdapter.addAdapter(new VLayoutSingleAdapter<Integer>(R.layout.item_home_title, mTitle[i]) {
                @Override
                public void onBindView(ViewHolder holder, int position, Integer data) {
                    holder.setText(R.id.tvBase, getResources().getString(data));
                }
            });
            mDelegateAdapter.addAdapter(new AreaAdapter(R.layout.grid_and_marquee, mSparseArray.get(mTitle[i])));
        }
    }

    private class AreaAdapter extends VLayoutSingleAdapter<List<ItemEntity>> {
        private GirdAdapter mGridAdapter;
        private static final int GRID_HEIGHT = 160;
        private List<ItemEntity> mGridList;

        public AreaAdapter(int id, List<ItemEntity> data) {
            super(id, data);
            mGridList = data;
        }

        @Override
        public void onBindView(ViewHolder holder, int position, List<ItemEntity> data) {
            ViewHolder viewHolder = (ViewHolder) holder;

            GridViewPager viewPager = viewHolder.getView(R.id.gv_menu);
            LinePageIndicator indicator = viewHolder.getView(R.id.layout_grid_indicator);

            if (mGridAdapter == null) {
                mGridAdapter = new GirdAdapter(mGridList);
                mGridAdapter.setOnItemClickListener(mOnItemGridClick);
                viewPager.setAdapter(mGridAdapter);
            } else {
                mGridAdapter.notifyDataSetChanged();
            }
            int h = UnitConversionUtils.dip2px(getContext(), GRID_HEIGHT);
            int height = getData().size() <= viewPager.getColumnsNum() ? h / 2 : h;
            viewPager.setLayoutParams(
                    new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, height));
            int pageCount = viewPager.getPageCount();
            if (pageCount > 1) {
                indicator.setVisibility(View.VISIBLE);
                indicator.setRoundMode(RoundRectView.MODE_ALL);
                indicator.setCornerRadius(UnitConversionUtils.dip2px(getContext(),2));
                indicator.setLineWidth((float) UnitConversionUtils.dip2px(getContext(),42) / pageCount);
                indicator.setViewPager(viewPager);
                viewPager.setCurrentItem(0);
            } else {
                indicator.setVisibility(View.GONE);
            }
        }
    }

    private class GirdAdapter extends GVPAdapter<ItemEntity> {

        GirdAdapter(List<ItemEntity> dataList) {
            super(dataList);
        }

        @Override
        public int getItemLayoutId() {
            return R.layout.item_practice_menu;
        }

        @Override
        public void bindItemView(View itemView, int position, ItemEntity data) {
            ImageView iconIv = itemView.findViewById(R.id.iv_icon);
            TextView nameTv = itemView.findViewById(R.id.tv_name);
            iconIv.setBackgroundResource(data.iconId);
            nameTv.setText(data.title);
        }
    }

    private OnItemClickListener<ItemEntity> mOnItemGridClick = new OnItemClickListener<ItemEntity>() {

        @Override
        public void onItemClick(View view, int position, ItemEntity data) {

        }
    };

    private static class ItemEntity {

        private ItemEntity(int icon, String title) {
            this.iconId = icon;
            this.title = title;
        }

        int iconId;
        String title;
    }
}
