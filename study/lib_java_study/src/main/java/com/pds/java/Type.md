### type（泛型）
参考：[泛型，有点难度，会让很多人懵逼，那是因为你没有看这篇文章！](https://mp.weixin.qq.com/s/JLxrIaUGz2VL8CTeMUKYNA)
###### GenericDeclaration
  所有声明泛型变量的公共接口，这个接口中定义了一个方法：
  ```
  public TypeVariable<?>[] getTypeParameters()
  ```
  这个方法用于获取声明的泛型变量类型清单。

  泛型变量可以在类和方法中进行声明，从上面类图中也可以看出来，java中任何类可以使用Class对象表示，方法可以用Method类表示，类图中可以知，Class类和Method类实现了GenericDeclaration接口，
  
  所以可以调用他们的getTypeParameters方法获取其声明的泛型参数列表
  
  示例：Demo1，Demo2
  
###### ParameterizedType 
这个接口表示参数化类型，例如List<String>、Map<Integer,String>、UserMapper<UserModel>这种带有泛型的类型。
```
Type[] getActualTypeArguments()
```
获取泛型类型中的类型列表，就是<>中包含的参数列表.
如：List<String>泛型类型列表只有一个是String，而Map<Integer,String>泛型类型中包含2个类型：Integer和String，UserMapper<UserModel>泛型类型为UserModel，实际上就是<和>中间包含的类型列表

```
Type getRawType()
```
返回参数化类型中的原始类型.
比如：List<String>的原始类型为List，UserMapper<UserModel>原始类型为UserMapper，也就是<符号前面的部分。

```
Type[]  getOwnerType()
```
返回当前类型所属的类型。
例如存在A<T>类，其中定义了内部类InnerA<I>，则InnerA<I>所属的类型为A<I>，如果是顶层类型则返回null。这种关系比较常见的示例是Map<K,V>接口与Map.Entry<K,V>接口，Map<K,V>接口是Map.Entry<K,V>接口的所有者。

###### TypeVariable
这个接口表示的是泛型变量.
例如：List<T>中的T就是类型变量；而class C1<T1,T2,T3>{}表示一个类，这个类中定义了3个泛型变量类型，分别是T1、T2和T2，泛型变量在java中使用TypeVariable接口来表示，可以通过这个接口提供的方法获取泛型变量类型的详细信息。
```
Type[] getBounds()
```
获取泛型变量类型的上边界,如果未明确什么上边界默认为Object。例如：class Test<K extend Person>中K的上边界只有一个，是Person；而class Test<T extend List & Iterable>中T的上边界有2个，是List和Iterable

```
D getGenericDeclaration()
```
获取声明该泛型变量的原始类型.
例如：class Test<K extend Person>中的K为泛型变量，这个泛型变量时Test类定义的时候声明的，说明如果调用getGenericDeclaration方法返回的就是Test对应的Class对象。

还有方法中也可以定义泛型类型的变量，如果在方法中定义，那么上面这个方法返回的就是定义泛型变量的方法了，返回的就是Method对象。

```
String getName()
```
获取在源码中定义时的名字，如：class Test<K extend Person>就是K；class Test1<T>中就是T。

###### WildcardType
表示的是通配符泛型，通配符使用问号表示.
例如：? extends Number和? super Integer。

```
Type[] getUpperBounds()
```
返回泛型变量的上边界列表。

```
Type[] getLowerBounds()
```
返回泛型变量的下边界列表。

###### GenericArrayType
表示的是数组类型，且数组中的元素是ParameterizedType或者TypeVariable。
例如：List<String>[]或者T[]。

```
Type getGenericComponentType()
```
这个方法返回数组的组成元素。














 
  
