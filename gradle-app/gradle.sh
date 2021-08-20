#!/bin/bash
echo "-----------start execute shell-------------"
# task-printProps
# printProps在gradle文件中注册的任务，如果这里执行命令不加"printProps"任务名，会报"To see a list of available tasks, run gradlew tasks"
# 环境参数配置：参考：https://docs.gradle.org/current/userguide/build_environment.html
../gradlew -q -PcommandLineProjectProp=commandLineProjectPropValue -Dorg.gradle.project.systemProjectProp=systemPropertyValue printProps

# task-performRelease
# 接受参数控制任务
../gradlew performRelease -PisCI=true --quiet

# task-showRepos
# 需要配置init.gradle文件
# 需要配置init.gradle文件(优先级：不同目录下的优先级请查看官方文档)
# 参考：https://docs.gradle.org/current/userguide/init_scripts.html
# 我的理解是在工程最开始的时候，预先配置，先于项目其它脚本执行,经过尝试放在"USER_HOME/.gradle/ "生效
# ../gradlew --init-script init.gradle
../gradlew --init-script init.gradle -q showRepos

# gradle :[module]:[任务名]
#参考：https://docs.gradle.org/current/userguide/intro_multi_project_builds.html
# lg:
gradle :api:build
#输出如下
#> Task :shared:compileJava
#> Task :shared:processResources
#> Task :shared:classes
#> Task :shared:jar
#> Task :api:compileJava
#> Task :api:processResources
#> Task :api:classes
#> Task :api:jar
#> Task :api:assemble
#> Task :api:compileTestJava
#> Task :api:processTestResources
#> Task :api:testClasses
#> Task :api:test
#> Task :api:check
#> Task :api:build

# 使用缓存构建命令
gradle --build-cache compileJava
# 缓存自定义配置，云缓存支持请参考：https://docs.gradle.org/current/userguide/build_cache.html



echo "-----------execute end-----------"
