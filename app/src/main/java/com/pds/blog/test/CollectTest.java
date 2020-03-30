package com.pds.blog.test;

import android.os.Build;
import androidx.annotation.RequiresApi;
import java.util.Arrays;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * @author: pengdaosong
 * CreateTime:  2020-03-29 17:26
 * Email：pengdaosong@medlinker.com
 * Description:
 */
public class CollectTest {

    @RequiresApi(api = Build.VERSION_CODES.N)
    public static void main(String[] arg){

        Integer[] o = {2, 1, 0, 3};
        Arrays.parallelPrefix(o, (integer, integer2) -> integer + integer2);

        for (Integer i:o) {
            System.out.println(i);
        }
    }
}
