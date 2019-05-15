package blog.pds.com.socket.core.thread

/**
 * @author: pengdaosong
 * CreateTime:  2019/5/14 11:00 AM
 * Email：pengdaosong@medlinker.com
 * Description:
 */

interface ICacher<T,P>{
    /** prepare for a number of T  if you need */
    fun prepare(p: P)

    /**obtain T from cache . if not exist , create(p) will be called */
    fun obtain(p: P?): T

    /** clear the cache */
    fun clear()

    /**@hide when cacher havn't , create by this mMethod
     */
    fun create(p: P?): T

    /** recycle it  */
    fun recycle(t: T)
}