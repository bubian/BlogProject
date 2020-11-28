package com.pds.debug;

import com.pds.debug.http.XdHttpServer;

public interface IXdDebugCommandHandler {
	XdHttpServer.XulHttpServerResponse execCommand(String url, XdHttpServer.XulHttpServerHandler serverHandler, XdHttpServer.XulHttpServerRequest request);
}
