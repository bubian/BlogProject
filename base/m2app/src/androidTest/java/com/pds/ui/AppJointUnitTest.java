package com.pds.ui;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import com.pds.m2app.ModuleCommonManager;

import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author: pengdaosong
 * CreateTime:  2020-05-14 16:25
 * Email：pengdaosong@medlinker.com
 * Description:
 */
@RunWith(AndroidJUnit4.class)
public class AppJointUnitTest {
    @Test
    public void appJointTest() {
        ModuleCommonManager.getInstance().getModuleService().test("m2app");
    }
}
