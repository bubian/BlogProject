package com.pds.gif;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import java.io.File;

public class MainActivity extends AppCompatActivity {
    Bitmap bitmap;
    GifHandler gifHandler;
    ImageView imageView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        imageView= findViewById(R.id.image);
    }

    public void ndkLoadGif(View view) {
        File file=new File(Environment.getExternalStorageDirectory(),"demo.gif");
        gifHandler = new GifHandler(file.getAbsolutePath());
        Log.i("tuch", "ndkLoadGif: "+file.getAbsolutePath());
        //得到gif   width  height  生成Bitmap
        int width=gifHandler.getWidth();
        int height=gifHandler.getHeight();
        bitmap= Bitmap.createBitmap(width,height, Bitmap.Config.ARGB_8888);
        int nextFrame=gifHandler.updateFrame(bitmap);
        handler.sendEmptyMessageDelayed(1,nextFrame);

    }

    Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            int mNextFrame=gifHandler.updateFrame(bitmap);
            handler.sendEmptyMessageDelayed(1,mNextFrame);
            imageView.setImageBitmap(bitmap);
        }
    };
}
