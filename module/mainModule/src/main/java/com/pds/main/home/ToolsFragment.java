package com.pds.main.home;

import com.alibaba.android.vlayout.LayoutHelper;
import com.alibaba.android.vlayout.layout.GridLayoutHelper;
import com.pds.base.act.BaseFragment;
import com.pds.base.adapter.databinding.DBVLayoutListAdapter;
import com.pds.base.adapter.viewhold.DBViewHolder;
import com.pds.entity.common.ItemEntity;
import com.pds.main.R;
import com.pds.router.module.ModuleGroupRouter;
import com.pds.sample.databinding.DbPracticeMenuBinding;
import com.pds.ui.view.vlayout.VLayoutRecycleView;

import java.util.ArrayList;
import java.util.List;

/**
 * @author: pengdaosong
 * @CreateTime: 2020/10/30 5:22 PM
 * @Email: pengdaosong@medlinker.com
 * @Description:
 */
public class ToolsFragment extends BaseFragment {

    private VLayoutRecycleView mRecycleView;

    private List<ItemEntity> data = new ArrayList<>();

    private static final String[] mTitle = {
            "地址查询"};
    private static final int[] mIcon = {
            R.mipmap.ic_address};

    private static final String[] mRouter = {
            ModuleGroupRouter.ADDRESS_FIND};

    @Override
    protected int layoutId() {
        return R.layout.frag_home_tools;
    }

    @Override
    protected void initView() {
        initData();
        mRecycleView = mView.findViewById(R.id.recycleView);
        mRecycleView.getDelegateAdapter().addAdapter(new DBVLayoutListAdapter<ItemEntity, DbPracticeMenuBinding>(R.layout.db_practice_menu, data) {
            @Override
            public LayoutHelper onCreateLayoutHelper() {
                int dp_8 = getResources().getDimensionPixelSize(com.pds.sample.R.dimen.dp_8);
                GridLayoutHelper gridLayoutHelper = new GridLayoutHelper(4, data.size(), 3 * dp_8, dp_8);
                gridLayoutHelper.setMarginBottom(2 * dp_8);
                gridLayoutHelper.setAutoExpand(false);
                return gridLayoutHelper;
            }

            @Override
            public void onBindView(DBViewHolder<DbPracticeMenuBinding> holder, int position, ItemEntity data) {
                holder.binding.setData(data);
                holder.binding.ivIcon.setBackgroundResource(data.iconId);
            }
        });
    }

    private void initData() {
        for (int i = 0; i < mRouter.length; i++) {
            data.add(new ItemEntity(mIcon[i], mTitle[i], mRouter[i]));
        }
    }
}
