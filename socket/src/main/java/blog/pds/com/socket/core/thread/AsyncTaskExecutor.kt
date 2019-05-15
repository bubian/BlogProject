package blog.pds.com.socket.core.thread

import java.util.concurrent.*
import java.util.concurrent.atomic.AtomicInteger

/**
 * @author: pengdaosong
 * CreateTime:  2019/5/14 12:49 PM
 * Email：pengdaosong@medlinker.com
 * Description:
 */

class AsyncTaskExecutor{
    companion object {
        private val CPU_COUNT = Runtime.getRuntime().availableProcessors()
        private val CORE_POOL_SIZE = Math.max(2, Math.min(CPU_COUNT - 1, 4))
        private val MAXIMUM_POOL_SIZE = CPU_COUNT * 2 + 1
        private const val KEEP_ALIVE_SECONDS = 30

        private val sThreadFactory = object : ThreadFactory {
            private val mCount = AtomicInteger(1)
            override fun newThread(r: Runnable): Thread {
                return Thread(r, "AsyncTask #" + mCount.getAndIncrement())
            }
        }

        private val sPoolWorkQueue = LinkedBlockingQueue<Runnable>(128)
        var THREAD_POOL_EXECUTOR: Executor
        init {
            val threadPoolExecutor = ThreadPoolExecutor(
                CORE_POOL_SIZE, MAXIMUM_POOL_SIZE, KEEP_ALIVE_SECONDS.toLong(), TimeUnit.SECONDS,
                sPoolWorkQueue, sThreadFactory, ThreadPoolExecutor.DiscardOldestPolicy()
            )
            threadPoolExecutor.allowCoreThreadTimeOut(true)
            THREAD_POOL_EXECUTOR = threadPoolExecutor
        }

        fun execute(command: Runnable) {
            THREAD_POOL_EXECUTOR.execute(command)
        }
    }
}