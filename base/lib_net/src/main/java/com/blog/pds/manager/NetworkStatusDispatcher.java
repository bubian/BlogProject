package com.blog.pds.manager;

import android.app.Application;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.Build;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleEventObserver;
import androidx.lifecycle.LifecycleOwner;

import com.blog.pds.utils.NetworkUtil;

import java.util.ArrayList;
import java.util.List;

import static android.content.Context.CONNECTIVITY_SERVICE;
import static androidx.lifecycle.Lifecycle.State.DESTROYED;

public class NetworkStatusDispatcher {

  private static final String TAG = "NetworkStatusDispatcher";
  private static final NetworkStatusDispatcher M_NETWORK_STATUS_MANGER = new NetworkStatusDispatcher();

  private List<NetworkStatusListener> mListeners = new ArrayList<>(5);


  private NetworkStatusDispatcher(){
    init(null);
  }

  @RequiresApi(api = Build.VERSION_CODES.N)
  private void initN(Application application){
    ConnectivityManager  connectivityManager = (ConnectivityManager) application.getSystemService(CONNECTIVITY_SERVICE);
    connectivityManager.registerDefaultNetworkCallback(new ConnectivityManager.NetworkCallback(){

    });
  }

  public static final NetworkStatusDispatcher instance(Application application){
    return M_NETWORK_STATUS_MANGER;
  }

  private void init(Application application){
    IntentFilter mFilter = new IntentFilter();
    //添加接收网络连接状态改变的action
    mFilter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
    application.registerReceiver(mReceiver, mFilter);
  }

  /**
   * 监听网络变化
   */
  private BroadcastReceiver mReceiver = new BroadcastReceiver() {
    @Override
    public void onReceive(Context context, Intent intent) {
      //网络未链接
      if (!NetworkUtil.isConnected(context)) {
        for (NetworkStatusListener listener : mListeners){
          listener.disconnect();
        }
      }else {
        NetworkUtil.NetState netState = NetworkUtil.getCurrentNetStateCode(context);
        for (NetworkStatusListener listener : mListeners){
          listener.connect(netState);
        }
      }
    }
  };

  class LifecycleBoundObserver implements LifecycleEventObserver {

    @NonNull
    final LifecycleOwner mOwner;
    final NetworkStatusListener mListener;

    LifecycleBoundObserver(@NonNull LifecycleOwner owner, NetworkStatusListener listener) {
      mListener = listener;
      mOwner = owner;
    }

    @Override
    public void onStateChanged(@NonNull LifecycleOwner source,
                               @NonNull Lifecycle.Event event) {
      if (mOwner.getLifecycle().getCurrentState() == DESTROYED) {
        unregister(mListener);
      }
    }
  }

  public void register(@NonNull LifecycleOwner owner, NetworkStatusListener listener) {
    if (owner.getLifecycle().getCurrentState() == DESTROYED) {
      return;
    }
    if (mListeners.contains(listener)) {
      return;
    }
    LifecycleBoundObserver wrapper = new LifecycleBoundObserver(owner, listener);
    mListeners.add(listener);
    owner.getLifecycle().addObserver(wrapper);
  }

  public void unregister(NetworkStatusListener listener){
    if (mListeners.contains(listener)){
      mListeners.remove(listener);
    }
  }

  public interface  NetworkStatusListener{
    void connect(NetworkUtil.NetState netState);
    void disconnect();
  }

}
