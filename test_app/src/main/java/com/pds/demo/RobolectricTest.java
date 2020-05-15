package com.pds.demo;

/**
 * @author: pengdaosong
 * CreateTime:  2020-05-15 17:16
 * Email：pengdaosong@medlinker.com
 * Description:开发工具提供了我们开发和编译的JVM环境，要运行app需要Dalvik或ART环境。而单元测试的是一个运行过程，我们没有相应的环境，所以运行依赖android.jar包的方法时将会抛出RuntimeException("stub!")。
 * 那么怎么办呢？使用Robolectric可以解决此类问题，它通过实现一套JVM能运行的Android代码，从而做到脱离Android运行环境进行测试。
 */

//参考：https://blog.csdn.net/qq_17766199/article/details/78710177
//@RunWith(RobolectricTestRunner.class)
public class RobolectricTest {

//    @Before
//    public void setUp(){
//        // 日志输出都是采用System.out.print()，但是在Android开发中，我们使用Log会更多。但是单元测试中无法输出Log信息。这时我们可以使用ShadowLog。这样 Log日志都将输出在控制面板中。
//        ShadowLog.stream = System.out;
//    }
//
//    private GlideTestActivity mainActivity;
//
//    @Before
//    public void setUpActivity(){
//        mainActivity = Robolectric.setupActivity(GlideTestActivity.class);
//    }
//
//    /**
//     * 创建Activity测试
//     */
//    @Test
//    public void testMainActivity() {
//        assertNotNull(mainActivity);
//    }
//
//    @Test
//    public void clickingButton_shouldChangeMessage() {
//        GlideTestActivity activity = Robolectric.setupActivity(GlideTestActivity.class);
//        assertNotNull(activity);
//    }
//
//    @Test
//    public void testJump() throws Exception {
//
//        // 触发按钮点击
////        mJumpBtn.performClick();
//
//        // 获取对应的Shadow类
//        ShadowActivity shadowActivity = Shadows.shadowOf(mainActivity);
//        // 借助Shadow类获取启动下一Activity的Intent
//        Intent nextIntent = shadowActivity.getNextStartedActivity();
//        // 校验Intent的正确性
//        assertEquals(nextIntent.getComponent().getClassName(), GlideTestActivity.class.getName());
//    }
}
