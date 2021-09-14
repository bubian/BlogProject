package com.pds.api

import kotlinx.coroutines.*


// 挂起函数
private suspend fun suspendFun1() {
    println("suspendFun1")
}

// 挂起函数
suspend fun suspendFun2() {
    println("suspendFun2")
}

private fun startCoroutine() {
    // funTest协程体
    val funTest: suspend CoroutineScope.() -> Unit = {
        println("funTest")
        suspendFun1()
        suspendFun2()
    }
    GlobalScope.launch(Dispatchers.Default, block = funTest)

    println("funTest22")

    Thread.sleep(2000)
}

private fun test4() = runBlocking{
    launch { doWorld() }
    println("suspend complete")

    Thread.sleep(10000)
}

// 这是你的第一个挂起函数
suspend fun doWorld() {
    println("suspend! name 11= ${Thread.currentThread().name}")
    delay(3000L)
    println("suspend! name = ${Thread.currentThread().name}")
}

fun main() {
    startCoroutine()
}
