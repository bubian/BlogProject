package com.pds.sample.module.pdf;

import android.graphics.Color;
import android.os.Bundle;
import android.text.Html;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.URLSpan;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.alibaba.android.vlayout.LayoutHelper;
import com.alibaba.android.vlayout.layout.GridLayoutHelper;
import com.pds.base.act.BaseActivity;
import com.pds.base.adapter.databinding.DBVLayoutListAdapter;
import com.pds.base.adapter.viewhold.DBViewHolder;
import com.pds.base.adapter.vlayout.VLayoutSingleAdapter;
import com.pds.entity.common.ItemEntity;
import com.pds.router.core.ARouterHelper;
import com.pds.router.module.BundleKey;
import com.pds.router.module.ModuleGroupRouter;
import com.pds.router.module.SampleGroupRouter;
import com.pds.sample.R;
import com.pds.sample.databinding.DbPracticeMenuBinding;
import com.pds.ui.view.RadiusTextView;
import com.pds.ui.view.vlayout.VLayoutRecycleView;
import com.pds.web.core.HybridConstant;

import java.util.ArrayList;
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

    private static final String[] mTitle = {
            "PDF",
            "Tbs-PDF",
            "Tbs-DOC",
            "Tbs-PPT",
            "Tbs-XLSX",
            "pdfJs-ser",
            "pdfJs-nat",
            "QB-m4a",
            "QB-mp3",
            "QB-pdf",
            "QB-mp4",
            "QB-zip"};
    private static final int[] mIcon = {
            R.mipmap.ic_pdf,
            R.mipmap.ic_x5_pdf,
            R.mipmap.ic_doc,
            R.mipmap.ic_pdf,
            R.mipmap.ic_xlsx,
            R.mipmap.ic_service,
            R.mipmap.ic_js,
            R.mipmap.ic_m4a,
            R.mipmap.ic_mp3,
            R.mipmap.ic_qb,
            R.mipmap.ic_video,
            R.mipmap.ic_zip};
    private static final String PDF_URL = "https://pub-med-casem.medlinker.com/guanxin_paitent_test.pdf";
    private static final String PDF_URL_ONE = "http://47.104.91.148/openssl编程.pdf?type=2";
    private static final String DOC_URL = "http://47.104.91.148/file_type.docx";
    private static final String PPT_URL = "http://47.104.91.148/城管执法.pptx";
    private static final String XLSX_URL = "http://47.104.91.148/file_geshi.xlsx";
    private static final String M4A_URL = "http://47.104.91.148/20200325_114746.m4a";
    private static final String PDF_URL_QB = "http://47.104.91.148/openssl编程.pdf";
    private static final String MP4_URL = "http://47.104.91.148/Dingga.mp4";
    private static final String ZIP_URL = "http://47.104.91.148/pdfjs.zip";
    private static final String MP3_URL = "http://47.104.91.148/test.mp3";
    private static final String[] mFileUrl = {
            PDF_URL,
            PDF_URL,
            DOC_URL,
            PPT_URL,
            XLSX_URL,
            PDF_URL,
            PDF_URL_ONE,
            M4A_URL,
            MP3_URL,
            PDF_URL_QB,
            MP4_URL,
            ZIP_URL};
    private List<ItemEntity> data;

    private static final String[] mRouter = {
            SampleGroupRouter.PDF_LOAD,
            SampleGroupRouter.FIlE_X5_LOAD,
            SampleGroupRouter.FIlE_X5_LOAD,
            SampleGroupRouter.FIlE_X5_LOAD,
            SampleGroupRouter.FIlE_X5_LOAD,
            SampleGroupRouter.PDF_JS,
            SampleGroupRouter.PDF_JS,
            SampleGroupRouter.X5_QB,
            SampleGroupRouter.X5_QB,
            SampleGroupRouter.X5_QB,
            SampleGroupRouter.X5_QB,
            SampleGroupRouter.X5_QB};

    private void initData() {
        data = new ArrayList<>(mRouter.length);
        for (int i = 0; i < mRouter.length; i++) {
            data.add(new ItemEntity(mIcon[i], mTitle[i], mRouter[i], mFileUrl[i]));
        }
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_file_load);
        initData();
        mRecycleView = findViewById(R.id.recyclerView);
        mRecycleView.getDelegateAdapter().addAdapter(new DBVLayoutListAdapter<ItemEntity, DbPracticeMenuBinding>(R.layout.db_practice_menu, data) {
            @Override
            public LayoutHelper onCreateLayoutHelper() {
                int dp_8 = getResources().getDimensionPixelSize(R.dimen.dp_8);
                GridLayoutHelper gridLayoutHelper = new GridLayoutHelper(4, data.size(), 3 * dp_8, dp_8);
                gridLayoutHelper.setMarginBottom(2*dp_8);
                gridLayoutHelper.setAutoExpand(false);
                return gridLayoutHelper;
            }

            @Override
            public void onBindView(DBViewHolder<DbPracticeMenuBinding> holder, int position, ItemEntity data) {
                holder.binding.setData(data);
                holder.binding.ivIcon.setBackgroundResource(data.iconId);
            }
        });

        mRecycleView.getDelegateAdapter().addAdapter(new VLayoutSingleAdapter() {
            @Override
            public View getItemView(ViewGroup parent, int viewType) {
                RadiusTextView textView = new RadiusTextView(FileLoadActivity.this);
                textView.setLineSpacing(0,1.3f);
                int dp_16 = getResources().getDimensionPixelSize(R.dimen.dp_16);
                textView.setPadding(dp_16,dp_16,dp_16,dp_16);
                textView.setAttributes(getResources().getColor(R.color.c_a8afc3), 2, Color.BLACK);
                textView.setLinksClickable(true);
                textView.setLinkTextColor(Color.BLUE);
                textView.setText(Html.fromHtml(DESCRIPTION));
                textView.setMovementMethod(LinkMovementMethod.getInstance());
                CharSequence text = textView.getText();
                if (text instanceof Spannable) {
                    int end = text.length();
                    Spannable sp = (Spannable) textView.getText();
                    URLSpan[] urls = sp.getSpans(0, end, URLSpan.class);
                    SpannableStringBuilder style = new SpannableStringBuilder(text);
                    style.clearSpans();
                    for (final URLSpan url : urls) {
                        //最主要的一点
                        CustomClickUrlSpan myURLSpan = new CustomClickUrlSpan(url.getURL(), view -> ARouterHelper.navWith(FileLoadActivity.this, ModuleGroupRouter.LINK_HYBRID,
                                BundleKey.URL,url.getURL()));
                        style.setSpan(myURLSpan, sp.getSpanStart(url), sp.getSpanEnd(url), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                    }
                    textView.setText(style);
                }
                return textView;
            }
        });
    }

    public static class CustomClickUrlSpan extends ClickableSpan {
        private String url;
        private OnLinkClickListener mListener;

        public CustomClickUrlSpan(String url, OnLinkClickListener listener) {
            this.url=url;
            this.mListener=listener;
        }

        @Override
        public void onClick(View widget) {
            if (mListener!=null){
                mListener.onLinkClick(widget);
            }
        }

        /**
         * 跳转链接接口
         */
        public interface OnLinkClickListener{
            void onLinkClick(View view);
        }
    }

    private static final String DESCRIPTION = "PDF使用原生开源库打开<br/>Tbs开头使用腾讯X5引擎文中的TbsReaderView打开<br/>" +
            "QB开头使用腾讯X5引擎中的QbSdk打开<br/>QSdk一共支持几十种文件格式。详情请查看腾讯X5文件接入功能文档<br/>" +
            "pdfJs-ser使用web导入服务端网页在线查看pdf文档<br/>" +
            "pdfJs-nat把pdfjs开源库集成在本地然后加载pdf，当然也可以把库下载到手机上，但是注意一定要把库里面的域名检测去掉<br/>" +
            "详细请参考我的博客：" + "<a href='https://www.jianshu.com/p/8c8d2363b8a7'>总结】- 文件(pdf,ppt,doc等)加载解决方案</a> ";
}
