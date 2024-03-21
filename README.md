# 如何启动项目

## 1. 启动consul

consul下载地址：https://www.consul.io/downloads.html 。版本为1.18.0

启动consul服务，使用以下命令：

```shell
consul agent -server -bind=127.0.0.1 -data-dir=data -ui -bootstrap -log-level=error
```
> 注意：consul默认ui端口为8500，需要进入ui界面配置。 记得在consul根目录下创建data文件夹（consul.exe和文件夹在同一个目录内），否则会报错。

## 2. consul配置Key/Value

创建key，名字为config/platform/data，格式为yaml。修改下面的配置为你的数据库配置后创建。

```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/goopper?useUnicode=true&characterEncoding=utf8&useSSL=false
    username: root
    password: 123456
    driver-class-name: com.mysql.cj.jdbc.Driver
  data:
    redis:
      host: localhost
      port: 6379
      database: 0
```

## 3. 启动项目

使用IDEA启动 [PlatformApplication.kt](src%2Fmain%2Fkotlin%2Ftop%2Fgoopper%2Fplatform%2FPlatformApplication.kt)