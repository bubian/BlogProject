package com.pds.util.xml.saxparser;

import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

public class MyErrorHander implements ErrorHandler {
    /*
     * 接收可恢复的错误的通知
     */
    @Override
    public void error(SAXParseException e) throws SAXException {
        System.err.println("Error ("+e.getLineNumber()+","
                +e.getColumnNumber()+") : "+e.getMessage());
    }

    /*
     * 接收不可恢复的错误的通知。
     */
    @Override
    public void fatalError(SAXParseException e) throws SAXException {
        System.err.println("FatalError ("+e.getLineNumber()+","
                +e.getColumnNumber()+") : "+e.getMessage());
    }

    /*
     * 接收不可恢复的错误的通知。
     */
    @Override
    public void warning(SAXParseException e) throws SAXException {
        System.err.println("Warning ("+e.getLineNumber()+","
                +e.getColumnNumber()+") : "+e.getMessage());
    }
}
