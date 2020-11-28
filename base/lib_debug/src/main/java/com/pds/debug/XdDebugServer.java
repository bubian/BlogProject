package com.pds.debug;

import android.content.Intent;
import android.text.TextUtils;

import com.pds.debug.http.XdHttpServer;
import com.pds.debug.utils.XdLog;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.nio.channels.SocketChannel;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

public class XdDebugServer extends XdHttpServer {

    public static XulHttpServerResponse PENDING_RESPONSE = new XulHttpServerResponse(null);
    private static XdHttpServer _debugServer;
    private static XdDebugMonitor _monitor;
    private static ArrayList<IXdDebugCommandHandler> _userHandlers;

    private XdDebugServer() {
        super(55550);
    }

    public static void startUp() {
        if (_debugServer != null) {
            return;
        }
        _debugServer = new XdDebugServer();
        _monitor = new XdDebugMonitor();
    }

    public static XdDebugMonitor getMonitor() {
        return _monitor;
    }

    public static synchronized boolean registerCommandHandler(IXdDebugCommandHandler debugCommandHandler) {
        if (_userHandlers == null) {
            _userHandlers = new ArrayList<IXdDebugCommandHandler>();
        }

        if (_userHandlers.contains(debugCommandHandler)) {
            return false;
        }
        _userHandlers.add(debugCommandHandler);
        return true;
    }

    @Override
    protected XulHttpServerHandler createHandler(XdHttpServer server, SocketChannel socketChannel) {
        return new XulDebugApiHandler(server, socketChannel);
    }

    private static class XulDebugApiHandler extends XulHttpServerHandler {

        private static final String TAG = XulDebugApiHandler.class.getSimpleName();
        private volatile SimpleDateFormat _dateTimeFormat;

        public XulDebugApiHandler(XdHttpServer server, SocketChannel socketChannel) {
            super(server, socketChannel);
        }

        @Override
        public XulHttpServerResponse getResponse(XulHttpServerRequest httpRequest) {
            XulHttpServerResponse response = super.getResponse(httpRequest);
            synchronized (this) {
                if (_dateTimeFormat == null) {
                    _dateTimeFormat = new SimpleDateFormat("ccc, dd MMM yyyy HH:mm:ss 'GMT'", Locale.US);
                    _dateTimeFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
                }
                response.addHeader("Date", _dateTimeFormat.format(new Date()))
                        .addHeader("Server", "Xul Debug Server/1.0");
            }
            return response;
        }

        @Override
        protected void handleHttpRequest(XulHttpServerRequest request) throws IOException {
            String path = request.path;
            XdLog.d(TAG, "path = " + path);
            if (path != null && path.startsWith("/api/")) {
                XulHttpServerResponse response;
                if ("/api/list-pages".equals(path)) {
                    response = null;
                } else {
                    response = unsupportedCommand(request);
                }

                if (response == PENDING_RESPONSE) {
                    return;
                }

                if (response != null) {
                    response.send();
                    return;
                }
            }
            super.handleHttpRequest(request);
        }

        private void responseFile(XulHttpServerRequest request, final File fileName, boolean autoDelete) throws IOException {
            if (fileName == null || !fileName.exists() || !fileName.canRead()) {
                super.handleHttpRequest(request);
                return;
            }

            final FileInputStream fileInputStream = new FileInputStream(fileName);
            final XulHttpServerResponse response = getResponse(request);
            response.addHeader("Content-Type", "application/oct-stream")
                    .writeStream(fileInputStream);

            if (autoDelete) {
                response.setCleanUp(new Runnable() {
                    @Override
                    public void run() {
                        fileName.delete();
                    }
                });
            }
            response.send();
        }

