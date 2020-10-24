package com.pds.application;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.alibaba.android.arouter.launcher.ARouter;
import com.pds.router.module.ModuleGroupRouter;
import com.pds.sample.module.pdf.PDFViewActivity;

/**
 * @author: pengdaosong
 * CreateTime:  2019/3/16 4:47 PM
 * Email：pengdaosong@medlinker.com
 * Description:
 */
public class MainActivity extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        findViewById(R.id.jump).setOnClickListener(v -> {
            ARouter.getInstance().build(ModuleGroupRouter.PDF)
                    .withString("url","https://pub-med-casem.medlinker.com/guanxin_paitent_test.pdf")
                    .navigation(this);
//            Intent intent = new Intent(this, PDFViewActivity.class);
//            intent.putExtra("url","https://pub-med-casem.medlinker.com/guanxin_paitent_test.pdf");
//            startActivity(intent);
        });
    }
}
