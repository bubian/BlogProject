package com.pds.tools.core.dokit.httpcarry.act;

import android.os.Bundle;
import android.text.Html;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.pds.tools.R;
import com.pds.tools.common.JsonUtils;
import com.pds.tools.core.dokit.httpcarry.entity.ApiEntity;

/**
 * @author: pengdaosong
 * @CreateTime: 2020/10/4 7:30 PM
 * @Email: pengdaosong@medlinker.com
 * @Description:
 */
public class ApiInfoActivity extends AppCompatActivity {

    private TextView mRequestTextView;
    private TextView mResponseTextView;
    private ApiEntity mMedApiEntity;
    private TextView mRequestTitle;
    private TextView mResponseTitle;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_api_info);
        mRequestTextView = findViewById(R.id.requestContent);
        mResponseTextView = findViewById(R.id.responseContent);
        mRequestTitle = findViewById(R.id.request);
        mResponseTitle = findViewById(R.id.response);
        mMedApiEntity = getIntent().getParcelableExtra("data");
        if (null == mMedApiEntity) {
            return;
        }
        mRequestTextView.setText(Html.fromHtml(buildRequestText()));
        mResponseTextView.setText(Html.fromHtml(buildResponseText()));
    }

    private String buildRequestText() {
        return buildHtmlStr("protocol：", mMedApiEntity.protocol) +
                buildHtmlStr("path：", mMedApiEntity.path) +
                buildHtmlStr("requestStartMessage：", mMedApiEntity.requestStartMessage) +
                buildHtmlStr("queries：", mMedApiEntity.queries) +
                buildHtmlStr("headers：", mMedApiEntity.headers) +
                buildHtmlStr("requestBody：", mMedApiEntity.requestBody);
    }

    private String buildResponseText() {
        return buildHtmlStr("responseState：",
                mMedApiEntity.responseCode + mMedApiEntity.responseMsg) +
                buildHtmlStr("responseUrl：", buildUrl(mMedApiEntity.responseUrl)) +
                buildHtmlStr("responseBody：",
                        JsonUtils.formatString(mMedApiEntity.responseBody, "<br>")) +
                buildHtmlStr("responseHeaders：", mMedApiEntity.responseHeaders);
    }

    private String buildUrl(String s) {
        if (null == s) {
            return "br";
        }

        int index = s.indexOf("?");
        if (index < 0) {
            return s;
        }

        String url = s.substring(0, index);
        String para = s.substring(index + 1);

        if (TextUtils.isEmpty(para)) {
            return s;
        }

        StringBuffer buffer = new StringBuffer();
        String[] paras = para.split("&");
        for (int i = 0; i < paras.length; i++) {
            String item = paras[i];
            String[] q = item.split("=");
            buffer.append(buildParaHtmlStr((i == 0 ? "?" : "&") + q[0] + "=", q[1]));

        }
        return url + buffer.toString();
    }

    private String buildHtmlStr(String title, String content) {
        return "<b><font color=\"red\">" + title + "</font></b>" + content + "<br>";
    }

    private String buildParaHtmlStr(String title, String content) {
        return "<b><font color=\"blue\">" + title + "</font></b>" + content + "<br>";
    }

    public void doRequest(View view) {
        view.setBackgroundResource(R.color.ff007aff);
        mResponseTitle.setBackgroundResource(R.color.ffa8afc3);
        mRequestTextView.setVisibility(View.VISIBLE);
        mResponseTextView.setVisibility(View.GONE);
    }

    public void doResponse(View view) {
        view.setBackgroundResource(R.color.ff007aff);
        mRequestTitle.setBackgroundResource(R.color.ffa8afc3);
        mRequestTextView.setVisibility(View.GONE);
        mResponseTextView.setVisibility(View.VISIBLE);
    }
}
