package com.pds.debug.work;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.drawable.shapes.RoundRectShape;
import android.util.Pair;

import com.pds.debug.utils.XdMemoryOutputStream;
import com.pds.debug.utils.XdPendingInputStream;
import com.pds.debug.utils.XdSimpleStack;

import java.io.InputStream;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.WeakHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;

public class XdWorker {
    private static final String TAG = XdWorker.class.getSimpleName();
    private static final String XUL_DOWNLOAD_WORKER = "XUL Download Worker";
    private static final String XUL_DRAWABLE_WORKER = "XUL Drawable Worker";
    private static final float[] EMPTY_ROUND_RECT_RADIUS = new float[8];

    private static void _cleanAutoCleanList(long timestamp) {
        synchronized (_autoCleanList) {
            for (int i = 0; i < _autoCleanList.size(); ++i) {
                Pair<Long, Object> longObjectPair = _autoCleanList.get(i);
                if (timestamp > longObjectPair.first) {
                    _autoCleanList.remove(i);
                    ++i;
                }
            }
        }
    }


    public interface IXulWorkerHandler {
        InputStream getAssets(String path);

        InputStream getAppData(String path);

        String resolvePath(DownloadItem downloadItem, String path);

        InputStream loadCachedData(String path);

        boolean storeCachedData(String path, InputStream stream);

        String calCacheKey(String url);
    }

    private static IXulWorkerHandler _handler;

    public static void setHandler(IXulWorkerHandler handler) {
        _handler = handler;
    }

    private static boolean waitPendingStream(InputStream stream) {
        if (stream == null) {
            return false;
        }
        if (stream instanceof XdPendingInputStream) {
            // 如果是可挂起流，则挂起等待
            XdPendingInputStream pendingInputStream = (XdPendingInputStream) stream;
            pendingInputStream.checkPending();
            return pendingInputStream.isReady();
        }
        return true;
    }


