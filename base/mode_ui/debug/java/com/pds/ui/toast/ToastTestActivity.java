package com.pds.ui.toast;

import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.pds.ui.window.WindowUi;
import com.pds.util.unit.UnitConversionUtils;

/**
 * @author: pengdaosong.
 * CreateTime:  2019/1/5 1:08 AM
 * Email：pengdaosong@medlinker.com.
 * Description:
 */
public class ToastTestActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getView());
    }

    private View getView() {

        LinearLayout linearLayout = new LinearLayout(this);
        linearLayout.setOrientation(LinearLayout.HORIZONTAL);
        linearLayout.setGravity(Gravity.CENTER_VERTICAL);

        TextView toast = getTextView("开始上传");
        toast.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new WindowUi(ToastTestActivity.this).show();
            }
        });
        linearLayout.addView(toast, getLayoutParams());
        TextView open = getTextView("打开页面");
        open.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ToastTestActivity1.startActivity(ToastTestActivity.this);
            }
        });
        linearLayout.addView(open, getLayoutParams());
        return linearLayout;
    }

    private TextView getTextView(String str) {
        int _8DP = UnitConversionUtils.dip2px(this, 8);
        int _4DP = UnitConversionUtils.dip2px(this, 4);
        TextView textView = new TextView(this);
        textView.setText(str);
        textView.setTextSize(TypedValue.COMPLEX_UNIT_SP,14);
        textView.setPadding(_8DP, _4DP, _8DP, _4DP);
        return textView;
    }

    private LinearLayout.LayoutParams getLayoutParams() {
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.gravity = Gravity.CENTER;
        return params;
    }

}
