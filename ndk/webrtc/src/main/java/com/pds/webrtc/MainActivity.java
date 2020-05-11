package com.pds.webrtc;

import android.os.Build;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;

import androidx.appcompat.app.AppCompatActivity;

import com.pds.webrtc.util.SignalingParameters;

import org.webrtc.AudioSource;
import org.webrtc.Camera1Enumerator;
import org.webrtc.CameraEnumerator;
import org.webrtc.EglBase;
import org.webrtc.IceCandidate;
import org.webrtc.Logging;
import org.webrtc.PeerConnection;
import org.webrtc.PeerConnectionFactory;
import org.webrtc.RendererCommon;
import org.webrtc.SessionDescription;
import org.webrtc.StatsReport;
import org.webrtc.SurfaceViewRenderer;
import org.webrtc.VideoCapturer;
import org.webrtc.VideoFileRenderer;
import org.webrtc.VideoRenderer;
import org.webrtc.VideoSource;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Administrator
 */
public class MainActivity extends AppCompatActivity implements RTCClient.SignalingEvents, PeerConnectionClient.PeerConnectionEvents {

    private static final String TAG = "tuch";
//    连接工厂类
    private PeerConnectionFactory factory;
//    连接
    private PeerConnection peerConnection;
    PeerConnectionFactory.Options options = null;
    private AudioSource audioSource;
    private VideoSource videoSource;
    private EglBase rootEglBase;
    private SurfaceViewRenderer remoteView;
    private SurfaceViewRenderer selfView;
    private VideoFileRenderer videoFileRenderer;
    SignalingParameters signalingParameters;
    RTCClient rtcClient;

    private final List<VideoRenderer.Callbacks> remoteRenderers = new ArrayList<VideoRenderer.Callbacks>();

    private final ProxyRenderer remoteProxyRenderer = new ProxyRenderer();
    private final ProxyRenderer localProxyRenderer = new ProxyRenderer();

    private PeerConnectionClient peerConnectionClient = null;
    static {
        System.loadLibrary("native-lib");
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN | WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
                | WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD | WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED
                | WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);
        setContentView(R.layout.activity_main);
        init();
    }


    public void init(){
        // Create video renderers.
        rootEglBase = EglBase.create();
        // Create UI controls.
        selfView = findViewById(R.id.pip_video_view);
        remoteView = findViewById(R.id.fullscreen_video_view);
        selfView.init(rootEglBase.getEglBaseContext(),null);
        selfView.setScalingType(RendererCommon.ScalingType.SCALE_ASPECT_FIT);
        selfView.setZOrderMediaOverlay(true);
        selfView.setEnableHardwareScaler(true);

        remoteView.init(rootEglBase.getEglBaseContext(), null);
        remoteView.setScalingType(RendererCommon.ScalingType.SCALE_ASPECT_FILL);
        remoteView.setEnableHardwareScaler(true);

        peerConnectionClient = PeerConnectionClient.getInstance();
//        remoteView.setMirror(true);
        peerConnectionClient.initPeerConnectionFactory(this);
        startCall();
    }

    private void startCall() {
        rtcClient = new RTCClient(this);
//        RoomConnectionParameters roomConnectionParameters = new RoomConnectionParameters("http://47.254.19.5:8080","room1",false,null);
        rtcClient.connect();
    }

    @Override
    public void onLocalDescription(SessionDescription sdp) {

    }

    @Override
    public void onIceCandidate(IceCandidate candidate) {

    }

    @Override
    public void onIceCandidatesRemoved(IceCandidate[] candidates) {

    }

    @Override
    public void onIceConnected() {

    }

    @Override
    public void onIceDisconnected() {

    }

    @Override
    public void onPeerConnectionClosed() {

    }

    @Override
    public void onPeerConnectionStatsReady(StatsReport[] reports) {

    }

    @Override
    public void onPeerConnectionError(String description) {

    }


    private class ProxyRenderer implements VideoRenderer.Callbacks {
        @Override
        synchronized public void renderFrame(VideoRenderer.I420Frame frame) {
            remoteView.renderFrame(frame);
        }
    }

    private VideoCapturer createCameraCapturer(CameraEnumerator enumerator) {
        final String[] deviceNames = enumerator.getDeviceNames();
        /**
         * WebRTC 视频采集的接口定义为 VideoCapturer，其中定义了初始化、启停、销毁等操作，以及接收启停事件、
         * 数据的回调。相机采集的实现是 CameraCapturer，针对不同的相机 API 又分为 Camera1Capturer 和 Camera2Capturer
         * 。相机采集大部分逻辑都封装在 CameraCapturer 中，只有创建 CameraSession 的代码在两个子类中有不同的实现。
         */
        // First, try to find front facing camera
        Logging.d(TAG, "Looking for front facing cameras.");
        for (String deviceName : deviceNames) {
            if (enumerator.isFrontFacing(deviceName)) {
                Logging.d(TAG, "Creating front facing camera capturer.");
                VideoCapturer videoCapturer = enumerator.createCapturer(deviceName, null);

                if (videoCapturer != null) {
                    return videoCapturer;
                }
            }
        }

        // Front facing camera not found, try something else
        Logging.d(TAG, "Looking for other cameras.");
        for (String deviceName : deviceNames) {
            if (!enumerator.isFrontFacing(deviceName)) {
                Logging.d(TAG, "Creating other camera capturer.");
                VideoCapturer videoCapturer = enumerator.createCapturer(deviceName, null);

                if (videoCapturer != null) {
                    return videoCapturer;
                }
            }
        }

        return null;
    }

    @Override
    public void onConnectedToRoom(SignalingParameters params) {
        VideoCapturer videoCapturer = null;
        signalingParameters = params;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Camera1Enumerator camera1Enumerator = new Camera1Enumerator(true);
            videoCapturer = createCameraCapturer(camera1Enumerator);
        }
        peerConnectionClient.createPeerConnection(rootEglBase.getEglBaseContext(),localProxyRenderer,remoteProxyRenderer,videoCapturer,params,this);

        if (params.offerSdp == null) {
//            发送邀请
            peerConnectionClient.createOffer();

        } else{
            peerConnectionClient.setRemoteDescription(params.offerSdp);
            // Create answer. Answer SDP will be sent to offering client in
            // PeerConnectionEvents.onLocalDescription event.
//            回答响应  接受邀请
            peerConnectionClient.createAnswer();
        }



    }


    @Override
    public void onRemoteDescription(SessionDescription sdp) {

    }

    @Override
    public void onRemoteIceCandidate(IceCandidate candidate) {

    }

    @Override
    public void onRemoteIceCandidatesRemoved(IceCandidate[] candidates) {

    }

    @Override
    public void onChannelClose() {

    }

    @Override
    public void onChannelError(String description) {

    }
}