        private XulHttpServerResponse responseCommandOutput(XulHttpServerRequest request, String command) {
            XulHttpServerResponse response;
            response = getResponse(request)
                    .addHeader("Content-Type", "text/plain");
            try {
                final Process exec = Runtime.getRuntime().exec(command);
                final InputStream inputStream = exec.getInputStream();
                response.writeStream(inputStream)
                        .addHeader("Transfer-Encoding", "chunked")
                        .setCleanUp(new Runnable() {
                            @Override
                            public void run() {
                                exec.destroy();
                            }
                        });
            } catch (Exception e) {
                e.printStackTrace(new PrintStream(response.getBodyStream()));
            }
            return response;
        }

        private XulHttpServerResponse startActivity(XulHttpServerRequest request, String[] params) {
            if (params == null) {
                return null;
            }
            String activity = params.length > 0 ? params[0].trim() : null;
            String action = params.length > 1 ? params[1].trim() : Intent.ACTION_MAIN;
            String category = params.length > 2 ? params[2].trim() : null;

            return startActivity(request, activity, action, category);
        }

        private XulHttpServerResponse startActivity(XulHttpServerRequest request, String activity, String action, String category) {
            try {
                Intent intent = new Intent(action);
                String packageName = XdDebugAdapter.getPackageName();
                if (!TextUtils.isEmpty(activity)) {
                    String[] activityInfo = activity.split(",");
                    if (activityInfo.length == 1) {
                    } else if (activityInfo.length == 2) {
                        packageName = activityInfo[0];
                        activity = activityInfo[1];
                    }
                    if (activity.startsWith(".")) {
                        activity = packageName + activity;
                    }
                    intent.setClass(XdDebugAdapter.getAppContext(), this.getClass().getClassLoader().loadClass(activity));
                }
                if (!TextUtils.isEmpty(packageName)) {
                    intent.setPackage(packageName);
                }

                if (!TextUtils.isEmpty(category)) {
                    intent.addCategory(category);
                }
                if (request.queries != null) {
                    for (Map.Entry<String, String> queryInfo : request.queries.entrySet()) {
                        intent.putExtra(queryInfo.getKey(), queryInfo.getValue());
                    }
                }
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                XdDebugAdapter.startActivity(intent);

                XulHttpServerResponse response = getResponse(request);
                return response.addHeader("Content-Type", "text/xml")
                        .writeBody("<result status=\"OK\"/>");
            } catch (Exception e) {
                XdLog.e(TAG, e);
            }
            return null;
        }

        private XulHttpServerResponse getUserObject(XulHttpServerRequest request, int objectId) {
            XulHttpServerResponse response = getResponse(request);
            if (_monitor.getUserObject(objectId, request, response)) {
                response.addHeader("Content-Type", "text/xml");
            } else {
                response.setStatus(404)
                        .cleanBody();
            }
            return response;
        }

        private XulHttpServerResponse execUserObjectCommand(XulHttpServerRequest request, int objectId, String command) {
            return _monitor.execUserObjectCommand(objectId, command, request, this);
        }

        private XulHttpServerResponse listUserObjects(XulHttpServerRequest request) {
            XulHttpServerResponse response = getResponse(request);
            if (_monitor.listUserObjects(request, response)) {
                response.addHeader("Content-Type", "text/xml");
            } else {
                response.setStatus(404)
                        .cleanBody();
            }
            return response;
        }

        private XulHttpServerResponse sendMotionEvent(XulHttpServerRequest request, String[] events) {
            return null;
        }

        private XulHttpServerResponse unsupportedCommand(XulHttpServerRequest request) {
            ArrayList<IXdDebugCommandHandler> userHandlers = _userHandlers;
            if (userHandlers != null) {
                for (int i = 0, userHandlersSize = userHandlers.size(); i < userHandlersSize; i++) {
                    IXdDebugCommandHandler handler = userHandlers.get(i);
                    try {
                        XulHttpServerResponse response = handler.execCommand(request.path, this, request);
                        if (response != null) {
                            return response;
                        }
                    } catch (Exception e) {
                        XdLog.e(TAG, e);
                    }
                }
            }
            return getResponse(request)
                    .setStatus(501)
                    .setMessage("Debug API Not implemented");
        }
    }
}
