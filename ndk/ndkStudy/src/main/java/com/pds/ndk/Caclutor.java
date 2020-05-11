package com.pds.ndk;

/**
 * Created by Administrator on 2017/11/6.
 */

public class Caclutor {
  @Replace(clazz = "kt.edu.pds.kt.mrp.ndk.Caclutor",method = "caculator")
  public int caculator()
  {
    //10/0
    int i=0;
    int j=10;
//模拟异常产生
    return j/i;
  }

  @Replace(clazz = "kt.edu.pds.kt.mrp.ndk.Caclutor",method = "caculator")
  public int caculator(int i)
  {
    //10/0
    int j=10;
//模拟异常产生
    return j/i;
  }
//    public int caculator1()
//    {
//     10/0    唯一需要
//        int i=1;
//        int j=10;
////模拟异常产生
//        return j/i;
//    }
}
