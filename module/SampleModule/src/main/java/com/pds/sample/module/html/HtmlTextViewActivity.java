package com.pds.sample.module.html;

import android.graphics.Color;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.pds.base.act.BaseActivity;
import com.pds.router.core.ARouterHelper;
import com.pds.router.module.ModuleGroupRouter;
import com.pds.router.module.SampleGroupRouter;
import com.pds.sample.R;

import org.sufficientlysecure.htmltextview.ClickableTableSpan;
import org.sufficientlysecure.htmltextview.DrawTableLinkSpan;
import org.sufficientlysecure.htmltextview.HtmlResImageGetter;
import org.sufficientlysecure.htmltextview.HtmlTextView;

/**
 * @author: pengdaosong
 * @CreateTime: 2020/11/16 5:16 PM
 * @Email: pengdaosong@medlinker.com
 * @Description:
 */
@Route(path = SampleGroupRouter.TV_HTML)
public class HtmlTextViewActivity extends BaseActivity {
    private HtmlTextView htmlTextView;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.html_textview);
        htmlTextView = findViewById(R.id.html_text);

        //text.setRemoveFromHtmlSpace(false); // default is true
        htmlTextView.setClickableTableSpan(new ClickableTableSpanImpl());
        DrawTableLinkSpan drawTableLinkSpan = new DrawTableLinkSpan();
        drawTableLinkSpan.setTableLinkText("[tap for table]");
        htmlTextView.setDrawTableLinkSpan(drawTableLinkSpan);

        // Best to use indentation that matches screen density.
        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        htmlTextView.setListIndentPx(metrics.density * 10);

        // a tag click listener
        htmlTextView.setOnClickATagListener((widget, spannedText, href) -> {
            final Toast toast = Toast.makeText(HtmlTextViewActivity.this, null, Toast.LENGTH_SHORT);
            toast.setText(href);
            toast.show();
            return false;
        });
        htmlTextView.blockQuoteBackgroundColor = getResources().getColor(R.color.color_f684033);
        htmlTextView.blockQuoteStripColor = Color.BLUE;

        htmlTextView.setHtml(R.raw.example, new HtmlResImageGetter(getBaseContext()));
    }

    class ClickableTableSpanImpl extends ClickableTableSpan {
        @Override
        public ClickableTableSpan newInstance() {
            return new ClickableTableSpanImpl();
        }

        @Override
        public void onClick(View widget) {
            ARouterHelper.nav(HtmlTextViewActivity.this, ModuleGroupRouter.HYBRID,getTableHtml());
        }
    }
}
