package com.pds.debug.http;

public abstract class XdHttpFilter {

	public abstract String name();

	public int doRequest(XdHttpStack.XulHttpCtx ctx, XdHttpRequest request) {
		return 0;
	}

	public int handleResponse(XdHttpStack.XulHttpCtx ctx, XdHttpResponse response) {
		return 0;
	}

}
