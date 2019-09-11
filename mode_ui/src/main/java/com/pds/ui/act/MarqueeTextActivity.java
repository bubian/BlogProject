package com.pds.ui.act;

import android.app.Activity;
import android.os.Bundle;
import androidx.annotation.Nullable;
import com.pds.ui.R;
import com.pds.ui.view.MarqueeTextView;

import java.util.ArrayList;
import java.util.Calendar;

/**
 * @author: pengdaosong.
 * CreateTime:  2019/1/7 9:15 PM
 * Email：pengdaosong@medlinker.com.
 * Description:
 */
public class MarqueeTextActivity extends Activity {

    private MarqueeTextView marqueeTextView;
    private ArrayList<String> dataList = new ArrayList<>(14);

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.marquee_text);
        marqueeTextView = findViewById(R.id.tv_time_content);
        initData();
        marqueeTextView.setContentList(dataList, getTimeText(),"Time");
    }

    private void initData() {
        dataList.add("采菊东篱下,悠然见南山");
        dataList.add("夕阳西下,断肠人在天涯");
        dataList.add("千山鸟飞绝,万经人种灭,一群白鹤上晴天");
        dataList.add("人生若只如初见，何事秋风悲画扇");
        dataList.add("曾经沧海难为水，除却巫山不是云");
        dataList.add("明月几时有？把酒问青天");
        dataList.add("恰同学少年，风华正茂");
        dataList.add("夜来风雨声，花落知多少");
        dataList.add("但愿人长久，千里共婵娟");
    }

    private String getTimeText() {
        Calendar calendar = Calendar.getInstance();
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        if (rangeInDefined(hour, 8, 19)) {
            return "山有木兮木有枝";
        } else {
            return "入我相思门";
        }
    }

    public static boolean rangeInDefined(int current, int min, int max) {
        return Math.max(min, current) == Math.min(current, max);
    }
}
