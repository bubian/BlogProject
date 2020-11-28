package com.pds.debug;

import com.pds.debug.http.XdHttpServer;

import org.xmlpull.v1.XmlSerializer;

public interface IXdDebuggableObject {
	String name();

	boolean isValid();

	boolean runInMainThread();

	boolean buildBriefInfo(XdHttpServer.XulHttpServerRequest request, XmlSerializer infoWriter);

	boolean buildDetailInfo(XdHttpServer.XulHttpServerRequest request, XmlSerializer infoWriter);

	XdHttpServer.XulHttpServerResponse execCommand(String command, XdHttpServer.XulHttpServerRequest request, XdHttpServer.XulHttpServerHandler serverHandler);
}
