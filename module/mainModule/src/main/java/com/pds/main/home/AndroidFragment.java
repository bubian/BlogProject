package com.pds.main.home;

import android.util.SparseArray;

import com.alibaba.android.vlayout.DelegateAdapter;
import com.pds.base.act.BaseFragment;
import com.pds.base.adapter.viewhold.ViewHolder;
import com.pds.base.adapter.vlayout.VLayoutSingleAdapter;
import com.pds.entity.common.ItemEntity;
import com.pds.main.R;
import com.pds.main.adapter.AndroidAdapter;
import com.pds.router.module.ModuleGroupRouter;
import com.pds.router.module.SampleGroupRouter;
import com.pds.ui.view.vlayout.VLayoutRecycleView;

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

    private static final int[] mTitle = {R.string.base_area, R.string.file_area, R.string.plugin_area};
    private List<ItemEntity> mUIAreaData;
    private List<ItemEntity> mFileAreaData;
    private List<ItemEntity> mPluginAreaData;
    private SparseArray<List<ItemEntity>> mSparseArray = new SparseArray<List<ItemEntity>>();

    @Override
    protected int layoutId() {
        return R.layout.frag_home_android;
    }

    @Override
    protected void initView() {
        mRecycleView = mView.findViewById(R.id.recycleView);
        mDelegateAdapter = mRecycleView.getDelegateAdapter();
        addAreaData();
        initArea();
    }

    private void addAreaData() {
        // 基本专区
        mUIAreaData = new ArrayList<>();
        mUIAreaData.add(buildItemEntity(R.mipmap.ic_ui, "UI学习", ModuleGroupRouter.UI_STUDY));
        mUIAreaData.add(buildItemEntity(R.mipmap.ic_record, "音频录制", ModuleGroupRouter.UI_AUDIO_RECORD));
        mUIAreaData.add(buildItemEntity(R.mipmap.ic_sample, "例子展示", SampleGroupRouter.SAMPLE_HOME));
        mSparseArray.append(mTitle[0], mUIAreaData);
        // 文件专区
        mFileAreaData = new ArrayList<>();
        mFileAreaData.add(buildItemEntity(R.mipmap.ic_file_browse, "文件浏览", SampleGroupRouter.FILE_LOAD));
        mSparseArray.append(mTitle[1], mFileAreaData);
        // 插件化专区
        mPluginAreaData = new ArrayList<>();
        mPluginAreaData.add(buildItemEntity(R.mipmap.ic_plugin, "号码查询", ModuleGroupRouter.PLUGIN_PHONE_PROXY));
        mSparseArray.append(mTitle[2], mPluginAreaData);
    }

    private ItemEntity buildItemEntity(int icon, String title, String router) {
        ItemEntity entity = new ItemEntity(icon, title, router);
        return entity;
    }

    private ItemEntity buildItemEntity(int icon, String title, String router,String extra) {
        ItemEntity entity = new ItemEntity(icon, title, router,extra);
        return entity;
    }

    /**
     * UI相关使用
     */
    private void initArea() {
        for (int i = 0; i < mTitle.length; i++) {
            mDelegateAdapter.addAdapter(new VLayoutSingleAdapter<Integer>(R.layout.item_home_title, mTitle[i]) {
                @Override
                public void onBindView(ViewHolder holder, int position, Integer data) {
                    holder.setText(R.id.tvBase, getResources().getString(data));
                }
            });
            mDelegateAdapter.addAdapter(new AndroidAdapter(R.layout.grid_and_marquee, mSparseArray.get(mTitle[i])));
        }
    }
}
