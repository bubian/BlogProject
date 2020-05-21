package com.pds.kotlin.grammar

import android.os.Build
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.ReceiveChannel
import kotlinx.coroutines.channels.produce
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flow
import java.nio.file.Files
import java.nio.file.Paths

/**
 * @author: pengdaosong
 * CreateTime:  2020-05-19 10:01
 * Email：pengdaosong@medlinker.com
 * Description:
 */

private fun fileStream(){
    val stream =
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            Files.newInputStream(Paths.get("/some/file.txt"))
        } else {
            TODO("VERSION.SDK_INT < O")
        }
    stream.buffered().reader().use { reader ->
        println(reader.readText())
    }

    var a = 1
    var b = 2
    a = b.also { b = a }
}

private fun sequenceTest() : Sequence<Int> = sequence {
    for (i in 1..3) {
        Thread.sleep(100) // 假装我们正在计算
        yield(i) // 产生下一个值
    }
}

// 序列
private fun test(){
    sequenceTest().forEach { value -> println(value) }
}

// 流
private fun flowTest() : Flow<Int> = flow{
    for (i in 1..3) {
        delay(100) // 假装我们在这里做了一些有用的事情
        emit(i) // 发送下一个值
    }
}

private fun test1() = runBlocking {
    // 启动并发的协程以验证主线程并未阻塞
    launch {
        for (k in 1..3) {
            println("I'm not blocked $k")
            delay(100)
        }
    }
    // 收集这个流
    flowTest().collect { value -> println(value) }
}

private suspend fun test2(){
    // 将一个整数区间转化为流
    (1..3).asFlow().collect { value -> println(value) }

}

private fun test3() = runBlocking{
    val channel = Channel<Int>()
    launch {
        // 这里可能是消耗大量 CPU 运算的异步逻辑，我们将仅仅做 5 次整数的平方并发送
        for (x in 1..5) channel.send(x * x)
    }
// 这里我们打印了 5 次被接收的整数：
    repeat(5) { println(channel.receive()) }
    println("Done!")
}

// 关闭与迭代通道
private fun test4() = runBlocking{
    val channel = Channel<Int>()
    launch {
        for (x in 1..5) channel.send(x * x)
        channel.close() // 我们结束发送
    }
// 这里我们使用 `for` 循环来打印所有被接收到的元素（直到通道被关闭）
    for (y in channel) println(y)
    println("Done!")
}

// 管道
fun CoroutineScope.produceNumbers() = produce<Int> {
    var x = 1
    while (true) send(x++) // 在流中开始从 1 生产无穷多个整数
}

fun CoroutineScope.square(numbers: ReceiveChannel<Int>): ReceiveChannel<Int> = produce {
    for (x in numbers) send(x * x)
}

private fun test5() = runBlocking{
    val numbers = produceNumbers() // 从 1 开始生成整数
    val squares = square(numbers) // 整数求平方
    repeat(5) {
        println(squares.receive()) // 输出前五个
    }
    println("Done!") // 至此已完成
    coroutineContext.cancelChildren() // 取消子协程
}

// 使用管道的素数
fun CoroutineScope.numbersFrom(start: Int) = produce<Int> {
    var x = start
    while (true) send(x++) // 开启了一个无限的整数流
}

fun CoroutineScope.filter(numbers: ReceiveChannel<Int>, prime: Int) = produce<Int> {
    for (x in numbers) if (x % prime != 0) send(x)
}

private fun test6() = runBlocking{
    var cur = numbersFrom(2)
    repeat(10) {
        val prime = cur.receive()
        println(prime)
        cur = filter(cur, prime)
    }
    coroutineContext.cancelChildren() // 取消所有的子协程来让主协程结束
}


fun main(){
    test1()
}


