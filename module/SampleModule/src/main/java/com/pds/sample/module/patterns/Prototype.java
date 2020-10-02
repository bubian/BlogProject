package com.pds.sample.module.patterns;

import java.io.IOException;
import java.util.ArrayList;

/**
 * @author: pengdaosong
 * CreateTime:  2019-06-24 14:59
 * Email：pengdaosong@medlinker.com
 * Description:
 */
public class Prototype implements Cloneable{

    private String name;
    private PrototypeA prototypeA;
    private ArrayList<Integer> integers;
    @Override
    protected Object clone() throws CloneNotSupportedException {
//        Prototype prototype = (Prototype) super.clone();
//        prototype.name = this.name;
//        prototype.integers = this.integers;
//        return prototype;

        return super.clone();
    }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public ArrayList<Integer> getIntegers() { return integers; }
    public void setIntegers(ArrayList<Integer> integers) { this.integers = integers; }


    static class PrototypeA{
        public PrototypeA(String name){ this.name = name;}
        public String name;
    }

    public static void main(String[] args) throws IOException {

        Prototype src = new Prototype();
        src.prototypeA = new PrototypeA("srcOne");
        src.name = "srcOne";
        //拷贝
        try {
            Prototype dest = (Prototype) src.clone();
            dest.prototypeA.name = "copyOne";

            System.out.println("src:name = " + src.prototypeA.name);
            System.out.println("dest:name = " + dest.prototypeA.name);

        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }

//        PrototypeA[] src = new PrototypeA[2];
//        src[0] = new PrototypeA("srcOne");
//        src[1] = new PrototypeA("srcTwo");
//        //拷贝
//        PrototypeA[] des = new PrototypeA[2];
//        System.arraycopy(src,0,des,0,src.length);
//        des[1].name = "copyTwo";
//        //打印
//        System.out.println("src:des[0] = " + src[0].name+  "  des[1] = "+src[1].name);
//        System.out.println("des:des[0] = " + des[0].name+  "  des[1] = "+des[1].name);

    }
}
