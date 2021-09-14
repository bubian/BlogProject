package com.pds.demo.grammar

/**
 *  inline: 声明在编译时，将函数的代码拷贝到调用的地方(内联)
 *  noinline: 声明 inline 函数的形参中，不希望内联的 lambda
 *  crossinline: 禁止传递给内联函数的 lambda 中的非局部返回
 */
inline fun sum(a: Int, b: Int, crossinline bad: (result: Int) -> Unit): Int {
    val r = a + b
    bad.invoke(r)
    return r
}

fun main() {
    repeat(100){
        sum(1, 2) {
            println(it)
        }
    }
}


