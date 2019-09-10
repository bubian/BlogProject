package blog.pds.com.socket.app.service

import android.util.Log
import blog.pds.com.data.protobuf.SocketBody
import blog.pds.com.data.protobuf.SocketHeader
import blog.pds.com.data.protobuf.SocketPackage
import blog.pds.com.socket.control.Constants
import java.io.BufferedReader
import java.io.InputStreamReader
import java.io.PrintWriter
import java.net.ServerSocket
import java.net.Socket

/**
 * @author: pengdaosong
 * CreateTime:  2019/5/13 11:22 AM
 * Email：pengdaosong@medlinker.com
 * Description:
 */

class SSocket {

    companion object {
        private const val TAG = Constants.SOCKET_TAG_SERVICE_PRE + "SSocket:"
    }

    /**
     * 创建一个服务器端socket，指定绑定的端口号，并监听此端口
     */
    private var serverSocket: ServerSocket = ServerSocket(6666)

    fun startAccept() {
        try {
//            Log.i(TAG,"start accept")
            //调用accept()方法开始监听，等待客户端的连接
            val socket = serverSocket.accept()
            //向客户端传递的信息
            val outputStream = socket.getOutputStream()
//            val printWriter = PrintWriter(outputStream,true)

            val dataS = "-----------socket connect success------------"

            Log.i(TAG,"dataS len = ${dataS.toByteArray().size}")

            val socketProtoBody = SocketBody.Body
                .newBuilder()
                .setId(1111)
                .setContent(dataS)
                .build()

            Log.i(TAG,"origin len = ${dataS.length}")

            val socketProtoHeader = SocketHeader.Header
                .newBuilder()
                .setType(1)
                .setLength(socketProtoBody.serializedSize)
                .build()

            val socketPackage = SocketPackage.Package
                .newBuilder()
                .setHeader(socketProtoHeader)
                .setBody(socketProtoBody)
                .build()

            val d = socketPackage.toByteArray()
            /**
             * 发生N个字节，接收要N+2个字节接收，暂时没有搞清楚原因
             */
            val l  = d.size

            Log.i(TAG,"origin d-len = $l")
            Log.i(TAG,"origin d = ${String(d)}")

            val p = ByteArray(l + 3)
            p[0] = 1
            p[1] = (l and 0x0000FF00 shr 8).toByte()
            p[2] = (l and 0x000000FF).toByte()
            d.copyInto(p,3,0,l)

            val s = String(p,Charsets.ISO_8859_1)

            Log.i(TAG,"origin p-len = ${p.size}")
            Log.i(TAG,"origin p = $s")
            Log.i(TAG,"origin p-s = ${s.length}")

            outputStream.write(p)
            outputStream.flush()
//            printWriter.write(s)
//            printWriter.flush()
            //获取输入流，并读取客户端信息
            val inputStream = socket.getInputStream()
            //把字节流转换成字符流
            val inputStreamReader = InputStreamReader(inputStream)
            //为字符流增加缓冲区
            val bufferedReader = BufferedReader(inputStreamReader)
            var data: String? = bufferedReader.readLine()

            while (data != null) {
//                Log.i(TAG,"accept data = $data")
                data = bufferedReader.readLine()
            }

            socket.shutdownInput()

            //关闭资源
//            printWriter.close()
            outputStream.close()
            inputStream.close()
            inputStreamReader.close()
            bufferedReader.close()
            socket.close()
            serverSocket.close()

        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            serverSocket.close()
        }
    }

    fun stop() {
        serverSocket.close()
    }
}