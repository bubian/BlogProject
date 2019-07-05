<project xmlns="http://maven.apache.org/POM/4.0.0" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
    xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/maven-v4_0_0.xsd">

    <!-- Pom的版本号，永远是4.0.0 -->
    <modelVersion>4.0.0</modelVersion>

    <!-- 组织域名的倒转写法，其实就是AndroidManifest.xml里面package节点的值-->
    <groupId>org.hello</groupId>

    <!-- 项目的名字，比如在IDE的project view里面显示的名字, 注意不一定是app显示在手机上的名字-->
    <artifactId>gs-maven-android</artifactId>

   <!-- 项目版本号-->
    <version>0.1.0</version>

<!-- 下面会有重复的groupId,artifactId, version 节点，意思都是一样的-->

   <!-- 表示打包成一个apk-->
    <packaging>apk</packaging>


    <properties>
        <!-- use UTF-8 for everything -->
        <project.build.sourceEncoding>UTF-8</project.build.sourceEncoding>
        <project.reporting.outputEncoding>UTF-8</project.reporting.outputEncoding>
    </properties>

   <!-- 这个项目所依赖的library，可以有好几个dependency子节点，每个表示一个依赖到的library-->
    <dependencies>
        <dependency>
            <groupId>com.google.android</groupId>
            <artifactId>android</artifactId>
            <version>4.1.1.4</version>
           <!-- Scope表示这个library存在的时期，有compile，runtime，test和provided，provided表示在运行时这个app所在的device会给他提供 -->
            <scope>provided</scope>
        </dependency>
    </dependencies>


   <!-- Build 节点配置的是项目编译的时候需要用到的一些东西,一个build节点下面有一个plugins子节点，在下面有多个plugin子节点，每个plugin子节点有groupId，artifactId，version等子节点，含义跟上面说到的基本一样。此外，plugin节点一般还有configuration节点，表示对这个plugin进一步的配置-->
    <build>
        <plugins>
            <plugin>
                <groupId>com.jayway.maven.plugins.android.generation2</groupId>
                <artifactId>android-maven-plugin</artifactId>
                <version>3.9.0-rc.1</version>
                <configuration>
                    <sdk>
                        <platform>19</platform>
                    </sdk>
                    <deleteConflictingFiles>true</deleteConflictingFiles>
                    <undeployBeforeDeploy>true</undeployBeforeDeploy>
                </configuration>
                <!--是否加载这个plugin的extensions，可以为true和false，也可以有一些列的extension子节点，在这里要设为true-->
                <extensions>true</extensions>
            </plugin>
            <plugin>
                <artifactId>maven-compiler-plugin</artifactId>
                <version>3.1</version>
                <configuration>
                    <source>1.6</source>
                    <target>1.6</target>
                </configuration>
            </plugin>
        </plugins>
    </build>
