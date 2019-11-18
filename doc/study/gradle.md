### gradle
#### <font color="#0060ff">任务</font>

- 创建任务

  doLast 代表task执行的最后一个action，通俗来讲就是task执行完毕后会回调doLast中的代码,操作符<< 是doLast方法的快捷版本.

    ```
    task hello << {
        println 'Hello world!'
    }
    ```

  1. 直接用任务名称创建
    
     def Task hello=task(hello)
  2. 任务名称+任务配置创建
  
     def Task hello=task(hello,group:BasePlugin.BUILD_GROUP)[其中group为任务配置项，它代表了分组]
     
  3. TaskContainer的create方法创建
  
     tasks.create(name: 'hello')
     
  此前创建任务的方式最终都会调用tasks的create方法，其中tasks类型为TaskContainer
  
  
- 任务依赖

  任务依赖会决定任务运行的先后顺序，被依赖的任务会在定义依赖的任务之前执行
  
  ```
  task hello << {
    println 'Hello world!'
  }
  task go(dependsOn: hello) << {
    println "go for it"
  }
  ```
  在hello任务的基础上增加了一个名为go的任务，通过dependsOn来指定依赖的任务为hello，因此go任务运行在hello之后
  
  
- 动态定义任务 
 
  动态定义任务指的是在运行时来定义任务的名称
  
  ```
  3.times {number ->
    task "task$number" << {
        println "task $number"
    }
  }
  ```

- 任务的分组和描述  

  Gradle有任务组的概念，可以为任务配置分组和描述，以便于更好的管理任务，拥有良好的可读性，哈哈
  ```
  task hello {
	group = 'build'
	description = 'hello world'
    doLast {
    	println "任务分组: ${group}"
        println "任务描述: ${description}"
    }
  }
  ```
  或者
  ```
  def Task hello=task(hello)
  hello.description ='hello world'
  hello.group=BasePlugin.BUILD_GROUP
  ```
#### <font color="#0060ff">日志</font>

  通过gradle -q + 任务名称来运行一个指定的task，这个q是命令行开关选项，通过开关选项可以控制输出的日志级别。
  
#### <font color="#0060ff">Gradle 命令行</font> 
- 获取所有任务信息

  gradle -q tasks
  
- 排除任务
  
  gradle hello -x + 任务名
  
- 获取任务帮助信息

  gradle -q help --task + 任务名
  
- 任务名称缩写
  
  保证缩写名唯一，便可以通过缩写名执行任务。
  
#### <font color="#0060ff">Gradle Wrapper</font>

命令：gradle wrapper（Gradle已经内置了Wrapper Task）

- 也可以用gradle命令行选项，来生成gradle wrapper
  ```
  –gradle-version：用于下载和执行指定的gradle版本。
  –distribution-type：指定下载Gradle发行版的类型，可用选项有bin和all，默认值是bin，-bin发行版只包含运行时，但不包含源码和文档。
  –gradle-distribution-url： 指定下载Gradle发行版的完整URL地址。
  –gradle-distribution-sha256-sum：使用的SHA 256散列和验证下载的Gradle发行版。
  ```
  比如使用命令行：gradle wrapper –gradle-version 4.2.1 –distribution-type all，就可以生成版本为4.2.1的包装器，并使用-all发行版。
  
- 自定义Gradle Wrapper
    
  Gradle已经内置了Wrapper Task，因此构建Gradle Wrapper会生成Gradle Wrapper的属性文件，这个属性文件可以通过自定义Wrapper Task来设置。比如我们想要修改要下载的Gralde版本为4.2.1，可以这么设置：
  ```
  task wrapper(type: Wrapper) {
    gradleVersion = '4.2.1'
  }
  ```
  
#### <font color="#0060ff">插件开发</font>
- 上传插件

  发布到Maven、ivy
  
  [https://docs.gradle.org/4.4/userguide/publishing_ivy.html]
  
  [https://docs.gradle.org/4.4/userguide/publishing_maven.html]
  
  发布到Gradle插件门户
  
  [https://docs.gradle.org/4.4/userguide/plugins.html#sec:plugins_block]
  
#### <font color="#0060ff">Gradle的库依赖管理</font>
- 通过transitive来禁止依赖传递
  ```
  implementation('com.xxx.xxx:xxx:3.6.3') {
     transitive false
  }
  ```
- Gradle的依赖检查

  执行gradle :app:dependencies
  

- Gradle的依赖冲突
  
  有时候我们不是想要排除某个库，而是需要强制使用统一的库的版本，force可以强制设置模块的库的版本
  
  ```
  configurations.all {
    resolutionStrategy {
        force 'com.squareup.okio:okio:2.1.0'
    }
  }
  dependencies {
    ...
  ｝
  ```
  
  有些时候需要排除库依赖传递中涉及的库，此时不能靠关闭依赖传递来解决问题，这时可以使用exclude。
  
  ```
  configurations {
    all*.exclude group: 'com.android.support', module: 'support-annotations'
  }
  dependencies {
    ...
  }
  ```
  
