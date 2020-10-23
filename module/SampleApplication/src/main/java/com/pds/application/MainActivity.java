package com.pds.application;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.pds.pdf.sample.PDFViewActivity;
import com.pds.router.core.ARouterHelper;
import com.pds.sample.module.pdf.PDFWebLoadActivity;
import com.pds.sample.router.ARouterPath;

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
            Intent intent = new Intent(this, PDFViewActivity.class);
            intent.putExtra("url","https://pub-med-casem.medlinker.com/guanxin_paitent_test.pdf");
            startActivity(intent);
        });
    }
}
