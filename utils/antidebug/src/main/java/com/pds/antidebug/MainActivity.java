package com.pds.antidebug;

import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity implements IAntiDebugCallback{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        AntiDebug.setAntiDebugCallback(this);
    }


    @Override
    public void beInjectedDebug() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(MainActivity.this, "app正在被调试或被注入", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
