package blog.pds.com.socket.core.dispatch

import android.os.ParcelFileDescriptor
import blog.pds.com.socket.IMemoryAidlInterface
import android.os.MemoryFile
import java.io.FileDescriptor


/**
 * 匿名共享内存
 * @author: pengdaosong
 * CreateTime:  2019-08-29 15:35
 * Email：pengdaosong@medlinker.com
 * Description:
 */
object MemoryAidlBinder : IMemoryAidlInterface.Stub() {
    public const val MEMORY_FILE_NAME = "memory_socket_blog"
    override fun getParcelFileDescriptor(): ParcelFileDescriptor? {
        var memoryFile: MemoryFile?
        try {
            memoryFile = MemoryFile(MEMORY_FILE_NAME, 2048)
            memoryFile.outputStream.write(byteArrayOf(1, 2, 3, 4, 5))
            val method = MemoryFile::class.java.getDeclaredMethod("getFileDescriptor")
            val des = method.invoke(memoryFile) as FileDescriptor
            return ParcelFileDescriptor.dup(des)
        } catch (e: Exception) {
        }
        return null
    }
}