package com.pds.webrtc;

import android.content.Context;
import android.os.ParcelFileDescriptor;
import android.util.Log;

import com.pds.webrtc.util.SignalingParameters;

import org.webrtc.AudioSource;
import org.webrtc.AudioTrack;
import org.webrtc.DataChannel;
import org.webrtc.EglBase;
import org.webrtc.IceCandidate;
import org.webrtc.MediaConstraints;
import org.webrtc.MediaStream;
import org.webrtc.PeerConnection;
import org.webrtc.PeerConnectionFactory;
import org.webrtc.RtpReceiver;
import org.webrtc.RtpSender;
import org.webrtc.SdpObserver;
import org.webrtc.SessionDescription;
import org.webrtc.StatsReport;
import org.webrtc.VideoCapturer;
import org.webrtc.VideoRenderer;
import org.webrtc.VideoSource;
import org.webrtc.VideoTrack;
import org.webrtc.voiceengine.WebRtcAudioManager;
import org.webrtc.voiceengine.WebRtcAudioUtils;

import java.nio.ByteBuffer;
import java.util.LinkedList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static com.pds.webrtc.util.StringUtil.preferCodec;


/**
 * Created by david on 2017/10/29.
 */
public class PeerConnectionClient {
    private static String TAG = "PeerConnectionClient";
    private static PeerConnectionClient ourInstance = new PeerConnectionClient();
    private final ExecutorService executor;
    public static PeerConnectionClient getInstance() {
        return ourInstance;
    }

    private PeerConnectionFactory factory;
    private PeerConnection peerConnection;
    PeerConnectionFactory.Options options = null;
    private AudioSource audioSource;
    private VideoSource videoSource;


    private MediaConstraints audioConstraints;
    private ParcelFileDescriptor aecDumpFileDescriptor;
    private MediaConstraints sdpMediaConstraints;


    private LinkedList<IceCandidate> queuedRemoteCandidates;
    private boolean isInitiator;
    private SessionDescription localSdp; // either offer or answer SDP
    private MediaStream mediaStream;
    private VideoCapturer videoCapturer;
    private VideoTrack localVideoTrack;
    private VideoTrack remoteVideoTrack;
    private RtpSender localVideoSender;
    // enableAudio is set to true if audio should be sent.
    private boolean enableAudio;
    private AudioTrack localAudioTrack;
    private DataChannel dataChannel;
    private boolean dataChannelEnabled;
    private SignalingParameters signalingParameters;

    private VideoRenderer.Callbacks localRender;
    private   VideoRenderer.Callbacks remoteRenders;

    private PeerConnectionEvents events;
//    对视屏进行限制
    private MediaConstraints pcConstraints;
    private static final int HD_VIDEO_WIDTH = 1280;
    private static final int HD_VIDEO_HEIGHT = 720;
    public static final String VIDEO_TRACK_ID = "ARDAMSv0";
    public static final String AUDIO_TRACK_ID = "ARDAMSa0";



    private final PCObserver pcObserver = new PCObserver();
//   建立信令的观察者
    private final SDPObserver sdpObserver = new SDPObserver();

    private PeerConnectionClient() {
        executor = Executors.newSingleThreadExecutor();
    }
//初始化工厂
    public  void initPeerConnectionFactory(Context context){
        PeerConnectionFactory.initializeInternalTracer();
//      设置传送数据的编码
        PeerConnectionFactory.initializeFieldTrials("WebRTC-IntelVP8/Enabled/");
//      如果设备支持opensl Es  如果设置true则只是用AudioTracle  不使用opensles
        WebRtcAudioManager.setBlacklistDeviceForOpenSLESUsage(true);
//      允许自动曝光控制
        WebRtcAudioUtils.setWebRtcBasedAcousticEchoCanceler(false);
//      自动增益控制是指使放大电路的增益自动地随信号强度而调整的自动控制方法。
//      实现这种功能的电路简称AGC环。AGC环是闭环电子电路，是一个负反馈系统
        WebRtcAudioUtils.setWebRtcBasedNoiseSuppressor(false);
        PeerConnectionFactory.initializeAndroidGlobals(context,true);
        factory = new PeerConnectionFactory();
    }

