package com.pds.debug.http;

import android.text.TextUtils;

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;

public class XdHttpRequest {
	public String method = "get";
	public String schema = "http";
	public String host;
	public int port = -1;
	public String path;
	public LinkedHashMap<String, String> queries;
	public String fragment;

	// post only members
	public ArrayList<String> header;
	public LinkedHashMap<String, String> form;
	public byte[] body;

	@Override
	public String toString() {
		final StringBuilder urlBuilder = new StringBuilder();

		XdHttpRequest request = this;
		urlBuilder.append(request.schema)
			.append("://");

		request.buildHostString(urlBuilder);

		if (!TextUtils.isEmpty(request.path)) {
			urlBuilder.append(request.path);
		}

		if (request.queries != null && !request.queries.isEmpty()) {
			urlBuilder.append("?");
			buildQueryString(urlBuilder, request.queries);
		}

		if (!TextUtils.isEmpty(request.fragment)) {
			urlBuilder.append("#")
				.append(request.fragment);
		}


		String url = urlBuilder.toString();
		return url;
	}

	public StringBuilder buildHostString(StringBuilder urlBuilder) {
		urlBuilder.append(host);
		if (port > 0) {
			urlBuilder.append(":").append(port);
		}
		return urlBuilder;
	}

	public String getHostString() {
		return buildHostString(new StringBuilder()).toString();
	}

	public static StringBuilder buildQueryString(StringBuilder urlBuilder, LinkedHashMap<String, String> params) {
		int idx = 0;
		if (params != null) {
			for (Map.Entry<String, String> entry : params.entrySet()) {
				if (idx > 0) {
					urlBuilder.append("&");
				}
				++idx;
				urlBuilder.append(URLEncoder.encode(entry.getKey()));
				if (entry.getValue() != null) {
					urlBuilder.append("=")
						.append(URLEncoder.encode(entry.getValue()));
				}
			}
		}
		return urlBuilder;
	}

	public String getQueryString() {
		return buildQueryString(new StringBuilder(), queries).toString();
	}

	public String getFormParams() {
		return buildQueryString(new StringBuilder(), form).toString();
	}

	public XdHttpRequest addQueryString(String key, String val) {
		if (queries == null) {
			queries = new LinkedHashMap<String, String>();
		}
		queries.put(key, val);
		return this;
	}

	public String getQueryString(String key) {
		if (queries == null) {
			return null;
		}
		return queries.get(key);
	}

	public String removeQueryString(String key) {
		if (queries == null) {
			return null;
		}
		return queries.remove(key);
	}

	public boolean hasQueryKey(String key) {
		if (queries == null) {
			return false;
		}
		return queries.containsKey(key);
	}

	public XdHttpRequest addHeaderParam(String key, String val) {
		if (header == null) {
			header = new ArrayList<String>();
		}
		header.add(key);
		header.add(val);
		return this;
	}

	public ArrayList<String> getHeaderParam() {
		if (header == null || header.isEmpty()) {
			return null;
		}
		return  header;
	}

	public XdHttpRequest addFormParam(String key, String val) {
		if (form == null) {
			form = new LinkedHashMap<String, String>();
		}
		form.put(key, val);
		return this;
	}

	public String getFormParam(String key) {
		if (form == null) {
			return null;
		}
		return form.get(key);
	}

	public String removeFormParam(String key) {
		if (form == null) {
			return null;
		}
		return form.remove(key);
	}

	public boolean hasFormKey(String key) {
		if (form == null) {
			return false;
		}
		return form.containsKey(key);
	}

	public XdHttpRequest makeClone() {
		XdHttpRequest newRequest = makeCloneNoQueryString();
		if (queries != null) {
			newRequest.queries = (LinkedHashMap<String, String>) queries.clone();
		}
		return newRequest;
	}

	public XdHttpRequest makeCloneNoQueryString() {
		XdHttpRequest newRequest = new XdHttpRequest();
		newRequest.method = method;
		newRequest.schema = schema;
		newRequest.host = host;
		newRequest.port = port;
		newRequest.path = path;
		newRequest.fragment = fragment;
		if (form!= null) {
			newRequest.form = (LinkedHashMap<String, String>) form.clone();
		}
		newRequest.body = body;
		return newRequest;
	}
}
