package blog.pds.com.data.utils

import java.nio.ByteBuffer
import java.nio.CharBuffer
import java.nio.charset.Charset





/**
 * @author: pengdaosong
 * CreateTime:  2019-09-06 11:28
 * Email：pengdaosong@medlinker.com
 * Description:
 */

class DataTypeTransform{

    companion object{
        /**
         * char[] 数组转为byte[] 数组
         */
        fun getBytes(chars: CharArray): ByteArray{
            val cs = Charset.forName("UTF-8")
            val cb = CharBuffer.allocate(chars.size)
            cb.put(chars)
            cb.flip()
            val bb = cs.encode(cb)
            return bb.array()

        }

        /**
         * byte[] 数组转为数组 char[]
         */
        fun getChars(byteArray: ByteArray): CharArray{
            val cs = Charset.forName("UTF-8")
            val bb = ByteBuffer.allocate(byteArray.size)
            bb.put(byteArray)
            bb.flip()
            val cb = cs.decode(bb)
            return cb.array()
        }
    }
}