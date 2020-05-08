package com.pds.util.xml.saxparser;

import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import java.io.IOException;

public class MyEntireResolver implements EntityResolver {
     /*
     * 允许应用程序解析外部实体。
     * 解析器将在打开任何外部实体（顶级文档实体除外）前调用此方法
     * 参数意义如下：
     *     publicId ： 被引用的外部实体的公共标识符，如果未提供，则为 null。
     *     systemId ： 被引用的外部实体的系统标识符。
     * 返回：
     *     一个描述新输入源的 InputSource 对象，或者返回 null，
     *     以请求解析器打开到系统标识符的常规 URI 连接。
     */
    @Override
    public InputSource resolveEntity(String publicId, String systemId) throws SAXException, IOException {
        return null;
    }
}
