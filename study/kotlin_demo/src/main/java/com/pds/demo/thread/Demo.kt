package com.pds.demo.thread

import kotlinx.coroutines.*
import kotlinx.coroutines.flow.collect

/**
 * @author: pengdaosong
 * @CreateTime:  2020/12/21 2:06 PM
 * @Email: pengdaosong@medlinker.com
 * @Description:
 */
fun threadDemo() {
    GlobalScope.launch { // 在后台启动一个新的协程并继续
        delay(1000L) // 非阻塞的等待 1 秒钟（默认时间单位是毫秒）
        println("World!") // 在延迟后打印输出
    }
    println("Hello,") // 协程已在等待时主线程还在继续
    Thread.sleep(2000L) // 阻塞主线程 2 秒钟来保证 JVM 存活
}

fun threadTest() = runBlocking {
    for (i in 0..100) {
        GlobalScope.launch { // 在后台启动一个新的协程并继续
            delay(100L) // 非阻塞的等待 1 秒钟（默认时间单位是毫秒）
            println("current thread name = ${Thread.currentThread().name}") // 在延迟后打印输出
        }
    }

    println("Hello,") // 协程已在等待时主线程还在继续
    Thread.sleep(20000L) // 阻塞主线程 2 秒钟来保证 JVM 存活
}

fun main() = runBlocking<Unit>{
    threadTest()
    launch {
        println("Hello, name ="+Thread.currentThread().name) // 协程已在等待时主线程还在继续
    }
}