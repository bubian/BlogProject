package com.pds.testapp.act;

import android.content.Context;
import android.content.Intent;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.rule.ActivityTestRule;

import com.pds.blog.GlideTestActivity;

import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.concurrent.CountDownLatch;

/**
 * @author: pengdaosong
 * CreateTime:  2020-05-14 16:35
 * Email：pengdaosong@medlinker.com
 * Description:
 */
@RunWith(AndroidJUnit4.class)
public class ActTest {

    @Test
    public void launchMarqueeTextPage() {
        Context context = InstrumentationRegistry.getInstrumentation().getTargetContext();
        Intent intent = new Intent(context, GlideTestActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        ActivityTestRule<GlideTestActivity> activityTestRule = new ActivityTestRule<>(GlideTestActivity.class, true, false);
        activityTestRule.launchActivity(intent);
        // 让界面不自动退出
        try {
            CountDownLatch countdown = new CountDownLatch(1);
            countdown.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

//        IdlingRegistry.getInstance().register(activityTestRule.getActivity().getCountingIdlingResource());
    }
}
