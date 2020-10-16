package com.pds.application;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.pds.hilt.ExampleActivity;

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
            Intent intent = new Intent(this, ExampleActivity.class);
            startActivity(intent);
        });
    }
}
