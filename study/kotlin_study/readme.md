### 类型投影
- out(型变)

  Array<out String>相当于java：Array<? extends String>
  
- in(逆变)
  Array<in String>相当于java：Array<? super String>
  
### 星投影
Kotlin 为此提供了所谓的星投影语法
- 对于 Foo <out T : TUpper>，其中 T 是一个具有上界 TUpper 的协变类型参数，Foo <*> 等价于 Foo <out TUpper>。 这意味着当 T 未知时，你可以安全地从 Foo <*> 读取 TUpper 的值。
- 对于 Foo <in T>，其中 T 是一个逆变类型参数，Foo <*> 等价于 Foo <in Nothing>。 这意味着当 T 未知时，没有什么可以以安全的方式写入 Foo <*>。
- 对于 Foo <T : TUpper>，其中 T 是一个具有上界 TUpper 的不型变类型参数，Foo<*> 对于读取值时等价于 Foo<out TUpper> 而对于写值时等价于 Foo<in Nothing>。

如果泛型类型具有多个类型参数，则每个类型参数都可以单独投影。 例如，如果类型被声明为 interface Function <in T, out U>，我们可以想象以下星投影：

- Function<*, String> 表示 Function<in Nothing, String>；
- Function<Int, *> 表示 Function<Int, out Any?>；
- Function<*, *> 表示 Function<in Nothing, out Any?>。

注意：星投影非常像 Java 的原始类型，但是安全

### 泛型约束
- 上界

  最常见的约束类型是与 Java 的 extends 关键字对应的 上界：fun <T : Comparable<T>> sort(list: List<T>) {  …… }