package com.pds.blog.web.ui;

import android.content.Intent;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.pds.blog.R;
import com.pds.blog.web.common.HbC;
import com.pds.blog.web.common.HbParamAnimation;

/**
 * @author: pengdaosong
 * CreateTime:  2020-04-29 13:32
 * Email：pengdaosong@medlinker.com
 * Description:
 */
public class HbBaseActivity extends AppCompatActivity {
    private static final String TAG = "HbBaseActivity";

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
    }

    @Override
    public void finish() {
        super.finish();
        HbParamAnimation animation = (HbParamAnimation) getIntent().getSerializableExtra(HbC.INTENT_EXTRA_KEY_ANIMATION);
        if (null == animation || animation.equals(HbParamAnimation.PUSH)) {
            overridePendingTransition(R.anim.hb_left_in, R.anim.hb_right_out);
        } else if (animation.equals(HbParamAnimation.POP)) {
            overridePendingTransition(R.anim.hb_right_in, R.anim.hb_left_out);
        } else if (animation.equals(HbParamAnimation.PRESENT)) {
            overridePendingTransition(R.anim.hb_top_in, R.anim.hb_bottom_out);
        }
    }
}
