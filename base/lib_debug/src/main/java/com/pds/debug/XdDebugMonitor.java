package com.pds.debug;

import android.graphics.Bitmap;
import android.graphics.Paint;

import com.pds.debug.http.XdHttpServer;
import com.pds.debug.utils.XdLog;
import com.pds.debug.utils.XdUtils;

import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;
import org.xmlpull.v1.XmlSerializer;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.text.SimpleDateFormat;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.TreeMap;
import java.util.WeakHashMap;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

public class XdDebugMonitor {

    private static final String[] xulPropStateMap = new String[]{
            "normal", "focused", "disabled", "-", "visible", "-", "-", "-", "invisible"
    };
    private static final String TAG = XdDebugMonitor.class.getSimpleName();
    WeakHashMap<Object, PageInfo> _pages = new WeakHashMap<Object, PageInfo>();
    LinkedHashMap<Integer, PageInfo> _pagesById = new LinkedHashMap<Integer, PageInfo>();
    Map<Integer, IXdDebuggableObject> _userObjectMap = new TreeMap<Integer, IXdDebuggableObject>();
    private volatile Bitmap _snapshotBmp;
    private volatile SimpleDateFormat _dateTimeFormat;

    private volatile Paint _debugInfoPaint;

    private static String propStateFromId(int state) {
        if (state < 0) {
            return "inline";
        }
        return xulPropStateMap[state];
    }

    public synchronized void onPageCreate(Object page) {
        PageInfo pageInfo = new PageInfo(page, page.hashCode());
        _pages.put(page, pageInfo);
        _pagesById.put(pageInfo.id, pageInfo);
    }

    public synchronized void onPageDestroy(Object page) {
        PageInfo pageInfo = _pages.remove(page);
        if (pageInfo == null) {
            return;
        }
        _pagesById.remove(pageInfo.id);
        pageInfo.status = "destroyed";
    }

    public synchronized void onPageStopped(Object page) {
        PageInfo pageInfo = _pages.get(page);
        if (pageInfo == null) {
            return;
        }
        pageInfo.status = "stopped";
    }

    public synchronized void onPageResumed(Object page) {
        PageInfo pageInfo = _pages.get(page);
        if (pageInfo == null) {
            return;
        }
        pageInfo.onResume();
    }

    public synchronized void onPagePaused(Object page) {
        PageInfo pageInfo = _pages.get(page);
        if (pageInfo == null) {
            return;
        }
        pageInfo.status = "paused";
    }

    public synchronized void onPageRefreshed(Object page, long drawingDuration) {
        PageInfo pageInfo = _pages.get(page);
        if (pageInfo == null) {
            return;
        }
        pageInfo.onPageRefreshed(drawingDuration);
    }

    public synchronized void onPageRenderIsReady(Object page) {
        PageInfo pageInfo = _pages.get(page);
        if (pageInfo == null) {
            return;
        }
        pageInfo.onPageRenderIsReady();
    }


    public synchronized boolean listUserObjects(XdHttpServer.XulHttpServerRequest request, XdHttpServer.XulHttpServerResponse response) {
        try {
            XmlSerializer writer = XmlPullParserFactory.newInstance().newSerializer();
            writer.setOutput(response.getBodyStream(), "utf-8");
            writer.startDocument("utf-8", Boolean.TRUE);
            writer.startTag(null, "objects");
            Iterator<Map.Entry<Integer, IXdDebuggableObject>> iterator = _userObjectMap.entrySet().iterator();
            while (iterator.hasNext()) {
                Map.Entry<Integer, IXdDebuggableObject> entry = iterator.next();
                IXdDebuggableObject object = entry.getValue();
                if (!object.isValid()) {
                    iterator.remove();
                    continue;
                }
                writer.startTag(null, "object");
                writer.attribute(null, "id", String.valueOf(entry.getKey()));
                writer.attribute(null, "name", object.name());
                try {
                    object.buildBriefInfo(request, writer);
                } catch (Exception e) {
                    XdLog.e(TAG, e);
                }

                writer.endTag(null, "object");
            }
            writer.endTag(null, "objects");
            writer.endDocument();
            writer.flush();
            return true;
        } catch (XmlPullParserException e) {
            XdLog.e(TAG, e);
        } catch (IOException e) {
            XdLog.e(TAG, e);
        }
        return false;
    }

