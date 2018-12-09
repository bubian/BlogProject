package com.pds.ui.act;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.pds.ui.R;
import com.pds.ui.view.UiTableLayoutView;

/**
 * @author pengdaosong
 * CreateTime:  2018/12/9 3:22 PM
 * Email：pengdaosong@medlinker.com.
 * Description:自定义表格控件
 */
public class TableActivity extends AppCompatActivity {

    private String[] mHorizontalTitle = {"阶段","上午","下午","晚上"};
    private String[] mVerticalTitle = {"日期","周一","周二","周三","周四","周五","周六","周日"};

    private String[] data = {"日期","周一","周二","周三","周四","周五","周六","周日","日期","周一","周二","周三","周四","周五","周六","周日"
    ,"日期","周一","周二","周三","周四","周五","周六","周日"};

    private UiTableLayoutView uiTableLayoutView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_table);
        uiTableLayoutView = findViewById(R.id.table);
        uiTableLayoutView
                .bindTableTitleData(mHorizontalTitle,mVerticalTitle,true)
//                .bindTableContentData(data);
                .bindTableContentEncoderData("76543210",-1,R.mipmap.hook);
    }
}
