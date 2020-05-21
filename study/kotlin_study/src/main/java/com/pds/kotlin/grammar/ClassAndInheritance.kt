package com.pds.kotlin.grammar

import android.content.Context
import android.util.AttributeSet
import android.view.View
import kotlin.properties.Delegates


/**
 * 在 Kotlin 中的一个类可以有一个主构造函数以及一个或多个次构造函数。主构造函数是类头的一部分：它跟在类名（与可选的类型参数）后。
 * 如果主构造函数没有任何注解或者可见性修饰符，可以省略这个 constructor 关键字。
 * 主构造函数不能包含任何的代码。初始化的代码可以放到以 init 关键字作为前缀的初始化块（initializer blocks）中。
 * 如果构造函数有注解或可见性修饰符，这个 constructor 关键字是必需的，并且这些修饰符在它前面：
 */
class Person constructor(firstName: String) {
    init {
        println("执行 Person 初始化代码块")
    }
}

/**
 * 在实例初始化期间，初始化块按照它们出现在类体中的顺序执行，与属性初始化器交织在一起：
 * 主构造的参数可以在初始化块中使用。它们也可以在类体内声明的属性初始化器中使用。
 *
 * 初始化块中的代码实际上会成为主构造函数的一部分。委托给主构造函数会作为次构造函数的第一条语句，因此所有初始化块与属性初始化器中的代码都会在次构造函数体之前执行。
 * 即使该类没有主构造函数，这种委托仍会隐式发生，并且仍会执行初始化块
 */
class InitOrderDemo(name: String) {
    lateinit var subject: User
    // 属性初始化器
    val firstProperty = "First property: $name".also(::println)
    // 初始化块
    init { println("First initializer block that prints $name") }
    // 属性初始化器
    val secondProperty = "Second property: ${name.length}".also(::println)
    // 初始化块
    init { println("Second initializer block that prints ${name.length}") }

    // 次构造函数，如果类有一个主构造函数，每个次构造函数需要委托给主构造函数， 可以直接委托或者通过别的次构造函数间接委托。委托到同一个类的另一个构造函数用 this 关键字即可
    constructor(name: String,sex: Int) : this(name) {
        println("执行 Person 次构造函数")
    }

    fun check(){
        if (this::subject.isInitialized) {
            println(subject)
        }
    }
}

// 如果派生类有一个主构造函数，其基类可以（并且必须） 用派生类主构造函数的参数就地初始化。
//
//如果派生类没有主构造函数，那么每个次构造函数必须使用 super 关键字初始化其基类型，或委托给另一个构造函数做到这一点。 注意，在这种情况下，不同的次构造函数可以调用基类型的不同的构造函数
class MyView : View {
    constructor(ctx: Context) : super(ctx)
    constructor(ctx: Context, attrs: AttributeSet) : super(ctx, attrs)
}
open class Rectangle {
    open fun draw() { println("Drawing a rectangle") }
    open val borderColor: String get() = "black"
}

class FilledRectangle: Rectangle() {
    override fun draw() { /* …… */ }
    override val borderColor: String get() = "black"

    inner class Filler {
        fun fill() { /* …… */ }
        fun drawAndFill() {
            super@FilledRectangle.draw() // 调用 Rectangle 的 draw() 实现
            fill()
            println("Drawn a filled rectangle with color ${super@FilledRectangle.borderColor}") // 使用 Rectangle 所实现的 borderColor 的 get()
        }
    }
}

interface Polygon {
    fun draw() { /* …… */ } // 接口成员默认就是“open”的
}

class Square() : Rectangle(), Polygon {
    // 编译器要求覆盖 draw()：
    override fun draw() {
        super<Rectangle>.draw() // 调用 Rectangle.draw()
        super<Polygon>.draw() // 调用 Polygon.draw()
    }
}


interface Base {
    fun printMessage()
    fun printMessageLine()
}

class BaseImpl(val x: Int) : Base {
    override fun printMessage() { print(x) }
    override fun printMessageLine() { println(x) }
}

class Derived(b: Base) : Base by b {
    override fun printMessage() { print("abc") }
}

class Per {
    var name: String by Delegates.observable("<no name>") {
            prop, old, new ->
        println("$old -> $new")
    }
}

fun main(){
    val a = InitOrderDemo("pds")

    val b = BaseImpl(10)
    Derived(b).printMessage()
    Derived(b).printMessageLine()
}

