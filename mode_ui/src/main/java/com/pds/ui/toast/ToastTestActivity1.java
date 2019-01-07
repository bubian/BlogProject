package com.pds.ui.toast;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;
import com.pds.util.UnitConversionUtils;

/**
 * @author: pengdaosong.
 * CreateTime:  2019/1/5 1:08 AM
 * Email：pengdaosong@medlinker.com.
 * Description:
 */
public class ToastTestActivity1 extends AppCompatActivity {

    public static void startActivity(Context context){
        Intent intent = new Intent(context,ToastTestActivity1.class);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getView());
    }

    private View getView(){
        int _8DP = UnitConversionUtils.dip2px(this,8);
        int _4DP = UnitConversionUtils.dip2px(this,4);
        TextView textView = new TextView(this);
        textView.setPadding(_8DP,_4DP,_8DP,_4DP);
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT);
        params.gravity = Gravity.CENTER;
        textView.setLayoutParams(params);
        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new UploadingCommunityToast(ToastTestActivity1.this,UploadingCommunityToast.UPLOAD_TYPE.START).show();
            }
        });
        return textView;
    }

}
