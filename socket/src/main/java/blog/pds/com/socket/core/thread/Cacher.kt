package blog.pds.com.socket.core.thread

/**
 * @author: pengdaosong
 * CreateTime:  2019/5/14 10:49 AM
 * Email：pengdaosong@medlinker.com
 * Description:
 */

abstract class Cacher<T,P> : ICacher<T,P>{

    /**
     * 静态常量声明
     */
   companion object {
       const val DEFAULT_MAX_POOL_SIZE = 10
   }


    private var node :Node<T>? = null
    private var currentPoolSize = 0
    private var maxPoolSize = DEFAULT_MAX_POOL_SIZE

    constructor()

    constructor(maxPoolSize : Int) {
        this.node = Node()
        this.maxPoolSize = maxPoolSize
    }



    override fun prepare(p: P) {
        synchronized(this) {
            var n = this.node
            var current = this.currentPoolSize
            val max = this.maxPoolSize

            while (current < max) {
                if (n?.t == null) {
                    n?.t = create(p)
                } else {
                    val n1 = Node<T>()
                    n1.next = n
                    n1.t = create(p)
                    n = n1 //new node is the front
                }
                current++
            }
            this.node = n
            this.currentPoolSize = current
        }
    }

    fun obtain(): T {
        return obtain(null)
    }

    override fun obtain(p: P?): T {
        synchronized(this) {
            if (this.node!!.t != null) {
                val tmp = this.node
                val t = tmp!!.t
                node = tmp.next
                tmp.next = null
                currentPoolSize--
                return t!!
            }
        }
        return create(p)
    }

    override fun clear() {
        synchronized(this) {
            var node: Node<T>? = this.node
            while (node != null) {
                node.t = null
                node = node.next
            }
            this.node = Node()
            currentPoolSize = 0
        }
    }

    override fun recycle(t: T) {
        synchronized(this) {
            if (currentPoolSize < maxPoolSize) {
                val nodeNew = Node<T>()
                nodeNew.next = node
                nodeNew.t = t
                this.node = nodeNew
                currentPoolSize++
                onRecycleSuccess(t)
            }
        }
    }

    private fun onRecycleSuccess(t: T) {}

    inner class Node<T> {
        var t: T? = null
        var next: Node<T>? = null
    }

}