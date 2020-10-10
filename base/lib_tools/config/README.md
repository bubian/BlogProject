### diffuse.jar

    JakeWharton大神开源的一款用于比较文件内容差异工具，支持APK, AAB, AAR, and JAR。
    工具会列出详细的差异报告，在排查有些bug的时候非常有用。
    
    更多参考：https://github.com/JakeWharton/diffuse

### dependency-tree-diff.jar

    列出依赖差异，更多参考：https://github.com/JakeWharton/dependency-tree-diff

    执行./gradlew :app:dependencies --configuration releaseRuntimeClasspath > old.txt命令，
    生成改变前的依赖配置信息。
    
    执行./gradlew :app:dependencies --configuration releaseRuntimeClasspath > new.txt命令，
    生成修改后的依赖配置信息
    
    执行./dependency-tree-diff.jar old.txt new.txt 生成改变前后依赖差异信息。

### 核心工具（core）
##### dokit

    基于滴滴公司开源的一款调试工具，根据自身业务二次开发的工具，功能强大。
    更多参考：http://xingyun.xiaojukeji.com/docs/dokit/#/intro
##### reflect
    反射增强工具。
    更多参考：https://github.com/tiann/FreeReflection  