    private static String _internalCalCacheKey(DownloadItem downloadItem, String url) {
        if (_handler == null) {
            return null;
        }
        try {
            return _handler.calCacheKey(url);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private static InputStream _internalLoadCachedData(String path) {
        if (_handler == null) {
            return null;
        }
        try {
            return _handler.loadCachedData(path);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private static boolean _internalStoreCachedData(String path, InputStream stream) {
        if (_handler == null) {
            return false;
        }
        try {
            return _handler.storeCachedData(path, stream);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    ////////////////////////////////////////////////////////////////
    public static class DownloadItem {
        int __retryCounter = 0;
        String __cacheKey = null;
        String __resolvedPath = "";

        public String url;
        // 是否直接从源读取文件
        public boolean isDirect = false;
        // 内容是否可缓存文件
        public boolean noFileCache = false;

        public String getInternalResolvedPath() {
            return __resolvedPath;
        }

        public String getInternalCacheKey() {
            return __cacheKey;
        }
    }

    // 挂起的任务
    private static final ArrayList<DownloadItem> _pendingDownloadTask = new ArrayList<DownloadItem>();


    ////////////////////////////////////////////////////////////////

    static volatile Thread[] _downloadWorkers = new Thread[8];
    static volatile Thread[] _drawableWorkers = new Thread[2];

    static final Object _drawableWorkerWaitObj = new Object();
    static final Object _downloadWorkerWaitObj = new Object();

    private static final LinkedList<Pair<Long, Object>> _autoCleanList = new LinkedList<Pair<Long, Object>>();
    private static WeakHashMap<Object, Object> _weakRefObjects = new WeakHashMap<Object, Object>();


    static void _threadWait(int ms) {
        try {
            String name = Thread.currentThread().getName();
            if (XUL_DOWNLOAD_WORKER.equals(name)) {
                synchronized (_downloadWorkerWaitObj) {
                    _downloadWorkerWaitObj.wait(ms);
                }
            }
            if (XUL_DRAWABLE_WORKER.equals(name)) {
                synchronized (_drawableWorkerWaitObj) {
                    _drawableWorkerWaitObj.wait(ms);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    static void _notifyDrawableWorker() {
        try {
            synchronized (_drawableWorkerWaitObj) {
                _drawableWorkerWaitObj.notify();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    static void _notifyAllDrawableWorker() {
        try {
            synchronized (_drawableWorkerWaitObj) {
                _drawableWorkerWaitObj.notifyAll();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    static void _notifyDownloadWorker() {
        try {
            synchronized (_downloadWorkerWaitObj) {
                _downloadWorkerWaitObj.notify();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    static void _notifyAllDownloadWorker() {
        try {
            synchronized (_downloadWorkerWaitObj) {
                _downloadWorkerWaitObj.notifyAll();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void _addPendingDownloadTask(DownloadItem item) {
        synchronized (_pendingDownloadTask) {
            _pendingDownloadTask.add(item);
        }
    }


    static AtomicInteger _workerCounter = new AtomicInteger(0);
    private static SSLContext _sslCtx = null;
    static acceptAllHostnameVerifier _hostVerifier = null;

    static class _downloadContext {
        Pattern assetsPat = Pattern.compile("^file:///\\.assets/(.+)$");
        Pattern appDataPat = Pattern.compile("^file:///\\.app/(.+)$");
        byte downloadBuffer[] = new byte[1024];
    }


    public static class XulDownloadOutputBuffer extends XdMemoryOutputStream {
        public XulDownloadOutputBuffer(int initialCapacity) {
            super(initialCapacity);
        }

        @Override
        public void onClose() {
            recycleDownloadBuffer(this);
        }
    }

    private static XdSimpleStack<XulDownloadOutputBuffer> _downloadBufferList = new XdSimpleStack<XulDownloadOutputBuffer>(16);

    synchronized private static void recycleDownloadBuffer(XulDownloadOutputBuffer buf) {
        _downloadBufferList.push(buf);
    }

    synchronized public static XulDownloadOutputBuffer obtainDownloadBuffer(int initialCapacity) {
        XulDownloadOutputBuffer buf = _downloadBufferList.pop();
        if (buf == null) {
            return new XulDownloadOutputBuffer(initialCapacity);
        }
        buf.reset(initialCapacity);
        return buf;
    }

    private static class acceptAllHostnameVerifier implements HostnameVerifier {
        @Override
        public boolean verify(String hostname, SSLSession session) {
            return true;
        }

    }

    /**
     * 证书验证类
     */
    public static class acceptAllTrustManager implements TrustManager, javax.net.ssl.X509TrustManager {
        public java.security.cert.X509Certificate[] getAcceptedIssuers() {
            return null;
        }

        public boolean isServerTrusted(java.security.cert.X509Certificate[] certs) {
            return true;
        }

        public boolean isClientTrusted(java.security.cert.X509Certificate[] certs) {
            return true;
        }

        public void checkServerTrusted(java.security.cert.X509Certificate[] certs, String authType)
                throws java.security.cert.CertificateException {
            return;
        }

        public void checkClientTrusted(java.security.cert.X509Certificate[] certs, String authType)
                throws java.security.cert.CertificateException {
            return;
        }
    }


    static boolean _isGIF(Pattern gifPattern, String path, InputStream inputStream) {
        Matcher matcher = gifPattern.matcher(path);
        if (matcher != null && matcher.matches()) {
            return true;
        }

        if (inputStream != null && inputStream.markSupported()) {
            try {
                inputStream.mark(64);
                if (inputStream.read() == 'G' &&
                        inputStream.read() == 'I' &&
                        inputStream.read() == 'F' &&
                        inputStream.read() == '8' &&
                        inputStream.read() == '9' &&
                        inputStream.read() == 'a'
                ) {
                    inputStream.reset();
                    return true;
                }
                inputStream.reset();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return false;
    }


    private static Bitmap toRoundCornerMutableBitmap(Canvas canvas, Paint paintClear, Bitmap srcBitmap, float[] roundRadius) {
        canvas.setBitmap(srcBitmap);
        RectF inset = new RectF(1, 1, 1, 1);
        if (roundRadius.length == 2) {
            float[] tmpRoundRadius = new float[8];
            for (int i = 0; i < 4; ++i) {
                tmpRoundRadius[i * 2 + 0] = roundRadius[0];
                tmpRoundRadius[i * 2 + 1] = roundRadius[1];
                roundRadius = tmpRoundRadius;
            }
        }
        canvas.save();
        canvas.translate(-1, -1);
        RoundRectShape roundRectShape = new RoundRectShape(null, inset, roundRadius);
        roundRectShape.resize(srcBitmap.getWidth() + 2, srcBitmap.getHeight() + 2);
        roundRectShape.draw(canvas, paintClear);
        canvas.restore();
        canvas.setBitmap(null);
        return srcBitmap;
    }

    static {
        ThreadGroup xulThreadGroup = new ThreadGroup("XUL");
        for (int i = 0; i < _downloadWorkers.length; i++) {
            _downloadWorkers[i] = new Thread(xulThreadGroup, XUL_DOWNLOAD_WORKER) {
                @Override
                public void run() {
                    // _downloadWorkerRun();
                }
            };
            // _downloadWorkers[i].setPriority(Thread.NORM_PRIORITY);
            _downloadWorkers[i].start();
        }

        for (int i = 0; i < _drawableWorkers.length; i++) {
            _drawableWorkers[i] = new Thread(xulThreadGroup, XUL_DRAWABLE_WORKER) {
                @Override
                public void run() {
                    // _drawableWorkerRun();
                }
            };
            _drawableWorkers[i].setPriority(Thread.NORM_PRIORITY - 2);
            _drawableWorkers[i].start();
        }

        try {
            _sslCtx = SSLContext.getInstance("SSL");
            _sslCtx.init(null, new TrustManager[]{new acceptAllTrustManager()}, new SecureRandom());
        } catch (Exception e) {
            e.printStackTrace();
        }

        _hostVerifier = new acceptAllHostnameVerifier();
    }

    public static class XulDownloadParams {
        public boolean post = false;
        public byte[] postBody;
        public String[] extHeaders;
        public int responseCode;
        public String responseMsg;
        public Map<String, List<String>> responseHeaders;

        public XulDownloadParams(boolean post, byte[] postBody, String[] extHeaders) {
            this.post = post;
            this.postBody = postBody;
            this.extHeaders = extHeaders;
        }
    }
}
