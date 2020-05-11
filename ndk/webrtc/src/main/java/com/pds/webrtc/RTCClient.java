package com.pds.webrtc;

import android.os.Handler;
import android.util.Log;

import com.pds.webrtc.util.SignalingParameters;

import org.json.JSONException;
import org.json.JSONObject;
import org.webrtc.IceCandidate;
import org.webrtc.PeerConnection;
import org.webrtc.SessionDescription;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.LinkedList;

import de.tavendo.autobahn.WebSocket;
import de.tavendo.autobahn.WebSocketConnection;
import de.tavendo.autobahn.WebSocketException;
import static android.content.ContentValues.TAG;

/**
 * Created by david on 2017/10/29.
 */

public class RTCClient {
    private SignalingEvents signalingevents;
//没初始化


    private  WebSocketChannelEvents events;
    private Handler handler = new Handler();
    private WebSocketConnection ws;
    private WebSocketObserver wsObserver;

    private String wsServerUrl="ws://192.168.1.5:8083/ws";
    private String roomID;
    private String clientID;
    public RTCClient(SignalingEvents events) {
        this.signalingevents = events;
    }

    String url;
    public  void connect() {
        Log.d(TAG, "Connect to room: " + url);
        LinkedList<PeerConnection.IceServer> iceServers=new LinkedList<PeerConnection.IceServer>();
        //打洞服务器
        iceServers.add(new PeerConnection.IceServer("stun:192.168.1.5:8081", "pds", "123456"));
//        iceServers.add(new PeerConnection.IceServer("turn:pds@ 192.168.1.5:8081", "pds", "123456"));
//假设这是给DN002 开视频  DN002又服务器返回
        SignalingParameters params = new SignalingParameters(iceServers,true,"15708468805","http://192.168.1.5:8083/ws","http://192.168.1.5:8083",null,null);
        Log.d(TAG, "Room connection completed.");
        signalingevents.onConnectedToRoom(params);
        ws = new WebSocketConnection();
        wsObserver = new WebSocketObserver();

        try {
//            连接信令服务器   注册自己
            URI uri = new URI(wsServerUrl);
            Log.e(TAG,"--->:start build uri");
            ws.connect(uri, wsObserver);
        } catch (WebSocketException e) {
            e.printStackTrace();
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        register("room1", "15708468805");
    }

    public void register(final String roomID, final String clientID) {
        this.roomID = roomID;
        this.clientID = clientID;
        JSONObject json = new JSONObject();
        try {
            json.put("cmd", "register");
            json.put("roomid", roomID);
            json.put("clientid", clientID);
            Log.d(TAG, "C->WSS: " + json.toString());
            ws.sendTextMessage(json.toString());
        } catch (JSONException ignored) {
        }catch (NullPointerException e){
            e.printStackTrace();
        }
    }

    public void send(String message) {
        JSONObject json = new JSONObject();
        try {
            json.put("cmd", "send");
            json.put("msg", message);
            message = json.toString();
            Log.d(TAG, "C->WSS: " + message);
            ws.sendTextMessage(message);
        } catch (JSONException e) {
        }
    }


    public interface WebSocketChannelEvents {
        void onWebSocketMessage(final String message);
        void onWebSocketClose();
        void onWebSocketError(final String description);
    }

    interface SignalingEvents {
        /**
         * Callback fired once the room's signaling parameters
         * SignalingParameters are extracted.
         */
        void onConnectedToRoom(final SignalingParameters params);

        /**
         * Callback fired once remote SDP is received.
         */
        void onRemoteDescription(final SessionDescription sdp);

        /**
         * Callback fired once remote Ice candidate is received.
         */
        void onRemoteIceCandidate(final IceCandidate candidate);

        /**
         * Callback fired once remote Ice candidate removals are received.
         */
        void onRemoteIceCandidatesRemoved(final IceCandidate[] candidates);

        /**
         * Callback fired once channel is closed.
         */
        void onChannelClose();

        /**
         * Callback fired once channel error happened.
         */
        void onChannelError(final String description);
    }
    private class WebSocketObserver implements WebSocket.WebSocketConnectionObserver {
        @Override
        public void onOpen() {
            handler.post(new Runnable() {
                @Override
                public void run() {
//                    我在房间1 等你
                    register("room1", clientID);
                }
            });
        }

        @Override
        public void onClose(WebSocketCloseNotification code, String reason) {

        }

        @Override
        public void onTextMessage(String payload) {
            Log.d(TAG, "WSS->C: " + payload);
            final String message = payload;
//            events.onWebSocketMessage(message);
        }

        @Override
        public void onRawTextMessage(byte[] payload) {}

        @Override
        public void onBinaryMessage(byte[] payload) {}
    }
}
