package com.pds.util.xml.xmlcommon;

import java.io.InputStream;

public interface IXmlParser {
    ParserResultDataList parser(InputStream in);
}