    public synchronized boolean registerDebuggableObject(IXdDebuggableObject obj) {
        _userObjectMap.put(obj.hashCode(), obj);
        return true;
    }

    public boolean getUserObject(int objectId, final XdHttpServer.XulHttpServerRequest request, final XdHttpServer.XulHttpServerResponse response) {
        try {
            final IXdDebuggableObject object;
            synchronized (this) {
                object = _userObjectMap.get(objectId);
                if (object == null || !object.isValid()) {
                    _userObjectMap.remove(objectId);
                    return false;
                }
            }

            final XmlSerializer writer = XmlPullParserFactory.newInstance().newSerializer();
            writer.setOutput(response.getBodyStream(), "utf-8");
            writer.startDocument("utf-8", Boolean.TRUE);
            writer.startTag(null, "object");

            writer.startTag(null, "object");
            writer.attribute(null, "id", String.valueOf(objectId));
            writer.attribute(null, "name", object.name());

            if (object.runInMainThread()) {
                final Semaphore sem = new Semaphore(0);
                XdDebugAdapter.postToMainLooper(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            object.buildDetailInfo(request, writer);
                        } catch (Exception e) {
                            XdLog.e(TAG, e);
                        }
                        sem.release();
                    }
                });
                sem.tryAcquire(20, TimeUnit.SECONDS);
            } else {
                object.buildDetailInfo(request, writer);
            }
            writer.endDocument();
            writer.flush();
            return true;
        } catch (Exception e) {
            XdLog.e(TAG, e);
        }
        return false;
    }

    public XdHttpServer.XulHttpServerResponse execUserObjectCommand(int objectId, final String command, final XdHttpServer.XulHttpServerRequest request, final XdHttpServer.XulHttpServerHandler serverHandler) {
        try {
            final IXdDebuggableObject object;
            final XdHttpServer.XulHttpServerResponse[] response = new XdHttpServer.XulHttpServerResponse[1];
            synchronized (this) {
                object = _userObjectMap.get(objectId);
                if (object == null || !object.isValid()) {
                    _userObjectMap.remove(objectId);
                    return null;
                }
            }

            if (object.runInMainThread()) {
                final Semaphore sem = new Semaphore(0);
                XdDebugAdapter.postToMainLooper(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            response[0] = object.execCommand(command, request, serverHandler);
                        } catch (Exception e) {
                            XdLog.e(TAG, e);
                        }
                        sem.release();
                    }
                });
                sem.tryAcquire(20, TimeUnit.SECONDS);
            } else {
                response[0] = object.execCommand(command, request, serverHandler);
            }
            return response[0];
        } catch (Exception e) {
            XdLog.e(TAG, e);
        }
        return null;
    }

    class PageInfo {
        int id;
        WeakReference<Object> page;
        String status;
        long createTime;
        long firstResumedTime;
        long renderIsReadyTime;
        long refreshTime;

        int refreshCount;
        long totalDrawingDuration;
        long maxDrawingDuration;
        long minDrawingDuration = Integer.MAX_VALUE;

        public PageInfo(Object page, int id) {
            this.page = new WeakReference<Object>(page);
            this.id = id;
            this.status = "create";
            this.createTime = XdUtils.timestamp();
        }

        public void onPageRefreshed(long drawingDuration) {
            ++refreshCount;
            totalDrawingDuration += drawingDuration;
            if (drawingDuration > maxDrawingDuration) {
                maxDrawingDuration = drawingDuration;
            }
            if (drawingDuration < minDrawingDuration) {
                minDrawingDuration = drawingDuration;
            }
            refreshTime = XdUtils.timestamp();
        }

        public long getRefreshTime() {
            long deltaTime = XdUtils.timestamp() - this.refreshTime;
            return System.currentTimeMillis() - deltaTime;
        }

        public void onResume() {
            status = "resumed";
            if (firstResumedTime == 0) {
                firstResumedTime = XdUtils.timestamp();
            }
        }

        public void onPageRenderIsReady() {
            renderIsReadyTime = XdUtils.timestamp();
        }
    }
}
