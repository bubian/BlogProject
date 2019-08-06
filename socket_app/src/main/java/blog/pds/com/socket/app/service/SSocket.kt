package blog.pds.com.socket.app

import android.util.Log
import blog.pds.com.socket.control.Constants
import java.io.*
import java.net.ServerSocket

/**
 * @author: pengdaosong
 * CreateTime:  2019/5/13 11:22 AM
 * Email：pengdaosong@medlinker.com
 * Description:
 */

class SSocket{

    companion object {
        private const val TAG = Constants.SOCKET_TAG_SERVICE_PRE + "SSocket:"
    }

    /**
     * 创建一个服务器端socket，指定绑定的端口号，并监听此端口
     */
    private var serverSocket: ServerSocket = ServerSocket(Constants.SOCKET_PORT)

    fun startAccept(){
        try {
            Log.i(TAG,"start accept")
            //调用accept()方法开始监听，等待客户端的连接
            val socket = serverSocket.accept()
            //向客户端传递的信息
            val outputStream = socket.getOutputStream()
            val printWriter = PrintWriter(outputStream)
            printWriter.write("-----------socket connect success------------")
            printWriter.flush()
            //获取输入流，并读取客户端信息
            val inputStream = socket.getInputStream()
            //把字节流转换成字符流
            val inputStreamReader = InputStreamReader(inputStream)
            //为字符流增加缓冲区
            val bufferedReader = BufferedReader(inputStreamReader)
            var data :String? = bufferedReader.readLine()

            while (data != null){
                Log.i(TAG,"accept data = $data")
                data = bufferedReader.readLine()
            }

            socket.shutdownInput()
            printWriter.write("-----------socket disconnect------------")
            printWriter.flush()

            //关闭资源
            printWriter.close()
            outputStream.close()
            inputStream.close()
            inputStreamReader.close()
            bufferedReader.close()
            socket.close()
            serverSocket.close()

        }catch (e:Exception){
            e.printStackTrace()
        }finally {
            serverSocket.close()
        }
    }

    fun stop(){
        serverSocket.close()
    }
}