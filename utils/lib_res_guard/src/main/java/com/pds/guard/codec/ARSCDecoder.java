/**
 * Baffle Project
 * The MIT License (MIT) Copyright (Baffle) 2015 guye
 * <p>
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software
 * and associated documentation files (the "Software"), to deal in the Software
 * without restriction, including without limitation the rights to use, copy, modify,
 * merge, publish, distribute, sublicense, and/or sell copies of the Software, and to
 * permit persons to whom the Software is furnished to do so, subject to the following conditions:
 * <p>
 * The above copyright notice and this permission notice shall be included in all copies
 * or substantial portions of the Software.
 * <p>
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED,
 * INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR
 * PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE
 * FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE,
 * ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 *
 * @author guye
 **/
package com.pds.guard.codec;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import com.pds.guard.arsc.ARSC;
import com.pds.guard.arsc.Header;
import com.pds.guard.arsc.PackageHeader;
import com.pds.guard.arsc.StringPoolRef;

/**
 * @author xiang
 * @date 2018/2/3
 */
public class ARSCDecoder {

    public static ARSC decode(File file) throws IOException {
        FileInputStream is = new FileInputStream(file);
        ByteBuffer buffer = ByteBuffer.allocate((int) file.length()).order(ByteOrder.LITTLE_ENDIAN);
        int len = 0;
        byte[] bytes = new byte[1024];
        while ((len = is.read(bytes)) != -1) {
            buffer.put(bytes, 0, len);
        }
        is.close();
        buffer.flip();
        ARSC arsc = read(file, buffer);
        return arsc;
    }

    private static ARSC read(File file, ByteBuffer buffer)
            throws IOException {
        ARSC arsc = new ARSC();
        arsc.mFile = file;
        //解析头
        arsc.mHeader = Header.read(buffer);
        System.out.println("resources.arsc 文件大小:" + arsc.mHeader.chunkSize);
        int packageNum = buffer.getInt();
        if (packageNum != 1) {
            throw new RuntimeException("package number is:" + packageNum);
        }
        //全局字符串资源池 包含了在string.xml中定义的值 与 res/xx/xx的路径地址
        arsc.mTableStrings = StringPoolRef.read(buffer);
        System.out.println("全局字符串资源池:");
        for (int i = 0; i < arsc.mTableStrings.getStrCount(); i++) {
            System.out.println("    " + arsc.mTableStrings.getString(i));
        }

        //包数据
        arsc.mPkgHeader = PackageHeader.read(buffer);
        //包数据中的类型字符串资源池
        arsc.mTypeNames = StringPoolRef.read(buffer);
        System.out.println("类型字符串资源池:");
        for (int i = 0; i < arsc.mTypeNames.getStrCount(); i++) {
            System.out.println("    " + arsc.mTypeNames.getString(i));
        }
        //资源名称字符串池
        arsc.mSpecNames = StringPoolRef.read(buffer);
        System.out.println("资源名称字符串资源池:");
        for (int i = 0; i < arsc.mSpecNames.getStrCount(); i++) {
            System.out.println("    " + arsc.mSpecNames.getString(i));
        }
        //type spec开始的地方
        arsc.mSpecIndex = buffer.position();
        arsc.remaing = buffer;
        return arsc;
    }


}
