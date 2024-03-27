# 本地开发指南

> 需要注意，下面的oauth2客户端的信息目前为 https://github.com/organizations/Goopper/settings/apps/goopper-dev
>
> 此APP是为本地开发所用，回调地址配置为 http://localhost:8888

# 不使用Consul配置中心

## 1. 修改配置文件

修改resources目录下的`bootstrap-dev.yml`文件，内容替换为你的配置：

```yaml
spring:
  application:
    name: platform-backend
  cloud:
    loadbalancer:
      enabled: false
    # 关闭consul
    consul:
      enabled: false
  # OAuth 信息
  security:
    oauth2:
      client:
        registration:
          github:
            client-id: Iv1.6a814d5cbc56f413
            client-secret: b075bbd36668fdf127e69e2a1aae02e5ec2019ac
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

## 2. 启动项目

使用IDEA启动 [PlatformApplication.kt](src%2Fmain%2Fkotlin%2Ftop%2Fgoopper%2Fplatform%2FPlatformApplication.kt) 。

为了启用`dev`环境，需要在启动配置中添加`-Dspring.profiles.active=dev`参数，或者修改启动配置的`Active profiles`为`dev`。

# 使用Consul配置中心

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
  # OAuth 信息
  security:
    oauth2:
      client:
        registration:
          github:
            client-id: Iv1.6a814d5cbc56f413
            client-secret: b075bbd36668fdf127e69e2a1aae02e5ec2019ac
  data:
    redis:
      host: localhost
      port: 6379
      database: 0
```

## 3. 启动项目

使用IDEA启动 [PlatformApplication.kt](src%2Fmain%2Fkotlin%2Ftop%2Fgoopper%2Fplatform%2FPlatformApplication.kt)