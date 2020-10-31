package com.pds.sample.module.pdf;

import android.graphics.Color;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.alibaba.android.vlayout.LayoutHelper;
import com.alibaba.android.vlayout.layout.GridLayoutHelper;
import com.pds.base.act.BaseActivity;
import com.pds.base.adapter.viewhold.ViewHolder;
import com.pds.base.adapter.vlayout.VLayoutListAdapter;
import com.pds.router.core.ARouterHelper;
import com.pds.router.module.SampleGroupRouter;
import com.pds.sample.R;
import com.pds.ui.view.vlayout.VLayoutRecycleView;

import java.util.Arrays;
import java.util.List;

/**
 * @author: pengdaosong
 * @CreateTime: 2020/10/26 1:01 PM
 * @Email: pengdaosong@medlinker.com
 * @Description:
 */
@Route(path = SampleGroupRouter.FILE_LOAD)
public class FileLoadActivity extends BaseActivity {

    private VLayoutRecycleView mRecycleView;

    private static final String[] d = {"pdf load", "x5-tbs-pdf", "x5-tbs-doc", "x5-tbs-ppt", "x5-tbs-xlsx","x5-tbs-m4a","QB-x5"};
    private static final String PDF_URL = "https://pub-med-casem.medlinker.com/guanxin_paitent_test.pdf";
    private static final String DOC_URL = "http://47.104.91.148/file_type.docx";
    private static final String PPT_URL = "http://47.104.91.148/城管执法.pptx";
    private static final String XLSX_URL = "http://47.104.91.148/file_geshi.xlsx";
    private static final String M4A_URL = "http://47.104.91.148/20200325_114746.m4a";
    private static final String[] params = {
            PDF_URL,
            PDF_URL,
            DOC_URL,
            PPT_URL,
            XLSX_URL,
            M4A_URL,
            PDF_URL};
    private static final List<String> data = Arrays.asList(d);

    private static final String[] router = {
            SampleGroupRouter.FILE_LOAD,
            SampleGroupRouter.FIlE_X5_LOAD,
            SampleGroupRouter.FIlE_X5_LOAD,
            SampleGroupRouter.FIlE_X5_LOAD,
            SampleGroupRouter.FIlE_X5_LOAD,
            SampleGroupRouter.X5_QB,
            SampleGroupRouter.X5_QB};

    private VLayoutListAdapter<String> adapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_file_load);
        mRecycleView = findViewById(R.id.recyclerView);
        mRecycleView.getDelegateAdapter().addAdapter(adapter = new VLayoutListAdapter<String>() {
            @Override
            public LayoutHelper onCreateLayoutHelper() {
                int dp_8 = getResources().getDimensionPixelSize(R.dimen.dp_8);
                GridLayoutHelper gridLayoutHelper = new GridLayoutHelper(3, data.size(), dp_8);
                gridLayoutHelper.setMarginLeft(dp_8 + dp_8);
                gridLayoutHelper.setMarginRight(dp_8 + dp_8);
                return gridLayoutHelper;
            }

            @Override
            public View getItemView(ViewGroup parent, int viewType) {
                TextView textView = new TextView(parent.getContext());
                textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
                int dp_8 = getResources().getDimensionPixelSize(R.dimen.dp_8);
                textView.setPadding(dp_8, dp_8, dp_8, dp_8);
                textView.setBackgroundResource(R.color.colorPrimary);
                textView.setTextColor(Color.WHITE);
                textView.setGravity(Gravity.CENTER);
                int wh = ViewGroup.LayoutParams.WRAP_CONTENT;
                ViewGroup.MarginLayoutParams params = new ViewGroup.MarginLayoutParams(wh, wh);
                textView.setLayoutParams(params);
                return textView;
            }

            @Override
            public void onBindView(ViewHolder holder, int position, String data) {
                ((TextView) holder.itemView).setText(data);
                holder.itemView.setOnClickListener(v -> ARouterHelper.nav(FileLoadActivity.this, router[position], params[position]));
            }
        });
        adapter.setDataList(data);
    }
}
