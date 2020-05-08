package com.pds.util.xml.saxparser;

import org.xml.sax.DTDHandler;
import org.xml.sax.SAXException;

public class MyDTDHander implements DTDHandler {
     /*
     * 接收注释声明事件的通知。
     * 参数意义如下：
     *     name - 注释名称。
     *     publicId - 注释的公共标识符，如果未提供，则为 null。
     *     systemId - 注释的系统标识符，如果未提供，则为 null。
     */
    @Override
    public void notationDecl(String name, String publicId, String systemId) throws SAXException {
        System.out.println(">>> notation declare : (name = "+name
                +",systemId = "+publicId
                +",publicId = "+systemId+")");
    }
    /*
     * 接收未解析的实体声明事件的通知。
     * 参数意义如下：
     *     name - 未解析的实体的名称。
     *     publicId - 实体的公共标识符，如果未提供，则为 null。
     *     systemId - 实体的系统标识符。
     *     notationName - 相关注释的名称。
     */
    @Override
    public void unparsedEntityDecl(String name, String publicId, String systemId, String notationName) throws SAXException {
        System.out.println(">>> unparsed entity declare : (name = "+name
                +",systemId = "+publicId
                +",publicId = "+systemId
                +",notationName = "+notationName+")");
    }
}
