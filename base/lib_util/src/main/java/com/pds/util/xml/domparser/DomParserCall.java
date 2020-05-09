package com.pds.util.xml.domparser;

import android.util.Log;

import com.pds.util.xml.xmlcommon.IXmlParser;
import com.pds.util.xml.xmlcommon.ParserResultDataList;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;
import org.xml.sax.SAXException;

import java.io.IOException;
import java.io.InputStream;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

public class DomParserCall implements IXmlParser {
    private ParserResultDataList resultDataList;
    private final String TAG = DomParserCall.class.getSimpleName();
    @Override
    public ParserResultDataList parser(InputStream in) {
        Log.i(TAG , "start dom parser");
        try {
            resultDataList = new ParserResultDataList();
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder db = dbf.newDocumentBuilder();
            Document document = db.parse(in);
            Element root  = document.getDocumentElement();
            NodeList nl = document.getElementsByTagName("manifest");
            //NodeList nl = root.getChildNodes();
            System.out.println("----------------------->" + nl.getLength());
            for(int i = 0 ; i < nl.getLength() ; i++){
                Element rootElement = (Element) nl.item(i);
                String content = rootElement.getAttribute("xmlns");
                System.out.println("----------------------->" + content);
                NodeList childNodes = rootElement.getChildNodes();
                for (int j = 0 ; j < childNodes.getLength() ; j++){
                    if(childNodes.item(j).getNodeType()== Node.ELEMENT_NODE){
                        String name = ((Element)childNodes.item(j)).getTagName();
                        String value = ((Text)childNodes.item(j).getFirstChild()).getData().trim();
                        System.out.println("----------------------->" +name + value);
                        if("uses-sdk".equals(name)){
                            resultDataList.setUses_sdk(value);
                        }else if ("action".equals(name)){
                            resultDataList.setAction(value);
                        }else if ("category".equals(name)){
                            resultDataList.setCategory(value);
                        }else {
                            continue;
                        }
                    }
                }
            }

        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        } catch (SAXException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return resultDataList;
    }
}
