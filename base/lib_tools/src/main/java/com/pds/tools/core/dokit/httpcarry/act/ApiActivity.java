package com.pds.tools.core.dokit.httpcarry.act;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.CheckBox;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;


import com.pds.tools.R;
import com.pds.tools.core.dokit.httpcarry.ApiCaptureManager;
import com.pds.tools.core.dokit.httpcarry.entity.ApiEntity;
import com.pds.tools.common.cache.PreferencesKey;
import com.pds.tools.common.cache.PreferencesManager;
import com.pds.tools.common.vlayout.VLayoutRecycleView;
import com.pds.tools.common.vlayout.adapter.VLayoutListAdapter;
import com.pds.tools.common.vlayout.viewhold.ViewHolder;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * @author: pengdaosong
 * @CreateTime: 2020/10/4 2:20 PM
 * @Email: pengdaosong@medlinker.com
 * @Description:
 */
public class ApiActivity extends AppCompatActivity {

    private VLayoutRecycleView mVLayoutRecycleView;
    private CheckBox mCheckBox;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_med_api);
        mVLayoutRecycleView = findViewById(R.id.recycleView);
        mCheckBox = findViewById(R.id.all_switch);
        mVLayoutRecycleView.getDelegateAdapter().addAdapter(mListAdapter);
        mListAdapter.setDataList(ApiCaptureManager.instance().getCache());
        mCheckBox.setOnCheckedChangeListener((v, isChecked) -> {
            PreferencesManager.putBoolean(this, PreferencesKey.MED_API_CAPTURE, isChecked);
        });

        ApiCaptureManager.instance().register(data -> {
            mListAdapter.setDataList(data);
        });

        boolean state = PreferencesManager.getBoolean(this, PreferencesKey.MED_API_CAPTURE, false);
        mCheckBox.setChecked(state);
    }

    private VLayoutListAdapter<ApiEntity> mListAdapter = new VLayoutListAdapter<ApiEntity>(
            R.layout.item_api) {

        @Override
        public void onBindView(ViewHolder holder, int position, ApiEntity data) {
            holder.itemView.setTag(data);
            holder.itemView.setOnClickListener(mListener);
            holder.setText(R.id.method, data.method);
            holder.setText(R.id.path, data.path);
            holder.setText(R.id.time,
                    milliToDateNine(data.time));
        }
    };

    private OnClickListener mListener = v -> {
        ApiEntity data = (ApiEntity) v.getTag();
        Intent intent = new Intent(this, ApiInfoActivity.class);
        intent.putExtra("data", data);
        startActivity(intent);
    };

    public void doClear(View view) {
        ApiCaptureManager.instance().clear();
    }

    public static String milliToDateNine(long time) {
        SimpleDateFormat dateFormat1 = new SimpleDateFormat("HH:mm:ss", Locale.CHINA);
        try {
            return dateFormat1.format(new Date(time));
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }
}
