package com.pds.main.adapter;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.pds.base.adapter.viewhold.ViewHolder;
import com.pds.base.adapter.vlayout.VLayoutSingleAdapter;
import com.pds.main.R;
import com.pds.main.entity.ItemEntity;
import com.pds.router.core.ARouterHelper;
import com.pds.ui.gvp.GVPAdapter;
import com.pds.ui.gvp.GridViewPager;
import com.pds.ui.gvp.OnItemClickListener;
import com.pds.ui.view.RoundRectView;
import com.pds.ui.view.indicator.LinePageIndicator;
import com.pds.util.unit.UnitConversionUtils;

import java.util.List;

/**
 * @author: pengdaosong
 * @CreateTime: 2020/10/30 10:20 PM
 * @Email: pengdaosong@medlinker.com
 * @Description:
 */
public class AndroidAdapter extends VLayoutSingleAdapter<List<ItemEntity>> {
    private GirdAdapter mGridAdapter;
    private static final int GRID_HEIGHT = 160;
    private List<ItemEntity> mGridList;

    public AndroidAdapter(int id, List<ItemEntity> data) {
        super(id, data);
        mGridList = data;
    }

    @Override
    public void onBindView(ViewHolder holder, int position, List<ItemEntity> data) {
        ViewHolder viewHolder = holder;

        GridViewPager viewPager = viewHolder.getView(R.id.gv_menu);
        LinePageIndicator indicator = viewHolder.getView(R.id.layout_grid_indicator);

        if (mGridAdapter == null) {
            mGridAdapter = new GirdAdapter(mGridList);
            mGridAdapter.setOnItemClickListener(mOnItemGridClick);
            viewPager.setAdapter(mGridAdapter);
        } else {
            mGridAdapter.notifyDataSetChanged();
        }
        Context context = viewPager.getContext();
        int h = UnitConversionUtils.dip2px(context, GRID_HEIGHT);
        int height = getData().size() <= viewPager.getColumnsNum() ? h / 2 : h;
        viewPager.setLayoutParams(
                new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, height));
        int pageCount = viewPager.getPageCount();
        if (pageCount > 1) {
            indicator.setVisibility(View.VISIBLE);
            indicator.setRoundMode(RoundRectView.MODE_ALL);
            indicator.setCornerRadius(UnitConversionUtils.dip2px(context, 2));
            indicator.setLineWidth((float) UnitConversionUtils.dip2px(context, 42) / pageCount);
            indicator.setViewPager(viewPager);
            viewPager.setCurrentItem(0);
        } else {
            indicator.setVisibility(View.GONE);
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

    private OnItemClickListener<ItemEntity> mOnItemGridClick = (view, position, data) -> ARouterHelper.nav((Activity) view.getContext(),data.url);
}
