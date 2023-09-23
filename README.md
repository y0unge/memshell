## 基本介绍

用于测试jetty内存马及Tomcat内存马在不同版本的兼容性


通过maven插件快速修改不同的中间件版本。

### jetty相关的插件

总共3个覆盖版本6-11。

maven 运行命令

```shell
jetty:run -Djetty.port=8086
```

#### version 6

```xml
<plugin>
    <groupId>org.mortbay.jetty</groupId>
    <artifactId>maven-jetty-plugin</artifactId>
    <version>6.1.26</version>
    <configuration>
        <webAppSourceDirectory>${basedir}/src/main/webapp</webAppSourceDirectory>
        <scanIntervalSeconds>3</scanIntervalSeconds>
        <contextPath>/</contextPath>
        <connectors>
            <connector implementation="org.mortbay.jetty.nio.SelectChannelConnector">
                <port>8086</port>
            </connector>
        </connectors>
    </configuration>
</plugin>
```


#### version 7-8

```xml
            <plugin>
                <groupId>org.mortbay.jetty</groupId>
                <artifactId>jetty-maven-plugin</artifactId>
<!--                <version>7.6.16.v20140903</version>-->
<!--                <version>7.0.0pre0</version>-->
                <version>8.0.0.M0</version>
<!--                <version>8.1.16.v20140903</version>-->
                <configuration>
                    <webAppSourceDirectory>${basedir}/src/main/webapp</webAppSourceDirectory>
                    <scanIntervalSeconds>3</scanIntervalSeconds>
                    <contextPath>/</contextPath>
                </configuration>
            </plugin>
```

#### version 9-11

10 和11 需要较高的jdk，这里11还会spring 的错误待修复。

```xml
<plugin>
    <groupId>org.eclipse.jetty</groupId>
    <artifactId>jetty-maven-plugin</artifactId>
<!--                <version>9.4.52.v20230823</version>-->
<!--                <version>9.0.0.M2</version>-->
    <version>10.0.0</version>
<!--                <version>11.0.16</version>-->
    <configuration>
        <httpConnector>
            <port>8086</port>
            <host>localhost</host>
        </httpConnector>
        <webAppSourceDirectory>${basedir}/src/main/webapp</webAppSourceDirectory>
    </configuration>
</plugin>
```

