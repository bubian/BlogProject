// ISocketAIDLData.aidl
package blog.pds.com.socket;

import blog.pds.com.socket.ISocketAIDLReceiveData;
// Declare any non-default types here with import statements

interface ISocketAIDLSendData {
    boolean sendSocketData(in byte[] data);
    boolean reregisterCallback(in ISocketAIDLReceiveData socketAIDLReciveData);
    boolean unReregisterCallback();
}
