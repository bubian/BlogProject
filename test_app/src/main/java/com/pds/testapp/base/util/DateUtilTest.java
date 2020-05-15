package com.pds.testapp.base.util;

import com.pds.util.date.DateUtils;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.text.ParseException;
import java.util.Arrays;
import java.util.Collection;

/**
 * @author: pengdaosong
 * CreateTime:  2020-05-15 13:49
 * Email：pengdaosong@medlinker.com
 * Description: 参数化测试
 */

@RunWith(Parameterized.class)
public class DateUtilTest {

    private String time;

    public DateUtilTest(String time){
        this.time = time;
    }

    @Parameterized.Parameters
    public static Collection primeNumbers(){
        return Arrays.asList(
                "2020-10-12 13:00:12",
                "2020-1-12",
                "2020年1月12日 15时12分04秒");
    }

    @Test(expected = ParseException.class)
    public void  dataToStampTest() throws Exception{
        System.out.println(DateUtils.dateToStamp(time));
    }
}
