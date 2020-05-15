package com.pds.demo;

import com.pds.blog.web.common.HbParam;

import org.junit.Test;

import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.mock;

/**
 * @author: pengdaosong
 * CreateTime:  2020-05-15 15:08
 * Email：pengdaosong@medlinker.com
 * Description:
 */
public class MockitoTest {

    @Test
    public void testIsNotNull(){
        HbParam mPerson = mock(HbParam.class); //<--使用mock方法
        assertNotNull(mPerson);
    }
}
