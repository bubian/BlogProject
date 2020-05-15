package com.pds.rule;

import org.junit.rules.TestRule;
import org.junit.runner.Description;
import org.junit.runners.model.Statement;

/**
 * @author: pengdaosong
 * CreateTime:  2020-05-15 14:43
 * Email：pengdaosong@medlinker.com
 * Description: 自定义Rule
 */
public class TipsRule implements TestRule {
    @Override
    public Statement apply(final Statement base, final Description description) {
        return new Statement() {
            // evaluate前执行方法相当于@Before
            @Override
            public void evaluate() throws Throwable {
                // 获取测试方法的名字
                String methodName = description.getMethodName();
                System.out.println("-------"+ methodName + "------>测试开始！");

                // 运行的测试方法
                base.evaluate();

                // evaluate后执行方法相当于@After
                System.out.println("-------"+ methodName + "------>测试结束！");
            }
        };
    }
}
