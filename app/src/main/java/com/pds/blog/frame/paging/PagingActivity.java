package com.pds.blog.frame.paging;

import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.pds.blog.R;
import com.pds.entity.PagingEntity;
import com.pds.frame.mvvm.VMActivity;
import com.pds.util.unit.UnitConversionUtils;

import in.srain.cube.views.ptr.PtrDefaultHandler;
import in.srain.cube.views.ptr.PtrDefaultHandler2;
import in.srain.cube.views.ptr.PtrFrameLayout;
import in.srain.cube.views.ptr.PtrHandler2;
import in.srain.cube.views.ptr.header.StoreHouseHeader;


/**
 * @author: pengdaosong
 * CreateTime:  2020/9/19 11:45 AM
 * Email：pengdaosong@medlinker.com
 * Description:
 */
public class PagingActivity extends VMActivity<PagingVM> {

    RecyclerView mRecyclerView;

    private PagingAdapter mPagingAdapter;
    private PtrFrameLayout mPtrFrameLayout;

    @Override
    protected void init(@Nullable Bundle savedInstanceState) {
        super.init(savedInstanceState);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_paging);
        mRecyclerView = findViewById(R.id.recyclerView);
        mPtrFrameLayout = findViewById(R.id.ptr_list_view_frame);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.setAdapter(mPagingAdapter = new PagingAdapter(new BaseDiff()));
        initPtrClassicFrameLayout();
        initData();
    }

    private void initPtrClassicFrameLayout() {
        StoreHouseHeader header = new StoreHouseHeader(this);
        header.setPadding(0, UnitConversionUtils.dip2px(this, 20), 0, UnitConversionUtils.dip2px(this, 20));
        header.initWithString("Ultra PTR");
        mPtrFrameLayout.setDurationToCloseHeader(1500);
        mPtrFrameLayout.setHeaderView(header);
        mPtrFrameLayout.addPtrUIHandler(header);

        StoreHouseHeader footer = new StoreHouseHeader(this);
        footer.setPadding(0, UnitConversionUtils.dip2px(this, 20), 0, UnitConversionUtils.dip2px(this, 20));
        footer.initWithString("Ultra Footer");

        mPtrFrameLayout.setFooterView(footer);
        mPtrFrameLayout.addPtrUIHandler(footer);

        mPtrFrameLayout.setPtrHandler(new PtrHandler2() {
            @Override
            public boolean checkCanDoRefresh(PtrFrameLayout frame, View content, View header) {
                return PtrDefaultHandler.checkContentCanBePulledDown(frame, content, header);
            }

            @Override
            public boolean checkCanDoLoadMore(PtrFrameLayout frame, View content, View footer) {
                return PtrDefaultHandler2.checkContentCanBePulledUp(frame, content, footer);
            }

            @Override
            public void onLoadMoreBegin(PtrFrameLayout frame) {
                frame.refreshComplete();
            }

            @Override
            public void onRefreshBegin(PtrFrameLayout frame) {
            }
        });
    }

    private void initData() {
        mViewModel.concertList.observe(this, pagingEntities -> {
            mPagingAdapter.submitList(pagingEntities);
        });

    }

    private static class BaseDiff extends DiffUtil.ItemCallback<PagingEntity> {

        @Override
        public boolean areItemsTheSame(@NonNull PagingEntity oldItem, @NonNull PagingEntity newItem) {
            return false;
        }

        @Override
        public boolean areContentsTheSame(@NonNull PagingEntity oldItem, @NonNull PagingEntity newItem) {
            return false;
        }
    }
}
