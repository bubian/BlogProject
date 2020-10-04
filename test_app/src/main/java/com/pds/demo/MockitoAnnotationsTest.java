package com.pds.demo;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatcher;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoJUnitRunner;
import org.mockito.junit.MockitoRule;

import static org.junit.Assert.assertNotNull;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.when;

/**
 * @author: pengdaosong
 * CreateTime:  2020-05-15 15:11
 * Email：pengdaosong@medlinker.com
 * Description:
 */
//<--使用MockitoJUnitRunner
@RunWith(MockitoJUnitRunner.class)
public class MockitoAnnotationsTest {
    //<--使用@Mock注解
    @Mock
//    HybridParam mHbParam;
    // Spy的创建与mock一样
    @Spy
    Person mPerson;
    @Mock
    Person mPerson1;

    //<--使用@Rule
    @Rule
    public MockitoRule mockitoRule = MockitoJUnit.rule();

    @Before
    public void setup(){
        //<--初始化
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testIsNotNull(){
//        assertNotNull(mHbParam);
    }

    @Test
    public void testPersonReturn(){
//        when(mHbParam.getId()).thenReturn(1111);
//        when(mHbParam.getId()).thenThrow(new NullPointerException("icon is null"));
//
//        System.out.println("------->"+ mHbParam.getId());
//        System.out.println("------->"+ mHbParam.getId());
    }

    @Test
    public void testPersonArgThat(){

        //自定义输入字符长度为偶数时，输出面条。
        when(mPerson.eat(argThat(new ArgumentMatcher<String>() {
            @Override
            public boolean matches(String argument) {
                return argument.length() % 2 == 0;
            }
        }))).thenReturn("面条");
        //输出面条
        System.out.println("------>"+mPerson.eat("1234"));

    }

    @Test
    public void testPersonInOrder(){

        mPerson.setName("坚果");
        mPerson.setSex(1);

        mPerson1.setName("樱桃");
        mPerson1.setSex(2);

        InOrder inOrder = Mockito.inOrder(mPerson,mPerson1);

        // 执行顺序正确
        inOrder.verify(mPerson).setName("坚果");
        inOrder.verify(mPerson).setSex(1);
        // 执行顺序错误
        inOrder.verify(mPerson1).setSex(2);
        inOrder.verify(mPerson1).setName("樱桃");
    }

    @InjectMocks
    Home mHome;
    @Test
    public void testHomeInjectMocks(){
        when(mPerson.getName()).thenReturn("坚果");
        System.out.println(mHome.getMaster());
    }

    static class Person{
        private String name;
        private int sex;
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        public int getSex() { return sex;}
        public void setSex(int sex) { this.sex = sex; }
        String eat(String food){ return food; }
    }

    static class Home{
        private Person mPerson;

        public Home(Person person){
            mPerson = person;
        }

        public String getMaster(){
            return mPerson.getName();
        }
    }
}
