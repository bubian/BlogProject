package blog.pds.com.data.utils

import java.io.Closeable
import java.io.IOException

/**
 * 资源释放工具
 *
 * @author noctis
 */
object CloseUtil {

    /**
     * 关闭多个IO流
     *
     * @param closeables io,io,io,...
     */
    fun close(vararg closeables: Closeable?) {
        for (closeable in closeables) {
            if (closeable != null) {
                try {
                    closeable.close()
                } catch (e: IOException) {
                    // ignore
                }

            }
        }
    }
}
