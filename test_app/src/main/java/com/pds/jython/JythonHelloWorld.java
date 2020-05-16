package com.pds.jython;

import org.python.util.PythonInterpreter;

/**
 * @author: pengdaosong
 * CreateTime:  2020-05-16 11:13
 * Email：pengdaosong@medlinker.com
 * Description:
 */
public class JythonHelloWorld {

    public static void main(String[] args){
        try (PythonInterpreter pythonInterpreter = new PythonInterpreter()){
            pythonInterpreter.exec("print('Hello Python World!')");
            pythonInterpreter.execfile("/Users/pengdaosong/pds/BlogProject/test_app/src/main/python/java.py");

        }
    }
}
