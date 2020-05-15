package com.pds.demo;

import com.pds.matcher.MobilePhoneMatcher;
import com.pds.rule.TipsRule;

import org.junit.Rule;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.both;
import static org.hamcrest.CoreMatchers.endsWith;
import static org.hamcrest.CoreMatchers.startsWith;
import static org.junit.Assert.assertThat;

/**
 * @author: pengdaosong
 * CreateTime:  2020-05-15 14:30
 * Email：pengdaosong@medlinker.com
 * Description:
 */
public class AssertTest {

    @Rule
    public TipsRule tipsRule = new TipsRule();

    @Test
    public void testAssertThat(){
        // both创建一个匹配项，当指定的两个都符合时者断言成功
        assertThat("Hello UT",both(startsWith("Hello")).and(endsWith("AUT")));
    }

    @Test
    public void testAssertThatMsg(){
        assertThat("字符串不符合要求","Hello UT",both(startsWith("Hello")).and(endsWith("AUT")));
    }

    /**
     * 自定义匹配器
     */
    @Test
    public void testAssertThatMatcher(){
        assertThat("19508460000",new MobilePhoneMatcher());
    }
}
