package com.pds.blog.patterns;

import java.io.*;

/**
 * @author: pengdaosong
 * CreateTime:  2019-06-22 10:20
 * Email：pengdaosong@medlinker.com
 * Description:
 */
public class Singleton implements Serializable {

    private static final Singleton INSTANCE = new Singleton();
    private Singleton() {}
    public static Singleton instance() { return INSTANCE; }
    private Object readResolve() {
        System.out.println("readResolve被调用了");
        return INSTANCE;
    }

    private Object writeReplace() {
        System.out.println("writeReplace被调用了");
        return INSTANCE;
    }

    public static void main(String[] args) throws IOException {
        testSingleton();
        Singleton4.INSTANCE.test();
    }

    private static void testSingleton() {

        Singleton singleton = Singleton.instance();
        try {
            FileOutputStream fos = new FileOutputStream(new File("singleTonTest.txt"));
            ObjectOutputStream oos = new ObjectOutputStream(fos);
            oos.writeObject(singleton);
            fos.close();
            oos.close();
            System.out.println("old:" + singleton.hashCode());

            FileInputStream fis = new FileInputStream(new File("singleTonTest.txt"));
            ObjectInputStream ois = new ObjectInputStream(fis);
            Singleton newSingleton = (Singleton) ois.readObject();
            fis.close();
            ois.close();
            System.out.println("new:" + newSingleton.hashCode());

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
}



class Singleton1 {
    private static class LazyHolder {
        private static final Singleton1 INSTANCE = new Singleton1();
    }
    private Singleton1 (){}
    public static final Singleton1 getInstance() {
        return LazyHolder.INSTANCE;
    }
}

class Singleton2{
    private static Singleton2 singleton2;
    private Singleton2() {}
    public static Singleton2 instance(){
        if (null == singleton2){
            singleton2 = new Singleton2();
        }
        return singleton2;
    }
}

class Singleton3{
    private volatile static Singleton3 singleton3;
    private Singleton3() {}
    public static Singleton3 instance(){
        if (null == singleton3){
            synchronized (Singleton3.class){
                if (null == singleton3){
                    singleton3 = new Singleton3();
                }
            }
        }
        return singleton3;
    }
}

enum Singleton4{
    /**
     * 单例
     */
    INSTANCE;
    public void test(){}
}
