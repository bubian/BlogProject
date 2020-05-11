package com.pds.webrtc.util;

import org.webrtc.IceCandidate;
import org.webrtc.PeerConnection;
import org.webrtc.SessionDescription;

import java.util.List;

/**
 * Created by david on 2017/10/30.
 */

public class SignalingParameters {
    public final List<PeerConnection.IceServer> iceServers;
    public final boolean initiator;
    //客户端id  又后台系统生成，webrtc并没设定客户端id  id为设备唯一标识符   我们这直接定死
    public final String clientId;
    public final String wssUrl;
    public final String wssPostUrl;
//    对方的sdp信息描述符
    public final SessionDescription offerSdp;
//    信令服务器列表
    public final List<IceCandidate> iceCandidates;

    public SignalingParameters(List<PeerConnection.IceServer> iceServers, boolean initiator,
                               String clientId, String wssUrl, String wssPostUrl, SessionDescription offerSdp,
                               List<IceCandidate> iceCandidates) {
        this.iceServers = iceServers;
        this.initiator = initiator;
        this.clientId = clientId;
        this.wssUrl = wssUrl;
        this.wssPostUrl = wssPostUrl;
        this.offerSdp = offerSdp;
        this.iceCandidates = iceCandidates;
    }
}