    public void createPeerConnection(final EglBase.Context renderEGLContext,
                                     final VideoRenderer.Callbacks localRender, final VideoRenderer.Callbacks remoteRender,
                                     final VideoCapturer videoCapturer, final SignalingParameters signalingParameters,PeerConnectionEvents events) {
        this.localRender = localRender;
        this.remoteRenders = remoteRender;
        this.videoCapturer = videoCapturer;
        this.signalingParameters = signalingParameters;
        this.events = events;
        executor.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    createMediaConstraintsInternal();
                    createPeerConnectionInternal(renderEGLContext);
                } catch (Exception e) {
                    throw e;
                }
            }
        });
    }
//    发出邀请
    public void createOffer() {
        executor.execute(new Runnable() {
            @Override
            public void run() {
                if (peerConnection != null) {
                    Log.d(TAG, "PC Create OFFER");
                    isInitiator = true;
                    peerConnection.createOffer(sdpObserver, sdpMediaConstraints);
                }
            }
        });
    }
//接受邀请
    public void createAnswer() {
        executor.execute(new Runnable() {
            @Override
            public void run() {
                if (peerConnection != null) {
                    PeerConnectionClient.this.isInitiator = false;
                    peerConnection.createAnswer(sdpObserver, sdpMediaConstraints);
                }
            }
        });
    }
