package blog.pds.com.socket.core.thread

import android.app.Activity
import android.os.Build
import androidx.fragment.app.Fragment
import android.util.Log
import blog.pds.com.socket.core.common.Constants
import java.lang.ref.WeakReference

/**
 * @author: pengdaosong
 * CreateTime:  2019/5/14 10:13 AM
 * Email：pengdaosong@medlinker.com
 * Description:
 */

class RunnablePool private constructor(){


    companion object {

        const val TAG = Constants.SOCKET_TAG_CLIENT_PRE + "rp:"

        var cacher : Cacher<Runner,Void>? = null

        fun initCacher(maxPoolSize : Int){
            cacher = object : Cacher<Runner,Void>(maxPoolSize){
                override fun create(p: Void?): Runner {
                   return object : Runner(){
                       override fun run() {
                           super.run()
                           recycle(this)
                       }
                   }
                }

            }
        }

        fun obtain(executor: IRunnbleExecutor, what: Int, vararg params: Any): Runner {
            if (cacher == null) {
                initCacher(10)
            }
            val runner = cacher!!.obtain()
            runner.executor = executor
            runner.what = what
            params.forEach {
               Log.d(TAG,"params = $it")
            }
            runner.params = params
            Log.i(TAG,"what = $what  params = $params  runner.params = ${runner.params}")
            for (c in runner.params!!){
                Log.d(TAG,"runner.params = $c")
            }
            return runner
        }

    }

    open class Runner : Runnable {
        private var weakExecutor: WeakReference<IRunnbleExecutor>? = null
        var params: Array<out Any>? = null
        var what: Int = 0

        var executor: IRunnbleExecutor? = null
            get() = if (weakExecutor != null) weakExecutor!!.get() else field

            set(executor) = if (executor is androidx.fragment.app.Fragment || executor is android.app.Fragment
                || executor is Activity) {
                this.weakExecutor = WeakReference(executor)
            } else {
                field = executor
            }

        override fun run() {
            val executor = executor
            if (executor == null) {
                Log.w(TAG, "mExecutor == null or is recycled(Fragment or Activity)")
                return
            }

            var shouldExecute = true
            if (executor is Activity) {
                if ((executor as Activity).isFinishing) {
                    Log.i(TAG, "executor is Activity and isFinishing() = true. ")
                    shouldExecute = false
                }
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1 && (executor as Activity).isDestroyed) {
                    Log.i(TAG, "executor is Activity and isDestroyed() = true. ")
                    shouldExecute = false
                }
            } else if (executor is androidx.fragment.app.Fragment) {
                if ((executor as androidx.fragment.app.Fragment).isDetached || (executor as androidx.fragment.app.Fragment).isRemoving) {
                    // ((Fragment) executor).isVisible()
                    Log.i(
                        TAG,
                        "executor is Fragment and isDestroyed() ||  isRemoving() = true. "
                    )
                    shouldExecute = false
                }
            } else if (executor is android.app.Fragment) {
                if ((executor as android.app.Fragment).isDetached || (executor as android.app.Fragment).isRemoving) {
                    // ((Fragment) executor).isVisible()
                    Log.i(
                        TAG,
                        "executor is android.app.Fragment and isDestroyed() ||  isRemoving() = true. "
                    )
                    shouldExecute = false
                }
            }
            if (shouldExecute) {
                executor.execute(what, this.params!!)
            }
            afterRun()
        }

        private fun afterRun() {
            this.weakExecutor = null
            this.executor = null
            this.params = null
        }
    }

     interface IRunnbleExecutor {

        fun execute(what: Int, params: Array<out Any>)
    }
}