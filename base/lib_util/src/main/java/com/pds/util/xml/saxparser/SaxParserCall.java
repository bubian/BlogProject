package com.pds.util.xml.saxparser;


import com.pds.util.xml.xmlcommon.IXmlParser;
import com.pds.util.xml.xmlcommon.ParserResultDataList;

import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;

import java.io.InputStream;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;


public class SaxParserCall implements IXmlParser {

    private XMLReader xmlReader;
    private SAXParserFactory spf;
    private GetDataSaxParserHander defaultHandler;
    private InputSource inputSource;
    @Override
    public ParserResultDataList parser(InputStream in){
        inputSource = new InputSource(in);
        defaultHandler = new GetDataSaxParserHander();
        MyDTDHander myDTDHander = new MyDTDHander();
        MyEntireResolver myEntireResolver = new MyEntireResolver();
        MyErrorHander myErrorHander = new MyErrorHander();
        SAXParser saxParser;
        try {
            saxParser = spf.newInstance().newSAXParser();
            xmlReader = saxParser.getXMLReader();
            xmlReader.setContentHandler(defaultHandler);
            xmlReader.setErrorHandler(defaultHandler);
            xmlReader.setDTDHandler(myDTDHander);
            xmlReader.setEntityResolver(myEntireResolver);
            xmlReader.setErrorHandler(myErrorHander);
         /**
         * 设置解析器的相关特性
         *     http://xml.org/sax/features/validation = true 表示开启验证特性
         *     http://xml.org/sax/features/namespaces = true 表示开启命名空间特性
         */
            //setFeature("http://xml.org/sax/features/validation", true);
            xmlReader.setFeature("http://xml.org/sax/features/namespaces",true);
            xmlReader.parse(inputSource);
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return defaultHandler.getSaxParserDataList();
    }

}
