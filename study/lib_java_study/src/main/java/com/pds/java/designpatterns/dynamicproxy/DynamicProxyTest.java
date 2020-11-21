package com.pds.java.designpatterns.dynamicproxy;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

/**
 * @author: pengdaosong
 * @CreateTime: 2020/11/21 10:12 PM
 * @Email: pengdaosong@medlinker.com
 * @Description:
 */
public class DynamicProxyTest {
    public static void main(String[] args) {
        InvocationHandler handler = new InvocationHandler() {
            @Override
            public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                System.out.println(method);
                if (method.getName().equals("morning")) {
                    System.out.println("Good morning, " + args[0]);
                }
                return null;
            }
        };
        IProxy proxy = (IProxy) Proxy.newProxyInstance(
                IProxy.class.getClassLoader(), // 传入ClassLoader
                new Class[] { IProxy.class }, // 传入要实现的接口
                handler); // 传入处理调用方法的InvocationHandler
        proxy.test("Bob");
    }
}

/**
 * 动态代理实际上是JDK在运行期动态创建class字节码并加载的过程，它并没有什么黑魔法，把上面的动态代理改写为静态实现类大概长这样：
 *
 * 其实就是JDK帮我们自动编写了一个上述类（不需要源码，可以直接生成字节码），并不存在可以直接实例化接口的黑魔法。
 */
class HelloDynamicProxy implements IProxy {
    InvocationHandler handler;
    public HelloDynamicProxy(InvocationHandler handler) {
        this.handler = handler;
    }
    public void test(String name) {
        // 生成的可能没有try{}catch(){},这里为了不报错加上的
        try {
            handler.invoke(
                    this,
                    IProxy.class.getMethod("morning", String.class),
                    new Object[] { name });
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }
    }
}
