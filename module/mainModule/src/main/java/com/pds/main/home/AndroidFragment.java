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

import static com.pds.entity.common.ItemEntity.buildItemEntity;

/**
 * @author: pengdaosong
 * @CreateTime: 2020/10/30 5:19 PM
 * @Email: pengdaosong@medlinker.com
 * @Description:
 */
public class AndroidFragment extends BaseFragment {

    private VLayoutRecycleView mRecycleView;
    private DelegateAdapter mDelegateAdapter;

    private static final int[] mTitle = {
            R.string.base_area,
            R.string.widget_area,
            R.string.file_area,
            R.string.plugin_area,
            R.string.google_view_widget,
            R.string.ndk_area};

    private List<ItemEntity> mUIAreaData;
    private List<ItemEntity> mFileAreaData;
    private List<ItemEntity> mPluginAreaData;
    private List<ItemEntity> mHotFixAreaData;
    private List<ItemEntity> mGoogleAreaData;
    private List<ItemEntity> mNDKAreaData;
    private List<ItemEntity> mWidgetAreaData;
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
        mUIAreaData.add(buildItemEntity(R.mipmap.ic_task, "服务任务", SampleGroupRouter.TV_SERVICE_TASK));
        mSparseArray.append(mTitle[0], mUIAreaData);
        // 控件专区
        mWidgetAreaData = new ArrayList<>();
        mWidgetAreaData.add(buildItemEntity(R.mipmap.ic_json, "JSON预览", SampleGroupRouter.WIDGET_PREVIEW,"json"));
        mSparseArray.append(mTitle[1], mWidgetAreaData);
        // 文件专区
        mFileAreaData = new ArrayList<>();
        mFileAreaData.add(buildItemEntity(R.mipmap.ic_file_browse, "文件浏览", SampleGroupRouter.FILE_LOAD));
        mSparseArray.append(mTitle[2], mFileAreaData);
        // 插件化专区
        mPluginAreaData = new ArrayList<>();
        mPluginAreaData.add(buildItemEntity(R.mipmap.ic_plugin, "号码查询", ModuleGroupRouter.PLUGIN_PHONE_PROXY));
        mPluginAreaData.add(buildItemEntity(R.mipmap.ic_a2b, "Activity替换", ModuleGroupRouter.PLUGIN_ACTIVITY_REPLACE));
        mPluginAreaData.add(buildItemEntity(R.mipmap.ic_bu_plugin, "商用插件", ModuleGroupRouter.PLUGIN_COMMERCIAL_PLUGIN));
        mSparseArray.append(mTitle[3], mPluginAreaData);
        // 热修复专区
        // mHotFixAreaData = new ArrayList<>();
        // mHotFixAreaData.add(buildItemEntity(R.mipmap.ic_hotfix, "美团热修改", ModuleGroupRouter.PLUGIN_PHONE_PROXY));
        // mSparseArray.append(mTitle[3], mHotFixAreaData);
        // google例子专区
        mGoogleAreaData = new ArrayList<>();
        mGoogleAreaData.add(buildItemEntity(R.mipmap.ic_widgets, "view组件", ModuleGroupRouter.GOOGLE_VIEW_WIDGET));
        mSparseArray.append(mTitle[4], mGoogleAreaData);
        // NDK专区
        mNDKAreaData = new ArrayList<>();
        mNDKAreaData.add(buildItemEntity(R.mipmap.ic_change_sound, "变声", ModuleGroupRouter.NDK_CHANGE_SOUND));
        mSparseArray.append(mTitle[5], mNDKAreaData);
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
