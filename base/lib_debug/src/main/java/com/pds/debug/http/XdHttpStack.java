package com.pds.debug.http;

import android.text.TextUtils;

import com.pds.debug.utils.XdLog;
import com.pds.debug.utils.XdUtils;

import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.concurrent.ArrayBlockingQueue;

public class XdHttpStack {

    private static final String TAG = XdHttpStack.class.getSimpleName();

    public static class XulHttpTask {

        private XdHttpRequest _request = new XdHttpRequest();
        private XulHttpResponseHandler _handler;
        private volatile boolean _isCancelled = false;

        public XulHttpTask addQuery(String key, String val) {
            _request.addQueryString(key, val);
            return this;
        }

        public XulHttpTask addQuery(String key, int val) {
            _request.addQueryString(key, String.valueOf(val));
            return this;
        }

        public XulHttpTask addHeader(String key, String val) {
            _request.addHeaderParam(key, val);
            return this;
        }

        public XulHttpTask addForm(String key, String val) {
            _request.addFormParam(key, val);
            return this;
        }

        public XulHttpTask addForm(String key, int val) {
            _request.addFormParam(key, String.valueOf(val));
            return this;
        }

        public XulHttpTask setBody(byte[] data) {
            _request.body = data;
            return this;
        }

        public void cancel() {
            _isCancelled = true;
        }

        public XulHttpTask get(XulHttpResponseHandler handler) {
            this._handler = handler;
            scheduleHttpTask(this);
            return this;
        }

        public XulHttpTask post(XulHttpResponseHandler handler) {
            this._handler = handler;
            _request.method = "post";
            scheduleHttpTask(this);
            return this;
        }

        public XulHttpTask setHost(String host) {
            this._request.host = host;
            return this;
        }

        public XulHttpTask setPort(int port) {
            this._request.port = port;
            return this;
        }

        public XulHttpTask setPath(String path) {
            this._request.path = path;
            return this;
        }

        public XulHttpTask setSchema(String schema) {
            this._request.schema = schema;
            return this;
        }
    }

    public interface XulHttpResponseHandler {
        int onResult(XulHttpTask task, XdHttpRequest request, XdHttpResponse response);
    }

    private static void scheduleHttpTask(XulHttpTask task) {
        final ArrayBlockingQueue<XulHttpTask> requestQueue = new ArrayBlockingQueue<>(4);
        if (requestQueue == null) {
            return;
        }
        synchronized (requestQueue) {
            requestQueue.add(task);
        }
        synchronized (_workers) {
            _workers.notify();
        }
    }

    public static class XulHttpCtx {
        private XulHttpTask _task;
        private XdHttpFilter _filter;
        private XulHttpCtx _prevCtx;
        private XulHttpCtx _nextCtx;
        private XdHttpRequest _request;
        private XdHttpResponse _response;
        private long _reqTime = 0;
        private long _respTime = 0;

        public XulHttpCtx(XulHttpTask task) {
            _task = task;
            _request = task._request;
        }

        public XulHttpCtx(XulHttpTask task, XdHttpRequest request) {
            _task = task;
            _request = request;
        }

        public XulHttpTask getTask() {
            return _task;
        }

        public XdHttpRequest getInitialRequest() {
            return _task._request;
        }

        public XdHttpRequest getRequest() {
            return this._request;
        }

        public void replaceRequest(XdHttpRequest req) {
            this._request = req;
        }

        public void postResponse(XdHttpResponse resp) {
            this._response = resp;
            scheduleHttpResponse(this);
        }

        private static void scheduleHttpResponse(XulHttpCtx ctx) {
            final ArrayBlockingQueue<XulHttpCtx> responseQueue = new ArrayBlockingQueue<>(4);
            if (responseQueue == null) {
                return;
            }
            synchronized (responseQueue) {
                responseQueue.add(ctx);
            }
            synchronized (_workers) {
                _workers.notify();
            }
        }

        public XulHttpCtx createNextContext() {
            final XulHttpCtx newCtx = new XulHttpCtx(_task, this._request);
            this._nextCtx = newCtx;
            newCtx._prevCtx = this;
            return newCtx;
        }

        private int doRequest(XdHttpFilter filter) {
            _reqTime = XdUtils.timestamp_us();
            _filter = filter;
            return filter.doRequest(this, _request);
        }

        private int handleResponse(XdHttpResponse response) {
            _respTime = XdUtils.timestamp_us();
            _response = response;
            return _filter.handleResponse(this, response);
        }

        public long getRequestTime() {
            return _reqTime;
        }

        public long getResponseTime() {
            return _respTime;
        }

        public XulHttpCtx getNextCtx() {
            return _nextCtx;
        }

        public XulHttpCtx getPrevCtx() {
            return _prevCtx;
        }

        public XdHttpFilter getFilter() {
            return _filter;
        }
    }

    static ArrayList<XdHttpFilter> _filterList = new ArrayList<XdHttpFilter>();

    public static XulHttpTask newTask(String url) {
        XulHttpTask xulHttpTask = new XulHttpTask();
        parseUrl(xulHttpTask, url);
        return xulHttpTask;
    }

    private static void parseUrl(XulHttpTask xulHttpTask, String url) {
        try {
            URL reqUrl = new URL(url);
            xulHttpTask.setSchema(reqUrl.getProtocol())
                    .setHost(reqUrl.getHost())
                    .setPort(reqUrl.getPort())
                    .setPath(reqUrl.getPath());
            String queryStr = reqUrl.getQuery();
            if (!TextUtils.isEmpty(queryStr)) {
                String[] params = queryStr.split("&");
                for (String param : params) {
                    String[] pair = param.split("=");
                    encodeParams(pair);
                    if (pair.length == 2) {
                        xulHttpTask.addQuery(pair[0], pair[1]);
                    } else if (pair.length == 1) {
                        xulHttpTask.addQuery(pair[0], "");
                    } // else 无效参数
                }
            }
        } catch (MalformedURLException e) {
            xulHttpTask.setPath(url);
        }
    }

    private static void encodeParams(String[] params) {
        if (params == null) {
            return;
        }

        for (int i = 0; i < params.length; i++) {
            try {
                params[i] = URLEncoder.encode(params[i], "UTF-8");
            } catch (UnsupportedEncodingException e) {
                XdLog.w(TAG, "Encode url param failed! param=" + params[i], e);
            }
        }
    }

    public static XulHttpTask newTask(String host, String path) {
        XulHttpTask xulHttpTask = new XulHttpTask();
        xulHttpTask.setHost(host)
                .setPath(path);
        return xulHttpTask;
    }

    private static Thread[] _workers = new Thread[3];

    static {
        for (int i = 0, workersLength = _workers.length; i < workersLength; i++) {
            Thread t = _workers[i] = new Thread() {
                @Override
                public void run() {
                    doWork();
                }
            };
            t.setName(String.format("HTTP Stack Worker - %d", i));
            t.start();
        }
    }

    private static void doWork() {
        boolean requestFirst = false;

        while (true) {
            requestFirst = !requestFirst;
            if (requestFirst) {
                if (handleHttpRequest()) {
                    continue;
                }
                if (handleHttpResponse()) {
                    continue;
                }
            } else {
                if (handleHttpResponse()) {
                    continue;
                }
                if (handleHttpRequest()) {
                    continue;
                }
            }
            synchronized (_workers) {
                try {
                    _workers.wait(50);
                } catch (InterruptedException e) {
                    XdLog.e(TAG, e);
                }
            }
        }
    }

    private static boolean handleHttpRequest() {
        return false;
    }

    private static boolean handleHttpResponse() {
        return true;
    }
}
