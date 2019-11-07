package blog.pds.com.socket.core.policy

/**
 * @author: pengdaosong
 * CreateTime:  2019/5/13 3:38 PM
 * Email：pengdaosong@medlinker.com
 * Description:
 */

 class CRetryPolicy{

    /**
     * 默认最大重试次数
     */
    private  val DEFAULT_MAX_RETRIES = 5

    /**
     * 当前重试次数.
     */
    private var mCurrentRetryCount: Int = 0

    /**
     * 最大重试次数.
     */
    private var mMaxNumRetries: Int = DEFAULT_MAX_RETRIES

    constructor()

    constructor(maxNumRetries: Int){
        this.mMaxNumRetries = mMaxNumRetries
    }

    fun getCurrentRetryCount(): Int {
        return mCurrentRetryCount
    }

    fun retry(ex: Exception){
        mCurrentRetryCount++
        if (!hasAttemptRemaining()) {
            throw ex
        }
    }

    /**
     * 重置当前重试次数
     */
    fun reset() {
        this.mCurrentRetryCount = 0
    }

    /**
     * Returns true if this policy has attempts remaining, false otherwise.
     */
    private fun hasAttemptRemaining(): Boolean {
        return mCurrentRetryCount <= mMaxNumRetries
    }
}
