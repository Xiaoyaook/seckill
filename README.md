# 电商秒杀系统后端总结

本项目中对于秒杀的高并发最关键的优化就是使用Redis缓存和RabbitMQ异步下单

对于恶意防刷，我们采用隐藏秒杀地址，数字验证码和接口限流来预防

主要页面有四个：用户登录，商品陈列，商品详情，订单详情

使用Swagger2，通过注解的形式自动生成API文档

系统架构图：
![系统架构图](./photo/seckill-system-architecture.png)

swagger生成的Api文档:
![api文档](./photo/swagger生成的Api文档.png)

## 一、环境搭建

我们使用Maven来管理项目：

### 在pom内添加以下依赖

* mysql-connector-java，JDBC 依赖
* mybatis-spring-boot-starter，Mybatis 依赖
* Druid，数据库连接池 依赖
* Jedis，Redis驱动 依赖
* fastjson，用来转换Java对象与JSON
* commons-codec，提供摘要运算、编码的包
* commons-lang3，提供一些基础的、通用的操作和处理的工具类
* spring-boot-starter-validation，jsr303参数校验框架 依赖
* spring-boot-starter-amqp，消息队列依赖
* spring-boot-starter-thymeleaf，Thymleaf 依赖(前后端分离后不再需要)
* Swagger2，swagger-ui，API文档依赖

### 在application.propeties中进行配置

本项目中，我们配置了：
* Mybatis
* Druid
* Redis
* RabbitMQ
* 静态配置

## 二、数据库设计

总共五张表：

* goods，商品基本信息
* seckill_goods，秒杀商品信息
* seckill_order，秒杀订单信息，创建唯一索引，保证数据不重复
* seckill_user，用户信息
* order_info，订单详情

## 三、前后端分离，后端API设计

/api (项目中没有加入这个)
* /goods/to_list : 商品列表
* /goods/detail/{goodsId} : 商品详情
* /login/do_login : 用户登录
* /seckill/path : 获取秒杀地址
* /seckill/verifyCode : 获取验证码
* /seckill/{path}/do_seckill : 进行秒杀
* /seckill/result : 获取秒杀结果
* /order/detail : 订单详情

## 四、代码编写

由API，按部就班编写代码实现功能。

介绍几个要点：

### 接口返回数据规范化

写一个Result和CodeMsg类，返回数据用Result封装一下

### 全局异常处理

首先自定义一个异常类GlobalException，之后

1. 新建一个GlobalExceptionHandler类 
2. 添加注解@ControllerAdvice
3. 在class中添加一个方法 
4. 在方法上添加@ExceptionHandler拦截相应的异常信息 
5. 如果返回的是View 方法的返回值是ModelAndView 
6. 如果返回的是String或者是Json数据，那么需要在方法上添加@ResponseBody注解

### 自定义校验器注解

校验器注解IsMobile
校验器注解的处理类IsMobileValidator

### Redis

首先对Redis连接池JedisPool进行配置。

其次是一些常规化的操作的封装，这里要注意String和Bean之间的转换问题。
在这里我们使用引入的fastjson库来解决该问题。

为了对Key有所区分，我们以KeyPrefix+Key组合成真正存放于Redis中的Key。
在这里我们首先创建最顶层的接口和实现该接口的抽象类，让子类复用，避免开发重复代码。
所有类型的Prefix都继承自该抽象类。

### 自定义解析器进行参数绑定

WebConfig实现WebMvcConfigurer接口，完成我们自定义解析器和拦截器的注册与绑定。

UserArgumentResolver实现HandlerMethodArgumentResolver接口，完成我们自定义的User参数的解析。

### 自定义拦截器

AccessInterceptor实现HandlerInterceptorAdapter接口，对自定义注解AccessLimit进行拦截。完成接口限流防刷功能。

### RabbitMQ

RabbitMQ提供了四种Exchange模式：fanout，direct，topic，header。
本项目中我们使用direct模式，完成异步下单操作。

