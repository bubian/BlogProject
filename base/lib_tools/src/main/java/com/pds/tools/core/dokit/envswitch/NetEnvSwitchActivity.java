package com.pds.tools.core.dokit.envswitch;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.widget.CheckBox;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.pds.tools.R;
import com.pds.tools.common.cache.PreferencesKey;
import com.pds.tools.common.cache.PreferencesManager;
import com.pds.tools.common.ui.ClearEditText;

import java.lang.reflect.Field;

/**
 * @author: pengdaosong
 * @CreateTime: 2020/9/30 10:07 PM
 * @Email: pengdaosong@medlinker.com
 * @Description: 用于环境切换，比如线上切qa
 */
public class NetEnvSwitchActivity extends AppCompatActivity {
    private static final String TAG = "MedNetEnvSwitchActivity";
//    private static final String BUILD_PATH = "com.medlinker.base.BuildConfig";
    private static final String BUILD_PATH = "com.pds.env.BuildConfig";
    private static final String ENV_FIELD = "API_URL_TYPE";

    private RadioButton mOnline;
    private RadioButton mQa;
    private RadioButton mDev;
    private CheckBox mCheckBox;
    private CheckBox mCustomBox;
    private RadioGroup mEnvRadioGroup;
    private ClearEditText mEtEnv;

    private static final int ONLINE = 3;
    private static final int QA = 4;
    private static final int DEV = 0;

    private int mOriginEnv;

    private String url = "origin";
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_med_env);
        getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        mCheckBox = findViewById(R.id.default_switch);
        mCustomBox = findViewById(R.id.custom_switch);
        mOnline = findViewById(R.id.online);
        mEnvRadioGroup = findViewById(R.id.envGroup);
        mEtEnv = findViewById(R.id.et_env);
        mQa = findViewById(R.id.qa);
        mDev = findViewById(R.id.dev);
        //建议将setOnCheckedChangeListener放在控件checkBox.setChecked前面 否则代码设置选中时会触发回调导致状态不正确
        mCheckBox.setOnCheckedChangeListener((v, isChecked) -> {
            if (isChecked) {
                doDefaultEnv();
            } else {
                mCustomBox.setChecked(true);
                doCustomEnv();
            }
            PreferencesManager.putBoolean(this, PreferencesKey.MED_NET_ENV_SWITCH, isChecked);
        });
        Log.d(TAG,"url = " + url);
        mCustomBox.setOnCheckedChangeListener((v, isChecked) -> {
            mCheckBox.setChecked(!isChecked);
            if (isChecked) {
                doCustomEnv();
            }else {
                mCheckBox.setChecked(true);
                doDefaultEnv();
            }
            PreferencesManager.putBoolean(this, PreferencesKey.MED_NET_ENV_SWITCH, !isChecked);
        });
        initEnv();
        Log.d(TAG,"url after = " + url);
        boolean defaultEnv = PreferencesManager.getBoolean(this, PreferencesKey.MED_NET_ENV_SWITCH, false);
        if (defaultEnv) {
            mCheckBox.setChecked(true);
            doDefaultEnv();
        } else {
            mCustomBox.setChecked(true);
            doCustomEnv();
        }
        mEnvRadioGroup.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId == R.id.online) {
                PreferencesManager.putInt(this,PreferencesKey.MED_NET_DEFAULT_ENV,ONLINE);
            } else if (checkedId == R.id.qa) {
                PreferencesManager.putInt(this,PreferencesKey.MED_NET_DEFAULT_ENV,QA);
            } else if (checkedId == R.id.dev) {
                PreferencesManager.putInt(this,PreferencesKey.MED_NET_DEFAULT_ENV,DEV);
            }else {
                PreferencesManager.putInt(this,PreferencesKey.MED_NET_DEFAULT_ENV,mOriginEnv);
            }
        });
    }

    private void doDefaultEnv() {
        mCustomBox.setChecked(false);
        mEtEnv.setTextColor(getResources().getColor(R.color.ff777777));
        int env = PreferencesManager.getInt(this, PreferencesKey.MED_NET_DEFAULT_ENV);
        if (env == DEV) {
            mDev.setChecked(true);
        } else if (env == QA) {
            mQa.setChecked(true);
        } else {
            mOnline.setChecked(true);
        }
    }

    private void doCustomEnv() {
        mCheckBox.setChecked(false);
        mEtEnv.setTextColor(getResources().getColor(R.color.dk_color_D26282));
        String env = PreferencesManager.getString(this, PreferencesKey.MED_NET_CUSTOM_ENV);
        mEtEnv.setText(env);
    }

    private void initEnv() {
        Log.d(TAG, "initEnv");
        try {
            Class clz = Class.forName(BUILD_PATH);
            if (null == clz) {
                return;
            }
            Field field = clz.getField(ENV_FIELD);
            if (null == field) {
                return;
            }
            field.setAccessible(true);
            mOriginEnv = field.getInt(clz);
            Log.d(TAG, "env:" + mOriginEnv);
            int e = PreferencesManager.getInt(this,PreferencesKey.MED_NET_DEFAULT_ENV,-1);
            if (e < 0){
                PreferencesManager.putInt(this,PreferencesKey.MED_NET_DEFAULT_ENV,mOriginEnv);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        String env = mEtEnv.getEditableText().toString();
        if (TextUtils.isEmpty(env)){
            PreferencesManager.putBoolean(this, PreferencesKey.MED_NET_ENV_SWITCH, true);
        }
        PreferencesManager.putString(this,PreferencesKey.MED_NET_CUSTOM_ENV,mEtEnv.getText().toString());
    }
}