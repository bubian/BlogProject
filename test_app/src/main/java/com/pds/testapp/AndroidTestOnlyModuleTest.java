package com.pds.testapp;

import android.content.Context;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.MediumTest;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import static androidx.test.core.app.ApplicationProvider.getApplicationContext;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;

// 引入需要测试工程的R文件
import com.pds.blog.R;

/**
 * @author: pengdaosong
 * CreateTime:  2020-05-14 13:02
 * Email：pengdaosong@medlinker.com
 * Description:
 */
@RunWith(AndroidJUnit4.class)
@MediumTest
public class AndroidTestOnlyModuleTest {

    private Context mContext;

    @Before
    public void initTargetContext() {
        // Obtain the target context from InstrumentationRegistry
        mContext = getApplicationContext();
        assertThat(mContext, notNullValue());
    }

    @Test
    public void verifyResourceString() {
        assertThat(mContext.getString(R.string.use_native),
                is(equalTo("Use native conversion1")));
    }

}
