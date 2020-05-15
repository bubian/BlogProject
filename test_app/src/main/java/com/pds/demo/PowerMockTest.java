package com.pds.demo;

/**
 * @author: pengdaosong
 * CreateTime:  2020-05-15 16:58
 * Email：pengdaosong@medlinker.com
 * Description:
 */
//@RunWith(PowerMockRunner.class)
public class PowerMockTest {

    // 使用PowerMock就必须加@RunWith(PowerMockRunner.class)，但是我们毕竟有时会使用多个测试框架，可能@RunWith会占用。这时我们可以使用@Rule。
    // @Rule
    // public PowerMockRule rule = new PowerMockRule();

//    @Test
//    @PrepareForTest({Banana.class})
//    public void testStaticMethod() {
//        //<-- mock静态类
//        PowerMockito.mockStatic(Banana.class);
//        Mockito.when(Banana.getColor()).thenReturn("绿色");
//        Assert.assertEquals("绿色", Banana.getColor());
//    }
//
//    @Test
//    @PrepareForTest({Banana.class})
//    public void testChangeColor() {
//        Whitebox.setInternalState(Banana.class, "COLOR", "红色的");
//        Assert.assertEquals("红色的", Banana.getColor());
//    }
//
//    @Test
//    @PrepareForTest({Banana.class})
//    public void testPrivateMethod() throws Exception {
//        Banana mBanana = PowerMockito.mock(Banana.class);
//        PowerMockito.when(mBanana.getBananaInfo()).thenCallRealMethod();
//        PowerMockito.when(mBanana, "flavor").thenReturn("苦苦的");
//        Assert.assertEquals("苦苦的黄色的", mBanana.getBananaInfo());
//        //验证flavor是否调用了一次
//        PowerMockito.verifyPrivate(mBanana).invoke("flavor");
//    }
//
//    @Test
//    @PrepareForTest({Banana.class})
//    public void skipPrivateMethod() {
//        Banana mBanana = new Banana();
//        //跳过flavor方法
//        PowerMockito.suppress(PowerMockito.method(Banana.class, "flavor"));
//        Assert.assertEquals("null黄色的", mBanana.getBananaInfo());
//    }
//
//    @Test
//    @PrepareForTest({Banana.class})
//    public void testChangeParentPrivate() throws Exception {
//        Banana mBanana = new Banana();
//        MemberModifier.field(Banana.class, "fruit").set(mBanana, "蔬菜");
//        Assert.assertEquals("蔬菜", mBanana.getFruit());
//    }
//
//    @Test
//    @PrepareForTest({Banana.class})
//    public void testFinalMethod() throws Exception {
//        Banana mBanana = PowerMockito.mock(Banana.class);
//        PowerMockito.when(mBanana.isLike()).thenReturn(false);
//        Assert.assertFalse(mBanana.isLike());
//    }
//
//    @Test
//    @PrepareForTest({Banana.class})
//    public void testNewClass() throws Exception {
//        Banana mBanana = PowerMockito.mock(Banana.class);
//        PowerMockito.when(mBanana.getBananaInfo()).thenReturn("大香蕉");
//        //如果new新对象，则返回这个上面设置的这个对象
//        PowerMockito.whenNew(Banana.class).withNoArguments().thenReturn(mBanana);
//        //new新的对象
//        Banana newBanana = new Banana();
//        Assert.assertEquals("大香蕉", newBanana.getBananaInfo());
//    }
//
//    static abstract class Fruit {
//        private String fruit = "水果";
//        public String getFruit() { return fruit; }
//    }
//
//    static class Banana extends Fruit{
//        private static String COLOR = "黄色的";
//
//        public Banana() {}
//        public static String getColor() { return COLOR; }
//        public String getBananaInfo() { return flavor() + getColor(); }
//        private String flavor() { return "甜甜的"; }
//        public final boolean isLike() { return true; }
//    }
}
