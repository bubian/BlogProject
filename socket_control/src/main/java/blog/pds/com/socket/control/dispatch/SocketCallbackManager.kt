package blog.pds.com.socket.control.dispatch


/**
 * @author: pengdaosong
 * CreateTime:  2019-09-04 15:53
 * Email：pengdaosong@medlinker.com
 * Description:
 */

object SocketCallbackManager{
    val socketCallbackList = emptySet<SocketCallback>().toMutableSet()

    public fun registerSocketCallback(socketCallback: SocketCallback?){
        socketCallback?.let { socketCallbackList.add(socketCallback) }
    }

    public fun unregisterSocketCallback(socketCallback: SocketCallback?){
        socketCallback?.let { socketCallbackList.remove(socketCallback) }

    }

    public fun removeAllSocketCallback(){
        socketCallbackList.clear()
    }
}