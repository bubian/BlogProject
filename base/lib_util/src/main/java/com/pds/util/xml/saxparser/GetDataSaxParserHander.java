package com.pds.util.xml.saxparser;

import android.util.Log;

import com.pds.util.xml.xmlcommon.ParserResultDataList;

import org.xml.sax.Attributes;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

public class GetDataSaxParserHander extends DefaultHandler {

    private final String TAG = GetDataSaxParserHander.class.getSimpleName();
    private ParserResultDataList prdl ;
    private StringBuilder valueBuffer;
    private String nodeElementFlag;
    int frontBlankCount = 0;
    @Override
    public void startDocument() throws SAXException {
        Log.i(TAG , "--------------------------->startDocument");
        prdl = new ParserResultDataList();
        valueBuffer = new StringBuilder();
        super.startDocument();
    }

    @Override
    public void endDocument() throws SAXException {
        Log.i(TAG , "--------------------------->endDocument");
        super.endDocument();
    }

    @Override
    public void endElement(String uri, String localName, String qName) throws SAXException {
        Log.i(TAG , uri+localName + qName+"--------------------------->endElement");
        String connent = valueBuffer.toString();
        if("uses-sdk".equals(qName)) {
         prdl.setUses_sdk(connent);
        }else if("action".equals(qName)){
            prdl.setAction(connent);
        }else if("category".equals(qName)){
            prdl.setCategory(connent);
        }
        if (valueBuffer.length() > 0){
            valueBuffer.delete(0 , valueBuffer.length());
        }
        nodeElementFlag = null;
        super.endElement(uri, localName, qName);
    }
    /*
     * uri 是名称空间
     * localName 是包含名称空间的标签，如果没有名称空间，则为空
     * qName 是不包含名称空间的标签
     * attributes
     * */
    @Override
    public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
        super.startElement(uri, localName, qName, attributes);
        Log.i(TAG, uri+localName + qName+"--------------------------->startElement");
        //清理的作用是去掉根级标签产生的空格
        if (valueBuffer.length() > 0){
            valueBuffer.delete(0 , valueBuffer.length());
        }
        nodeElementFlag = qName;
    }
    /*
     * 在startElement被调用后调用，用于接收字节数据的通知，注意：Sax解析可能应为数据过长把一个完整的数据拆成两半的情况
     * ，所以用缓存来消除这种情况，不过缓存的时候要加上对元素的判断，不然会把元素与元素之间的空格当做字节数据传输过来那部分也缓存起来
     * ，导致显示的时候元素的前面会出现很多空格的现象
     */
    @Override
    public void characters(char[] ch, int start, int length) throws SAXException {
        super.characters(ch, start, length);
        Log.i(TAG, "--------------------------->characters");
        Log.i(TAG, "--------------------------->"+ new String(ch , start , length)+"!");
        if(null != nodeElementFlag){
            String str = new String(ch , start , length);
            if(null != str && '\n' != ch[0]){
                valueBuffer.append(str);
            }
        }

//        StringBuffer buffer = new StringBuffer();
//        for(int i = begin ; i < begin+length ; i++){
//            switch(ch[i]){
//                case '\\':buffer.append("\\\\");break;
//                case '\r':buffer.append("\\r");break;
//                case '\n':buffer.append("\\n");break;
//                case '\t':buffer.append("\\t");break;
//                case '\"':buffer.append("\\\"");break;
//                default : buffer.append(ch[i]);
//            }
    }

    @Override
    public void ignorableWhitespace(char[] ch, int start, int length) throws SAXException {
        super.ignorableWhitespace(ch, start, length);
        Log.i(TAG, new String(ch , start ,length)+"--------------------------->ignorableWhitespace");
    }

     /*
     * 开始前缀 URI 名称空间范围映射。
     * 此事件的信息对于常规的命名空间处理并非必需：
     * 当 http://xml.org/sax/features/namespaces 功能为 true（默认）时，
     * SAX XML 读取器将自动替换元素和属性名称的前缀。
     * 参数意义如下：
     * @param prefix ：前缀
     * @param uri ：命名空间
     */
    @Override
    public void startPrefixMapping(String prefix, String uri)
            throws SAXException {
        System.out.println(this.toBlankString(this.frontBlankCount++)+
                ">>> start prefix_mapping : xmlns:"+prefix+" = "
                +"\""+uri+"\"");

    }

    //命名空间范围映射结束时调用
    @Override
    public void endPrefixMapping(String prefix) throws SAXException {
        super.endPrefixMapping(prefix);
        Log.i(TAG, prefix + "--------------------------->endPrefixMapping");
    }

    /*
     * 接收处理指令的通知。
     * 参数意义如下：
     * @param target: 处理指令目标
     * @param data: 处理指令数据，如果未提供，则为 null。
     */

    @Override
    public void processingInstruction(String target, String data) throws SAXException {
        super.processingInstruction(target, data);
        Log.i(TAG, target + data + "--------------------------->processingInstruction");
    }

     /*
     * 接收用来查找 SAX 文档事件起源的对象。
     * 参数意义如下：
     * locator : 可以返回任何 SAX 文档事件位置的对象
     */
    @Override
    public void setDocumentLocator(Locator locator) {
        System.out.println(this.toBlankString(this.frontBlankCount)+
                ">>> set document_locator : (lineNumber = "+locator.getLineNumber()
                +",columnNumber = "+locator.getColumnNumber()
                +",systemId = "+locator.getSystemId()
                +",publicId = "+locator.getPublicId()+")");

    }



    /*
     * 接收跳过的实体的通知。
     * 参数意义如下：
     * name : 所跳过的实体的名称。如果它是参数实体，则名称将以 '%' 开头，如果它是外部 DTD 子集，则将是字符串 "[dtd]"
     */
    @Override
    public void skippedEntity(String name) throws SAXException {
        super.skippedEntity(name);
    }

    private String toBlankString(int count){
        StringBuffer buffer = new StringBuffer();
        for(int i = 0;i<count;i++)
            buffer.append("    ");
        return buffer.toString();
    }
    public  ParserResultDataList getSaxParserDataList(){
        return prdl;
    }
}
