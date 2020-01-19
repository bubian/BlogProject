package com.pds.blog.refresh;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.alibaba.android.vlayout.DelegateAdapter;
import com.alibaba.android.vlayout.LayoutHelper;
import com.alibaba.android.vlayout.layout.SingleLayoutHelper;
import com.pds.blog.R;
import com.pds.blog.base.BaseActivity;
import com.pds.ui.view.refresh.view.TwoPointRefreshView;
import com.pds.ui.view.refresh.CustomSwipeToRefresh;
import com.pds.ui.view.refresh.view.CircleImageRefreshView;
import com.pds.ui.view.refresh.view.ZoomRefreshHeaderView;
import com.pds.ui.view.rl.VLayoutRecycleView;
import com.pds.util.unit.UnitConversionUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * @author: pengdaosong
 * CreateTime:  2020-01-14 16:14
 * Email：pengdaosong@medlinker.com
 * Description:
 */
public class RefreshActivity extends BaseActivity {

    private VLayoutRecycleView vLayoutRecycleView;
    private CustomSwipeToRefresh swipeToRefresh;
    private HeaderAdapter headerAdapter;
    private ContentAdapter contentAdapter;

    private ZoomRefreshHeaderView zoomHeaderView;
    private CircleImageRefreshView defaultRefreshHeaderView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_refresh);
        initView();

//        zoomHeaderView = new ZoomRefreshHeaderView(this);
        defaultRefreshHeaderView = new CircleImageRefreshView(this);
//        swipeToRefresh.setRefreshHeaderView(zoomHeaderView);
        // 初始化内容数据
        initData();
    }

    private void initView() {
        vLayoutRecycleView = findViewById(R.id.refresh_vl);
        swipeToRefresh = findViewById(R.id.refresh_st);
        headerAdapter = new HeaderAdapter(this);
        contentAdapter = new ContentAdapter(this);

        vLayoutRecycleView.getDelegateAdapter().addAdapter(headerAdapter);
        vLayoutRecycleView.getDelegateAdapter().addAdapter(contentAdapter);
    }

    private void initData() {
        List<String> data = new ArrayList<>();
        for (int i = 0; i < 18 ; i++){
            data.add("数据："+ i);
        }
        contentAdapter.setData(data);
    }

    private class HeaderAdapter extends DelegateAdapter.Adapter{

        private List<String> data = new ArrayList<>();
        private Context context;

        public void setData(List<String> mData) {
            this.data = mData;
            notifyDataSetChanged();
        }

        public HeaderAdapter(Context c) {
            context = c;
        }

        @Override
        public LayoutHelper onCreateLayoutHelper() {
            return new SingleLayoutHelper();
        }

        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.refresh_header_content, parent,false);
            RecyclerView.ViewHolder holder = new RefreshHolder(itemView);
            return holder;
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

        }

        @Override
        public int getItemCount() {
            return 1;
        }
    }

    private class ContentAdapter extends DelegateAdapter.Adapter{

        private List<String> data = new ArrayList<>();
        private Context context;

        public void setData(List<String> data) {
            this.data.addAll(data);
            notifyDataSetChanged();
        }

        public ContentAdapter(Context c) {
            context = c;
        }

        @Override
        public LayoutHelper onCreateLayoutHelper() {
            return new SingleLayoutHelper();
        }

        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            TextView tv = new TextView(context);
            ViewGroup.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, UnitConversionUtils.dip2px(context,50));
            tv.setLayoutParams(params);
            tv.setTextSize(16);
            tv.setTextColor(Color.BLACK);
            tv.setGravity(Gravity.CENTER);
            RecyclerView.ViewHolder holder = new RefreshHolder(tv);
            return holder;
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
            RefreshHolder h = (RefreshHolder) holder;
            View itemView = holder.itemView;
            if (itemView instanceof TwoPointRefreshView){
//                zoomHeaderView.callback(((BaseRefreshHeaderView)itemView)
//                        .bindSwipeToRefresh(swipeToRefresh));
            }
            h.setTv(data.get(position));
        }

        @Override
        public int getItemCount() {
            return data.size();
        }
    }

    private class RefreshHolder extends RecyclerView.ViewHolder{

        public RefreshHolder(@NonNull View itemView) {
            super(itemView);
        }

        public void setTv(String name){
            ((TextView)itemView).setText(name);
        }
    }
}