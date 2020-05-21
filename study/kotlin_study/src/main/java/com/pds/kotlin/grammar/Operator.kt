package com.pds.kotlin.grammar

/**
 * @author: pengdaosong
 * CreateTime:  2020-05-19 10:33
 * Email：pengdaosong@medlinker.com
 * Description:
 */

fun main(){
//    alsoTest()
//    alsoTest1()
    withTest()
}

/**
 * also:调用{}代码块，以'this'作为参数（这里是b），also最终返回this
 * 执行代码块后，b变成了a的值，而{}代码块是以'this'（也就是b）作为参数（值传递），
 * 所有{}执行完后，返回的'this'依然是传入参数时b的值，而不是被赋值后的b的值
 */
fun alsoTest(){
    var a = 1
    var b = 2

    a = b.also { b = a }

    println("a = $a,b = $b")
}

/**
 * 这里传入的是对象引用的地址，所以依然是值传递
 */
fun alsoTest1(){
    var a = User()
    var b = User("dao",2)
    a = b.also {
        b = a
    }
    println("a = $a\nb = $b")
}

/**
 *with:以接收者（这里就是参数a）作为{}的参数，然后返回结果，所以在with后{}可以连续调用a的中的变量或者方法。
 *with定义with(receiver: T, block: T.() -> R): R，T.()可以看成是T的扩展函数，而函数体就是{}，所以在{}中持有接收者的对象实例的。返回值是范型R。
 */
fun withTest() {
    val a = User()

    val b = with(a){
      println(name)
      println(sex.toString())
      return@with("123")
    }
    println("a = $a\nb = $b")
}

/**
 * let定义：fun <T, R> T.let(block: (T) -> R): R，(T)可以看出，以'this'作为参数调用{}代码块，返回类型为R.
 * 在let中不能像with那样操作，因为{}并没有持有调用者实例，可以去看一下T.()和()的区别。
 */
fun  letTest(){
    val a =User()
    a?.let {
        println("a = $it")
    }
}

class User(var name : String = "pds", var sex : Int = 1){
    override fun toString(): String {
        return "name = $name, sex = $sex"
    }

    private var phone: String = "157"

}