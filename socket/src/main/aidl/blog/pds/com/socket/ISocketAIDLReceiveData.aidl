// ISocketAIDLReciveData.aidl
package blog.pds.com.socket;

// Declare any non-default types here with import statements

interface ISocketAIDLReceiveData {
      void receiveSocketData(in int type,in byte[] data);
      void socketConnectState(int state);
}
