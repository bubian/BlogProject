package com.blog.pds.manager;

import android.app.Application;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;

import com.blog.pds.utils.NetworkUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * @author: pengdaosong.
 * CreateTime:  2018/12/12 1:50 PM
 * Email：pengdaosong@medlinker.com.
 * Description:
 */
public class NetworkStatusDispatcher {

  private static final String TAG = "NetworkStatusDispatcher";
  private static final NetworkStatusDispatcher M_NETWORK_STATUS_MANGER = new NetworkStatusDispatcher();

  private List<NetworkStatusListener> mListeners = new ArrayList<>(5);


  private NetworkStatusDispatcher(){
    init(null);
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

  public void register(NetworkStatusListener listener){
    if (mListeners.contains(listener)){
      return;
    }
    mListeners.add(listener);
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
