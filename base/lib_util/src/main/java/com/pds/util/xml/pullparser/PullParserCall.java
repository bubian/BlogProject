package com.pds.util.xml.pullparser;

import android.util.Log;

import com.pds.util.xml.xmlcommon.IXmlParser;
import com.pds.util.xml.xmlcommon.ParserResultDataList;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;
import java.io.IOException;
import java.io.InputStream;


/**
 * Created by renfei on 2016/3/6.
 */
public class PullParserCall implements IXmlParser {
    private final String TAG = PullParserCall.class.getSimpleName();
    private ParserResultDataList resultDataList;
    @Override
    public ParserResultDataList parser(InputStream in) {
        try {
            XmlPullParserFactory xpf = XmlPullParserFactory.newInstance();
            XmlPullParser xpp = xpf.newPullParser();
            xpp.setInput(in ,"utf-8");
            int eventType = xpp.getEventType();
            while (eventType != XmlPullParser.END_DOCUMENT){
                switch(eventType){
                    case XmlPullParser.START_DOCUMENT:
                        resultDataList = new ParserResultDataList();
                        break;
                    case XmlPullParser.START_TAG:
                        String name = xpp.getName();
                        Log.i(TAG , name);
                        if("manifest".equals(name)){
                            Log.i(TAG , "--------------------->开始根目录");
                        }else if("uses-sdk".equals(name)){
                            resultDataList.setUses_sdk(xpp.nextText());
                        }else if ("action".equals(name)){
                            resultDataList.setAction(xpp.nextText());
                        }else if ("category".equals(name)){
                            resultDataList.setCategory(xpp.nextText());
                        }else {
                            break;
                        }
                        break;
                    case XmlPullParser.END_TAG:
                        Log.i(TAG , "--------------------->结束根目录");
                        Log.i(TAG , xpp.getName());
                        break;
                    default:
                        break;
                }
                eventType = xpp.next();
            }
        } catch (XmlPullParserException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return resultDataList;
    }
}