//回答邀请 视频通话
    public void setRemoteDescription(final SessionDescription sdp) {
        executor.execute(new Runnable() {
            @Override
            public void run() {
                String sdpDescription = sdp.description;
                sdpDescription = preferCodec(sdpDescription,"ISAC", true);
//                if (peerConnectionParameters.audioStartBitrate > 0) {
//                    sdpDescription = setStartBitrate(
//                            AUDIO_CODEC_OPUS, false, sdpDescription, peerConnectionParameters.audioStartBitrate);
//                }
                Log.d(TAG, "Set remote SDP.");
                SessionDescription sdpRemote = new SessionDescription(sdp.type, sdpDescription);
                peerConnection.setRemoteDescription(sdpObserver, sdpRemote);
            }
        });
    }

    private void createMediaConstraintsInternal() {
        // Create peer connection constraints.
        pcConstraints = new MediaConstraints();
        pcConstraints.optional.add(
                new MediaConstraints.KeyValuePair("DtlsSrtpKeyAgreement", "true"));


        // Create audio constraints.
        audioConstraints = new MediaConstraints();
        audioConstraints.mandatory.add(
                new MediaConstraints.KeyValuePair("googEchoCancellation", "false"));
        audioConstraints.mandatory.add(
                new MediaConstraints.KeyValuePair("googAutoGainControl", "false"));
        audioConstraints.mandatory.add(
                new MediaConstraints.KeyValuePair("googHighpassFilter", "false"));
        audioConstraints.mandatory.add(
                new MediaConstraints.KeyValuePair("googNoiseSuppression", "false"));
        audioConstraints.mandatory.add(
                new MediaConstraints.KeyValuePair("levelControl", "true"));

        // Create SDP constraints.
        sdpMediaConstraints = new MediaConstraints();
//        接受音频邀请
        sdpMediaConstraints.mandatory.add(
                new MediaConstraints.KeyValuePair("OfferToReceiveAudio", "true"));
//        接受视频邀请
        sdpMediaConstraints.mandatory.add(
                new MediaConstraints.KeyValuePair("OfferToReceiveVideo", "true"));

    }
    private void createPeerConnectionInternal(EglBase.Context renderEGLContext) {
        queuedRemoteCandidates = new LinkedList<IceCandidate>();
//        允许视频加速
        factory.setVideoHwAccelerationOptions(renderEGLContext, renderEGLContext);

        //设置打洞服务器
        PeerConnection.RTCConfiguration rtcConfig =
                new PeerConnection.RTCConfiguration(signalingParameters.iceServers);
        rtcConfig.tcpCandidatePolicy = PeerConnection.TcpCandidatePolicy.DISABLED;
        rtcConfig.bundlePolicy = PeerConnection.BundlePolicy.MAXBUNDLE;
        rtcConfig.rtcpMuxPolicy = PeerConnection.RtcpMuxPolicy.REQUIRE;
        rtcConfig.continualGatheringPolicy = PeerConnection.ContinualGatheringPolicy.GATHER_CONTINUALLY;
        rtcConfig.keyType = PeerConnection.KeyType.ECDSA;

//        ICE服务器列表  MediaConstraints    连接信令服务器的接口回调
        peerConnection = factory.createPeerConnection(rtcConfig, pcConstraints, pcObserver);
//其实 VideoTrack/AudioTrack 已经可以播放了，不过我们先不考虑本地播放。那么如果要把他们发送到对方客户端，我们需要把他们添加到媒体流
        mediaStream = factory.createLocalMediaStream("ARDAMS");
//
        mediaStream.addTrack(createVideoTrack(videoCapturer));
//    设置音频
        mediaStream.addTrack(createAudioTrack());
        peerConnection.addStream(mediaStream);

    }

    private AudioTrack createAudioTrack() {
        audioSource = factory.createAudioSource(audioConstraints);
        localAudioTrack = factory.createAudioTrack(AUDIO_TRACK_ID, audioSource);
        localAudioTrack.setEnabled(true);
        return localAudioTrack;
    }


    private void findVideoSender() {
        for (RtpSender sender : peerConnection.getSenders()) {
            if (sender.track() != null) {
                String trackType = sender.track().kind();
                if (trackType.equals("video")) {
                    localVideoSender = sender;
                }
            }
        }
    }

    private VideoTrack createVideoTrack(VideoCapturer capturer) {
        videoSource = factory.createVideoSource(capturer);
        capturer.startCapture(HD_VIDEO_WIDTH, HD_VIDEO_HEIGHT, 30);
        localVideoTrack = factory.createVideoTrack(VIDEO_TRACK_ID, videoSource);
        localVideoTrack.setEnabled(true);
//        将另一端的数据读取到了 并回调 localRender
        localVideoTrack.addRenderer(new VideoRenderer(localRender));
        return localVideoTrack;
    }


    // Implementation detail: observe ICE & stream changes and react accordingly.
    private class PCObserver implements PeerConnection.Observer {
        @Override
        public void onIceCandidate(final IceCandidate candidate) {
            executor.execute(new Runnable() {
                @Override
                public void run() {
                    events.onIceCandidate(candidate);
                }
            });
        }

        @Override
        public void onIceCandidatesRemoved(final IceCandidate[] candidates) {
            executor.execute(new Runnable() {
                @Override
                public void run() {
                    events.onIceCandidatesRemoved(candidates);
                }
            });
        }

        @Override
        public void onSignalingChange(PeerConnection.SignalingState newState) {
        }

        @Override
        public void onIceConnectionChange(final PeerConnection.IceConnectionState newState) {

        }

        @Override
        public void onIceGatheringChange(PeerConnection.IceGatheringState newState) {
            Log.d(TAG, "IceGatheringState: " + newState);
        }

        @Override
        public void onIceConnectionReceivingChange(boolean receiving) {
            Log.d(TAG, "IceConnectionReceiving changed to " + receiving);
        }
//在连接通道正常的情况下，对方的PeerConnection.Observer监听就会调用onAddStream()响应函数并提供接收到的媒体流。
        @Override
        public void onAddStream(final MediaStream stream) {
            executor.execute(new Runnable() {
                @Override
                public void run() {
                    if (peerConnection == null ) {
                        return;
                    }
                    if (stream.audioTracks.size() > 1 || stream.videoTracks.size() > 1) {
                        return;
                    }
                    if (stream.videoTracks.size() == 1) {
                        remoteVideoTrack = stream.videoTracks.get(0);
                        remoteVideoTrack.setEnabled(true);
//                        将远方的视频信息绘制到屏幕上
                        remoteVideoTrack.addRenderer(new VideoRenderer(remoteRenders));
                    }
                }
            });
        }

        @Override
        public void onRemoveStream(final MediaStream stream) {
            executor.execute(new Runnable() {
                @Override
                public void run() {
                    remoteVideoTrack = null;
                }
            });
        }

        @Override
        public void onDataChannel(final DataChannel dc) {
            Log.d(TAG, "New Data channel " + dc.label());

            if (!dataChannelEnabled) {
                return;
            }

            dc.registerObserver(new DataChannel.Observer() {
                @Override
                public void onBufferedAmountChange(long previousAmount) {
                    Log.d(TAG, "Data channel buffered amount changed: " + dc.label() + ": " + dc.state());
                }

                @Override
                public void onStateChange() {
                    Log.d(TAG, "Data channel state changed: " + dc.label() + ": " + dc.state());
                }

                @Override
                public void onMessage(final DataChannel.Buffer buffer) {
                    if (buffer.binary) {
                        Log.d(TAG, "Received binary msg over " + dc);
                        return;
                    }
                    ByteBuffer data = buffer.data;
                    final byte[] bytes = new byte[data.capacity()];
                    data.get(bytes);
                    String strData = new String(bytes);
                    Log.d(TAG, "Got msg: " + strData + " over " + dc);
                }
            });
        }

        @Override
        public void onRenegotiationNeeded() {
            // No need to do anything; AppRTC follows a pre-agreed-upon
            // signaling/negotiation protocol.
        }

        @Override
        public void onAddTrack(final RtpReceiver receiver, final MediaStream[] mediaStreams) {}
    }
    public interface PeerConnectionEvents {
        /**
         * Callback fired once local SDP is created and set.
         */
        void onLocalDescription(final SessionDescription sdp);

        /**
         * Callback fired once local Ice candidate is generated.
         */
        void onIceCandidate(final IceCandidate candidate);

        /**
         * Callback fired once local ICE candidates are removed.
         */
        void onIceCandidatesRemoved(final IceCandidate[] candidates);

        /**
         * Callback fired once connection is established (IceConnectionState is
         * CONNECTED).
         */
        void onIceConnected();

        /**
         * Callback fired once connection is closed (IceConnectionState is
         * DISCONNECTED).
         */
        void onIceDisconnected();

        /**
         * Callback fired once peer connection is closed.
         */
        void onPeerConnectionClosed();

        /**
         * Callback fired once peer connection statistics is ready.
         */
        void onPeerConnectionStatsReady(final StatsReport[] reports);

        /**
         * Callback fired once peer connection error happened.
         */
        void onPeerConnectionError(final String description);
    }

    // Implementation detail: handle offer creation/signaling and answer setting,
    // as well as adding remote ICE candidates once the answer SDP is set.
    private class SDPObserver implements SdpObserver {
        @Override
        public void onCreateSuccess(final SessionDescription origSdp) {
            String sdpDescription = origSdp.description;
            sdpDescription = preferCodec(sdpDescription, "ISAC", true);
            final SessionDescription sdp = new SessionDescription(origSdp.type, sdpDescription);
            localSdp = sdp;
            executor.execute(new Runnable() {
                @Override
                public void run() {
                    if (peerConnection != null) {
                        Log.d(TAG, "Set local SDP from " + sdp.type);
                        peerConnection.setLocalDescription(sdpObserver, sdp);
                    }
                }
            });
        }

        @Override
        public void onSetSuccess() {
            executor.execute(new Runnable() {
                @Override
                public void run() {
                    if (peerConnection == null) {
                        return;
                    }
                    if (isInitiator) {
//                        发出的邀请有人响应   不需要做处理
                        // For offering peer connection we first create offer and set
                        // local SDP, then after receiving answer set remote SDP.
                        if (peerConnection.getRemoteDescription() == null) {
                            // We've just set our local SDP so time to send it.
                            Log.d(TAG, "Local SDP set succesfully");
                        }
//                            events.onLocalDescription(localSdp);
//                        } else {
//                            Log.d(TAG, "Remote SDP set succesfully");
//                        }
                    } else {
//                        收到了邀请仅仅设置自己的sdp  就可以了
                        // For answering peer connection we set remote SDP and then
                        // create answer and set local SDP.
                        if (peerConnection.getLocalDescription() != null) {
                            // We've just set our local SDP so time to send it, drain
                            // remote and send local ICE candidates.
                            Log.d(TAG, "Local SDP set succesfully");
                            events.onLocalDescription(localSdp);
                        } else {
                            Log.d(TAG, "Remote SDP set succesfully");
                        }
                    }
                }
            });
        }

        @Override
        public void onCreateFailure(final String error) {
        }

        @Override
        public void onSetFailure(final String error) {
        }
    }

}
